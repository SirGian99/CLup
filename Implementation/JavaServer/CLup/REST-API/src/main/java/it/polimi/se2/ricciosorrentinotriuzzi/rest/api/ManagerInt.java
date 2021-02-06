package it.polimi.se2.ricciosorrentinotriuzzi.rest.api;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.ManagerController;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Booking;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Customer;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Lineup;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.VisitRequest;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Path("/manager/")

public class ManagerInt {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.business.components.ManagerController")
    private ManagerController mc;

    @GET
    @Path("{id}/store/{storeID}/queue")
    @Produces("application/json")
    public Response getQueue(@PathParam("storeID") String store)  {
        List<Lineup> queue = mc.getStoreQueue(store);
        if(queue != null) {
            JSONObject jsonResponse = new JSONObject();
            JSONArray toAppend = new JSONArray();
            jsonResponse.put("queueLength", queue.size());
            for (Lineup lu : queue)
                toAppend.put(lu.toJson());
            jsonResponse.put("queue", toAppend);
            return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
        } else {
            System.out.println("Error while getting queue");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @GET
    @Path("{storeID}/bookings")
    @Produces("application/json")
    public Response getNextBookings(@PathParam("storeID") String store)  {
        List<Booking> bookings = mc.getPendingBookings(store);
        if(bookings != null) {
            JSONObject jsonResponse = new JSONObject();
            JSONArray toAppend = new JSONArray();
            for (Booking b : bookings)
                toAppend.put(b.toJson());
            jsonResponse.put("bookings", toAppend);
            return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
        } else {
            System.out.println("Error while getting future bookings");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @GET
    @Path("{storeID}/currentVisits")
    @Produces("application/json")
    public Response getCurrentVisits(@PathParam("storeID") String store)  {
        List<VisitRequest> requests = mc.getCurrentVisits(store);
        if(requests != null) {
            JSONObject jsonResponse = new JSONObject();
            JSONObject toAppend = new JSONObject();
            JSONArray bookings = new JSONArray();
            JSONArray lineups = new JSONArray();
            for (VisitRequest vr : requests) {
                if (vr.isBooking())
                    bookings.put(((Booking) vr).toJson());
                else
                    lineups.put(((Lineup) vr).toJson());
            }
            toAppend.put("bookings", bookings);
            toAppend.put("lineups", lineups);
            jsonResponse.put("visitRequests", toAppend);
            return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
        } else {
            System.out.println("Error while getting future bookings");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    /* ALTERNATIVA a getCurrentVisits
    public Response getCurrentVisits(@PathParam("storeID") String store)  {
        List<VisitRequest> requests = mc.getCurrentVisits(store);
        if(requests != null) {
            JSONObject jsonResponse = new JSONObject();
            JSONArray toAppend = new JSONArray();
            for (VisitRequest vr : requests)
                if(vr.isBooking())
                    toAppend.put(((Booking) vr).toJson());
                else
                    toAppend.put(((Lineup) vr).toJson());
            jsonResponse.put("visitRequests", toAppend);
            return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
        } else {
            System.out.println("Error while getting future bookings");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }
     */

}
    /*
    @GET
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

     */
