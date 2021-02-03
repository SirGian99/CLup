package it.polimi.se2.ricciosorrentinotriuzzi;

import org.eclipse.persistence.annotations.ReturnInsert;
import org.eclipse.persistence.annotations.UuidGenerator;
import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.UUID;

@Entity
@Table(name = "lineup")
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

    public Lineup() {uuid = UUID.randomUUID().toString();}

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
    public Boolean isPending() { return (state == VisitRequestStatus.PENDING); }
    @Override
    public Boolean isReady() { return (state == VisitRequestStatus.READY); }
    @Override
    public Boolean isFulfilled() { return (state == VisitRequestStatus.FULFILLED); }
    @Override
    public Boolean isCompleted() { return (state == VisitRequestStatus.COMPLETED); }
    @Override
    public Boolean isActive() { return (state != VisitRequestStatus.COMPLETED); }

    @Override
    public String toString() {
        return "Lineup{" +
                "uuid='" + uuid + '\'' +
                ", hfid='" + hfid + '\'' +
                ", state=" + state +
                ", dateTimeOfCreation=" + dateTimeOfCreation +
                '}';
    }

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        json.put("estimatedTimeOfEntrance", getEstimatedTimeOfEntrance());
        json.put("storeID", getStore().getId());
        JSONObject jsonVisitToken = new JSONObject();
        jsonVisitToken.put("uuid", getUuid());
        jsonVisitToken.put("hfid", getHfid());
        json.put("visitToken",jsonVisitToken);
        json.put("numberOfPeople", getNumberOfPeople());
        return json;
    }
}
