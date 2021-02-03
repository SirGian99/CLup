import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

@Path("/StoreInfoInt/")
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
    public StoreInfo getStoreGeneralInfo(@PathParam("storeID") String storeID) {
        return ssh.getStoreGeneralInfo(storeID);
    }

    @GET
    @Path("store/{storeID}/generalInfo2")
    @Produces("application/json")
    public Response getStoreGeneralInfo2(@PathParam("storeID") String storeID) {
        JSONObject jsonResponse = ssh.getStoreGeneralInfo2(storeID);
        return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("chainstore")
    @Produces("application/json")
    public ChainsAndAutonomousStores getChainsAndAutonomousStores(@QueryParam("city") String city) {
        return ssh.getChainsAndAutonomousStores(city);
    }
    @GET
    @Path("chainstore2")
    @Produces("application/json")
    public Response getChainsAndAutonomousStores2(@QueryParam("city") String city) {
        JSONObject jsonResponse = ssh.getChainsAndAutonomousStores2(city);
        return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("chain/{name}/stores")
    @Produces("application/json")
    public Stores getChainStores(@PathParam("name") String chain, @QueryParam("city") String city) {
        return ssh.getChainStores(chain, city);
    }

    @GET
    @Path("chain/{name}/stores2")
    @Produces("application/json")
    public Response getChainStores2(@PathParam("name") String chain, @QueryParam("city") String city) {
        JSONObject jsonResponse = ssh.getChainStores2(chain, city);
        return Response.ok().entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
    }
}
