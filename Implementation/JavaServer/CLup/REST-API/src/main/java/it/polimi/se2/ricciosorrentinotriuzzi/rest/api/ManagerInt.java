package it.polimi.se2.ricciosorrentinotriuzzi.rest.api;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.ManagerController;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Booking;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Lineup;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.VisitRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    @Path("store/{storeID}/bookings")
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
    @Path("store/{storeID}/completedVisits")
    @Produces("application/json")
    public Response getCompletedVisits(@PathParam("storeID") String store)  {
        List<VisitRequest> requests = mc.getCompletedVisits(store);
        if (requests != null) {
            JSONObject jsonResponse = getJsonFromRequests(requests);
            return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
        } else {
            System.out.println("Error while getting future bookings");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @GET
    @Path("store/{storeID}/currentVisits")
    @Produces("application/json")
    public Response getCurrentVisits(@PathParam("storeID") String store) {
        List<VisitRequest> requests = mc.getCurrentVisits(store);
        if (requests != null) {
            JSONObject jsonResponse = getJsonFromRequests(requests);
            return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
        } else {
            System.out.println("Error while getting future bookings");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    private JSONObject getJsonFromRequests(List<VisitRequest> requests) {
            JSONObject json = new JSONObject();
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
        json.put("visitRequests", toAppend);
            return json;
        }
    }

    /* ALTERNATIVA a getJsonFromRequests
        JSONObject jsonResponse = new JSONObject();
        JSONArray toAppend = new JSONArray();
        for (VisitRequest vr : requests)
            if(vr.isBooking())
                toAppend.put(((Booking) vr).toJson());
            else
                toAppend.put(((Lineup) vr).toJson());
        jsonResponse.put("visitRequests", toAppend);
     */