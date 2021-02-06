package it.polimi.se2.ricciosorrentinotriuzzi.entities;

import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lineup")
//@NamedQuery(name = "Lineup.getStoreQueue", query = "SELECT l FROM Lineup l where (l.store.id LIKE :store) and (l.state = :pending or l.state= :ready" )
public class Lineup extends VisitRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String uuid;
    private String hfid;
    @ManyToOne
    @JoinColumn(name = "store")
    private Store store;
    @ManyToOne
    @JoinColumn(name = "customer")
    private Customer customer;
    private Timestamp estimatedTimeOfEntrance;
    private Integer numberOfPeople;
    private VisitRequestStatus state;
    private Timestamp dateTimeOfCreation;
    private Timestamp visitStartingTime;
    private Timestamp visitCompletionTime;


    public Lineup() {
        this.uuid = UUID.randomUUID().toString();
    }

    public Lineup(Store store, Customer customer, Timestamp estimatedTimeOfEntrance, Integer numberOfPeople) {
        this.dateTimeOfCreation = Timestamp.valueOf(LocalDateTime.now());
        this.uuid = UUID.randomUUID().toString();
        this.hfid = "L-" +(char)( Integer.parseInt(this.dateTimeOfCreation.toString().substring(8, 10)) % 26 + 65) + String.valueOf(Integer.parseInt(this.uuid.substring(4, 8), 16) % 999);
        this.store = store;
        this.customer = customer;
        this.estimatedTimeOfEntrance = estimatedTimeOfEntrance;
        this.numberOfPeople = numberOfPeople;
        this.state = VisitRequestStatus.PENDING;
    }

    @Override
    public VisitRequestStatus getState() {
        return state;
    }
    @Override
    public void setState(VisitRequestStatus state) {
        this.state = state;
    }

    @Override
    public Timestamp getVisitStartingTime() {
        return visitStartingTime;
    }
    @Override
    public void setVisitStartingTime(Timestamp visitStartingTime) {
        this.visitStartingTime = visitStartingTime;
    }

    @Override
    public Timestamp getVisitCompletionTime() {
        return visitCompletionTime;
    }
    @Override
    public void setVisitCompletionTime(Timestamp visitCompletionTime) { this.visitCompletionTime = visitCompletionTime; }

    @Override
    public String getUuid() {
        return uuid;
    }
    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getHfid() {
        return hfid;
    }
    @Override
    public void setHfid(String hfid) {
        this.hfid = hfid;
    }

    @Override
    public Store getStore() { return store; }
    @Override
    public void setStore(Store store) {
        this.store = store;
        store.addLineup(this);
    }

    @Override
    public Customer getCustomer() { return customer; }
    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
        customer.addLineup(this);
    }

    @Override
    public Integer getNumberOfPeople() { return numberOfPeople; }
    @Override
    public void setNumberOfPeople(Integer numberOfPeople) { this.numberOfPeople = numberOfPeople; }

    public Timestamp getEstimatedTimeOfEntrance() { return estimatedTimeOfEntrance; }
    public void setEstimatedTimeOfEntrance(Timestamp estimatedTimeOfEntrance) {
        this.estimatedTimeOfEntrance = estimatedTimeOfEntrance;
    }

    @Override
    public Timestamp getDateTimeOfCreation() { return dateTimeOfCreation; }
    @Override
    public void setDateTimeOfCreation(Timestamp dateTimeOfCreation) { this.dateTimeOfCreation = dateTimeOfCreation; }

    @Override
    public boolean isPending() { return (state == VisitRequestStatus.PENDING); }
    @Override
    public boolean isReady() { return (state == VisitRequestStatus.READY); }
    @Override
    public boolean isFulfilled() { return (state == VisitRequestStatus.FULFILLED); }
    @Override
    public boolean isCompleted() { return (state == VisitRequestStatus.COMPLETED); }
    @Override
    public boolean isActive() { return (state != VisitRequestStatus.COMPLETED); }


    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        json.put("estimatedTimeOfEntrance", getEstimatedTimeOfEntrance());
        json.put("storeID", getStore().getId());
        JSONObject jsonVisitToken = new JSONObject();
        jsonVisitToken.put("uuid", getUuid());
        jsonVisitToken.put("hfid", getHfid());
        json.put("visitToken",jsonVisitToken);
        json.put("numberOfPeople", getNumberOfPeople());
        json.put("state", getState().getValue());
        return json;
    }
}
