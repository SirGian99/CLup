package it.polimi.se2.ricciosorrentinotriuzzi.components;
import it.polimi.se2.ricciosorrentinotriuzzi.*;
import javax.ejb.*;
import javax.persistence.*;
import javax.sound.sampled.Line;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

@Stateless
public class DataModel {
    @PersistenceContext(unitName = "PCLup")
    private EntityManager em;

    public DataModel() {}

    public List<Lineup> getQueue(String storeID){
        return em.createQuery(
                "SELECT l FROM Lineup l WHERE l.store.id LIKE :storeID and l.state = :pending")
                .setParameter("storeID", storeID).setParameter("pending", VisitRequestStatus.PENDING)
                .getResultList();
        //return em.createNamedQuery("Lineup.getStoreQueue", Lineup.class).setParameter(1, storeID).getResultList();
                //.setParameter("pending", VisitRequestStatus.PENDING).setParameter("ready", VisitRequestStatus.READY).getResultList();
    }

    public Timestamp getQueueDisposalTime(String storeID){
        List<Lineup> queue = getQueue(storeID);
        int size = queue.size();
        System.out.println("La dimensione è " + size);
        if (size>0){
            for (Lineup lineup : queue)
                System.out.println("Sono una lur: " + lineup.getHfid() +" uuid: " + lineup.getUuid() + "timeofc: " + lineup.getDateTimeOfCreation()
                + " EstTimeOfEntrance: " + lineup.getEstimatedTimeOfEntrance());
            return queue.get(queue.size()-1).getEstimatedTimeOfEntrance();
        }
        return Timestamp.valueOf(LocalDateTime.now());
        /*Timestamp toReturn = Timestamp.from(Instant.now().plus(
                getQueue(storeID).size() * getStore(storeID).getAverageVisitDuration().getTime(),
                ChronoUnit.MILLIS));
        System.out.println("Appena calcolata: " + toReturn);
        return toReturn;*/
    }

    public int checkReadyRequest(String storeID, String visitToken){
        VisitRequest request = getVisitRequest(visitToken);
        //System.out.println("checkreadyReq: " + request.isReady() +" status: " + request.getState()+"\n");
        return  (request != null && request.getStore().getId().equals(storeID) && request.isReady()) ? request.getNumberOfPeople() : 0;
    }

    public boolean startVisit(String visitToken, String storeID, int numberOfPeople){
        VisitRequest request = getVisitRequest(visitToken);
        Store store = request.getStore();
        if (store.getId().equals(storeID) && request.isReady()) {
            request.setVisitStartingTime(Timestamp.valueOf(LocalDateTime.now()));
            request.setState(VisitRequestStatus.FULFILLED);
            request.setNumberOfPeople(numberOfPeople);
            store.setCurrentOccupancy(store.getCurrentOccupancy() + numberOfPeople);
            if (request.isBooking()) {
                List<Productsection> productsections = ((Booking) request).getProductSections();
                for (Productsection section : productsections) {
                    double oldOcc = section.getCurrentOccupancy();
                    section.setCurrentOccupancy(oldOcc + numberOfPeople * (double) section.getMaximumOccupancy() / store.getMaximumOccupancy());
                }
            }
            return true;
        }
        return false;
    }

    public List<Booking> getBookings(String storeID, Timestamp start, Timestamp end){
        System.out.println("Store ID: " + storeID);
        Store store = em.find(Store.class, storeID);
        LinkedList<Booking> toReturn= new LinkedList<>();
            System.out.println("start: " + start + " end" + end);

        for(Booking booking : store.getBookings() ){
            LocalDateTime beginning = booking.getDesiredStartingTime().toLocalDateTime();
            LocalDateTime ending = beginning.plus(Duration.ofNanos(booking.getDesiredDuration().toLocalTime().toNanoOfDay()));
            System.out.println("Booking: " + booking.getHfid() + " start: " + beginning + " end: " + ending +" duration: " + booking.getDesiredDuration());
            if(beginning.isBefore(end.toLocalDateTime()) && ending.isAfter(start.toLocalDateTime())) {
                toReturn.addLast(booking);
                System.out.println("Aggiunto");
            }
        }
        return toReturn;
    }

    public List<Booking> getCustomerBookings(String customerID, Timestamp start, Timestamp end) {
        Customer c = em.find(Customer.class, customerID);
        LinkedList<Booking> toReturn = new LinkedList<>();

        for(Booking booking : c.getBookings()) {
            LocalDateTime beginning = booking.getDesiredStartingTime().toLocalDateTime();
            LocalDateTime ending = beginning.plus(Duration.ofNanos(booking.getDesiredDuration().toLocalTime().toNanoOfDay()));
            if (beginning.isBefore(end.toLocalDateTime()) && ending.isAfter(start.toLocalDateTime()))
                toReturn.addLast(booking);
        }
        return toReturn;
    }

    public void allowVisitRequest(VisitRequest request){
        if (request!= null && request.isPending()) {
            request.setState(VisitRequestStatus.READY);
            if(!request.isBooking()){
                ((Lineup)request).setEstimatedTimeOfEntrance(Timestamp.valueOf(LocalDateTime.now()));
            }
        }
    }

    public int checkFulfilledRequest(String storeID, String visitToken){
        VisitRequest request = getVisitRequest(visitToken);
        return  request != null && request.getStore().getId().equals(storeID) && request.isFulfilled() ? request.getNumberOfPeople() : 0;
    }

    public boolean endVisit(String visitToken, String storeID, int numberOfPeople){
        VisitRequest request = getVisitRequest(visitToken);
        Store store = request.getStore();
        if (store.getId().equals(storeID) && request.isFulfilled()) {
            request.setVisitCompletionTime(Timestamp.valueOf(LocalDateTime.now()));
            request.setState(VisitRequestStatus.COMPLETED);
            store.setCurrentOccupancy(store.getCurrentOccupancy() - numberOfPeople);
            if (request.isBooking()) {
                List<Productsection> productsections = ((Booking) request).getProductSections();
                for (Productsection section : productsections) {
                    double oldOcc = section.getCurrentOccupancy();
                    section.setCurrentOccupancy(oldOcc - numberOfPeople * (double) section.getMaximumOccupancy() / store.getMaximumOccupancy());
                }
                return true;
            }
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

    public void insertRequest(VisitRequest request) {
        em.persist(request);
        System.out.println("New request to persist with uuid: "+request.getUuid());
    }

    public void removeRequest(VisitRequest request) {
        if (request!=null) {
            if (request.isBooking()) {
                request.getCustomer().getBookings().remove(request);
                request.getStore().getBookings().remove(request);
            } else {
                request.getCustomer().getLineups().remove(request);
                request.getStore().getLineups().remove(request);
            }
            em.remove(request);
        }
    }

    public void insertCustomer(Customer c) {
        em.persist(c);
    }

    public VisitRequest getVisitRequest(String visitRequestToken) {
        VisitRequest request = em.find(Lineup.class, visitRequestToken);
        if (request == null) {
            request = em.find(Booking.class, visitRequestToken);
        }
        return request;
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

    public Chain getChainFromStore(String storeID) {
        return em.find(Store.class, storeID).getChain();
    }

    public Chain getChainByName(String name) {
        return em.find(Chain.class, name);
    }

    public Address getAddress(long addressID) {
        return em.find(Address.class, addressID);
    }

    public List<Address> getAddressesByCity(String city){
        return em.createNamedQuery("Address.getAllByCity", Address.class).setParameter(1, city).getResultList();
    }

    public Productsection getSection(Long id) {
        return em.find(Productsection.class, id);
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
