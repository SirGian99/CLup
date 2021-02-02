package it.polimi.se2.ricciosorrentinotriuzzi;

import org.eclipse.persistence.annotations.ReturnInsert;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
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
    public void setVisitCompletionTime(Timestamp visitCompletionTime) {
        this.visitCompletionTime = visitCompletionTime;
    }

    private Timestamp visitCompletionTime;

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHfid() {
        return hfid;
    }
    public void setHfid(String hfid) {
        this.hfid = hfid;
    }

    public Store getStore() {
        return store;
    }
    public void setStore(Store store) {
        this.store = store;
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Timestamp getEstimatedTimeOfEntrance() {
        return estimatedTimeOfEntrance;
    }
    public void setEstimatedTimeOfEntrance(Timestamp estimatedTimeOfEntrance) { this.estimatedTimeOfEntrance = estimatedTimeOfEntrance; }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }
    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    /*public VisitRequestStatus getState() {
        return state;
    }
    public void setState(VisitRequestStatus state) {
        this.state = state;
    }*/

    public Timestamp getDateTimeOfCreation() {
        return dateTimeOfCreation;
    }
    public void setDateTimeOfCreation(Timestamp dateTimeOfCreation) {
        this.dateTimeOfCreation = dateTimeOfCreation;
    }
    public Boolean isPending() {
        return (state == VisitRequestStatus.PENDING);
    }

    public Boolean isReady() {
        return (state == VisitRequestStatus.READY);
    }

    public Boolean isFulfilled() {
        return (state == VisitRequestStatus.FULFILLED);
    }

    public Boolean isCompleted() {
        return (state == VisitRequestStatus.COMPLETED);
    }

    public Boolean isActive() {
        return (state != VisitRequestStatus.COMPLETED);
    }

    @Override
    public String toString() {
        return "Lineup{" +
                "uuid='" + uuid + '\'' +
                ", hfid='" + hfid + '\'' +
                ", state=" + state +
                ", dateTimeOfCreation=" + dateTimeOfCreation +
                '}';
    }
}
