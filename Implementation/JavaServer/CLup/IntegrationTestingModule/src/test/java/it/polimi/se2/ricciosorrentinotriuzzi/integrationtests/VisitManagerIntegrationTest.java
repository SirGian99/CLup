package it.polimi.se2.ricciosorrentinotriuzzi.integrationtests;
import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.*;
import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.component.mockcomponent.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.Persistence;

class VisitManagerIntegrationTest {

    private TestDataModel dataModel1;
    //private DataModel dataModel;
    private TestStoreStatusHandler customerController;
    @BeforeEach
    void setUp() throws NamingException {

        /*EJBContainer ejbContainer = EJBContainer.createEJBContainer();
        Context ctx = ejbContainer.getContext();
        DataModel service = (DataModel) ctx.lookup("java:global/classes/DataModel");
        service.setEm(Persistence.createEntityManagerFactory("PCLup").createEntityManager());
        */
        dataModel1 = new TestDataModel();
        //dataModel1 = new DataModel(true);
        customerController = new TestStoreStatusHandler(dataModel1);
        //dataModel.getEm().getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        //dataModel.getEm().getTransaction().rollback();
    }

    @Test
    void validateAccess() {
        System.out.println(customerController.getStoreGeneralInfo("056dc34a-6536-11eb-a3e0-dca632747890"));
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