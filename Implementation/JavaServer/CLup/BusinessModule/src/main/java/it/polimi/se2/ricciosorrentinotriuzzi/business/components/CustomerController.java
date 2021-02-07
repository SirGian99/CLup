package it.polimi.se2.ricciosorrentinotriuzzi.business.components;

import it.polimi.se2.ricciosorrentinotriuzzi.entities.Booking;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Customer;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Lineup;
import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.LinkedList;
import java.util.List;

@Stateless
public class CustomerController {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.component/DataModel")
    protected DataModel dataModel;

    public CustomerController() {
    }

    public Customer registerApp(String appid) {
        return dataModel.newAppCustomer(appid);
    }

    public Customer getCustomerByID(String customerID) {
        return dataModel.getCustomer(customerID);
    }

    public List<Booking> getCustomerActiveBookings(String customerID) {
        Customer customer = dataModel.getCustomer(customerID);
        if (customer == null) return null;
        List<Booking> activeBookings = new LinkedList<>();
        for (Booking booking : customer.getBookings())
            if (booking.isActive())
                activeBookings.add(booking);
        return activeBookings;
    }

    public List<Lineup> getCustomerActiveLineups(String customerID) {
        Customer customer = dataModel.getCustomer(customerID);
        if (customer == null) return null;
        List<Lineup> activeLineups = new LinkedList<>();
        for (Lineup lineup : customer.getLineups())
            if (lineup.isActive())
                activeLineups.add(lineup);
        return activeLineups;
    }
}
