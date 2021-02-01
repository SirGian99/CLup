package it.polimi.se2.ricciosorrentinotriuzzi;

import java.sql.Timestamp;

public abstract class VisitRequest {
    private String uuid;
    private String hfid;
    private Store store;
    private Customer customer;
    private Integer numberOfPeople;
    private Integer state;
    private Timestamp dateTimeOfCreation;
    private Timestamp visitStartingTime;
    private Timestamp visitCompletionTime;



}
