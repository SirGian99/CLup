import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/")

public class AccessControlInt {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi/VisitManager")
    private VisitManager visitManager;

    @POST
    @Path("access/request")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response makeAccessRequest(String body){
        JSONObject json = new JSONObject(body);
        String token = json.getString("token");
        String storeID = json.getString("storeID");
        //System.out.println(token + "\npoi\n" + storeID);
        int numOfPeople = visitManager.validateAccess(token, storeID);
        //System.out.println("Ho ricevuto la richiesta, num:" + numOfPeople);
        if (numOfPeople>0){
            return Response.status(Response.Status.OK).entity("{\" validated \" : \"true\", \"numberOfPeople\" : " + numOfPeople + "}")
                    .type(MediaType.APPLICATION_JSON).build();
        }else {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("exit/request")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response makeExitRequest(String body){
        JSONObject json = new JSONObject(body);
        String token = json.getString("token");
        String storeID = json.getString("storeID");
        int numOfPeople = visitManager.validateExit(token, storeID);
        System.out.println("Ho ricevuto la richiesta, num:" + numOfPeople);
        if (numOfPeople>0){
            return Response.status(Response.Status.OK).entity("{\" validated \" : \"true\", \"numberOfPeople\" : " + numOfPeople + "}")
                    .type(MediaType.APPLICATION_JSON).build();
        }else {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("access/confirm")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmAccessRequest(String body){
        JSONObject json = new JSONObject(body);
        String token = json.getString("token");
        String storeID = json.getString("storeID");
        int numberOfPeople = json.getInt("numberOfPeople");
        return numberOfPeople > 0 && visitManager.confirmAccess(token, storeID, numberOfPeople) ? Response.status(Response.Status.OK).build() : Response.serverError().build();
    }

    @POST
    @Path("exit/confirm")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmExitRequest(String body){
        JSONObject json = new JSONObject(body);
        String token = json.getString("token");
        String storeID = json.getString("storeID");
        int numberOfPeople = json.getInt("numberOfPeople");
        return numberOfPeople > 0 && visitManager.confirmExit(token, storeID, numberOfPeople) ? Response.status(Response.Status.OK).build() : Response.serverError().build();
    }
}