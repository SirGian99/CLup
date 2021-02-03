package it.polimi.se2.ricciosorrentinotriuzzi;

import java.sql.Timestamp;

public abstract class VisitRequest {
    private String uuid;
    private String hfid;
    private Store store;
    private Customer customer;
    private Integer numberOfPeople;
    private VisitRequestStatus state;
    private Timestamp dateTimeOfCreation;
    private Timestamp visitStartingTime;
    private Timestamp visitCompletionTime;

    public String getUuid() {
        return uuid;
    }

    public String getHfid() {
        return hfid;
    }

    public Store getStore() {
        return store;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public VisitRequestStatus getState() {
        return state;
    }

    public Timestamp getDateTimeOfCreation() {
        return dateTimeOfCreation;
    }

    public Timestamp getVisitStartingTime() { return visitStartingTime; }

    public Timestamp getVisitCompletionTime() {
        return visitCompletionTime;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setHfid(String hfid) {
        this.hfid = hfid;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public void setState(VisitRequestStatus state) {
        this.state = state;
    }

    public void setDateTimeOfCreation(Timestamp dateTimeOfCreation) {
        this.dateTimeOfCreation = dateTimeOfCreation;
    }

    public void setVisitStartingTime(Timestamp visitStartingTime) {
        this.visitStartingTime = visitStartingTime;
    }

    public void setVisitCompletionTime(Timestamp visitCompletionTime) {
        this.visitCompletionTime = visitCompletionTime;
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

}
