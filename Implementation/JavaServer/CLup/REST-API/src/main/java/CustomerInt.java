import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/customer/")

public class CustomerInt {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;

    @PUT
    @Path("registerApp")
    @Consumes("application/json")
    public Response registerApp2(String body)  {
        JSONObject json = new JSONObject(body);
        String appid = json.getString("appID");
        System.out.println("Registering app customer with id: "+appid);
        Customer c = dataModel.newAppCustomer(appid);
        if (c != null) {
            System.out.println("Success while registering appcustomer");
            return Response.ok().build();
        } else {
            System.out.println("Error while registering appcustomer");
            return Response.serverError().build();
        }
    }

}