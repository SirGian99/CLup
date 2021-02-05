package it.polimi.se2.ricciosorrentinotriuzzi.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "manager")
@NamedQuery(name = "Manager.findByUsername", query = "SELECT m FROM Manager m where m.username = ?1")

public class Manager implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String username;
    private String password;
    private String name;
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "managers")
    private List<Store> stores;

    public Manager() {id = UUID.randomUUID().toString();}

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public List<Store> getStores() {
        return stores;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void addStore(Store store){
        if (this.stores == null){
            this.stores = new LinkedList<>();
        }
        stores.add(store);
    }
}
