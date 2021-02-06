package it.polimi.se2.ricciosorrentinotriuzzi.entities;

import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "chain")

public class Chain implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String name;
    private String description;
    @Lob
    private byte[] image;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chain", cascade = { CascadeType.PERSIST, CascadeType.REMOVE,
            CascadeType.REFRESH })
    private List<Store> stores;

    public Chain() {
    }

    public Chain(String name, String description, byte[] image, List<Store> stores) {
        this.name = name;
        this.description = description;
        this.image = image;
        if (stores==null)
            this.stores = new LinkedList<>();
        else
            this.stores = stores;
    }

    public Chain(String name, String description, List<Store> stores) {
        this.name = name;
        this.description = description;
        if (stores==null)
            this.stores = new LinkedList<>();
        else
            this.stores = stores;
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

    public List<Store> getStoreList() {
        return stores;
    }

    public void setStoreList(List<Store> stores) {
        this.stores = stores;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", getName());
        json.put("description", getDescription());
        return json;
    }


}
