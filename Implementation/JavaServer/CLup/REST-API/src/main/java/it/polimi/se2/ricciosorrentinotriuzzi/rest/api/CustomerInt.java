package it.polimi.se2.ricciosorrentinotriuzzi.rest.api;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.CustomerController;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Booking;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Customer;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Lineup;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/customer/")

public class CustomerInt {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.business.components.CustomerController")
    private CustomerController cc;

    @PUT
    @Path("registerApp")
    @Consumes("application/json")
    @Produces("application/json")
    public Response registerApp(String body)  {
        JSONObject json = new JSONObject(body);
        String appid = json.getString("appID");
        System.out.println("Registering app customer with id: "+appid);
        Customer c = cc.registerApp(appid);
        if (c != null) {
            System.out.println("Success while registering appcustomer");
            return Response.ok().build();
        } else {
            System.out.println("Error while registering appcustomer");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getCustomerRequests(@PathParam("id") String customerID) {
        List<Booking> bookings = cc.getCustomerActiveBookings(customerID);
        List<Lineup> lineups = cc.getCustomerActiveLineups(customerID);
        if(bookings == null || lineups == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        System.out.println("Requests of "+customerID);
        JSONObject jsonResponse = new JSONObject();
        JSONArray toAppend = new JSONArray();
        for(Booking b : bookings) {
            toAppend.put(b.toJson());
        }
        System.out.println("Bookings:\n"+toAppend);
        jsonResponse.put("bookingRequests", toAppend);
        toAppend = new JSONArray();
        for(Lineup lu : lineups) {
            toAppend.put(lu.toJson());
        }
        System.out.println("LUR:\n"+toAppend);
        jsonResponse.put("lineupRequests", toAppend);
        return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
    }

}