package it.polimi.se2.ricciosorrentinotriuzzi.component.mockcomponent;

import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class TestDataModel extends DataModel {
    public TestDataModel() {
        super();
        this.em = Persistence.createEntityManagerFactory("PCLup").createEntityManager();
        }

    public EntityManager getEm() {
        return em;
    }
    public void dbInit(){
        em.createQuery("DELETE FROM Lineup").executeUpdate();
        em.createQuery("DELETE FROM Booking ").executeUpdate();
        em.createQuery("DELETE FROM Productsection").executeUpdate();
        em.createQuery("DELETE FROM Customer").executeUpdate();
        em.createQuery("DELETE FROM Store").executeUpdate();
        em.createQuery("DELETE FROM Address").executeUpdate();
        em.createQuery("DELETE FROM Dayinterval ").executeUpdate();
        em.createQuery("DELETE FROM Chain").executeUpdate();
        em.createQuery("DELETE FROM Manager ").executeUpdate();
    }
}