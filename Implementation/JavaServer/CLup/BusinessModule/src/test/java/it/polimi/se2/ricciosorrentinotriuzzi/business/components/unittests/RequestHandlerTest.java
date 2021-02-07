package it.polimi.se2.ricciosorrentinotriuzzi.business.components.unittests;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.VisitManager;
import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.TestRequestHandler;
import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class RequestHandlerTest {

    private DataModel dataModel;
    private TestRequestHandler requestHandler;
    private VisitManager visitManager;
    private Store store;
    private Customer customer;
    private Booking br;
    private Lineup lur;

    @BeforeEach
    void setUp() {
        dataModel = mock(DataModel.class);
        visitManager = mock(VisitManager.class);
        requestHandler = new TestRequestHandler(dataModel, visitManager);
        customer = new Customer("customerTestID", true);
        Dayinterval workingHour = new Dayinterval(
                DayOfWeek.from(LocalDateTime.now()).getValue(),
                Time.valueOf("00:00:00"),
                Time.valueOf("23:59:59")
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
    }

    @Test
    void lineup() {
        Timestamp estimatedDisposalTime = Timestamp.valueOf(LocalDateTime.now());
        when(dataModel.getStore(store.getId())).thenReturn(store);
        when(dataModel.getCustomer(customer.getId())).thenReturn(customer);
        when(dataModel.getQueueDisposalTime(store.getId())).thenReturn(estimatedDisposalTime);
        doAnswer(invocation -> {
            VisitRequest request = (VisitRequest) invocation.getArguments()[0];
            if (request.isBooking()) {
                request.getStore().addBooking((Booking) request);
                request.getCustomer().addBooking((Booking) request);
            } else {
                request.getStore().addLineup((Lineup) request);
                request.getCustomer().addLineup((Lineup) request);
            }
            return true;
        }).when(dataModel).insertRequest(any(VisitRequest.class));
        lur = requestHandler.lineup(3, customer.getId(), store.getId());
        assertTrue(store.getLineups().contains(lur));

        lur = requestHandler.lineup(3, customer.getId(), store.getId());
        //The customer is already lined up
        assertFalse(store.getLineups().contains(lur));

        store.setLineups(new LinkedList<>());
        customer.setLineups(new LinkedList<>());
        //The customer is not in line anymore

        lur = requestHandler.lineup(store.getMaximumOccupancy() + 1, customer.getId(), store.getId());
        //The customer cannot line up since it would exceed its maximum occupancy
        assertFalse(store.getLineups().contains(lur));

        store.setWorkingHours(new LinkedList<>());
        //The store is now always closed
        lur = requestHandler.lineup(1, customer.getId(), store.getId());
        //The customer cannot line up since the store is closed
        assertFalse(store.getLineups().contains(lur));
    }

    @Test
    void book() {
        Booking br1;
        Timestamp estimatedDisposalTime = Timestamp.valueOf(LocalDateTime.now());
        Timestamp desiredStart = Timestamp.valueOf(LocalDateTime.now().plusMinutes(3));
        Time duration = Time.valueOf("00:10:00");
        when(dataModel.getStore(store.getId())).thenReturn(store);
        when(dataModel.getCustomer(customer.getId())).thenReturn(customer);
        when(dataModel.getQueueDisposalTime(store.getId())).thenReturn(estimatedDisposalTime);
        doAnswer(invocation -> {
            VisitRequest request = (VisitRequest) invocation.getArguments()[0];
            if (request.isBooking()) {
                request.getStore().addBooking((Booking) request);
                request.getCustomer().addBooking((Booking) request);
            } else {
                request.getStore().addLineup((Lineup) request);
                request.getCustomer().addLineup((Lineup) request);
            }
            return true;
        }).when(dataModel).insertRequest(any(VisitRequest.class));
        br = requestHandler.book(3, customer.getId(), store.getId(), desiredStart, duration,
                new ArrayList<>());
        br1 = br;
        assertTrue(store.getBookings().contains(br));


        // The customer tries to book a visit for a time interval which overlaps with a request he previously
        // made (the previous one). So, the checkBookings will provide the request handler a "true" value
        // when(dataModel.getCustomerBookings(anyString(), any(), any())).thenReturn(customer.getBookings());
        duration = Time.valueOf("00:05:00");
        Timestamp end = Timestamp.valueOf(desiredStart.toLocalDateTime().plusHours(duration.toLocalTime().getHour())
                .plusMinutes(duration.toLocalTime().getMinute()));
        when(dataModel.checkBookings(customer.getId(), desiredStart, end)).thenReturn(true);
        br = requestHandler.book(3, customer.getId(), store.getId(), desiredStart,
                duration, new ArrayList<>());
        assertNull(br);

        // The customer tries to book a visit before the estimated queue disposal time
        estimatedDisposalTime = Timestamp.valueOf(LocalDateTime.now());
        when(dataModel.getQueueDisposalTime(store.getId())).thenReturn(estimatedDisposalTime);
        desiredStart = Timestamp.valueOf(LocalDateTime.now().minusMinutes(3));
        br = requestHandler.book(3, customer.getId(), store.getId(), desiredStart,
                Time.valueOf("00:05:00"), new ArrayList<>());
        assertNull(br);

        // The time interval selected by the customer is already maximized by other booking requests,
        // so a new booking cannot be placed
        br1.setNumberOfPeople(store.getMaximumOccupancy());
        br1.setCustomer(new Customer());
        List<Booking> alreadyPlacedBookings = new LinkedList<>();
        alreadyPlacedBookings.add(br1);
        desiredStart = Timestamp.valueOf(LocalDateTime.now().plusMinutes(1));
        when(dataModel.getCustomerBookings(anyString(), any(), any())).thenReturn(new LinkedList<>());
        when(dataModel.getBookings(anyString(), any(), any())).thenReturn(alreadyPlacedBookings);
        br = requestHandler.book(3, customer.getId(), store.getId(), desiredStart,
                Time.valueOf("00:05:00"), new ArrayList<>());
        assertNull(br);

        //The customer cannot Book a visit since the store is closed when he desires to visit it
        store.setWorkingHours(new LinkedList<>());
        //The store is now always closed
        br = requestHandler.book(3, customer.getId(), store.getId(), desiredStart,
                Time.valueOf("00:05:00"), new ArrayList<>());
        assertNull(br);
    }
}