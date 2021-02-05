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
}