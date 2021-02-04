package it.polimi.se2.ricciosorrentinotriuzzi;

import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "address")
@NamedQuery(name = "Address.getAllByCity", query = "SELECT a FROM Address a WHERE a.city LIKE ?1")

public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String streetName;
    private String streetNumber;
    private String city;
    private String postalCode;
    private String country;
    @OneToOne(mappedBy = "address")
    private Store store;

    public long getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


    public String getStreetName() {
        return streetName;
    }
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }


    public String getStreetNumber() {
        return streetNumber;
    }
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }


    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }


    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }


    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }


    public Store getStore() {
        return store;
    }
    public void setStore(Store store) {
        this.store = store;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", getId());
        json.put("streetName", getStreetName());
        json.put("streetNumber", getStreetNumber());
        json.put("postalCode", getPostalCode());
        json.put("city", getCity());
        json.put("country", getId());
        return json;
    }
}
