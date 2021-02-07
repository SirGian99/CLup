package it.polimi.se2.ricciosorrentinotriuzzi.integrationtests;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.TestCustomerController;
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
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerControllerIntegrationTest {

    private TestDataModel dataModel;
    private TestCustomerController customerController;
    private Customer customer;
    private List<Booking> bookings = new LinkedList<>();
    private List<Lineup> lineups = new LinkedList<>();

    @BeforeEach
    void setUp(){
        dataModel = new TestDataModel();
        customerController = new TestCustomerController(dataModel);

        List<Dayinterval> workingHours = new LinkedList<>();
        Store store = new Store("test", "descriptionTest", 0, 10,
                Time.valueOf(LocalTime.of(0, 30)), 10.0, null, null,
                null, null, null, null);

        customer = new Customer(UUID.randomUUID().toString(), true);

        bookings.add(new Booking(store, customer, 1, Timestamp.valueOf(LocalDateTime.now()),Time.valueOf(LocalTime.of(0,30)), null));
        bookings.get(0).setState(VisitRequestStatus.FULFILLED);

        //After this for loop the store will contain 7 different bookings and 3 lineup requests with different states.
        for (int i = 1; i<8; i++){
            bookings.add(new Booking(store, customer,1, Timestamp.valueOf(LocalDateTime.now().plusDays(i)),
                    Time.valueOf(LocalTime.of(0,30)), null));
            if (i<4) {
                lineups.add(new Lineup(store, customer, Timestamp.valueOf(LocalDateTime.now().plus(
                        Duration.ofNanos(store.getAverageVisitDuration().toLocalTime().toNanoOfDay()))), 1));
                lineups.get(i - 1).setState(VisitRequestStatus.getVisitStatusFromInt(i));
            }
            workingHours.add(new Dayinterval(i, Time.valueOf(LocalTime.of(0, 0)),
                    Time.valueOf(LocalTime.of(23, 59, 59))));
        }

        dataModel.getEm().getTransaction().begin();
        // Test entities are persisted on the database

        // After this foreach loop the store is always open
        for (Dayinterval workingHour : workingHours) {
            dataModel.getEm().persist(workingHour);
            store.addWorkingHour(workingHour);
        }
        dataModel.getEm().persist(store);
        dataModel.getEm().persist(customer);

        for (Booking booking : bookings)
            dataModel.insertRequest(booking);
        for (Lineup lineup : lineups)
            dataModel.insertRequest(lineup);

    }

    @AfterEach
    void tearDown() {
        // The rollback is executed in order to not persist changes on the database
        dataModel.getEm().getTransaction().rollback();
    }

    @Test
    void registerApp() {
        String newID = UUID.randomUUID().toString();
        //A new customer is registered into the CLup DB
        Customer customer = customerController.registerApp(newID);
        assertEquals(customerController.getCustomerByID(newID), customer);
    }

    @Test
    void getCustomerActiveBookings() {
        List<Booking> activeBookings = customerController.getCustomerActiveBookings(customer.getId());
        // The next foreach loop checks whether all the active bookings of the customer are actually in the customer
        // controller result list.
        for (Booking booking : bookings)
            assert (!booking.isActive() && !activeBookings.contains(booking)) ||
                    (booking.isActive() && activeBookings.contains(booking));
    }

    @Test
    void getCustomerActiveLineups() {
        List<Lineup> activeLineup = customerController.getCustomerActiveLineups(customer.getId());
        // The next foreach loop checks whether all the active lineups of the customer are actually in the customer
        // controller result list.
        for (Lineup lineup : lineups)
            assert (!lineup.isActive() && !activeLineup.contains(lineup)) ||
                    (activeLineup.contains(lineup) && lineup.isActive());
    }
}