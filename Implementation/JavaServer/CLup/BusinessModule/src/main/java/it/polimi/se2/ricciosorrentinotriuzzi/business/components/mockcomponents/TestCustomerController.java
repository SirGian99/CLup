package it.polimi.se2.ricciosorrentinotriuzzi.business.components.mockcomponents;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.CustomerController;
import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Booking;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Customer;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Lineup;

import java.util.List;

public class TestCustomerController extends CustomerController {

    public TestCustomerController(DataModel dataModel) {
        super();
        this.dataModel = dataModel;
    }

    @Override
    public Customer registerApp(String appid) {
        return super.registerApp(appid);
    }

    @Override
    public Customer getCustomerByID(String customerID) {
        return super.getCustomerByID(customerID);
    }

    @Override
    public List<Booking> getCustomerActiveBookings(String customerID) {
        return super.getCustomerActiveBookings(customerID);
    }

    @Override
    public List<Lineup> getCustomerActiveLineups(String customerID) {
        return super.getCustomerActiveLineups(customerID);
    }
}