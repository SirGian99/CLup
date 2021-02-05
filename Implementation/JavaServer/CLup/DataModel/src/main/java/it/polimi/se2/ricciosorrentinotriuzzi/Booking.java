package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class Booking extends VisitRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String uuid;
    private String hfid;
    @ManyToOne(fetch = FetchType.EAGER)
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
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "bookingproduct",
                joinColumns = @JoinColumn(name = "booking"),
                inverseJoinColumns = @JoinColumn(name = "productSection"))
    private List<Productsection> productSections;

    public Booking() {
        uuid = UUID.randomUUID().toString();
        productSections = new LinkedList<>();
    }

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
        store.addBooking(this);
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
    public void setHfid(String hfid) {
        this.hfid = hfid;
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
    public void addProductSection(Productsection ps) {
        productSections.add(ps);
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
    public boolean isPending() {
        return (state == VisitRequestStatus.PENDING);
    }
    @Override
    public boolean isReady() {
        return (state == VisitRequestStatus.READY);
    }
    @Override
    public boolean isFulfilled() {
        return (state == VisitRequestStatus.FULFILLED);
    }
    @Override
    public boolean isCompleted() {
        return (state == VisitRequestStatus.COMPLETED);
    }
    @Override
    public boolean isActive() {
        return (state != VisitRequestStatus.COMPLETED);
    }

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        JSONObject jsonVisitToken = new JSONObject();
        jsonVisitToken.put("uuid", getUuid());
        jsonVisitToken.put("hfid", getHfid());
        json.put("visitToken",jsonVisitToken);
        json.put("storeID", getStore().getId());
        json.put("customerID", getCustomer().getId());
        json.put("numberOfPeople", getNumberOfPeople());
        JSONObject jsonTimeInterval = new JSONObject();
        jsonTimeInterval.put("start", getDesiredStartingTime());
        jsonTimeInterval.put("duration", getDesiredDuration());
        json.put("desiredTimeInterval", jsonTimeInterval);
        json.put("state", getState().getValue());
        JSONArray jsonProductSections = new JSONArray();
        for(Productsection ps : getProductSections())
            jsonProductSections.put(ps.getName());
        json.put("productSectionsNames", jsonProductSections);
        return json;
    }

    @Override
    public boolean isBooking(){
        return true;
    }
}
