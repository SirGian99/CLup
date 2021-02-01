package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "customer")
//////provare a vedere questione proxy con named query
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private Byte isAppCustomer;
    @OneToMany(mappedBy = "appCustomer")
    private List<Attitude> attitudes;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "appCustomer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lineup> lineups;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }


    public Byte getIsAppCustomer() {
        return isAppCustomer;
    }
    public void setIsAppCustomer(Byte isAppCustomer) {
        this.isAppCustomer = isAppCustomer;
    }
}
