import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;

import javax.ejb.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.Timer;

@Stateless
public class VisitManager {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;

    public VisitManager() {
    }

    public int validateAccess(String visitToken, String storeID){
        //VisitRequest request = dataModel.getVisitRequest(visitToken);
        //return  request != null && request.getStore().getId().equals(storeID) && request.isReady();
        return dataModel.checkReadyRequest(storeID, visitToken);
    }

    public boolean confirmAccess(String visitToken, String storeID, int numberOfPeople){
        int oldNumberOfPeople = dataModel.getVisitRequest(visitToken).getNumberOfPeople();
        boolean toReturn = dataModel.startVisit(visitToken, storeID, numberOfPeople);
        if (oldNumberOfPeople>numberOfPeople){
            checkNewReadyRequest(storeID);
        }
        return toReturn;
        /*VisitRequest request = dataModel.getVisitRequest(visitToken);
        if(request != null && request.getStore().getId().equals(storeID) && request.isFulfilled()){
            //devo aggiornare
        }

        return true;
        */
    }

    public int validateExit(String visitToken, String storeID){
        VisitRequest request = dataModel.getVisitRequest(visitToken);
        //System.out.println(request.getHfid());
        //System.out.println(request.getStore());
        return  request != null && request.getStore().getId().equals(storeID) && request.isFulfilled() ? request.getNumberOfPeople() : 0;
    }

    public boolean confirmExit(String visitToken, String storeID, int numberOfPeople) {
        boolean toReturn = dataModel.endVisit(visitToken, storeID, numberOfPeople);
        checkNewReadyRequest(storeID);
        return toReturn;
    }

//    @Asynchronous
//    public void newRequest(String token){
//        System.out.println("NEW REQUEST! UUID: " + token);
//
//        VisitRequest request = dataModel.getVisitRequest(token);
//        Store store = dataModel.getVisitRequest(token).getStore();
//        int currentReadyOccupancy = store.getCurrentOccupancy();
//        for (VisitRequest visitRequest: store.getLineups()) {
//            if (visitRequest.isReady())
//                currentReadyOccupancy += visitRequest.getNumberOfPeople();
//        }
//        for (VisitRequest visitRequest: store.getBookings()) {
//            if (visitRequest.isReady())
//                currentReadyOccupancy += visitRequest.getNumberOfPeople();
//        }
//
//        if(request.isBooking()){
//            System.out.println("Parto!");
//            Date date = ((Booking) request).getDesiredStartingTime();
//            Timer timer = new Timer();
//            int finalCurrentReadyOccupancy = currentReadyOccupancy;
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    System.out.println("Vengo lanciato alle ore: "+ LocalTime.now());
//                    for (Productsection section : ((Booking)request).getProductSections()) {
//                        if(section.getCurrentOccupancy() +
//                                request.getNumberOfPeople() * (double)section.getMaximumOccupancy()/store.getMaximumOccupancy() >
//                                section.getMaximumOccupancy())
//                            return;
//                    }
//                    System.out.println(request.getNumberOfPeople() + " + " + finalCurrentReadyOccupancy + " <= " + store.getMaximumOccupancy());
//                    if (request.getNumberOfPeople() + finalCurrentReadyOccupancy <= store.getMaximumOccupancy()) {
//                        setReadyRequest(token);
//                    }
//                }
//            }, date);
//        }
//        else {
//            if (request.getNumberOfPeople() + currentReadyOccupancy <= store.getMaximumOccupancy()) {
//                setReadyRequest(token); ///TODO controlla che si possano passare
//            }
//        }
//    }

