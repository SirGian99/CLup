package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class Booking extends VisitRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String uuid;
    private String hfid;
    @ManyToOne
    @JoinColumn(name = "store")
    private Store store;
    @ManyToOne
    @JoinColumn(name = "appCustomer")
    private Customer appCustomer;
    private Integer numberOfPeople;
    private VisitRequestStatus state;
    private Timestamp dateTimeOfCreation;
    private Timestamp desiredStartingTime;
    private Time desiredDuration;
    private Timestamp visitStartingTime;
    private Timestamp visitCompletionTime;
    @ManyToMany
    @JoinTable(name = "bookingproduct",
                joinColumns = @JoinColumn(name = "booking"),
                inverseJoinColumns = @JoinColumn(name = "productSection"))
    private List<Productsection> productSections;

    public Booking() {uuid = UUID.randomUUID().toString();}

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String token) {
        this.uuid = token;
    }


    public Store getStore() {
        return store;
    }
    public void setStore(Store store) {
        this.store = store;
    }


    public Customer getAppCustomer() {
        return appCustomer;
    }
    public void setAppCustomer(Customer appCustomer) {
        this.appCustomer = appCustomer;
    }


    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }
    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }


    public VisitRequestStatus getState() {
        return state;
    }
    public void setState(VisitRequestStatus state) {
        this.state = state;
    }


    public Timestamp getDateTimeOfCreation() {
        return dateTimeOfCreation;
    }
    public void setDateTimeOfCreation(Timestamp dateTimeOfCreation) {
        this.dateTimeOfCreation = dateTimeOfCreation;
    }


    public String getHfid() {
        return hfid;
    }


    public Timestamp getDesiredStartingTime() {
        return desiredStartingTime;
    }
    public void setDesiredStartingTime(Timestamp desiredStartingTime) { this.desiredStartingTime = desiredStartingTime; }


    public Time getDesiredDuration() {
        return desiredDuration;
    }
    public void setDesiredDuration(Time desiredDuration) {
        this.desiredDuration = desiredDuration;
    }


    public Timestamp getVisitStartingTime() {
        return visitStartingTime;
    }
    public void setVisitStartingTime(Timestamp visitStartingTime) {
        this.visitStartingTime = visitStartingTime;
    }


    public Timestamp getVisitCompletionTime() {
        return visitCompletionTime;
    }
    public void setVisitCompletionTime(Timestamp visitCompletionTime) { this.visitCompletionTime = visitCompletionTime; }

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
}
