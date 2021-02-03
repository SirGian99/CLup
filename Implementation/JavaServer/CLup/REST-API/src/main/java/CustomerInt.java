import it.polimi.se2.ricciosorrentinotriuzzi.*;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/customer/")

public class CustomerInt {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.CustomerController")
    private CustomerController cc;

    @PUT
    @Path("registerApp")
    @Consumes("application/json")
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("customer/{id}")
    @Produces("application/json")
    public Response getCustomerRequests(@PathParam("id") String customer) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

}