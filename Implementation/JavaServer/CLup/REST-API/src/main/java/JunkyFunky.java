import javax.ejb.EJB;
import javax.ws.rs.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;
@Path("/JunkyFunky/{store}")

public class JunkyFunky {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;

    @GET
    @Produces("text/plain")
    public String getStoreName(@PathParam("store") String storeID) {
        //return storeID;
        return dataModel.getStoreName(storeID);
    }
}