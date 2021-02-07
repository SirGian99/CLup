package it.polimi.se2.ricciosorrentinotriuzzi.business.components;

import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;

import javax.ejb.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.Timer;

@Stateless
public class VisitManager {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.component/DataModel")
    protected DataModel dataModel;

    public VisitManager() {
    }

    public int validateAccess(String visitToken, String storeID) {
        return dataModel.checkReadyRequest(storeID, visitToken);
    }

    public boolean confirmAccess(String visitToken, String storeID, int numberOfPeople) {
        int oldNumberOfPeople = dataModel.getVisitRequest(visitToken).getNumberOfPeople();
        boolean toReturn = dataModel.startVisit(visitToken, storeID, numberOfPeople);
        if (oldNumberOfPeople > numberOfPeople) {
            checkNewReadyRequest(storeID);
        }
        return toReturn;
    }

    public int validateExit(String visitToken, String storeID) {
        VisitRequest request = dataModel.getVisitRequest(visitToken);
        return request != null && request.getStore().getId().equals(storeID) && request.isFulfilled() ? request.getNumberOfPeople() : 0;
    }

    public boolean confirmExit(String visitToken, String storeID, int numberOfPeople) {
        boolean toReturn = dataModel.completeVisit(visitToken, storeID, numberOfPeople);
        checkNewReadyRequest(storeID);
        return toReturn;
    }

    @Asynchronous
    public void newRequest(VisitRequest request) {
        Store store = request.getStore();
        int currentReadyOccupancy = store.getCurrentOccupancy();
        for (VisitRequest visitRequest : store.getLineups()) {
            if (visitRequest.isReady())
                currentReadyOccupancy += visitRequest.getNumberOfPeople();
        }
        for (VisitRequest visitRequest : store.getBookings()) {
            if (visitRequest.isReady())
                currentReadyOccupancy += visitRequest.getNumberOfPeople();
        }

        if (request.isBooking()) {
            Date date = ((Booking) request).getDesiredStartingTime();
            Timer timer = new Timer();
            int finalCurrentReadyOccupancy = currentReadyOccupancy;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    scheduleBooking((Booking) request, finalCurrentReadyOccupancy, store);
                }
            }, date);
        } else {
            System.out.println(request.getNumberOfPeople() + " + " + currentReadyOccupancy + " <= " + store.getMaximumOccupancy() +"?");
            if (request.getNumberOfPeople() + currentReadyOccupancy <= store.getMaximumOccupancy()) {
                dataModel.allowVisitRequest(request);
            }
        }
    }

    // This method is invoked whenever a customer exits the store or a pending or ready request is canceled.
    // The VisitManager checks if there is a request which is allowed to enter the store, given priority to bookings
    // over lineups. Bookings are considered in order of "desired starting time", from the earliest to the latest, if
    // its value is past the current time, while lineups in order of "time of creation", from the earliest to the latest
    @Asynchronous
    public void checkNewReadyRequest(String storeID) {
        Store store = dataModel.getStore(storeID);
        int currentReadyOccupancy = store.getCurrentOccupancy();
        for (VisitRequest visitRequest : store.getLineups()) {
            if (visitRequest.isReady())
                currentReadyOccupancy += visitRequest.getNumberOfPeople();
        }
        for (VisitRequest visitRequest : store.getBookings()) {
            if (visitRequest.isReady())
                currentReadyOccupancy += visitRequest.getNumberOfPeople();
        }
        List<Booking> pendingBookings = dataModel.getBookings(storeID, Timestamp.valueOf(LocalDate.now().atStartOfDay()), Timestamp.valueOf(LocalDateTime.now()));
        for (Booking booking : pendingBookings) {
            if (booking.isPending()) {
                if (currentReadyOccupancy + booking.getNumberOfPeople() <= store.getMaximumOccupancy()) {
                    for (Productsection section : booking.getProductSections()) {
                        if (section.getCurrentOccupancy() +
                                booking.getNumberOfPeople() * (double) section.getMaximumOccupancy() / store.getMaximumOccupancy() >
                                section.getMaximumOccupancy())
                            return;
                    }
                    dataModel.allowVisitRequest(booking);
                    currentReadyOccupancy += booking.getNumberOfPeople();
                } else return;
            }
        }

        for (Lineup lineup : store.getLineups()) {
            if (lineup.isPending()) {
                if (currentReadyOccupancy + lineup.getNumberOfPeople() <= store.getMaximumOccupancy()) {
                    dataModel.allowVisitRequest(lineup);
                    currentReadyOccupancy += lineup.getNumberOfPeople();
                }
            }
        }
    }

    protected void scheduleBooking(Booking request, int finalCurrentReadyOccupancy, Store store) {
        System.out.println("Scheduled for booking " + request.getHfid() + " at " + request.getDateTimeOfCreation());
        System.out.println("Checking if at " + request.getDesiredStartingTime() + " the request can be set to ready");
        for (Productsection section : (request).getProductSections()) {
            if (section.getCurrentOccupancy() +
                    request.getNumberOfPeople() * (double) section.getMaximumOccupancy() / store.getMaximumOccupancy() >
                    section.getMaximumOccupancy())
                return;
        }
        if (request.getNumberOfPeople() + finalCurrentReadyOccupancy <= store.getMaximumOccupancy()) {
            dataModel.allowVisitRequest(request);
        }
    }
}
