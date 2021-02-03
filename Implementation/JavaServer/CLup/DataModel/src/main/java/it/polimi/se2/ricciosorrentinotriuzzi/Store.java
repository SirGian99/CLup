package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.*;

@Entity
@Table(name = "store")
public class Store implements Serializable {
    private static final long serialVersionUID = 1L;
    //coda non c'è
    @Id
    private String id;
    private String name;
    private String description;
    @Lob
    private byte[] image;
    private int currentOccupancy;
    private int maximumOccupancy;
    private double averageVisitDuration;
    private Double safetyThreshold;
    @ManyToOne
    @JoinColumn(name = "chain")
    private Chain chain;
    private String passepartoutuuid;
    private String passepartouthfid;
    @OneToOne
    @JoinColumn(name = "address")
    private Address address;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("desiredStartingTime ASC")
    private List<Booking> bookings;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dateTimeOfCreation ASC")
    private List<Lineup> lineups;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "storemanager",
            joinColumns = @JoinColumn(name = "store"),
            inverseJoinColumns = @JoinColumn(name = "manager"))
    private List<Manager> managers;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Productsection> productSections;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "workinghours",
            joinColumns = @JoinColumn(name = "store"),
            inverseJoinColumns = @JoinColumn(name = "dayInterval"))
    private List<Dayinterval> workingHours;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="tassaddresses",
            joinColumns=@JoinColumn(name="store")
    )
    @Column(name="uri")
    private List<String> tassAddresses;

    public Store() {id = UUID.randomUUID().toString();}

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }


    public int getCurrentOccupancy() {
        return currentOccupancy;
    }
    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }


    public int getMaximumOccupancy() {
        return maximumOccupancy;
    }
    public void setMaximumOccupancy(int maximumOccupancy) {
        this.maximumOccupancy = maximumOccupancy;
    }


    public double getAverageVisitDuration() {
        return averageVisitDuration;
    }
    public void setAverageVisitDuration(double averageVisitDuration) { this.averageVisitDuration = averageVisitDuration; }


    public Double getSafetyThreshold() {
        return safetyThreshold;
    }
    public void setSafetyThreshold(Double safetyThreshold) {
        this.safetyThreshold = safetyThreshold;
    }


    public Chain getChain() {
        return chain;
    }
    public void setChain(Chain chain) {
        this.chain = chain;
    }


    public String getPassepartoutuuid() {
        return passepartoutuuid;
    }
    public void setPassepartoutuuid(String passepartoutuuid) {
        this.passepartoutuuid = passepartoutuuid;
    }


    public String getPassepartouthfid() {
        return passepartouthfid;
    }
    public void setPassepartouthfid(String passepartouthfid) {
        this.passepartouthfid = passepartouthfid;
    }


    public List<Booking> getBookings() {
        return bookings;
    }
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }


    public List<Lineup> getLineups() {
        return lineups;
    }
    public void setLineups(List<Lineup> lineups) {
        this.lineups = lineups;
    }


    public List<Manager> getManagers() {
        return managers;
    }
    public void addManager(Manager manager) {
        if (this.managers == null) {
            this.managers = new LinkedList<>();
        }
        this.managers.add(manager);
        manager.addStore(this);
        }


    public List<Productsection> getProductSections() {
        return productSections;
    }
    public void setProductSections(List<Productsection> productSections) {
        this.productSections = productSections;
    }


    public List<Dayinterval> getWorkingHours() {
        return workingHours;
    }
    public void setWorkingHours(List<Dayinterval> workingHours) {
        this.workingHours = workingHours;
    }


    public List<String> getTassAddresses() {
        return tassAddresses;
    }
    public void setTassAddresses(List<String> tassAddresses) {
        this.tassAddresses = tassAddresses;
    }


    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isOpenAt(LocalDateTime datetime) {
        int dayOfWeek = datetime.getDayOfWeek().getValue();
        for (Dayinterval di: workingHours) {
            if (
                    dayOfWeek == di.getDayOfTheWeek() &&
                    datetime.toLocalTime().isAfter(di.getStart().toLocalTime()) &&
                    datetime.toLocalTime().isBefore(di.getEnd().toLocalTime())
            ) {
                return true;
            }
        }
        return false;
    }

}
