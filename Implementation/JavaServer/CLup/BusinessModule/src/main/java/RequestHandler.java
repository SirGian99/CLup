import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;

import javax.ejb.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class RequestHandler {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi/VisitManager")
    private VisitManager visitManager;

    public RequestHandler() {}

    public Lineup lineup(int numberOfPeople, String customerID, String storeID) {
        Store s = dataModel.getStore(storeID);
        if (s == null) {
            System.out.println("Non esiste uno store con id: "+storeID);
            return null;
        }
        Customer c = dataModel.getCustomer(customerID);
        if (c == null) {
            System.out.println("Non esiste un customer con id: "+customerID);
            return null;
        }
        List<Lineup> custLineUps = c.getLineups();
        if (custLineUps != null && !custLineUps.isEmpty()) {
            for (Lineup l: custLineUps) {
                if (l.isPending()) {
                    System.out.println("Il customer ha già in coda per un negozio");
                    return null;
                }
            }
        }
        LocalDateTime now = LocalDateTime.now();
        if (!s.isOpenAt(now)) {
            System.out.println("Lo store non è aperto");
            return null;
        }
        Lineup lur = new Lineup();
        System.out.println("Now:" + Timestamp.valueOf(LocalDateTime.now()) +"\nEstimated queue disposal time: " + dataModel.getQueueDisposalTime(storeID));
        System.out.println("Avg.dur. dello store: " + dataModel.getStore(storeID).getAverageVisitDuration().getTime());
        lur.setEstimatedTimeOfEntrance(Timestamp.valueOf(dataModel.getQueueDisposalTime(storeID).toLocalDateTime().plus(Duration.ofNanos(dataModel.getStore(storeID).getAverageVisitDuration().toLocalTime().toNanoOfDay()))));
        lur.setCustomer(c);
        lur.setStore(s);
        lur.setNumberOfPeople(numberOfPeople);
        lur.setDateTimeOfCreation(Timestamp.valueOf(now));
        lur.setState(VisitRequestStatus.PENDING);
        lur.setHfid("L-" +(char)( Integer.parseInt(lur.getDateTimeOfCreation().toString().substring(8, 9)) % 26 + 65) + String.valueOf(Integer.parseInt(lur.getUuid().substring(4, 8), 16) % 999));
        dataModel.insertRequest(lur);
        visitManager.newRequest(lur);
        return lur;
    }

    public Booking book(int numberOfPeople, String customerID, String storeID,
                        Timestamp desiredStart, Time duration, ArrayList<String> sectionIDs) {
        Store s = dataModel.getStore(storeID);
        if (s == null) {
            System.out.println("Non esiste uno store con id: "+storeID);
            return null;
        }
        Customer c = dataModel.getCustomer(customerID);
        if (c == null) {
            System.out.println("Non esiste un customer con id: "+customerID);
            return null;
        }
        if (!s.isOpenAt(desiredStart.toLocalDateTime(), duration.toLocalTime())) {
            System.out.println("Lo store è chiuso nell'orario selezionato");
            return null;
        }
        if (desiredStart.before(dataModel.getQueueDisposalTime(storeID))) {
            System.out.println("Il booking inizia prima del queue disposal time");
            return null;
        }
        Timestamp end = Timestamp.valueOf(desiredStart.toLocalDateTime().plusHours(duration.toLocalTime().getHour()).plusMinutes(duration.toLocalTime().getHour()));
        List<Booking> overlappingBookings = dataModel.getCustomerBookings(customerID,desiredStart,end);
        if (!overlappingBookings.isEmpty()) {
            System.out.println("Il customer ha un overlapping booking");
            return null;
        }

        // check sulle occupancy
        List<Booking> otherBookings = dataModel.getBookings(storeID, desiredStart, end);
        int maxStoreOcc = s.getMaximumOccupancy();
        for (Booking booking : otherBookings) {
            maxStoreOcc -= booking.getNumberOfPeople();
            if (maxStoreOcc <=0) {
                System.out.println("Non c'è abbastanza spazio nello store per questo booking");
                return null;
            }
        }

        Booking br = new Booking();
        br.setCustomer(c);
        br.setStore(s);
        br.setNumberOfPeople(numberOfPeople);
        br.setDateTimeOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        br.setState(VisitRequestStatus.PENDING);
        br.setDesiredStartingTime(desiredStart);
        br.setDesiredDuration(duration);
        br.setHfid("B-" + (char)( Integer.parseInt(br.getDesiredStartingTime().toString().substring(8, 9)) % 26 + 65) + String.valueOf(Integer.parseInt(br.getUuid().substring(4, 8), 16) % 999));
        for (String sid: sectionIDs) {
            Productsection ps = dataModel.getSection(Long.valueOf(sid));
            if (ps.getStore().equals(s)) {
                br.addProductSection(ps);
            }
        }
        dataModel.insertRequest(br);
        visitManager.newRequest(br);
        return br;
    }

    public void cancelRequest(String uuid) {
        VisitRequest request = dataModel.getVisitRequest(uuid);
        if (request.isPending() || request.isReady()) {
            dataModel.removeRequest(request);
            //check if other customers can enter
            visitManager.checkNewReadyRequest(request.getStore().getId());
        }
        System.out.println("Request "+uuid+" has been deleted!");
    }
}