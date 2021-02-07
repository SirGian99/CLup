package it.polimi.se2.ricciosorrentinotriuzzi.business.components.unittests;

import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;
import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.sql.Time;
import java.sql.Timestamp;
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
        Dayinterval workingHour = new Dayinterval();
        address = new Address();
        store = new Store();
        chain = new Chain();
        workingHour.setDayOfTheWeek(1);
        workingHour.setStart(Time.valueOf("00:00:00"));
        workingHour.setEnd(Time.valueOf("23:00:00"));
        workingHour.setId(0);

        store.setName("testName");
        store.setAverageVisitDuration(Time.valueOf("1:30:30"));
        store.setCurrentOccupancy(0);
        store.setMaximumOccupancy(10);
        store.setSafetyThreshold(0.0);
        store.setDescription("Test store");
        store.setId("storeTest");
        store.setAddress(address);
        store.setProductSections(new LinkedList<>());
        store.setLineups(new LinkedList<>());

        address.setCity("Milan");
        address.setCountry("Italy");
        address.setPostalCode("21100");
        address.setStore(store);
        address.setStreetName("Piazza Leonardo da Vinci");
        address.setStreetNumber("0");

        chain.setName("PoliMi");
        chain.setDescription("Politecnico of Milan");
        chain.setStoreList(new LinkedList<>());

        List<Dayinterval> workingHours = new LinkedList<>();
        workingHours.add(workingHour);
        store.setWorkingHours(workingHours);
    }


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
        Assertions.assertEquals(store.toJson().put("estimatedQueueDisposalTime",estimatedDisposalTime).put("queueLength", queue.size()).toString(),ssh.getStoreGeneralInfo("storeTest").toString() );
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


}