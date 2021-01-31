package it.polimi.se2.ricciosorrentinotriuzzi.components;
import it.polimi.se2.ricciosorrentinotriuzzi.*;
import javax.ejb.*;
import javax.persistence.*;

@Stateless
public class DataModel {
    @PersistenceContext(unitName = "PCLup")
    private EntityManager em;

    public DataModel() {
    }

    public String getStoreName(String storeID){
        return em.find(Store.class, storeID).getName();
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
