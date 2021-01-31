package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "store")
public class Store implements Serializable {
    private static final long serialVersionUID = 1L;
    //coda non c'è
    @Id
    private String id;
    private String name;
    private String description;
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
    private Integer address;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lineup> lineups;
    @ManyToMany
    @JoinTable(name = "storemanager",
            joinColumns = @JoinColumn(name = "store"),
            inverseJoinColumns = @JoinColumn(name = "manager"))
    private List<Manager> managers;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Productsection> productSections;
    @ManyToMany
    @JoinTable(name = "workinghours",
            joinColumns = @JoinColumn(name = "store"),
            inverseJoinColumns = @JoinColumn(name = "dayInterval"))
    private List<Dayinterval> workingHours;

    @ElementCollection
    @CollectionTable(
            name="tassaddresses",
            joinColumns=@JoinColumn(name="store")
    )
    @Column(name="uri")
    private List<String> tassAddresses;




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Lob
    @Column(name = "image")
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Basic
    @Column(name = "currentOccupancy")
    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    @Basic
    @Column(name = "maximumOccupancy")
    public int getMaximumOccupancy() {
        return maximumOccupancy;
    }

    public void setMaximumOccupancy(int maximumOccupancy) {
        this.maximumOccupancy = maximumOccupancy;
    }

    @Basic
    @Column(name = "averageVisitDuration")
    public double getAverageVisitDuration() {
        return averageVisitDuration;
    }

    public void setAverageVisitDuration(double averageVisitDuration) {
        this.averageVisitDuration = averageVisitDuration;
    }

    @Basic
    @Column(name = "safetyThreshold")
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

    public void setManagers(List<Manager> managers) {
        this.managers = managers;
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

    @Basic
    @Column(name = "address")
    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }


}
