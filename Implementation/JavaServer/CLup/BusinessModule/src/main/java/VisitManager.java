import it.polimi.se2.ricciosorrentinotriuzzi.VisitRequest;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;

import javax.ejb.*;

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
        return dataModel.startVisit(visitToken, storeID, numberOfPeople);
        /*VisitRequest request = dataModel.getVisitRequest(visitToken);
        if(request != null && request.getStore().getId().equals(storeID) && request.isFulfilled()){
            //devo aggiornare
        }

        return true;
        */
    }

    public int validateExit(String visitToken, String storeID){
        VisitRequest request = dataModel.getVisitRequest(visitToken);
        return  request != null && request.getStore().getId().equals(storeID) && request.isFulfilled() ? request.getNumberOfPeople() : 0;
    }

    public boolean confirmExit(String visitToken, String storeID, int numberOfPeople) {
        return dataModel.endVisit(visitToken, storeID, numberOfPeople);
    }


}
