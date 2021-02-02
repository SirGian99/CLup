package it.polimi.se2.ricciosorrentinotriuzzi.components;
import it.polimi.se2.ricciosorrentinotriuzzi.*;
import javax.ejb.*;
import javax.persistence.*;
import java.awt.print.Book;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Stateless
public class DataModel {
    @PersistenceContext(unitName = "PCLup")
    private EntityManager em;

    public DataModel() {}

    public VisitRequest getVisitRequest(String visitRequestToken){
        VisitRequest request = em.find(Lineup.class, visitRequestToken);
        if (request == null){
            request = em.find(Booking.class, visitRequestToken);
        }
        return request;
    }

    public int checkReadyRequest(String storeID, String visitToken){
        VisitRequest request = getVisitRequest(visitToken);
        //System.out.println("checkreadyReq: " + request.isReady() +" status: " + request.getState()+"\n");
        return  (request != null && request.getStore().getId().equals(storeID) && request.isReady()) ? request.getNumberOfPeople() : 0;
    }

    public boolean startVisit(String visitToken, String storeID, int numberOfPeople){
        VisitRequest request = getVisitRequest(visitToken);
        Store store = request.getStore();
        List<Productsection> productsections = store.getProductSections();
        if (store.getId().equals(storeID)) {
            request.setVisitStartingTime(Timestamp.valueOf(LocalDateTime.now()));
            request.setState(VisitRequestStatus.FULFILLED);
            request.setNumberOfPeople(numberOfPeople);
            store.setCurrentOccupancy(store.getCurrentOccupancy() + numberOfPeople);
            for (Productsection section: productsections) {
                double oldOcc = section.getCurrentOccupancy();
                section.setCurrentOccupancy(oldOcc + numberOfPeople * (double)section.getMaximumOccupancy()/store.getMaximumOccupancy());
            }
            return true;
        }
        return false;
    }

    public List<Booking> getBookings(String storeID, Timestamp start, Timestamp end){
    Store store = em.find(Store.class, storeID);
    LinkedList<Booking> toReturn= new LinkedList<>();

    for(Booking booking : store.getBookings() ){
        LocalDateTime beginning = booking.getDesiredStartingTime().toLocalDateTime();
        LocalDateTime ending = beginning.plus(Duration.ofNanos(booking.getDesiredDuration().toLocalTime().toNanoOfDay()));
        if(beginning.isBefore(end.toLocalDateTime()) && ending.isAfter(start.toLocalDateTime()))
            toReturn.addLast(booking);
    }
    return toReturn;
    }

    public void allowVisitRequest(String token){
        VisitRequest request = getVisitRequest(token);
        if (request!= null)
        request.setState(VisitRequestStatus.READY);
    }

    public int checkFulfilledRequest(String storeID, String visitToken){
        VisitRequest request = getVisitRequest(visitToken);
        return  request != null && request.getStore().getId().equals(storeID) && request.isFulfilled() ? request.getNumberOfPeople() : 0;
    }

    public boolean endVisit(String visitToken, String storeID, int numberOfPeople){
        VisitRequest request = getVisitRequest(visitToken);
        Store store = request.getStore();
        List<Productsection> productsections = store.getProductSections();
        if (store.getId().equals(storeID)) {
            request.setVisitCompletionTime(Timestamp.valueOf(LocalDateTime.now()));
            request.setState(VisitRequestStatus.COMPLETED);
            store.setCurrentOccupancy(store.getCurrentOccupancy() - numberOfPeople);
            for (Productsection section: productsections) {
                double oldOcc = section.getCurrentOccupancy();
                section.setCurrentOccupancy(oldOcc - numberOfPeople * (double)section.getMaximumOccupancy()/store.getMaximumOccupancy());
            }
            return true;
        }
        return false;
    }

    public String getStoreName(String storeID){
        return em.find(Store.class, storeID).getName();
    }

    public void insertNewStore(String name, String chain, int currentOcc, int maxOcc, double safetyTh, String managerUsername){
        Store store = new Store();
        store.setName(name);
        store.setChain(em.find(Chain.class, chain));
        store.setCurrentOccupancy(currentOcc);
        store.setMaximumOccupancy(maxOcc);
        store.addManager(getManager(managerUsername));
        store.setSafetyThreshold(safetyTh);
        em.persist(store);
    }

    public void addManager(String storeID, String managerUser) {
        Store store = em.find(Store.class, storeID);
        Manager manager = getManager(managerUser);
        store.addManager(manager);
    }

    public Store getStore(String storeID){
        return em.find(Store.class, storeID);
    }

    public Manager getManager(String managerUsername){
        return em.createNamedQuery("Manager.findByUsername", Manager.class).setParameter(1, managerUsername).getSingleResult();
    }

