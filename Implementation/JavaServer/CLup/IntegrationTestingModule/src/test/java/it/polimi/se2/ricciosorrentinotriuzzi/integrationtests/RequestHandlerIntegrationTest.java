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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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
    void setUp() {
        dataModel = new TestDataModel();
        visitManager = mock(VisitManager.class);
        requestHandler = new TestRequestHandler(dataModel, visitManager);

        Dayinterval workingHour = new Dayinterval(LocalDateTime.now().getDayOfWeek().getValue(),
                Time.valueOf(LocalTime.of(0, 0)),
                Time.valueOf(LocalTime.of(23, 59, 59)));
        store = new Store("test", "descriptionTest", 0, 10,
                Time.valueOf(LocalTime.of(0, 30)), 10.0, null, null, null,
                null, null, null);

        customer = new Customer(UUID.randomUUID().toString(), true);
        booking = new Booking(store, customer, 1, Timestamp.valueOf(LocalDateTime.now().plusSeconds(3)),
                Time.valueOf(LocalTime.of(0, 30)), null);
        // The estimated time of entrance is set to the one the request handler would set when the lineup request is
        // placed, which is equal to now + the average visit duration of the store, since it's queue is empty
        lineup = new Lineup(store, customer, Timestamp.valueOf(LocalDateTime.now().plus(
                Duration.ofNanos(store.getAverageVisitDuration().toLocalTime().toNanoOfDay()))), 1);
        dataModel.getEm().getTransaction().begin();
        // Test entities are persisted on the database
        dataModel.getEm().persist(workingHour);
        dataModel.getEm().persist(store);
        dataModel.getEm().persist(customer);
        //dataModel.getEm().persist(lineup);
        //dataModel.getEm().persist(booking);
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
        assertNull(requestHandler.lineup(store.getMaximumOccupancy() + 1, customer.getId(), store.getId()));
        // The following request is rejected since the number of people is less than 1
        assertNull(requestHandler.lineup(0, customer.getId(), store.getId()));
        // The following requests are rejected since the first customer and the second store do not exist
        assertNull(requestHandler.lineup(1, "absentInDB", store.getId()));
        assertNull(requestHandler.lineup(1, customer.getId(), "absentInDB"));

        // All the following request should be accepted since they verify the constraint on the number of people
        for (int i = store.getMaximumOccupancy(); i > 0; i--) {
            lineup = requestHandler.lineup(i, customer.getId(), store.getId());
            assertNotNull(lineup);
            assertTrue(store.getLineups().contains(lineup));
            requestHandler.cancelRequest(lineup.getUuid());
        }
        storeInit(store);

        // The second request should be rejected since the customer is already in line
        lineup = requestHandler.lineup(1, customer.getId(), store.getId());
        assertNotNull(lineup);
        assertTrue(store.getLineups().contains(lineup));
        assertNull(requestHandler.lineup(1, customer.getId(), store.getId()));
        // The following two lines simulate that the customer has exited the store
        lineup.setState(VisitRequestStatus.COMPLETED);
        store.setCurrentOccupancy(0);

        // The new lineup is allowed since the customer exited the store
        Lineup newLineUp = requestHandler.lineup(1, customer.getId(), store.getId());
        assertNotNull(newLineUp);
        assertTrue(store.getLineups().contains(newLineUp));
        newLineUp.setState(VisitRequestStatus.COMPLETED);
        store.setCurrentOccupancy(0);

        // The following line of code modifies the working hours of the store, which will be closed after its execution
        store.getWorkingHours().get(0).setEnd(Time.valueOf(LocalTime.now()));
        // The new request cannot be placed since the store is closed
        assertNull(requestHandler.lineup(1, customer.getId(), store.getId()));
    }

    @Test
    void book() {
        // The following request is rejected since it is for a number of people higher than the store maximum occupancy
        Timestamp desiredStartingTime = Timestamp.valueOf(LocalDateTime.now().plusMinutes(10));
        Time estimatedDuration = Time.valueOf("00:15:00");
        assertNull(requestHandler.book(store.getMaximumOccupancy() + 1, customer.getId(), store.getId(),
                desiredStartingTime, estimatedDuration, new ArrayList<>()));
        // The following request is rejected since the number of people is less than 1
        assertNull(requestHandler.book(0, customer.getId(), store.getId(),
                desiredStartingTime, estimatedDuration, new ArrayList<>()));
        // The following requests are rejected since the first customer and the second store do not exist
        assertNull(requestHandler.book(1, "absentInDB", store.getId(),
                desiredStartingTime, estimatedDuration, new ArrayList<>()));
        assertNull(requestHandler.book(1, customer.getId(), "absentInDB",
                desiredStartingTime, estimatedDuration, new ArrayList<>()));

        // All the following request should be accepted since they verify the constraint on the number of people
        for (int i = store.getMaximumOccupancy(); i > 0; i--) {
            booking = requestHandler.book(i, customer.getId(), store.getId(),
                    desiredStartingTime, estimatedDuration, new ArrayList<>());
            assertTrue(store.getBookings().contains(booking));
            requestHandler.cancelRequest(booking.getUuid());
        }

        booking = requestHandler.book(1, customer.getId(), store.getId(),
                desiredStartingTime, estimatedDuration, new ArrayList<>());
        Booking overlappingBr = requestHandler.book(1, customer.getId(), store.getId(),
                Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)), estimatedDuration, new ArrayList<>());
        assertNotNull(booking);

        // When the customer tries to book a visit for a time interval which overlaps with a booking he previously made
        // the new booking request is rejected
        assertTrue(store.getBookings().contains(booking));
        assertNull(overlappingBr);

        // The following  code generate a line up request which makes the queue disposal time equal to now plus the
        // average visit duration of the store. So a new booking request for
        lineup = requestHandler.lineup(1, customer.getId(), store.getId());
        assertNotNull(lineup);
        assertNull(requestHandler.book(1, customer.getId(), store.getId(),
                Timestamp.valueOf(LocalDateTime.now().plusMinutes(1)), estimatedDuration, new ArrayList<>()));
        requestHandler.cancelRequest(lineup.getUuid());

        // A new customer, which is not an app customer, is created. So, it must not be allowed to make booking requests
        Customer newCustomer = new Customer(UUID.randomUUID().toString(), false);
        dataModel.insertCustomer(newCustomer);
        assertNull(requestHandler.book(1, newCustomer.getId(), store.getId(),
                Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)), estimatedDuration, new ArrayList<>()));

        // An app customer places a booking which maximizes the store occupancy in its time interval. So, new bookings
        // from other customers are not allowed
        newCustomer.setIsAppCustomer(true);
        assertNotNull(requestHandler.book(store.getMaximumOccupancy(), newCustomer.getId(), store.getId(),
                Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)), estimatedDuration, new ArrayList<>()));
        assertNull(requestHandler.book(1, customer.getId(), store.getId(),
                Timestamp.valueOf(LocalDateTime.now().plusMinutes(5)), estimatedDuration, new ArrayList<>()));
        assertNull(requestHandler.book(1, customer.getId(), store.getId(),
                Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)), estimatedDuration, new ArrayList<>()));

        //A booking placed for tomorrow is rejected since the store will be closed
        assertNull(requestHandler.book(1, customer.getId(), store.getId(),
                Timestamp.valueOf(LocalDateTime.now().plusDays(1)), estimatedDuration, new ArrayList<>()));
    }

    @Test
    void cancelRequest() {
        requestHandler.cancelRequest(lineup.getUuid());
        assertFalse(store.getLineups().contains(lineup));
        Booking booking = requestHandler.book(1, customer.getId(), store.getId(),
                Timestamp.valueOf(LocalDateTime.now().plusMinutes(1)), Time.valueOf("00:10:00"), new ArrayList<>());
        requestHandler.cancelRequest(booking.getUuid());
        assertFalse(store.getBookings().contains(booking));

    }

    void storeInit(Store store) {
        store.setCurrentOccupancy(0);
        store.setLineups(new LinkedList<>());
        store.setBookings(new LinkedList<>());
    }
}