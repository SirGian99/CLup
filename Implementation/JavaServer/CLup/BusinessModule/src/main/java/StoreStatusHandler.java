import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
@Stateless
public class StoreStatusHandler {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;
    public StoreStatusHandler() {
    }
    public String getStoreNameByID(String storeID) {
        return dataModel.getStore(storeID).getName();
    }

    public JSONObject getStoreGeneralInfo(String storeID) {
        Store store = dataModel.getStore(storeID);
        return store.toJson().put("estimatedQueueDisposalTime", dataModel.getQueueDisposalTime(storeID));
    }

    public JSONObject getChainsAndAutonomousStores(String city) {
        JSONObject json = new JSONObject();
        JSONArray chains = new JSONArray();
        JSONArray stores = new JSONArray();
        List<Address> addresses = new ArrayList<>();
        if(city != null) {
            addresses = dataModel.getAddressesByCity(city);
        }
        for(Address a : addresses) {
            Store store = a.getStore();
            if(store != null) {
                Chain chain = store.getChain();
                if (chain != null) {
                    chains.put(chain.toJson());
                } else {
                    stores.put(store.toJson());
                }
             }
        }
        json.put("chains", chains);
        json.put("autonomousStores", stores);
        return json;
    }

    public JSONObject getChainStores(String chain, String city) {
        JSONObject json = new JSONObject();
        JSONArray stores = new JSONArray();
        if(city != null) {
            List<Address> addresses = dataModel.getAddressesByCity(city);
            for (Address a: addresses) {
                Store store = a.getStore();
                if(store != null && store.getChain() != null && store.getChain().getName().equals(chain))
                    stores.put(store.toJson());
            }
        }
         else {
            Chain c = dataModel.getChainByName(chain);
            if(c != null)
                for(Store s : c.getStoreList())
                    stores.put(s.toJson());
        }
         json.put("stores", stores);
        return json;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }
}