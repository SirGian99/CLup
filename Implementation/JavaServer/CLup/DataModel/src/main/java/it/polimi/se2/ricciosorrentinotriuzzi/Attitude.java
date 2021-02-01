package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;

@Entity
@Table(name = "attitude")

public class Attitude implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "appCustomer")
    private Customer appCustomer;
    @ManyToOne
    @JoinColumn (name = "store")
    private Store store;
    private Time averageVisitDuration;


    public Customer getAppCustomer() {
        return appCustomer;
    }
    public void setAppCustomer(Customer appCustomer) {
        this.appCustomer = appCustomer;
    }


    public Store getStore() {
        return store;
    }
    public void setStore(Store store) {
        this.store = store;
    }


    public Time getAverageVisitDuration() {
        return averageVisitDuration;
    }
    public void setAverageVisitDuration(Time averageVisitDuration) {
        this.averageVisitDuration = averageVisitDuration;
    }
}
