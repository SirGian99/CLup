package it.polimi.se2.ricciosorrentinotriuzzi.integrationtests;
import it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents.*;
import it.polimi.se2.ricciosorrentinotriuzzi.component.mockcomponent.*;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.Persistence;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

class VisitManagerIntegrationTest {

    private TestDataModel dataModel;
    private TestVisitManager visitManager;
    private Chain chain;
    private Store store;
    private Address address;
    private Manager manager;
    private Dayinterval workingHour;
    private Customer customer;
    private Booking booking;
    private Lineup lineup;
    @BeforeEach
    void setUp() throws NamingException {
        dataModel = new TestDataModel();
        visitManager = new TestVisitManager(dataModel);
        chain = new Chain("chainTest", "Chain of test", null);
        address = new Address("Piazza Leonardo Da Vinci", "32", "Milan",
                "21133", "Italy", null);
        manager =new Manager("username", "password", "name");
        workingHour = new Dayinterval(Calendar.getInstance().get(Calendar.DAY_OF_WEEK),
                Time.valueOf(LocalTime.of(0, 0)),
                Time.valueOf(LocalTime.of(23, 59, 59)));
        store = new Store("test", "descriptionTest",0, 10,
                Time.valueOf(LocalTime.of(0,30)),10.0, chain,null,null,
                null,null,null,null,null);

        customer = new Customer(UUID.randomUUID().toString(), true);
        booking = new Booking(store, customer,1, Timestamp.valueOf(LocalDateTime.now().plusSeconds(5)),
                Time.valueOf(LocalTime.of(0,30)), null);
        //The estimated time of entrance is set to the one the request handler would set when the lineup request is
        // placed, which is equal to now + the average visit duration of the store, since it's queue is empty
        lineup = new Lineup(store, customer, Timestamp.valueOf(LocalDateTime.now().plus(
                Duration.ofNanos(store.getAverageVisitDuration().toLocalTime().toNanoOfDay()))),1);


        dataModel.getEm().getTransaction().begin();
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
        dataModel.getEm().getTransaction().rollback();
    }

    @Test
    void validateAccess() {
        validateRequestAccess(lineup);
        validateRequestAccess(booking);
    }

    @Test
    void confirmAccess() {
        confirmRequestAccess(lineup);
        confirmRequestAccess(booking);
    }

    @Test
    void validateExit(){
        validateRequestExit(lineup);
        validateRequestExit(booking);
    }

    @Test
    void confirmExit() {
        confirmRequestExit(lineup);
        confirmRequestExit(booking);
    }

    void validateRequestAccess(VisitRequest request) {
        if (request.isBooking())
            store.addBooking((Booking)request);
        else
            store.addLineup((Lineup)request);
        //Since the request is not fulfilled, the result should be equal to 0 people allowed to access
        assert (visitManager.validateAccess(request.getUuid(), store.getId()) == 0);
        request.setState(VisitRequestStatus.FULFILLED);
        assert (visitManager.validateAccess(request.getUuid(), store.getId()) == 0);
        request.setState(VisitRequestStatus.COMPLETED);
        assert (visitManager.validateAccess(request.getUuid(), store.getId()) == 0);
        //Since the request is ready, the result should be equal to a number of people allowed equal to the one specified
        //in the request
        request.setState(VisitRequestStatus.READY);
        assert (visitManager.validateAccess(request.getUuid(), store.getId()) == request.getNumberOfPeople());

    }

    void validateRequestExit(VisitRequest request) {
        if (request.isBooking())
            store.addBooking((Booking)request);
        else
            store.addLineup((Lineup)request);
        //Since the request is not fulfilled, the result should be equal to 0 people allowed to exit
        assert (visitManager.validateExit(request.getUuid(), store.getId()) == 0);
        request.setState(VisitRequestStatus.READY);
        assert (visitManager.validateExit(request.getUuid(), store.getId()) == 0);
        request.setState(VisitRequestStatus.COMPLETED);
        assert (visitManager.validateExit(request.getUuid(), store.getId()) == 0);
        //Since the request is fulfilled, the result should be equal to a number of people allowed equal to the one specified
        //in the request
        request.setState(VisitRequestStatus.FULFILLED);
        assert (visitManager.validateExit(request.getUuid(), store.getId()) == request.getNumberOfPeople());

    }

