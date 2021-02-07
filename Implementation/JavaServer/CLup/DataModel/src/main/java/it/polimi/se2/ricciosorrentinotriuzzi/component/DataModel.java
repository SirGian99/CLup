package it.polimi.se2.ricciosorrentinotriuzzi.component;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;

import javax.ejb.*;
import javax.persistence.*;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Stateless
public class DataModel {
    @PersistenceContext(unitName = "PCLup")
    protected EntityManager em;

    public DataModel() {}

    public List<Lineup> getQueue(String storeID){
        return em.createQuery(
                "SELECT l FROM Lineup l WHERE l.store.id LIKE :storeID and l.state = :pending")
                .setParameter("storeID", storeID).setParameter("pending", VisitRequestStatus.PENDING)
                .getResultList();
        //return em.createNamedQuery("Lineup.getStoreQueue", Lineup.class).setParameter(1, storeID).getResultList();
                //.setParameter("pending", VisitRequestStatus.PENDING).setParameter("ready", VisitRequestStatus.READY).getResultList();
    }

    public List<Store> getAllStores(){
        return em.createQuery("select s from Store s").getResultList();
    }

    public List<VisitRequest> getVisitRequests(String storeID, Timestamp date) {
        List<VisitRequest> toReturn = new ArrayList<>();
        toReturn.addAll(em.createQuery(
                "SELECT l FROM Lineup l WHERE l.store.id LIKE :storeID")
                .setParameter("storeID", storeID).getResultList());
        toReturn.addAll(em.createQuery(
                "SELECT l FROM Booking l WHERE l.store.id LIKE :storeID")
                .setParameter("storeID", storeID).getResultList());
        return toReturn;
    }

/*
//TODO ERA USATO DAL BEAN PER AGGIORNARE PERIODICAMENTE LA AVG VISIT DUR
    public List<VisitRequest> getVisitRequest(String storeID, Timestamp date){
        List<VisitRequest> toReturn = new LinkedList<>();
        toReturn.addAll(em.createQuery(
                "SELECT l FROM Lineup l WHERE l.store.id LIKE :storeID and l.visitCompletionTime > :endingTime")
                .setParameter("endingTime", date).setParameter("storeID", storeID).getResultList());
        toReturn.addAll(em.createQuery(
                "SELECT l FROM Booking l WHERE l.store.id LIKE :storeID and l.visitCompletionTime > :endingTime")
                .setParameter("endingTime", date).setParameter("storeID", storeID).getResultList());
        return toReturn;
    }*/

    public Timestamp getQueueDisposalTime(String storeID){
        List<Lineup> queue = getQueue(storeID);
        int size = queue.size();
        //System.out.println("La dimensione della coda di "+storeID+" è " + size);
        if (size>0){
            /*for (Lineup lineup : queue)
                System.out.println("Sono una lur: " + lineup.getHfid() +" uuid: " + lineup.getUuid() + "timeofc: " + lineup.getDateTimeOfCreation()
                + " EstTimeOfEntrance: " + lineup.getEstimatedTimeOfEntrance());*/
            Timestamp ts = queue.get(queue.size()-1).getEstimatedTimeOfEntrance();
            return ts.before(Timestamp.valueOf(LocalDateTime.now())) ? Timestamp.valueOf(LocalDateTime.now()) : ts;
        }
        return Timestamp.valueOf(LocalDateTime.now());
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

    public boolean completeVisit(String visitToken, String storeID, int numberOfPeople){
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

    public void insertRequest(VisitRequest request) {
        if (request.isBooking()){
            request.getStore().addBooking((Booking) request);
            request.getCustomer().addBooking((Booking) request);
        } else {
            request.getStore().addLineup((Lineup) request);
            request.getCustomer().addLineup((Lineup) request);
        }
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
            c.setIsAppCustomer(true);
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

    public List<VisitRequest> getActiveRequests(String storeID) {
        List<VisitRequest> result = new LinkedList<>();
        List<Lineup> lurs =
                em.createQuery("SELECT l FROM Lineup l WHERE l.store.id LIKE :storeID and l.state <> :completed")
                .setParameter("storeID", storeID).setParameter("completed", VisitRequestStatus.COMPLETED)
                .getResultList();
        List<Booking> brs =
                em.createQuery("SELECT b FROM Booking b WHERE b.store.id LIKE :storeID and b.state <> :completed")
                        .setParameter("storeID", storeID).setParameter("completed", VisitRequestStatus.COMPLETED)
                        .getResultList();
        result.addAll(lurs);
        result.addAll(brs);
        return result;
    }
}
