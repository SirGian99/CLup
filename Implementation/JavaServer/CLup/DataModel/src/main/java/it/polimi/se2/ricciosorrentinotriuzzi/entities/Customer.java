package it.polimi.se2.ricciosorrentinotriuzzi.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "customer")
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

    public Customer() {
    }

    public Customer(String id, boolean isAppCustomer) {
        this.id = id;
        this.isAppCustomer = isAppCustomer ? (byte) 1 : (byte) 0;
        this.attitudes = new LinkedList<>();
        this.bookings = new LinkedList<>();
        this.lineups = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public boolean isAppCustomer() {
        return isAppCustomer.equals((byte) (1));
    }

    public void setIsAppCustomer(boolean isAppCustomer) {
        this.isAppCustomer = isAppCustomer ? (byte) 1 : (byte) 0;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking b) {
        if (this.bookings == null) {
            this.bookings = new LinkedList<>();
        }
        bookings.add(b);
    }

    public List<Lineup> getLineups() {
        return lineups;
    }

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
