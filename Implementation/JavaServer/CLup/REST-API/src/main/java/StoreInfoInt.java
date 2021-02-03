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
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;

    @GET
    @Path("{store}")
    @Produces("text/plain")
    public String getStoreName(@PathParam("store") String storeID) {
        return dataModel.getStore(storeID).getName();
    }


    @GET
    @Path("store/{storeID}/generalInfo")
    @Produces("application/json")
    public StoreInfo getStoreGeneralInfo(@PathParam("storeID") String storeID) {
        Chain chain = dataModel.getChain(storeID);
        Store store = dataModel.getStore(storeID);
        Address address = null;
        if(store.getAddress() != null) {
            address = dataModel.getAddress(store.getAddress().getId());
        }
        return new StoreInfo(chain, store, address);
    }

    @GET
    @Path("chainstore")
    @Produces("application/json")
    public ChainsAndAutonomousStores getChainsAndAutonomousStores(@QueryParam("city") String city) {
        List<Address> addresses = dataModel.getAddressesByCity(city);
        Set<Chain> chains = new HashSet<>();
        Set<Store> stores = new HashSet<>();
        for (Address a: addresses) {
            Store store = a.getStore();
            if(store != null) {
                Chain chain = store.getChain();
                if (chain != null) {
                    chains.add(chain);
                } else {
                    stores.add(store);
                }
            }
        }
        return new ChainsAndAutonomousStores(new ArrayList<Chain>(chains), new ArrayList<Store>(stores));
    }
}
