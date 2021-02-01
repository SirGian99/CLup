package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "customer")
//////provare a vedere questione proxy con named query
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Byte isAppCustomer;
    @OneToMany(mappedBy = "appCustomer")
    private List<Attitude> attitudes;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "appCustomer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lineup> lineups;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "isAppCustomer")
    public Byte getIsAppCustomer() {
        return isAppCustomer;
    }

    public void setIsAppCustomer(Byte isAppCustomer) {
        this.isAppCustomer = isAppCustomer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != null ? !id.equals(customer.id) : customer.id != null) return false;
        if (isAppCustomer != null ? !isAppCustomer.equals(customer.isAppCustomer) : customer.isAppCustomer != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (isAppCustomer != null ? isAppCustomer.hashCode() : 0);
        return result;
    }
}