    public VisitRequest insertRequest(VisitRequest request) {
        em.persist(request);
        System.out.println("New lur persisted with uuid: "+request.getUuid());
        //TODO devi fare anche la append to queue se è una lur
        return request;
    }

    public void insertCustomer(Customer c) {
        em.persist(c);
    }

    public Customer newAppCustomer(String id) {
        Customer c;
        c = em.find(Customer.class, id);
        if (c == null) {
            c = new Customer();
            c.setId(id);
            c.setIsAppCustomer((byte) 1);
            em.persist(c);
        }
        return c;
    }

    public Customer getCustomer(String id) {
        return em.find(Customer.class, id);
    }

    public Chain getChain(String storeID) {
        return em.find(Store.class, storeID).getChain();
    }

    public Address getAddress(long addressID) {
        return em.find(Address.class, addressID);
    }


    /* public Chain getChains(city: String) -> [Chain]: returns all the available chains and independent stores in the city specified by the homonym parameter
    getChain(storeID: ID) -> Chain: returns the chain of the store identified by parameter storeID
    getStores(chainName: String, city: String) -> [Store]: returns all the stores belonging to the chain specified by parameter chainName and located in the city specified by the homonym parameter. The result depends on which parameters have a defined value
    getStores(managerID: ID) -> [Store]: returns the set of the stores administered by the manager identified by parameter managerID
    getStore(storeID: ID) -> [Store]: returns the store identified by parameter storeID
    getQueue(storeID: ID) -> Queue: returns the queue of the store identified by parameter storeID
    appendToQueue(lineUpRequest: LineUpRequest, store: Store): persists the line-up request specified by the homonym parameter and appends it to the queue of the store specified by parameter store
    insertManager(manager: Manager): persists the manager passed as input
    validateCredentials(username: String, password: String) -> (ID, String): checks the credentials and returns the corresponding manager id and a new generated authentication token
    managerLogout(managerID: ID): invalidates the authentication token used by the manager identified by parameter managerID.
    getManager(username: String) -> Manager: returns the manager identified by parameter username.
            getManager(id: ID) -> Manager: returns the manager identified by parameter id.
            addManager(manager: Manager, store: Store): adds the manager specified by parameter manager to the managers of the store specified by parameter store.
            insertSection(section: ProductSection): persists the product section passed as input.
            getSections(storeID: ID) -> [ProductSection]: returns all the product sections of the store identified by parameter storeID.
    removeSection(section: ProductSection): deletes the product section identified by parameter sectionID.
    insertRequest(request: VisitRequest): persists the visit request passed as input.
            getVisitRequest(visitToken: Token) -> VisitRequest: returns the visit request identified by parameter visitToken.
    getActiveRequests(customerID: ID) -> [VisitRequest]: returns all the active requests of the customer identified by parameter customerID.
    getActiveRequests(storeID: ID) -> [VisitRequest]: returns all the active requests of the store identified by parameter storeID.
    getLineUpRequest(appID: ID) -> LineUpRequest: returns the active line-up request placed by the app-customer identified by parameter appID.
            getBookings(storeID: ID, timeInterval: TimeInterval) -> [BookingRequest]: returns the set of booking requests already placed for the store identified by parameter storeID whose time intervals overlap with the one specified by parameter timeInterval.
            checkBookings(appID: ID, timeInterval: TimeInterval) -> Bool: returns true if the customer identified by parameter appID has placed at least one booking in the time interval specified by the homonym parameter. False otherwise.
    checkReadyRequest(visitToken: Token) -> Bool: returns true if the request identified by parameter visitToken is in the ready state. False otherwise.
    allowVisitRequest(visitToken: Token): marks the visit request identified by parameter visitToken as ready.
    startVisit(visitToken: Token): marks the visit request identified by parameter visitToken as fulfilled and creates a visit associated with it.
            completeVisit(visitToken: Token): marks the visit request identified by parameter visitToken as completed and associates it with a completed visit.
            removeFromQueue(visitToken: Token): removes the line-up request identified by parameter visitToken from the queue of the store it is associated with.
    removeRequest(request: VisitRequest): removes the visit request passed as input.
            insertCustomer(customer: Customer): persists the customer passed as input.
    getCustomer(customerID: ID) -> Customer: returns the customer identified by parameter customerID.
            getVisitCustomer(visitToken: Token) -> Customer: returns the customer of the one that made the request.
    getAverageDuration(appID: ID, storeID: ID) -> Duration: returns the average duration of a visit of the app-customer identified by parameter appID to the store specified by parameter storeID
    getCustomerAttitude(appID: ID, storeID: ID) -> Attitude: returns the attitude of the app-customer identified by parameter appID and related to the store identified by parameter storeID.
*/
}
