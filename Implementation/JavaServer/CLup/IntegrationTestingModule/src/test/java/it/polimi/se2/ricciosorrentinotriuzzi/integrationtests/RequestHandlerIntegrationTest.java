package it.polimi.se2.ricciosorrentinotriuzzi.integrationtests;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.VisitManager;
import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.TestRequestHandler;
import it.polimi.se2.ricciosorrentinotriuzzi.component.mockcomponent.TestDataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;


class RequestHandlerIntegrationTest {

    private TestDataModel dataModel;
    private VisitManager visitManager;
    private TestRequestHandler requestHandler;
    private Customer customer;
    private Store store;
    private Booking booking;
    private Lineup lineup;
    @BeforeEach
    void setUp(){
        dataModel = new TestDataModel();
        visitManager = mock(VisitManager.class);
        requestHandler = new TestRequestHandler(dataModel, visitManager);

        Chain chain = new Chain("chainTest", "Chain of test", null);
        Address address = new Address("Piazza Leonardo Da Vinci", "32", "Milan",
                "21133", "Italy", null);
        Manager manager = new Manager("username", "password", "name");
        Dayinterval workingHour = new Dayinterval(LocalDateTime.now().getDayOfWeek().getValue(),
                Time.valueOf(LocalTime.of(0, 0)),
                Time.valueOf(LocalTime.of(23, 59, 59)));
        store = new Store("test", "descriptionTest",0, 10,
                Time.valueOf(LocalTime.of(0,30)),10.0, chain,null,null,
                null,null,null,null,null);

        customer = new Customer(UUID.randomUUID().toString(), true);
        booking = new Booking(store, customer,1, Timestamp.valueOf(LocalDateTime.now().plusSeconds(3)),
                Time.valueOf(LocalTime.of(0,30)), null);
        // The estimated time of entrance is set to the one the request handler would set when the lineup request is
        // placed, which is equal to now + the average visit duration of the store, since it's queue is empty
        lineup = new Lineup(store, customer, Timestamp.valueOf(LocalDateTime.now().plus(
                Duration.ofNanos(store.getAverageVisitDuration().toLocalTime().toNanoOfDay()))),1);


        dataModel.getEm().getTransaction().begin();
        // Test entities are persisted on the database
        dataModel.getEm().persist(chain);
        dataModel.getEm().persist(address);
        dataModel.getEm().persist(workingHour);
        dataModel.getEm().persist(manager);
        dataModel.getEm().persist(store);
        dataModel.getEm().persist(customer);
        dataModel.getEm().persist(lineup);
        dataModel.getEm().persist(booking);
        store.setAddress(address);
        store.addManager(manager);
        store.addWorkingHour(workingHour);
    }

    @AfterEach
    void tearDown() {
        // The rollback is executed in order to not persist changes on the database
        dataModel.getEm().getTransaction().rollback();
    }

    @Test
    void lineup() {
        // The following request is rejected since it is for a number of people higher than the store maximum occupancy
        assertNull(requestHandler.lineup(store.getMaximumOccupancy()+1, customer.getId(), store.getId()));
        // The following request is rejected since the number of people is less than 1
        assertNull(requestHandler.lineup(0, customer.getId(), store.getId()));
        // The following requests are rejected since the first customer and the second store do not exist
        assertNull(requestHandler.lineup(1, "absentInDB", store.getId()));
        assertNull(requestHandler.lineup(1, customer.getId(), "absentInDB"));

        // All the following request should be accepted since they verify the constraint on the number of people
        for (int i = store.getMaximumOccupancy(); i>0; i--) {
            Lineup lur = requestHandler.lineup(i, customer.getId(), store.getId());
            assertNotNull(lur);
            assertTrue(store.getLineups().contains(lur));
            requestHandler.cancelRequest(lur.getUuid());
        }
        storeInit(store);

        // The second request should be rejected since the customer is already in line
        Lineup lur = requestHandler.lineup(1, customer.getId(), store.getId());
        assertNotNull(lur);
        assertNull(requestHandler.lineup(1, customer.getId(), store.getId()));
        // The following two lines simulate that the customer has exited the store
        lur.setState(VisitRequestStatus.COMPLETED);
        store.setCurrentOccupancy(0);

        // The new lineup is allowed since the customer exited the store
        Lineup newLineUp = requestHandler.lineup(1, customer.getId(), store.getId());
        assertNotNull(newLineUp);
        newLineUp.setState(VisitRequestStatus.COMPLETED);
        store.setCurrentOccupancy(0);

        // The following line of code modifies the working hours of the store, which will be closed after its execution
        store.getWorkingHours().get(0).setEnd(Time.valueOf(LocalTime.now()));
        // The new request cannot be placed since the store is closed
        assertNull(requestHandler.lineup(1, customer.getId(), store.getId()));
    }

    @Test
    void book() {
        System.out.println(7 - (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) % 7));

    }

    @Test
    void cancelRequest() {
    }

    void storeInit(Store store){
        store.setCurrentOccupancy(0);
        store.setLineups(new LinkedList<>());
        store.setBookings(new LinkedList<>());
    }
}