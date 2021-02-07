package it.polimi.se2.ricciosorrentinotriuzzi.business.components;

import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Address;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Chain;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Store;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;

@Stateless
public class StoreStatusHandler {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.component/DataModel")
    protected DataModel dataModel;

    public Store getStoreGeneralInfo(String storeID) {
        Store store = dataModel.getStore(storeID);
        store.setQueueDisposalTime(dataModel.getQueueDisposalTime(storeID));
        store.setQueueLength(dataModel.getQueue(storeID).size());
        return store;
    }

    public Set<Chain> getChains(String city) {
        List<Address> addresses = new ArrayList<>();
        Set<Chain> chains = new HashSet<>();
        if (city != null) {
            addresses = dataModel.getAddressesByCity(city);
        }
        for (Address a : addresses) {
            Store store = a.getStore();
            if (store != null && store.getChain() != null)
                chains.add(store.getChain());
        }
        return chains;
    }

    public List<Store> getAutonomousStores(String city) {
        List<Address> addresses = new ArrayList<>();
        List<Store> stores = new LinkedList<>();
        if (city != null) {
            addresses = dataModel.getAddressesByCity(city);
        }
        for (Address a : addresses) {
            Store store = a.getStore();
            if (store != null && store.getChain() == null) {
                store.setQueueDisposalTime(dataModel.getQueueDisposalTime(store.getId()));
                store.setQueueLength(dataModel.getQueue(store.getId()).size());
                stores.add(store);
            }
        }
        return stores;
    }

    public List<Store> getChainStores(String chain, String city) {
        List<Store> stores = new LinkedList<>();
        if (city != null) {
            List<Address> addresses = dataModel.getAddressesByCity(city);
            for (Address a : addresses) {
                Store store = a.getStore();
                if (store != null && store.getChain() != null && store.getChain().getName().equals(chain)) {
                    store.setQueueDisposalTime(dataModel.getQueueDisposalTime(store.getId()));
                    store.setQueueLength(dataModel.getQueue(store.getId()).size());
                    stores.add(store);
                }
            }
        } else {
            Chain c = dataModel.getChainByName(chain);
            if (c != null)
                for (Store store : c.getStoreList()) {
                    store.setQueueDisposalTime(dataModel.getQueueDisposalTime(store.getId()));
                    store.setQueueLength(dataModel.getQueue(store.getId()).size());
                    stores.add(store);
                }
        }
        return stores;
    }
}