package it.polimi.se2.ricciosorrentinotriuzzi.integrationtests;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.TestStoreStatusHandler;
import it.polimi.se2.ricciosorrentinotriuzzi.component.mockcomponent.TestDataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StoreStatusHandlerIntegrationTest {

    private TestStoreStatusHandler storeStatusHandler;
    private TestDataModel dataModel;
    private Chain chain;
    private Address address1;
    private Address address2;
    private Manager manager;
    private Store store;
    private Store chainstore;

    @BeforeEach
    void setUp() {
        dataModel = new TestDataModel();
        storeStatusHandler = new TestStoreStatusHandler(dataModel);

        chain = new Chain("chainTest", "Chain of test", null);
        address1 = new Address("Piazza Leonardo Da Vinci", "32", "Milan",
                "21133", "Italy", null);
        address2 = new Address("Via Ernesto Di Marino", "0", "Cava de' Tirreni",
                "84013", "Italy", null);
        manager = new Manager("username", "password", "name");
        List<Dayinterval> workingHours = new LinkedList<>();
        workingHours.add(new Dayinterval(LocalDateTime.now().getDayOfWeek().getValue(),
                Time.valueOf(LocalTime.of(0, 0)),
                Time.valueOf(LocalTime.of(23, 59, 59))));
        store = new Store("test", "descriptionTest",0, 10,
                Time.valueOf(LocalTime.of(0,30)),10.0, chain, address1,null,
                null,null,null,workingHours,null);
        chainstore = new Store("test", "descriptionTest",0, 10,
                Time.valueOf(LocalTime.of(0,30)),10.0, chain, address2,null,
                null,null,null,workingHours,null);
        address1.setStore(store);
        address2.setStore(chainstore);
        dataModel.getEm().getTransaction().begin();
        // Test entities are persisted on the database
        dataModel.getEm().persist(workingHours.get(0));
        dataModel.getEm().persist(address1);
        dataModel.getEm().persist(address2);
        dataModel.getEm().persist(store);
        dataModel.getEm().persist(chainstore);
    }

    @AfterEach
    void tearDown() {
        // The rollback is executed in order to not persist changes on the database
        dataModel.getEm().getTransaction().rollback();
    }

    @Test
    void getChainsAndAutonomousStores() {
    }

    @Test
    void getChainStores() {
    }
}