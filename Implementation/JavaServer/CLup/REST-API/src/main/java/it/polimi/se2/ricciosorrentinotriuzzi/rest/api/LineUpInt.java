package it.polimi.se2.ricciosorrentinotriuzzi.rest.api;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.RequestHandler;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Lineup;
import org.json.JSONObject;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/lineup")
public class LineUpInt {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.business.components.RequestHandler")
    RequestHandler rh;

    @POST
    @Path("")
    @Consumes("application/json")
    @Produces("application/json")
    public Response lineup(String body) {
        JSONObject json = new JSONObject(body);
        String customerID = json.getString("customerID");
        String storeID = json.getString("storeID");
        int numberOfPeople = json.getInt("numberOfPeople");
        System.out.println("Making lur of "+customerID+"\nfor store: "+storeID+"\nfor "+numberOfPeople);
        Lineup lur = rh.lineup(numberOfPeople, customerID, storeID);
        if (lur != null) {
            System.out.println("LUR accepted");
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("validated", true);
            jsonResponse.put("estimatedTimeOfEntrance", lur.getEstimatedTimeOfEntrance());
            JSONObject jsonVisitToken = new JSONObject();
            jsonVisitToken.put("uuid", lur.getUuid());
            jsonVisitToken.put("hfid", lur.getHfid());
            jsonResponse.put("visitToken",jsonVisitToken);
            return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
        } else {
            System.out.println("LUR rejected");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @DELETE
    @Path("/{token}")
    public Response deleteLineup(@PathParam("token") String uuid)  {
        rh.cancelRequest(uuid);
        return Response.ok().build();
    }
}