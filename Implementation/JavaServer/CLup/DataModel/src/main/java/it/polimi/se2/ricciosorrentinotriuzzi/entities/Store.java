package it.polimi.se2.ricciosorrentinotriuzzi.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Entity
@Table(name = "store")
public class Store implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String name;
    private String description;
    @Lob
    private byte[] image;
    private int currentOccupancy;
    private int maximumOccupancy;
    private Time averageVisitDuration;
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

    @Transient
    private Timestamp queueDisposalTime;
    @Transient
    private int queueLength;

    public Store() {
        this.id = UUID.randomUUID().toString();
    }

    public Store(String name, String description, int currentOccupancy, int maximumOccupancy, Time averageVisitDuration, Double safetyThreshold, Chain chain, Address address, List<Manager> managers, List<Productsection> productSections, List<Dayinterval> workingHours, List<String> tassAddresses) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.currentOccupancy = currentOccupancy;
        this.averageVisitDuration = averageVisitDuration;
        this.safetyThreshold = safetyThreshold;
        this.chain = chain;
        this.address = address;
        this.bookings = listInit(null);
        this.lineups = listInit(null);
        this.managers = listInit(managers);
        this.productSections = listInit(productSections);
        this.maximumOccupancy = 0;
        for (Productsection productSection : this.productSections) {
            this.maximumOccupancy += productSection.getMaximumOccupancy();
        }
        this.maximumOccupancy = this.maximumOccupancy==0 ? maximumOccupancy : this.maximumOccupancy;
        this.workingHours = listInit(workingHours);
        this.tassAddresses = listInit(tassAddresses);
    }

     private <E> List<E> listInit(List<E> list){
        return list == null ? new LinkedList<E>():list;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }


    public void setAverageVisitDuration(Time averageVisitDuration) {
        this.averageVisitDuration = averageVisitDuration;
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


    public Time getAverageVisitDuration() {
        return averageVisitDuration;
    }


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
    public void addBooking(Booking b) {
        if (this.bookings == null) {
            this.bookings = new LinkedList<>();
        }
        bookings.add(b);
    }


    public List<Lineup> getLineups() {
        return lineups;
    }
    public void setLineups(List<Lineup> lineups) {
        this.lineups = lineups;
    }
    public void addLineup(Lineup l) {
        if (this.lineups == null) {
            this.lineups = new LinkedList<>();
        }
        lineups.add(l);
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

    public void addWorkingHour(Dayinterval workingHours) {
        this.workingHours.add(workingHours);
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
        address.setStore(this);
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

    public boolean isOpenAt(LocalDateTime datetime, LocalTime duration) {
        int dayOfWeek = datetime.getDayOfWeek().getValue();
        for (Dayinterval di: workingHours) {
            if (
                dayOfWeek == di.getDayOfTheWeek() &&
                datetime.toLocalTime().isAfter(di.getStart().toLocalTime()) &&
                datetime.toLocalTime().plusHours(duration.getHour()).plusMinutes(duration.getMinute()).isBefore(di.getEnd().toLocalTime())
            ) {
                return true;
            }
        }
        return false;
    }

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        if(getAddress() != null)
            json.put("address", getAddress().toJson());
        json.put("name", getName());
        json.put("id", getId());
        json.put("description", getDescription());
        if(getChain() != null)
            json.put("chainName", getChain().getName());
        json.put("currentOccupancy", getCurrentOccupancy());
        json.put("maximumOccupancy", getMaximumOccupancy());
        //TODO CONTROLLA CHE SIANO SENSATI
        json.put("estimatedQueueDisposalTime", queueDisposalTime);
        json.put("queueLenght", queueLength);
        json.put("averageVisitDuration", getAverageVisitDuration());
        json.put("safetyThreshold", getSafetyThreshold());
        JSONArray productSections = new JSONArray();
        for (Productsection ps: getProductSections())
            productSections.put(ps.toJson());
        json.put("productSections", productSections);
        JSONArray workingHours = new JSONArray();
        for (Dayinterval di : getWorkingHours())
            workingHours.put(di.toJson());
        json.put("workingHours", workingHours);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return id.equals(store.id);
    }

    public Timestamp getQueueDisposalTime() {
        return queueDisposalTime;
    }

    public void setQueueDisposalTime(Timestamp queueDisposalTime) {
        this.queueDisposalTime = queueDisposalTime;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }
}
