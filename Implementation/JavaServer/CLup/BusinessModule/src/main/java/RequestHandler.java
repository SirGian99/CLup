import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;

import javax.ejb.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

@Stateless
public class RequestHandler {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;

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
        if (c.getLineups() != null && !c.getLineups().isEmpty()) {
            System.out.println("Il customer ha già fatto una lineup");
            return null;
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
        //TODO chiama VisitManager.newRequest(token)
        dataModel.insertRequest(lur);
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
        if (!s.isOpenAt(desiredStart.toLocalDateTime(), duration.toLocalTime())) {return null;}
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

        //TODO chiama VisitManager.newRequest(token)
        dataModel.insertRequest(br);
        return br;
    }

    public void cancelRequest(String uuid) {
        dataModel.removeRequest(dataModel.getVisitRequest(uuid));
    }
}