package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
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
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking b) {
        if (this.bookings == null) {
            this.bookings = new LinkedList<>();
        }
        bookings.add(b);
    }

    public List<Lineup> getLineups() { return lineups; }
    public void addLineup(Lineup l) {
        if (this.lineups == null) {
            this.lineups = new LinkedList<>();
        }
        lineups.add(l);
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void setLineups(List<Lineup> lineups) {
        this.lineups = lineups;
    }
}
