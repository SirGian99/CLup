import javax.ejb.EJB;
import javax.ws.rs.*;

import it.polimi.se2.ricciosorrentinotriuzzi.Manager;
import it.polimi.se2.ricciosorrentinotriuzzi.Store;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;

import java.util.List;

@Path("/JunkyFunky/")

public class JunkyFunky {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;

    @GET
    @Path("store/{store}")
    @Produces("text/plain")
    public String getStoreName(@PathParam("store") String storeID) {
        //return storeID;
        return dataModel.getStoreName(storeID);
    }

    @GET
    @Path("manager/{manager}")
    @Produces("text/plain")
    public String getManagerByUser(@PathParam("manager") String manager) {
        //return storeID;
        return dataModel.getManager(manager).getName();
    }

    @GET
    @Path("store/{store}/manager/{manager}")
    @Produces("text/plain")
    public String addStoreManager(@PathParam("manager") String manager, @PathParam("store") String store) {
        dataModel.addManager(store,manager);

        List<Store> stores =  dataModel.getManager(manager).getStores();
        List<Manager> managers =  dataModel.getStore(store).getManagers();
        StringBuilder toRet  = new StringBuilder();
        toRet.append("Manager \n");
        for (Manager store1: managers) {

            toRet.append(store1.getUsername()).append(" ");

        }
        toRet.append("\nStore \n");

        for (Store store1 : stores){
            toRet.append(store1.getName()).append(" ");
        }

        return toRet.toString();

    }

    @GET
    @Path("store/{storeName}/chain/{chain}/manager/{managerUsername}")
    @Produces("text/plain")
    public String insertStore(@PathParam("storeName") String storeName, @PathParam("chain") String chain, @PathParam("managerUsername") String managerUsername){
        dataModel.insertNewStore(storeName, chain, 1,2,3, managerUsername);
        return "OK!";
    }
}