    @Asynchronous
    public void newRequest(VisitRequest request){
        System.out.println("NEW REQUEST! UUID: " + request.getUuid());
        Store store = request.getStore();
        int currentReadyOccupancy = store.getCurrentOccupancy();
        for (VisitRequest visitRequest: store.getLineups()) {
            if (visitRequest.isReady())
                currentReadyOccupancy += visitRequest.getNumberOfPeople();
        }
        for (VisitRequest visitRequest: store.getBookings()) {
            if (visitRequest.isReady())
                currentReadyOccupancy += visitRequest.getNumberOfPeople();
        }

        if(request.isBooking()){
            System.out.println("Parto!");
            Date date = ((Booking) request).getDesiredStartingTime();
            Timer timer = new Timer();
            int finalCurrentReadyOccupancy = currentReadyOccupancy;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Vengo lanciato alle ore: "+ LocalTime.now());
                    for (Productsection section : ((Booking)request).getProductSections()) {
                        if(section.getCurrentOccupancy() +
                                request.getNumberOfPeople() * (double)section.getMaximumOccupancy()/store.getMaximumOccupancy() >
                                section.getMaximumOccupancy())
                            return;
                    }
                    System.out.println(request.getNumberOfPeople() + " + " + finalCurrentReadyOccupancy + " <= " + store.getMaximumOccupancy());
                    if (request.getNumberOfPeople() + finalCurrentReadyOccupancy <= store.getMaximumOccupancy()) {
                        setReadyRequest(request.getUuid());
                    }
                }
            }, date);
        }
        else {
            if (request.getNumberOfPeople() + currentReadyOccupancy <= store.getMaximumOccupancy()) {
                setReadyRequest(request.getUuid()); ///TODO controlla che si possano passare
            }
        }
    }


    private void setReadyRequest(String token){
        dataModel.allowVisitRequest(token);
    }

    @Asynchronous
    public void checkNewReadyRequest(String storeID){
        Store store = dataModel.getStore(storeID);
        int currentReadyOccupancy = store.getCurrentOccupancy();
        System.out.println("Current occ: " + currentReadyOccupancy);
        for (VisitRequest visitRequest: store.getLineups()) {
            if (visitRequest.isReady())
                currentReadyOccupancy += visitRequest.getNumberOfPeople();
        }
        for (VisitRequest visitRequest: store.getBookings()) {
            if (visitRequest.isReady())
                currentReadyOccupancy += visitRequest.getNumberOfPeople();
        }
        System.out.println("The current ready occupancy is " + currentReadyOccupancy);
        List<Booking> pendingBookings = dataModel.getBookings(storeID, Timestamp.valueOf(LocalDate.now().atStartOfDay()), Timestamp.valueOf(LocalDateTime.now()));
        for (Booking booking:pendingBookings) {
            System.out.println("Booking analyzed: " + booking.getHfid() + " status: " + booking.getState() + " isPending: " + booking.isPending());
            if (booking.isPending()) {
                if (currentReadyOccupancy + booking.getNumberOfPeople() <= store.getMaximumOccupancy()) {
                    for (Productsection section : booking.getProductSections()) {
                        if(section.getCurrentOccupancy() +
                                booking.getNumberOfPeople() * (double)section.getMaximumOccupancy()/store.getMaximumOccupancy() >
                                section.getMaximumOccupancy())
                            return;
                    }
                    dataModel.allowVisitRequest(booking.getUuid());
                    currentReadyOccupancy += booking.getNumberOfPeople();
                } else return;
            }
        }

        for (Lineup lineup : store.getLineups()){
            if (lineup.isPending()){
                if (currentReadyOccupancy + lineup.getNumberOfPeople() <= store.getMaximumOccupancy()){
                    dataModel.allowVisitRequest(lineup.getUuid());
                    currentReadyOccupancy += lineup.getNumberOfPeople();
                }
            }
        }
        /*
        Prendo tutti i booking non ancora ready i cui time interval si overlappano con (now, quando)?.
        Dopodiché, vedo se possono entrare. Per fare ciò, mi salvo localmente la curr.occ. dello store
        e la incremento mano mano, finché non raggiunge il suo massimo.

         */
    }


}
