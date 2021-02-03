import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polimi.se2.ricciosorrentinotriuzzi.Store;

import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;
import org.json.JSONArray;
import org.json.JSONObject;

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
        Chain chain = dataModel.getChainFromStore(storeID);
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
        System.out.println("\n\n\n" + city);
        List<Address> addresses = new ArrayList<>();
        if(city != null) {
            addresses = dataModel.getAddressesByCity(city);
        }
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

    @GET
    @Path("chain/{name}/stores")
    @Produces("application/json")
    public Stores getChainStores(@PathParam("name") String chain, @QueryParam("city") String city) {
        ArrayList<StoreInfo> stores = new ArrayList<>();
        if(city != null) {
            List<Address> addresses = dataModel.getAddressesByCity(city);
            for (Address a: addresses) {
                Store store = a.getStore();
                if(store != null && store.getChain() != null)
                    if(store.getChain().getName().equals(chain))
                        stores.add(new StoreInfo(store.getChain(), store, a));
            }
        } else {
            Chain c = dataModel.getChainByName(chain);
            if(c != null)
                for(Store s : c.storeList())
                    stores.add(new StoreInfo(s.getChain(), s, s.getAddress()));
        }
        return new Stores(stores);
    }
}
