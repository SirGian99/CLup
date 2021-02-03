import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateless
public class StoreStatusHandler {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;
    public StoreStatusHandler() {
    }
    public String getStoreNameByID(String storeID) {
        return dataModel.getStore(storeID).getName();
    }

    public StoreInfo getStoreGeneralInfo(String storeID) {
        Chain chain = dataModel.getChainFromStore(storeID);
        Store store = dataModel.getStore(storeID);
        Address address = null;
        if (store.getAddress() != null) {
            address = dataModel.getAddress(store.getAddress().getId());
        }
        return new StoreInfo(chain, store, address);
    }
    public JSONObject getStoreGeneralInfo2(String storeID) {
        Store store = dataModel.getStore(storeID);
        return store.toJson();
    }

    public ChainsAndAutonomousStores getChainsAndAutonomousStores(String city) {
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
    public JSONObject getChainsAndAutonomousStores2(String city) {
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
        json.put("stores", stores);
        return json;
    }

    public Stores getChainStores(String chain, String city) {
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

    public JSONObject getChainStores2(String chain, String city) {
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
                for(Store s : c.storeList())
                    stores.put(s.toJson());
        }
         json.put("stores", stores);
        return json;
    }
}

class StoreInfo implements Serializable {
    public String chainName;
    public String name;
    public AddressJson address;
    public String description;
    public byte[] image;
    public Integer currentOccupancy;
    public Integer maximumOccupancy;
    public Double safetyThreshold;
    public ArrayList<DayIntervalJson> workingHours;
    public ArrayList<ProductsectionJson> productSections;

    public StoreInfo(Chain chain, Store store, Address address){
        if (chain != null) {
            this.chainName = chain.getName();
        }
        this.name = store.getName();
        this.description = store.getDescription();
        this.image = store.getImage();
        this.currentOccupancy = store.getCurrentOccupancy();
        this.maximumOccupancy = store.getMaximumOccupancy();
        this.safetyThreshold = store.getSafetyThreshold();
        this.address = null;
        if (address != null) {
            this.address = new AddressJson(address);
        }
        workingHours = new ArrayList<>();
        for (Dayinterval di:store.getWorkingHours()) {
            workingHours.add(new DayIntervalJson(di));
        }
        productSections = new ArrayList<>();
        for (Productsection ps:store.getProductSections()) {
            productSections.add(new ProductsectionJson(ps));
        }
    }
}

class AddressJson implements Serializable {
    public long id;
    public String streetName;
    public String streetNumber;
    public String city;
    public String postalCode;
    public String country;
    public AddressJson(Address a) {
        this.id = a.getId();
        this.streetName = a.getStreetName();
        this.streetNumber = a.getStreetNumber();
        this.city = a.getCity();
        this.postalCode = a.getPostalCode();
        this.country = a.getCountry();
    }
}

class ProductsectionJson implements Serializable {
    public String name;
    public Double currentOccupancy;
    public Integer maximumOccupancy;
    public ProductsectionJson(Productsection ps) {
        this.name = ps.getName();
        this.currentOccupancy = ps.getCurrentOccupancy();
        this.maximumOccupancy = ps.getMaximumOccupancy();
    }
}

class DayIntervalJson implements Serializable {
    public long id;
    public int dayOfTheWeek;
    public String start;
    public String end;
    public DayIntervalJson(Dayinterval di) {
        this.id = di.getId();
        this.dayOfTheWeek = di.getDayOfTheWeek();
        this.start = di.getStart().toString();
        this.end = di.getEnd().toString();
    }
}

class ChainsAndAutonomousStores implements Serializable{
    public List<Chain> chains;
    public List<StoreInfo> stores;

    public ChainsAndAutonomousStores(ArrayList<Chain> chains, ArrayList<Store> stores) {
        this.chains = chains;
        this.stores = new ArrayList<>();
        for(Store s : stores){
            this.stores.add(new StoreInfo(s.getChain(), s, s.getAddress()));
        }
    }
}

class Stores implements Serializable {
    public List<StoreInfo> stores;
    public Stores(ArrayList<StoreInfo> stores){
        this.stores = stores;
    }
}