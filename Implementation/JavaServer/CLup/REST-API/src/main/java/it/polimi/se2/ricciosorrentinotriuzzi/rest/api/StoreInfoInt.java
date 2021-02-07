package it.polimi.se2.ricciosorrentinotriuzzi.rest.api;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.StoreStatusHandler;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.*;
import org.json.*;

import java.util.*;


@Path("/")
public class StoreInfoInt {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.business.components.StoreStatusHandler")
    private StoreStatusHandler ssh;

    @GET
    @Path("store/{storeID}/generalInfo")
    @Produces("application/json")
    public Response getStoreGeneralInfo(@PathParam("storeID") String storeID) {
        Store s = ssh.getStoreGeneralInfo(storeID);
        return Response.ok().entity(s.toJson().toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("chainstore")
    @Produces("application/json")
    public Response getChainsAndAutonomousStores(@QueryParam("city") String city) {
        Set<Chain> chains = ssh.getChains(city);
        List<Store> autstores = ssh.getAutonomousStores(city);
        JSONObject jsonResponse = new JSONObject();
        JSONArray chainsJArray = new JSONArray();
        JSONArray autstoresJArray = new JSONArray();
        for (Chain c : chains) {
            chainsJArray.put(c.toJson());
        }
        for (Store s : autstores) {
            autstoresJArray.put(s.toJson());
        }
        jsonResponse.put("chains", chainsJArray);
        jsonResponse.put("autonomousStores", autstoresJArray);
        return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
    }


    @GET
    @Path("chain/{name}/stores")
    @Produces("application/json")
    public Response getChainStores(@PathParam("name") String chainName, @QueryParam("city") String city) {
        List<Store> stores = ssh.getChainStores(chainName, city);
        JSONObject jsonResponse = new JSONObject();
        JSONArray storesJArray = new JSONArray();
        for (Store s : stores) {
            storesJArray.put(s.toJson());
        }
        jsonResponse.put("stores",storesJArray);
        return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
    }


    @GET
    @Path("store/{storeID}/activeRequests")
    @Produces("application/json")
    public Response getVisitsInProgress(@PathParam("storeID") String store) {
        List<VisitRequest> requests = ssh.getActiveRequests(store);
        if (requests != null) {
            JSONObject jsonResponse = getJsonFromRequests(requests);
            return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
        } else {
            System.out.println("Error while getting visits in progress");
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
