package it.polimi.se2.ricciosorrentinotriuzzi.business.components;

import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;

import javax.ejb.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Stateless
public class RequestHandler {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.component/DataModel")
    protected DataModel dataModel;
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.business.components/VisitManager")
    protected VisitManager visitManager;

    public RequestHandler() {}

    public Lineup lineup(int numberOfPeople, String customerID, String storeID) {
        Store store = dataModel.getStore(storeID);
        if (store == null) {
            System.out.println("Non esiste uno store con id: "+storeID);
            return null;
        }
        if (numberOfPeople > store.getMaximumOccupancy() || numberOfPeople<=0)
            return null;
        Customer customer = dataModel.getCustomer(customerID);
        if (customer == null) {
            System.out.println("Non esiste un customer con id: "+customerID);
            return null;
        }
        List<Lineup> customerLineups = customer.getLineups();
        if (customerLineups != null && !customerLineups.isEmpty()) {
            for (Lineup l: customerLineups) {
                if (l.isPending() || l.isReady()) {
                    System.out.println("Il customer è già in coda per un negozio");
                    return null;
                }
            }
        }
        LocalDateTime now = LocalDateTime.now();
        if (!store.isOpenAt(now)) {
            System.out.println("Lo store non è aperto");
            return null;
        }

        Lineup newLineup = new Lineup(store, customer,Timestamp.valueOf(dataModel.getQueueDisposalTime(storeID).toLocalDateTime().plus(
                    Duration.ofNanos(store.getAverageVisitDuration().toLocalTime().toNanoOfDay()))), numberOfPeople);
        dataModel.insertRequest(newLineup);
        visitManager.newRequest(newLineup);
        return newLineup;
    }

    public Booking book(int numberOfPeople, String customerID, String storeID,
                        Timestamp desiredStart, Time duration, ArrayList<String> sectionIDs) {
        Store store = dataModel.getStore(storeID);
        if (store == null) {
            System.out.println("Non esiste uno store con id: "+storeID);
            return null;
        }
        if (numberOfPeople > store.getMaximumOccupancy() || numberOfPeople<=0)
            return null;
        Customer customer = dataModel.getCustomer(customerID);
        if (customer == null || !customer.isAppCustomer()) {
            System.out.println("Non esiste un customer con id: "+customerID);
            return null;
        }
        if (!store.isOpenAt(desiredStart.toLocalDateTime(), duration.toLocalTime())) {
            System.out.println("Lo store è chiuso nell'orario selezionato");
            return null;
        }
        if (desiredStart.before(dataModel.getQueueDisposalTime(storeID))) {
            System.out.println("Il booking inizia prima del queue disposal time");
            return null;
        }
        Timestamp end = Timestamp.valueOf(desiredStart.toLocalDateTime().plusHours(duration.toLocalTime().getHour()).plusMinutes(duration.toLocalTime().getMinute()));
        System.out.println("start: "+ desiredStart+ " end: " + end);
        if (dataModel.checkBookings(customerID,desiredStart,end)) {
            System.out.println("Il customer ha un overlapping booking");
            return null;
        }

        // check sulle occupancy
        List<Booking> otherBookings = dataModel.getBookings(storeID, desiredStart, end);
        System.out.println(otherBookings);
        int maxStoreOcc = store.getMaximumOccupancy();
        for (Booking booking : otherBookings) {
            System.out.println(booking.toJson());
            maxStoreOcc -= booking.getNumberOfPeople();
            if (maxStoreOcc <=0) {
                System.out.println("Non c'è abbastanza spazio nello store per questo booking");
                return null;
            }
        }

        List<Productsection> productsections = new LinkedList<>();
        for (String sid: sectionIDs) {
            Productsection ps = dataModel.getSection(Long.valueOf(sid));
            if (ps.getStore().equals(store))
                productsections.add(ps);
        }

        Booking newBooking = new Booking(store, customer, numberOfPeople, desiredStart, duration, productsections);
        dataModel.insertRequest(newBooking);
        visitManager.newRequest(newBooking);
        return newBooking;
    }

    public void cancelRequest(String uuid) {
        VisitRequest request = dataModel.getVisitRequest(uuid);
        if (request != null && (request.isPending() || request.isReady())) {
            dataModel.removeRequest(request);
            //check if other customers can enter
            visitManager.checkNewReadyRequest(request.getStore().getId());
        }
        System.out.println("Request "+uuid+" has been deleted!");
    }
}