package it.polimi.se2.ricciosorrentinotriuzzi.business.components.unittests;

import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;
import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.*;
import org.junit.jupiter.api.*;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

class StoreStatusHandlerTest {

    private DataModel dataModel;
    private TestStoreStatusHandler ssh;
    private Chain chain;
    private Store store1;
    private Store store2;
    private Store store3;
    private List<Address> milanAddresses = new LinkedList<>();
    private List<Address> turinAddresses = new LinkedList<>();

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
        Address address1 = new Address(
                "Piazza Leonardo da Vinci",
                "1",
                "Milano",
                "21100",
                "Italia",
                null);
        Address address2 = new Address(
                "Piazzale Loreto",
                "2",
                "Milano",
                "21100",
                "Italia",
                null);
        Address address3 = new Address(
                "Corso Castelfidardo",
                "2",
                "Torino",
                "10138",
                "Italia",
                null);
        store1 = new Store(
                "testName",
                "Test description",
                0,
                10,
                Time.valueOf("00:30:00"),
                0.0,
                chain,
                address1,
                null,
                null,
                null,
                null);
        store1.addWorkingHour(workingHour);
        store2 = new Store(
                "secondTestName",
                "Test description",
                0,
                10,
                Time.valueOf("00:30:00"),
                0.0,
                null,
                address2,
                null,
                null,
                null,
                null);
        store3 = new Store(
                "thirdTestName",
                "Test description",
                0,
                10,
                Time.valueOf("00:30:00"),
                0.0,
                chain,
                address3,
                null,
                null,
                null,
                null);
        store2.addWorkingHour(workingHour);
        chain.addStore(store1);
        chain.addStore(store3);
        address1.setStore(store1);
        address2.setStore(store2);
        address3.setStore(store3);
        milanAddresses.add(address1);
        milanAddresses.add(address2);
        turinAddresses.add(address3);
    }

    @Test
    void getStoreGeneralInfo() {
        when(dataModel.getStore(store1.getId())).thenReturn(store1);
        assert (ssh.getStoreGeneralInfo(store1.getId()).equals(store1));
    }

    @Test
    void getChains() {
        when(dataModel.getAddressesByCity(milanAddresses.get(0).getCity())).thenReturn(milanAddresses);
        Set<Chain> chains = ssh.getChains(milanAddresses.get(0).getCity());
        assert (chains.size() == 1 && chains.contains(chain));
        when(dataModel.getAddressesByCity(turinAddresses.get(0).getCity())).thenReturn(turinAddresses);
        chains = ssh.getChains(turinAddresses.get(0).getCity());
        assert (chains.size() == 1 && chains.contains(chain));
    }

    @Test
    void getAutonomousStores(){
        when(dataModel.getAddressesByCity(milanAddresses.get(0).getCity())).thenReturn(milanAddresses);
        List<Store> stores = ssh.getAutonomousStores(milanAddresses.get(0).getCity());
        assert (stores.size() == 1 && stores.contains(store2));
        when(dataModel.getAddressesByCity(turinAddresses.get(0).getCity())).thenReturn(turinAddresses);
        stores = ssh.getAutonomousStores(turinAddresses.get(0).getCity());
        assert (stores.size() == 0);
    }

    @Test
    void getChainStores() {
        when(dataModel.getAddressesByCity(milanAddresses.get(0).getCity())).thenReturn(milanAddresses);
        when(dataModel.getAddressesByCity(turinAddresses.get(0).getCity())).thenReturn(turinAddresses);
        when(dataModel.getChainByName(chain.getName())).thenReturn(chain);
        List<Store> stores = ssh.getChainStores(chain.getName(), null);
        assert (stores.size() == 2 && stores.contains(store1) && stores.contains(store3));
        stores = ssh.getChainStores(chain.getName(), milanAddresses.get(0).getCity());
        assert (stores.size() == 1 && stores.contains(store1));
        stores = ssh.getChainStores(chain.getName(), turinAddresses.get(0).getCity());
        assert (stores.size() == 1 && stores.contains(store3));
    }


}