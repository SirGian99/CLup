import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;

import javax.ejb.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Stateless
public class RequestHandler {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;

    public RequestHandler() {}

    public Lineup lineup(int numberOfPeople, String customerID, String storeID) {
        Store s = dataModel.getStore(storeID);
        Customer c = dataModel.getCustomer(customerID);
        LocalDateTime now = LocalDateTime.now();
        if (!s.isOpenAt(now)) {return null;}
        Lineup lur = new Lineup();
        lur.setCustomer(c);
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

    public void cancelLineup(String uuid) {
        dataModel.removeRequest(dataModel.getVisitRequest(uuid));
    }
}