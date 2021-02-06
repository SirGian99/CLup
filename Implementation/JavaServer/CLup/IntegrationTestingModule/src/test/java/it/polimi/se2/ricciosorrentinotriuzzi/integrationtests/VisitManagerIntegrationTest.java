package it.polimi.se2.ricciosorrentinotriuzzi.integrationtests;
import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.*;
import it.polimi.se2.ricciosorrentinotriuzzi.component.mockcomponent.*;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.Persistence;
import java.sql.Time;
import java.time.LocalTime;

class VisitManagerIntegrationTest {

    private TestDataModel dataModel;
    private TestVisitManager visitManager;
    private Chain chain;
    private Store store;
    @BeforeEach
    void setUp() throws NamingException {
        dataModel = new TestDataModel();
        visitManager = new TestVisitManager(dataModel);
        chain = new Chain("chainTest", "Chain of test", null);
        //store = new Store("test", "descriptionTest",0, 10, Time.valueOf(LocalTime.of(0,30)),  10, chain, "pptuuid", String passepartouthfid, Address address, List<Booking> bookings, List<Lineup> lineups, List<Manager> managers, List<Productsection> productSections, List<Dayinterval> workingHours, List<String> tassAddresses);
        dataModel.getEm().getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        dataModel.getEm().getTransaction().rollback();
    }

    @Test
    void validateAccess() {
    }

    @Test
    void confirmAccess() {
    }

    @Test
    void validateExit() {
    }

    @Test
    void confirmExit() {
    }

    @Test
    void newRequest() {
    }

    @Test
    void checkNewReadyRequest() {
    }

    @Test
    void scheduleBooking() {
    }

    @Test
    void removedRequest() {
    }
}