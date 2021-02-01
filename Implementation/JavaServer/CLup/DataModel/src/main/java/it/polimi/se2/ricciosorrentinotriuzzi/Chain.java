package it.polimi.se2.ricciosorrentinotriuzzi;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "chain")
public class Chain implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private byte[] image;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chain", cascade = { CascadeType.PERSIST, CascadeType.REMOVE,
            CascadeType.REFRESH })
    private List<Store> stores;

    @Id
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

    @Basic
    @Column(name = "image")
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chain chain = (Chain) o;

        if (name != null ? !name.equals(chain.name) : chain.name != null) return false;
        if (description != null ? !description.equals(chain.description) : chain.description != null) return false;
        if (!Arrays.equals(image, chain.image)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
