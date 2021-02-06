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
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    private DataModel dataModel;
    private TestCustomerController customerController;
    private Store store;
    private Customer customer;
    private Lineup lineup1;
    private Booking booking1;
    private Lineup lineup2;
    private Booking booking2;

    @BeforeEach
    void setUp() {
        dataModel = mock(DataModel.class);
        customerController = new TestCustomerController(dataModel);

        booking1 = new Booking();
        lineup1 = new Lineup();
        booking2 = new Booking();
        lineup2 = new Lineup();
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

        lineup1.setEstimatedTimeOfEntrance(Timestamp.valueOf(LocalDateTime.now().plus(Duration.ofNanos(store.getAverageVisitDuration().toLocalTime().toNanoOfDay()))));
        lineup1.setCustomer(customer);
        lineup1.setStore(store);
        lineup1.setNumberOfPeople(1);
        lineup1.setDateTimeOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        lineup1.setState(VisitRequestStatus.PENDING);
        store.getLineups().add(lineup1);

        booking1.setCustomer(customer);
        booking1.setStore(store);
        booking1.setNumberOfPeople(1);
        booking1.setDateTimeOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        booking1.setState(VisitRequestStatus.READY);
        booking1.setDesiredStartingTime(Timestamp.valueOf(LocalDateTime.now().plusSeconds(1)));
        booking1.setDesiredDuration(Time.valueOf("00:05:00"));
        store.getBookings().add(booking1);

        lineup2.setEstimatedTimeOfEntrance(Timestamp.valueOf(LocalDateTime.now().plus(Duration.ofNanos(store.getAverageVisitDuration().toLocalTime().toNanoOfDay()))));
        lineup2.setCustomer(customer);
        lineup2.setStore(store);
        lineup2.setNumberOfPeople(1);
        lineup2.setDateTimeOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        lineup2.setState(VisitRequestStatus.FULFILLED);
        store.getLineups().add(lineup2);

        booking2.setCustomer(customer);
        booking2.setStore(store);
        booking2.setNumberOfPeople(1);
        booking2.setDateTimeOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        booking2.setState(VisitRequestStatus.COMPLETED);
        booking2.setDesiredStartingTime(Timestamp.valueOf(LocalDateTime.now().plusSeconds(1)));
        booking2.setDesiredDuration(Time.valueOf("00:05:00"));
        store.getBookings().add(booking2);
    }

    @Test
    void getCustomerActiveBookings() {
        when(dataModel.getCustomer(customer.getId())).thenReturn(customer);
        for (Booking actBook: customerController.getCustomerActiveBookings(customer.getId()))
            assertTrue(booking1.isActive());
    }

    @Test
    void getCustomerActiveLineups() {
        when(dataModel.getCustomer(customer.getId())).thenReturn(customer);
        for (Lineup actLur: customerController.getCustomerActiveLineups(customer.getId()))
            assertTrue(actLur.isActive());
    }


}