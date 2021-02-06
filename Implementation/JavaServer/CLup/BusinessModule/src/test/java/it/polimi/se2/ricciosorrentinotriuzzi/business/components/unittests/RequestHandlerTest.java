package it.polimi.se2.ricciosorrentinotriuzzi.business.components.unittests;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.VisitManager;
import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;
import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.*;
import org.junit.jupiter.api.*;

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

        store = new Store();
        customer = new Customer();
        Dayinterval workingHour = new Dayinterval();
        Address address = new Address();

        workingHour.setDayOfTheWeek(DayOfWeek.from(LocalDateTime.now()).getValue());
        workingHour.setStart(Time.valueOf("00:00:00"));
        workingHour.setEnd(Time.valueOf("23:00:00"));
        workingHour.setId(0);

        store.setName("testName");
        store.setAverageVisitDuration(Time.valueOf("1:30:30"));
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
        customer.setIsAppCustomer(true);

        address.setCity("Milan");
        address.setCountry("Italy");
        address.setPostalCode("21100");
        address.setStore(store);
        address.setStreetName("Piazza Leonardo da Vinci");
        address.setStreetNumber("0");

        store.addWorkingHour(workingHour);
    }

    @Test
    void lineup() {
        Timestamp estimatedDisposalTime = Timestamp.valueOf(LocalDateTime.now());
        when(dataModel.getStore(store.getId())).thenReturn(store);
        when(dataModel.getCustomer(customer.getId())).thenReturn(customer);
        when(dataModel.getQueueDisposalTime(store.getId())).thenReturn(estimatedDisposalTime);
        lur = requestHandler.lineup(3, customer.getId(), store.getId());
        assertTrue(store.getLineups().contains(lur));

        lur = requestHandler.lineup(3, customer.getId(), store.getId());
        //The customer is already lined up
        assertFalse(store.getLineups().contains(lur));

        store.setLineups(new LinkedList<>());
        customer.setLineups(new LinkedList<>());
        //The customer is not in line anymore

        lur = requestHandler.lineup(store.getMaximumOccupancy()+1, customer.getId(), store.getId());
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
        List<Booking> overlappingBookings = new LinkedList<>();
        Timestamp estimatedDisposalTime = Timestamp.valueOf(LocalDateTime.now());
        Timestamp desiredStart = Timestamp.valueOf(LocalDateTime.now().plusMinutes(3));
        when(dataModel.getStore(store.getId())).thenReturn(store);
        when(dataModel.getCustomer(customer.getId())).thenReturn(customer);
        when(dataModel.getQueueDisposalTime(store.getId())).thenReturn(estimatedDisposalTime);
        br = requestHandler.book(3, customer.getId(), store.getId(), desiredStart, Time.valueOf("00:10:00"), new ArrayList<>());
        br1 = br;
        assertTrue(store.getBookings().contains(br));


        //The customer tries to book a visit for a time interval which overlaps with a request he previously made (the previous one). So,
        //the getCustomerBookings will provide the request handler with a list containing the previous booking
        when(dataModel.getCustomerBookings(anyString(), any(), any())).thenReturn(customer.getBookings());
        br = requestHandler.book(3, customer.getId(), store.getId(), desiredStart,
                Time.valueOf("00:05:00"), new ArrayList<>());
        assertNull(br);

        //The customer tries to book a visit before the estimated queue disposal time
        estimatedDisposalTime = Timestamp.valueOf(LocalDateTime.now());
        when(dataModel.getQueueDisposalTime(store.getId())).thenReturn(estimatedDisposalTime);
        desiredStart = Timestamp.valueOf(LocalDateTime.now().minusMinutes(3));
        br = requestHandler.book(3, customer.getId(), store.getId(), desiredStart, Time.valueOf("00:05:00"), new ArrayList<>());
        assertNull(br);

        //The time interval selected by the customer is already maximized by other booking requests, so a new booking cannot be placed
        br1.setNumberOfPeople(store.getMaximumOccupancy());
        br1.setCustomer(new Customer());
        List<Booking> alreadyPlacedBookings = new LinkedList<>();
        alreadyPlacedBookings.add(br1);
        desiredStart = Timestamp.valueOf(LocalDateTime.now().plusMinutes(1));
        when(dataModel.getCustomerBookings(anyString(), any(), any())).thenReturn(new LinkedList<>());
        when(dataModel.getBookings(anyString(), any(), any())).thenReturn(alreadyPlacedBookings);
        br = requestHandler.book(3, customer.getId(), store.getId(), desiredStart, Time.valueOf("00:05:00"), new ArrayList<>());
        assertNull(br);

        //The customer cannot Book a visit since the store is closed when he desires to visit it
        store.setWorkingHours(new LinkedList<>());
        //The store is now always closed
        br = requestHandler.book(3, customer.getId(), store.getId(), desiredStart, Time.valueOf("00:05:00"), new ArrayList<>());
        assertNull(br);
    }
}