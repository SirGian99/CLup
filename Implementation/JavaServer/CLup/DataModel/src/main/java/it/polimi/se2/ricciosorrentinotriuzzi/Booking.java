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
    private Customer customer;
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

    @Override
    public String getUuid() {
        return uuid;
    }
    @Override
    public void setUuid(String token) {
        this.uuid = token;
    }

    @Override
    public Store getStore() {
        return store;
    }
    @Override
    public void setStore(Store store) {
        this.store = store;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }
    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
        customer.addBooking(this);
    }

    @Override
    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }
    @Override
    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
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
    public Timestamp getDateTimeOfCreation() {
        return dateTimeOfCreation;
    }
    @Override
    public void setDateTimeOfCreation(Timestamp dateTimeOfCreation) {
        this.dateTimeOfCreation = dateTimeOfCreation;
    }

    @Override
    public String getHfid() {
        return hfid;
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

    public List<Productsection> getProductSections() {
        return productSections;
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

    @Override
    public Boolean isPending() {
        return (state == VisitRequestStatus.PENDING);
    }
    @Override
    public Boolean isReady() {
        return (state == VisitRequestStatus.READY);
    }
    @Override
    public Boolean isFulfilled() {
        return (state == VisitRequestStatus.FULFILLED);
    }
    @Override
    public Boolean isCompleted() {
        return (state == VisitRequestStatus.COMPLETED);
    }
    @Override
    public Boolean isActive() {
        return (state != VisitRequestStatus.COMPLETED);
    }
}
