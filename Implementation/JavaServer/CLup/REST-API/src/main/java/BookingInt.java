import it.polimi.se2.ricciosorrentinotriuzzi.*;
import org.json.*;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

@Path("/booking/")
public class BookingInt {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.RequestHandler")
    RequestHandler rh;

    @POST
    @Path("")
    @Consumes("application/json")
    @Produces("application/json")
    public Response booking(String body) {
        JSONObject json = new JSONObject(body);
        System.out.println("\n\nIl body:\n"+body+"\n\n");
        String customerID = json.getString("customerID");
        String storeID = json.getString("storeID");
        int numberOfPeople = json.getInt("numberOfPeople");

        ArrayList<String> sectionsIDs = new ArrayList<>();
        for (Object o: json.getJSONArray("sectionsIDs")) {
            sectionsIDs.add((String) o);
        }

        JSONObject desiredTimeInterval = json.getJSONObject("desiredTimeInterval");
        Timestamp start = Timestamp.valueOf(desiredTimeInterval.getString("start"));
        Time duration = Time.valueOf(desiredTimeInterval.getString("duration"));
        System.out.println("Making br of "+customerID+"\nfor store: "+storeID+"\nfor "+numberOfPeople);
        Booking br = rh.book(numberOfPeople,customerID,storeID,start,duration,sectionsIDs);
        if (br != null) {
            System.out.println("BR accepted");
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("validated", true);
            JSONObject jsonVisitToken = new JSONObject();
            jsonVisitToken.put("uuid", br.getUuid());
            jsonVisitToken.put("hfid", br.getHfid());
            jsonResponse.put("visitToken",jsonVisitToken);
            return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
        } else {
            System.out.println("BR rejected");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @DELETE
    @Path("/{token}")
    public Response deleteBooking(@PathParam("token") String uuid)  {
        rh.cancelRequest(uuid);
        return Response.ok().build();
    }
}
