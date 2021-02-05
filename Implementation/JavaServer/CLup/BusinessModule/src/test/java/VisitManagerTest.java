import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.print.Book;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

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
        visitManager = new TestVisitManager();
        visitManager.setDataModel(dataModel);
        booking = new Booking();
        lineup = new Lineup();
        store = new Store();
        customer = new Customer();

        Dayinterval workingHour = new Dayinterval();
        Address address = new Address();

        workingHour.setDayOfTheWeek(DayOfWeek.from(LocalDateTime.now()).getValue());
        workingHour.setStart(Time.valueOf("00:00:00"));
        workingHour.setEnd(Time.valueOf("23:00:00"));
        workingHour.setId(0);

        store.setName("testName");
        store.setAverageVisitDuration(Time.valueOf("0:30:30"));
        store.setCurrentOccupancy(0);
        store.setMaximumOccupancy(10);
        store.setSafetyThreshold(0.0);
        store.setDescription("Test store");
        store.setId("storeTest");
        store.setAddress(address);
        store.setProductSections(new LinkedList<>());
        store.setLineups(new LinkedList<>());
        store.setBookings(new LinkedList<>());

        customer.setId("customerTest");
        customer.setIsAppCustomer((byte) 1);

        address.setCity("Milan");
        address.setCountry("Italy");
        address.setPostalCode("21100");
        address.setStore(store);
        address.setStreetName("Piazza Leonardo da Vinci");
        address.setStreetNumber("0");

        List<Dayinterval> workingHours = new LinkedList<>();
        workingHours.add(workingHour);
        store.setWorkingHours(workingHours);

        lineup.setEstimatedTimeOfEntrance(Timestamp.valueOf(LocalDateTime.now().plus(Duration.ofNanos(store.getAverageVisitDuration().toLocalTime().toNanoOfDay()))));
        lineup.setCustomer(customer);
        lineup.setStore(store);
        lineup.setNumberOfPeople(1);
        lineup.setDateTimeOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        lineup.setState(VisitRequestStatus.PENDING);
        store.getLineups().add(lineup);

        booking.setCustomer(customer);
        booking.setStore(store);
        booking.setNumberOfPeople(1);
        booking.setDateTimeOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        booking.setState(VisitRequestStatus.PENDING);
        booking.setDesiredStartingTime(Timestamp.valueOf(LocalDateTime.now().plusSeconds(1)));
        booking.setDesiredDuration(Time.valueOf("00:05:00"));
        store.getBookings().add(booking);
    }

    @Test
    void newBookingRequest() throws InterruptedException {
        doAnswer(invocation -> {
            VisitRequest request = (VisitRequest)invocation.getArguments()[0];
            if (request!= null && request.isPending()) {
                request.setState(VisitRequestStatus.READY);
            }
            return true;
        }).when(dataModel).allowVisitRequest(any(VisitRequest.class));

        visitManager.newRequest(booking);
        //Since the booking request status will be set to ready asynchronously by another thread, we must wait until that
        //thread is over. To do so, a convenience class that extends the visit manager has been created, with a synchronized
        //method overriding the one called asynchronously by the visit manager
        synchronized (visitManager) {
            visitManager.wait();
            assertTrue(booking.isReady());
        }

        //The following code generates a new booking request which is pending until no request is pending nor ready for
        // the selected store, since the number of people specified is equal to the store maximum occupancy. Since the
        //previous placed booking is now in ready state, the new one should remain in pending state
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
    void newLineUpRequest(){
        doAnswer(invocation -> {
            VisitRequest request = (VisitRequest)invocation.getArguments()[0];
            if (request!= null && request.isPending()) {
                request.setState(VisitRequestStatus.READY);
                ((Lineup)request).setEstimatedTimeOfEntrance(Timestamp.valueOf(LocalDateTime.now()));
            }
            return true;
        }).when(dataModel).allowVisitRequest(any(VisitRequest.class));

        visitManager.newRequest(lineup);
        //Since both the store and its queue are empty, and the number of people specified in the line up is under the
        // maximum occupancy of the store, the request is immediately allowed to enter and so set in ready state
        assertTrue(lineup.isReady());

        //The following code generates a new lineUpRequest which is pending until no request is pending nor ready for
        // the selected store, since the number of people specified is equal to the store maximum occupancy. Since the
        // previous placed lineup is now in ready state, the new one should remain in pending state
        Lineup pendingLineUp = new Lineup();
        pendingLineUp.setEstimatedTimeOfEntrance(Timestamp.valueOf(LocalDateTime.now().plus(Duration.ofNanos(store.getAverageVisitDuration().toLocalTime().toNanoOfDay()))));
        pendingLineUp.setCustomer(customer);
        pendingLineUp.setStore(store);
        pendingLineUp.setNumberOfPeople(store.getMaximumOccupancy());
        pendingLineUp.setDateTimeOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        pendingLineUp.setState(VisitRequestStatus.PENDING);
        store.getLineups().add(pendingLineUp);


        visitManager.newRequest(pendingLineUp);
        assertTrue(pendingLineUp.isPending());
    }
    @Test
    void checkNewReadyRequest() {
////TODO
    }

    private class TestVisitManager extends VisitManager{
        @Override
        protected synchronized void scheduleBooking(Booking request, int finalCurrentReadyOccupancy, Store store) {
            super.scheduleBooking(request, finalCurrentReadyOccupancy, store);
            this.notifyAll();
        }
    }
}