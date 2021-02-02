import it.polimi.se2.ricciosorrentinotriuzzi.*;
import org.json.JSONObject;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class LineUpInt {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.RequestHandler")
    RequestHandler rh;

    @POST
    @Path("lineup")
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
            return Response.ok().entity(jsonResponse).type(MediaType.APPLICATION_JSON).build();
        } else {
            System.out.println("LUR rejected");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("lineup/{token}")
    public Response deleteLineup(@PathParam("token") String uuid)  {
        rh.cancelLineup(uuid);
        return Response.ok().build();
    }
}