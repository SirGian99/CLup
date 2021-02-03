import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;

import javax.ejb.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        lur.setCustomer(c); //aggiorna entrambi i lati della relazione
        lur.setStore(s);
        lur.setNumberOfPeople(numberOfPeople);
        lur.setDateTimeOfCreation(Timestamp.valueOf(now));
        lur.setState(VisitRequestStatus.PENDING);
        //TODO devi settare anche l'ete (per ora messo pezzotto)
        lur.setEstimatedTimeOfEntrance(Timestamp.valueOf(now.plusMinutes(30)));
        dataModel.insertRequest(lur);
        visitManager.newRequest(lur);
        //TODO devi fare anche la append to queue se è una lur
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
            return null;}
        //|| desiredStart.before(Timestamp.from(Instant.now()))
        //check sulla current queue disp time

        Timestamp end = Timestamp.valueOf(desiredStart.toLocalDateTime().plusHours(duration.toLocalTime().getHour()).plusMinutes(duration.toLocalTime().getHour()));
        List<Booking> overlappingBookings = dataModel.getCustomerBookings(customerID,desiredStart,end);
        if (!overlappingBookings.isEmpty()) {
            System.out.println("Il customer ha un overlapping booking");
            return null;
        }
        Booking br = new Booking();
        br.setCustomer(c);
        br.setStore(s);
        br.setNumberOfPeople(numberOfPeople);
        br.setDateTimeOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        br.setState(VisitRequestStatus.PENDING);
        br.setDesiredStartingTime(desiredStart);
        br.setDesiredDuration(duration);
        //TODO sistema sto HFID
        br.setHfid("B"+(new Random(10).nextInt()));
        //TODO controlli sulle sections
        //for (String sid: sectionIDs) { }
        //TODO check sulle occupancy...
        //TODO chiama VisitManager.newRequest(token)
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
    }
}