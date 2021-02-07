package it.polimi.se2.ricciosorrentinotriuzzi.rest.api;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.VisitManager;
import org.json.JSONObject;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/")
public class AccessControlInt {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.business.components.VisitManager")
    private VisitManager visitManager;

    @POST
    @Path("access/request")
    @Consumes("application/json")
    @Produces("application/json")
    public Response makeAccessRequest(String body){
        JSONObject json = new JSONObject(body);
        String token = json.getString("token");
        String storeID = json.getString("storeID");
        //System.out.println(token + "\npoi\n" + storeID);
        int numOfPeople = visitManager.validateAccess(token, storeID);
        if (numOfPeople>0){
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("validated", true);
            jsonResponse.put("numberOfPeople", numOfPeople);
            return Response.status(Response.Status.OK).entity(jsonResponse.toString())
                    .type(MediaType.APPLICATION_JSON).build();
        }else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @POST
    @Path("exit/request")
    @Consumes("application/json")
    @Produces("application/json")
    public Response makeExitRequest(String body){
        JSONObject json = new JSONObject(body);
        String token = json.getString("token");
        String storeID = json.getString("storeID");
        int numOfPeople = visitManager.validateExit(token, storeID);
        System.out.println("Ho ricevuto la richiesta, num:" + numOfPeople);
        if (numOfPeople>0){
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("validated", true);
            jsonResponse.put("numberOfPeople", numOfPeople);
            return Response.status(Response.Status.OK).entity(jsonResponse.toString())
                    .type(MediaType.APPLICATION_JSON).build();
        }else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @POST
    @Path("access/confirm")
    @Consumes("application/json")
    @Produces("application/json")
    public Response confirmAccessRequest(String body){
        JSONObject json = new JSONObject(body);
        String token = json.getString("token");
        String storeID = json.getString("storeID");
        int numberOfPeople = json.getInt("numberOfPeople");
        return numberOfPeople > 0 && visitManager.confirmAccess(token, storeID, numberOfPeople) ? Response.status(Response.Status.OK).build() : Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

    @POST
    @Path("exit/confirm")
    @Consumes("application/json")
    @Produces("application/json")
    public Response confirmExitRequest(String body){
        JSONObject json = new JSONObject(body);
        String token = json.getString("token");
        String storeID = json.getString("storeID");
        int numberOfPeople = json.getInt("numberOfPeople");
        return numberOfPeople > 0 && visitManager.confirmExit(token, storeID, numberOfPeople) ? Response.status(Response.Status.OK).build() : Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }
}