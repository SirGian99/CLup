package it.polimi.se2.ricciosorrentinotriuzzi.business.components;

import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Stateless
public class ManagerController {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.component/DataModel")
    protected DataModel dataModel;

    public List<Lineup> getStoreQueue(String storeID) {
        return dataModel.getQueue(storeID);
    }

    public List<Booking> getPendingBookings(String storeID) {
        List<Booking> bookings = dataModel.getBookings(storeID, Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.MAX));
        if (bookings != null)
            bookings.removeIf(b -> !(b.isPending()));
        return bookings;
    }

    public List<VisitRequest> getCompletedVisits(String storeID) {
        List<VisitRequest> requests = dataModel.getVisitRequests(storeID, Timestamp.valueOf(LocalDateTime.now()));
        if (requests != null)
            requests.removeIf(r -> !r.isCompleted());
        return requests;
    }

    public List<VisitRequest> getVisitsInProgress(String storeID) {
        List<VisitRequest> requests = dataModel.getVisitRequests(storeID, Timestamp.valueOf(LocalDateTime.now()));
        if (requests != null)
            requests.removeIf(r -> !r.isFulfilled());
        return requests;
    }
}