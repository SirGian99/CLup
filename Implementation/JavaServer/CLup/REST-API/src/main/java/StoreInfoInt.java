import javax.ejb.EJB;
import javax.ws.rs.*;

import it.polimi.se2.ricciosorrentinotriuzzi.Store;

import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/StoreInfoInt/")
public class StoreInfoInt {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.StoreStatusHandler")
    private StoreStatusHandler ssh;

    @GET
    @Path("{store}")
    @Produces("text/plain")
    public String getStoreName(@PathParam("store") String storeID) {
        return ssh.getStoreNameByID(storeID);
    }

    @GET
    @Path("store/{storeID}/generalInfo")
    @Produces("application/json")
    public StoreInfo getStoreGeneralInfo(@PathParam("storeID") String storeID) {
        return ssh.getStoreGeneralInfo(storeID);
    }

    @GET
    @Path("chainstore")
    @Produces("application/json")
    public ChainsAndAutonomousStores getChainsAndAutonomousStores(@QueryParam("city") String city) {
        return ssh.getChainsAndAutonomousStores(city);
    }

    @GET
    @Path("chain/{name}/stores")
    @Produces("application/json")
    public Stores getChainStores(@PathParam("name") String chain, @QueryParam("city") String city) {
        return ssh.getChainStores(chain, city);
    }
}