    void confirmRequestAccess(VisitRequest request) {
        if (request.isBooking())
            store.addBooking((Booking)request);
        else
            store.addLineup((Lineup)request);
        //Since the request is not ready, the result of confirmAccess should be false
        assert (!visitManager.confirmAccess(request.getUuid(), store.getId(), request.getNumberOfPeople()));
        request.setState(VisitRequestStatus.FULFILLED);
        assert (!visitManager.confirmAccess(request.getUuid(), store.getId(), request.getNumberOfPeople()));
        request.setState(VisitRequestStatus.COMPLETED);
        assert (!visitManager.confirmAccess(request.getUuid(), store.getId(), request.getNumberOfPeople()));
        //Since the request is ready, the result should be equal to true and the request status should be fulfilled,
        //the current occupancy of the store updated and the visit starting time different from null
        request.setState(VisitRequestStatus.READY);
        int oldCurrentOccupancy = store.getCurrentOccupancy();
        assert (request.getVisitStartingTime() == null && visitManager.confirmAccess(request.getUuid(), store.getId(),
                request.getNumberOfPeople()) && request.isFulfilled() &&
                store.getCurrentOccupancy() == oldCurrentOccupancy + request.getNumberOfPeople() &&
                request.getVisitStartingTime() != null);
        //If the number of people who actually access the store is less than the number of people specified in the
        //request, the number of people in the request is also updated
        request.setState(VisitRequestStatus.READY);
        int numberOfPeople = request.getNumberOfPeople()-1;
        oldCurrentOccupancy = store.getCurrentOccupancy();
        request.setVisitStartingTime(null);
        assert (request.getVisitStartingTime() == null && visitManager.confirmAccess(request.getUuid(), store.getId(),
                numberOfPeople) && request.isFulfilled() && request.getNumberOfPeople() == numberOfPeople
                && store.getCurrentOccupancy() == oldCurrentOccupancy + request.getNumberOfPeople() &&
                request.getVisitStartingTime() != null);
    }

    void confirmRequestExit(VisitRequest request) {
        if (request.isBooking())
            store.addBooking((Booking)request);
        else
            store.addLineup((Lineup)request);
        //Since the request is not fulfilled, the result of confirmExit should be false
        assert (!visitManager.confirmExit(request.getUuid(), store.getId(), request.getNumberOfPeople()));
        request.setState(VisitRequestStatus.READY);
        assert (!visitManager.confirmExit(request.getUuid(), store.getId(), request.getNumberOfPeople()));
        request.setState(VisitRequestStatus.COMPLETED);
        assert (!visitManager.confirmExit(request.getUuid(), store.getId(), request.getNumberOfPeople()));
        //Since the request is fulfilled, the result should be equal to true and the request status should be completed,
        //the current occupancy of the store updated and the completion time must be null before and not null after the
        //execution of the confirmExit method
        request.setState(VisitRequestStatus.FULFILLED);
        store.setCurrentOccupancy(store.getCurrentOccupancy() + request.getNumberOfPeople());
        int oldCurrentOccupancy = store.getCurrentOccupancy();

        assert (request.getVisitCompletionTime() == null && visitManager.confirmExit(request.getUuid(), store.getId(),
                request.getNumberOfPeople()) && request.isCompleted() &&
                store.getCurrentOccupancy() == oldCurrentOccupancy - request.getNumberOfPeople() &&
                request.getVisitCompletionTime() != null);
    }

    @Test
    void newRequest() {
        testNewRequest(lineup);
    }

    void testNewRequest(VisitRequest request){
        if (request.isBooking()) {
            store.addBooking((Booking) request);

        }
        else {
            store.addLineup((Lineup) request);
            LocalDateTime now = LocalDateTime.now();
            //Let's simulate that the store already has a customer inside it, and the new placed line up request has
            //been made by a number of people equal to the store maximum occupancy. Of course, the request should not
            //be set in ready state by the visit manager
            store.setCurrentOccupancy(store.getCurrentOccupancy()+1);
            request.setNumberOfPeople(store.getMaximumOccupancy());

            visitManager.newRequest(request);
            assert (request.isPending() && ((Lineup) request).getEstimatedTimeOfEntrance().after(Timestamp.valueOf(now)));

            //If the request has been made by a number of people less then or equal to the left occupancy currently
            // available in the store, then the request should be in ready state and its estimated time of entrance
            //should be set to now
            request.setNumberOfPeople(store.getMaximumOccupancy()-store.getCurrentOccupancy());
            visitManager.newRequest(request);
            now = LocalDateTime.now();
            assert (request.isReady() && ((Lineup) request).getEstimatedTimeOfEntrance().before(Timestamp.valueOf(now)));
            }
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