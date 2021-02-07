package it.polimi.se2.ricciosorrentinotriuzzi.business.components.unittests;

import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;
import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreStatusHandlerTest {

    private DataModel dataModel;
    private TestStoreStatusHandler ssh;
    private Chain chain;
    private Store store;
    private Address address;

    @BeforeEach
    public void setUp() {
        dataModel = mock(DataModel.class);
        ssh = new TestStoreStatusHandler(dataModel);
        chain = new Chain("PoliMi","Politecnico");
        Dayinterval workingHour = new Dayinterval (
                DayOfWeek.from(LocalDateTime.now()).getValue(),
                Time.valueOf("00:00:00"),
                Time.valueOf("23:00:00")
        );
        address = new Address(
                "Piazza Leonardo da Vinci",
                "1",
                "Milano",
                "21100",
                "Italia",
                null);
        store = new Store(
                "testName",
                "Test description",
                0,
                10,
                Time.valueOf("00:30:00"),
                0.0,
                chain,
                address,
                null,
                null,
                null,
                null);
        store.addWorkingHour(workingHour);
        address.setStore(store);
    }

/*
    @Test
    void getStoreGeneralInfo() {
        Timestamp estimatedDisposalTime = Timestamp.valueOf(LocalDateTime.now());
        List<Lineup> queue = new LinkedList<>();
        for(Lineup inQueue : store.getLineups())
            if (inQueue.isPending())
                queue.add(inQueue);
        when(dataModel.getStore(store.getId())).thenReturn(store);
        when(dataModel.getQueueDisposalTime(store.getId())).thenReturn(estimatedDisposalTime);
        when(dataModel.getQueue(store.getId())).thenReturn(queue);
        Assertions.assertEquals(store.toJson().put("estimatedQueueDisposalTime",estimatedDisposalTime).put("queueLength", queue.size()).toString(),ssh.getStoreGeneralInfo(store.getId()).toString() );
    }

    @Test
    void getChainsAndAutonomousStores() {
        List<Address> addresses = new LinkedList<>();
        addresses.add(address);
        when(dataModel.getAddressesByCity(address.getCity())).thenReturn(addresses);
        JSONObject json = ssh.getChainsAndAutonomousStores(address.getCity());
        JSONArray autStores = json.getJSONArray("autonomousStores");
        System.out.println(autStores);
        assertEquals(autStores.get(0).toString(), store.toJson().toString());
    }

    @Test
    void getChainStores() {
        store.setChain(chain);
        chain.getStoreList().add(store);

        List<Address> addresses = new LinkedList<>();
        addresses.add(address);
        when(dataModel.getAddressesByCity(address.getCity())).thenReturn(addresses);
        when(dataModel.getChainByName(chain.getName())).thenReturn(chain);
        JSONArray stores = ssh.getChainStores(chain.getName(), null).getJSONArray("stores");
        assertEquals(stores.get(0).toString(), store.toJson().toString());
        stores = ssh.getChainStores(chain.getName(), chain.getName()).getJSONArray("stores");
        assertEquals(stores.toString(), new JSONArray().toString());
    }


 */

}