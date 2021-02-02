package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

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
    private Integer state;
    private Timestamp dateTimeOfCreation;

    public Lineup(){}

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

    public Integer getState() {
        return state;
    }
    public void setState(Integer state) {
        this.state = state;
    }

    public Timestamp getDateTimeOfCreation() {
        return dateTimeOfCreation;
    }
    public void setDateTimeOfCreation(Timestamp dateTimeOfCreation) {
        this.dateTimeOfCreation = dateTimeOfCreation;
    }
}
