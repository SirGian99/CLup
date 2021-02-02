import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;

import javax.ejb.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Stateless
public class RequestHandler {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;

    public RequestHandler() {}

    public Lineup lineup(Integer numberOfPeople, String appID, String storeID) {
        Store s = dataModel.getStore(storeID);
        Customer c = dataModel.getCustomer(appID);
        Lineup lur = new Lineup();
        lur.setCustomer(c);
        lur.setStore(s);
        lur.setNumberOfPeople(numberOfPeople);
        lur.setDateTimeOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        lur.setState(0);
        //TODO chiama VisitManager.newRequest(token)
        dataModel.insertRequest(lur);
        return lur;
    }
}