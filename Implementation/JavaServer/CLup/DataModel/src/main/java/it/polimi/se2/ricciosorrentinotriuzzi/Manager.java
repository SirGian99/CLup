package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "manager")

public class Manager implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String username;
    private String password;
    private String name;
    @ManyToMany
    @JoinTable(name = "storemanager",
            joinColumns = @JoinColumn(name = "manager"),
            inverseJoinColumns = @JoinColumn(name = "store"))
    private List<Store> stores;


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


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
