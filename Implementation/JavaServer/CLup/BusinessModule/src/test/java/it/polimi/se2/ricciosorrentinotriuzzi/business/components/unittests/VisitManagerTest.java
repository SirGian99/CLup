package it.polimi.se2.ricciosorrentinotriuzzi.business.components.unittests;

import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;
import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

class VisitManagerTest {

    private DataModel dataModel;
    private TestVisitManager visitManager;
    private Booking booking;
    private Lineup lineup;
    private Store store;
    private Customer customer;

    @BeforeEach
    void setUp() {
        dataModel = mock(DataModel.class);
        visitManager = new TestVisitManager(dataModel);
        customer = new Customer("customerTestID", true);
        Dayinterval workingHour = new Dayinterval(
                DayOfWeek.from(LocalDateTime.now()).getValue(),
                Time.valueOf("00:00:00"),
                Time.valueOf("23:00:00")
        );
        Address address = new Address(
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
                null, address,
                null,
                null,
                null,
                null);
        store.addWorkingHour(workingHour);
        address.setStore(store);

        lineup = new Lineup(
                store,
                customer,
                Timestamp.valueOf(LocalDateTime.now().plus(Duration.ofNanos(store.getAverageVisitDuration()
                        .toLocalTime().toNanoOfDay()))),
                1);
        store.addLineup(lineup);

        booking = new Booking(
                store,
                customer,
                1,
                Timestamp.valueOf(LocalDateTime.now().plusSeconds(1)),
                Time.valueOf("00:05:00"),
                null);
        store.addBooking(booking);
    }

    @Test
    void newBookingRequest() throws InterruptedException {
        doAnswer(invocation -> {
            VisitRequest request = (VisitRequest) invocation.getArguments()[0];
            if (request != null && request.isPending()) {
                request.setState(VisitRequestStatus.READY);
            }
            return true;
        }).when(dataModel).allowVisitRequest(any(VisitRequest.class));

        visitManager.newRequest(booking);
        // Since the booking request status will be set to ready asynchronously by another thread, we must wait until
        // that thread is over. To do so, a convenience class that extends the visit manager has been created, with a
        // synchronized method overriding the one called asynchronously by the visit manager
        synchronized (visitManager) {
            visitManager.wait();
            assertTrue(booking.isReady());
        }

        // The following code generates a new booking request which is pending until no request is pending nor ready for
        // the selected store, since the number of people specified is equal to the store maximum occupancy. Since the
        // previous placed booking is now in ready state, the new one should remain in pending state
        Booking pendingBooking = new Booking();
        pendingBooking.setCustomer(customer);
        pendingBooking.setStore(store);
        pendingBooking.setNumberOfPeople(store.getMaximumOccupancy());
        pendingBooking.setDateTimeOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        pendingBooking.setState(VisitRequestStatus.PENDING);
        pendingBooking.setDesiredStartingTime(Timestamp.valueOf(LocalDateTime.now().plusSeconds(1)));
        pendingBooking.setDesiredDuration(Time.valueOf("00:05:00"));
        store.getBookings().add(pendingBooking);
        visitManager.newRequest(pendingBooking);
        synchronized (visitManager) {
            visitManager.wait();
            assertTrue(pendingBooking.isPending());
        }
    }

    @Test
    void newLineUpRequest() {
        doAnswer(invocation -> {
            VisitRequest request = (VisitRequest) invocation.getArguments()[0];
            if (request != null && request.isPending()) {
                request.setState(VisitRequestStatus.READY);
                ((Lineup) request).setEstimatedTimeOfEntrance(Timestamp.valueOf(LocalDateTime.now()));
            }
            return true;
        }).when(dataModel).allowVisitRequest(any(VisitRequest.class));

        visitManager.newRequest(lineup);
        // Since both the store and its queue are empty, and the number of people specified in the line up is under the
        // maximum occupancy of the store, the request is immediately allowed to enter and so set in ready state
        assertTrue(lineup.isReady());

        // The following code generates a new lineUpRequest which is pending until no request is pending nor ready for
        // the selected store, since the number of people specified is equal to the store maximum occupancy. Since the
        // previous placed lineup is now in ready state, the new one should remain in pending state
        Lineup pendingLineUp = new Lineup();
        pendingLineUp.setEstimatedTimeOfEntrance(Timestamp.valueOf(LocalDateTime.now().plus(Duration.ofNanos(
                store.getAverageVisitDuration().toLocalTime().toNanoOfDay()))));
        pendingLineUp.setCustomer(customer);
        pendingLineUp.setStore(store);
        pendingLineUp.setNumberOfPeople(store.getMaximumOccupancy());
        pendingLineUp.setDateTimeOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        pendingLineUp.setState(VisitRequestStatus.PENDING);
        store.getLineups().add(pendingLineUp);

        visitManager.newRequest(pendingLineUp);
        assertTrue(pendingLineUp.isPending());
    }
}