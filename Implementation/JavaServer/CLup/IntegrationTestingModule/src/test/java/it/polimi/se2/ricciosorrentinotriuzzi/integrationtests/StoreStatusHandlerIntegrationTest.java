package it.polimi.se2.ricciosorrentinotriuzzi.integrationtests;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.TestStoreStatusHandler;
import it.polimi.se2.ricciosorrentinotriuzzi.component.mockcomponent.TestDataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class StoreStatusHandlerIntegrationTest {

    private TestDataModel dataModel;
    private TestStoreStatusHandler ssh;
    private Chain chain;
    private Store store1;
    private Store store2;
    private Store store3;
    private List<Address> cityOneAddresses = new LinkedList<>();
    private List<Address> cityTwoAddresses = new LinkedList<>();

    @BeforeEach
    public void setUp() {
        dataModel = new TestDataModel();
        ssh = new TestStoreStatusHandler(dataModel);
        chain = new Chain("PoliMi", "Politecnico");
        Dayinterval workingHour = new Dayinterval(
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
        cityOneAddresses.add(address1);
        cityOneAddresses.add(address2);
        cityTwoAddresses.add(address3);

        dataModel.getEm().getTransaction().begin();
        // The database is emptied
        dataModel.dbInit();
        // Test entities are persisted on the database
        dataModel.getEm().persist(workingHour);
        dataModel.getEm().persist(address1);
        dataModel.getEm().persist(address2);
        dataModel.getEm().persist(address3);
        dataModel.getEm().persist(chain);
        dataModel.getEm().persist(store1);
        dataModel.getEm().persist(store2);
        dataModel.getEm().persist(store3);
    }

    @AfterEach
    void tearDown() {
        // The rollback is executed in order to not persist changes on the database
        dataModel.getEm().getTransaction().rollback();
    }

    @Test
    void getStoreGeneralInfo() {
        assert (ssh.getStoreGeneralInfo(store1.getId()).equals(store1));
    }

    @Test
    void getChains() {
        Set<Chain> chains = ssh.getChains(cityOneAddresses.get(0).getCity());
        assert (chains.size() == 1 && chains.contains(chain));
        chains = ssh.getChains(cityTwoAddresses.get(0).getCity());
        assert (chains.size() == 1 && chains.contains(chain));
    }

    @Test
    void getAutonomousStores() {
        List<Store> stores = ssh.getAutonomousStores(cityOneAddresses.get(0).getCity());
        assert (stores.size() == 1 && stores.contains(store2));
        stores = ssh.getAutonomousStores(cityTwoAddresses.get(0).getCity());
        assert (stores.size() == 0);
    }

    @Test
    void getChainStores() {
        List<Store> stores = ssh.getChainStores(chain.getName(), null);
        assert (stores.size() == 2 && stores.contains(store1) && stores.contains(store3));
        stores = ssh.getChainStores(chain.getName(), cityOneAddresses.get(0).getCity());
        assert (stores.size() == 1 && stores.contains(store1));
        stores = ssh.getChainStores(chain.getName(), cityTwoAddresses.get(0).getCity());
        assert (stores.size() == 1 && stores.contains(store3));
    }
}