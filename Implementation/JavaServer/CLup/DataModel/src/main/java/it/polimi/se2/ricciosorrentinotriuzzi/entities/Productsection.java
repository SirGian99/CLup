package it.polimi.se2.ricciosorrentinotriuzzi.entities;

import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "productsection")

public class Productsection implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "store")
    private Store store;
    private String name;
    private Double currentOccupancy;
    private Integer maximumOccupancy;

    public Productsection() {
    }

    public Productsection( Store store, String name, Double currentOccupancy, Integer maximumOccupancy) {
        this.store = store;
        this.name = name;
        this.currentOccupancy = currentOccupancy;
        this.maximumOccupancy = maximumOccupancy;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Store getStore() {
        return store;
    }
    public void setStore(Store store) {
        this.store = store;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public Double getCurrentOccupancy() {
        return currentOccupancy;
    }
    public void setCurrentOccupancy(Double currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }


    public Integer getMaximumOccupancy() {
        return maximumOccupancy;
    }
    public void setMaximumOccupancy(Integer maximumOccupancy) {
        this.maximumOccupancy = maximumOccupancy;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", getId());
        json.put("name", getName());
        json.put("currentOccupancy", getCurrentOccupancy());
        json.put("maximumOccupancy", getMaximumOccupancy());
        return json;
    }
}
