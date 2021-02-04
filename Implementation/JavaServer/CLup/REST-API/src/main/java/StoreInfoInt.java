import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

@Path("/")
public class StoreInfoInt {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.StoreStatusHandler")
    private StoreStatusHandler ssh;

    @GET
    @Path("{store}")
    @Produces("text/plain")
    public String getStoreName(@PathParam("store") String storeID) {
        return ssh.getStoreNameByID(storeID);
    }

    @GET
    @Path("store/{storeID}/generalInfo")
    @Produces("application/json")
    public Response getStoreGeneralInfo(@PathParam("storeID") String storeID) {
        JSONObject jsonResponse = ssh.getStoreGeneralInfo(storeID);
        return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("chainstore")
    @Produces("application/json")
    public Response getChainsAndAutonomousStores(@QueryParam("city") String city) {
        JSONObject jsonResponse = ssh.getChainsAndAutonomousStores(city);
        return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
    }


    @GET
    @Path("chain/{name}/stores")
    @Produces("application/json")
    public Response getChainStores(@PathParam("name") String chainName, @QueryParam("city") String city) {
        JSONObject jsonResponse = ssh.getChainStores(chainName, city);
        return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
    }
}
