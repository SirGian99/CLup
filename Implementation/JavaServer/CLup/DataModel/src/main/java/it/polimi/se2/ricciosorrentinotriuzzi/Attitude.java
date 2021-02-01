package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;

@Entity
@Table(name = "attitude")

public class Attitude implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "appCustomer")
    private Customer appCustomer;
    @ManyToOne
    @JoinColumn (name = "store")
    private Store store;
    private Time averageVisitDuration;

    @Column(name = "appCustomer")
    public Customer getAppCustomer() {
        return appCustomer;
    }

    public void setAppCustomer(Customer appCustomer) {
        this.appCustomer = appCustomer;
    }

    @Column(name = "store")
    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @Basic
    @Column(name = "averageVisitDuration")
    public Time getAverageVisitDuration() {
        return averageVisitDuration;
    }

    public void setAverageVisitDuration(Time averageVisitDuration) {
        this.averageVisitDuration = averageVisitDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attitude attitude = (Attitude) o;

        if (appCustomer != null ? !appCustomer.equals(attitude.appCustomer) : attitude.appCustomer != null)
            return false;
        if (store != null ? !store.equals(attitude.store) : attitude.store != null) return false;
        if (averageVisitDuration != null ? !averageVisitDuration.equals(attitude.averageVisitDuration) : attitude.averageVisitDuration != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = appCustomer != null ? appCustomer.hashCode() : 0;
        result = 31 * result + (store != null ? store.hashCode() : 0);
        result = 31 * result + (averageVisitDuration != null ? averageVisitDuration.hashCode() : 0);
        return result;
    }
}
