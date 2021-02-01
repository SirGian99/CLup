package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.*;
import java.util.Date;
import java.util.List;

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
    private Integer state;
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

    @Basic
    @Column(name = "numberOfPeople")
    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    @Basic
    @Column(name = "state")
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Basic
    @Column(name = "dateTimeOfCreation")
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

    public void setDesiredStartingTime(Timestamp desiredStartingTime) {
        this.desiredStartingTime = desiredStartingTime;
    }

    public Time getDesiredDuration() {
        return desiredDuration;
    }

    public void setDesiredDuration(Time desiredDuration) {
        this.desiredDuration = desiredDuration;
    }

    @Basic
    @Column(name = "visitStartingTime")
    public Timestamp getVisitStartingTime() {
        return visitStartingTime;
    }

    public void setVisitStartingTime(Timestamp visitStartingTime) {
        this.visitStartingTime = visitStartingTime;
    }

    @Basic
    @Column(name = "visitCompletionTime")
    public Timestamp getVisitCompletionTime() {
        return visitCompletionTime;
    }

    public void setVisitCompletionTime(Timestamp visitCompletionTime) {
        this.visitCompletionTime = visitCompletionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Booking booking = (Booking) o;

        if (uuid != null ? !uuid.equals(booking.uuid) : booking.uuid != null) return false;
        if (store != null ? !store.equals(booking.store) : booking.store != null) return false;
        if (appCustomer != null ? !appCustomer.equals(booking.appCustomer) : booking.appCustomer != null) return false;
        if (numberOfPeople != null ? !numberOfPeople.equals(booking.numberOfPeople) : booking.numberOfPeople != null)
            return false;
        if (state != null ? !state.equals(booking.state) : booking.state != null) return false;
        if (dateTimeOfCreation != null ? !dateTimeOfCreation.equals(booking.dateTimeOfCreation) : booking.dateTimeOfCreation != null)
            return false;
        if (visitStartingTime != null ? !visitStartingTime.equals(booking.visitStartingTime) : booking.visitStartingTime != null)
            return false;
        if (visitCompletionTime != null ? !visitCompletionTime.equals(booking.visitCompletionTime) : booking.visitCompletionTime != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (store != null ? store.hashCode() : 0);
        result = 31 * result + (appCustomer != null ? appCustomer.hashCode() : 0);
        result = 31 * result + (numberOfPeople != null ? numberOfPeople.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (dateTimeOfCreation != null ? dateTimeOfCreation.hashCode() : 0);
        result = 31 * result + (visitStartingTime != null ? visitStartingTime.hashCode() : 0);
        result = 31 * result + (visitCompletionTime != null ? visitCompletionTime.hashCode() : 0);
        return result;
    }
}
