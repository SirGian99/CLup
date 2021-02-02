import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import it.polimi.se2.ricciosorrentinotriuzzi.Manager;
import it.polimi.se2.ricciosorrentinotriuzzi.Store;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;

import java.util.List;

@Path("/JunkyFunky/")

public class JunkyFunky {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi/VisitManager")
    private VisitManager visitManager;

    @GET
    @Path("store/{store}")
    @Produces("text/plain")
    public String getStoreName(@PathParam("store") String storeID) {
        //return storeID;
        return dataModel.getStoreName(storeID);
    }

    @GET
    @Path("manager/{manager}")
    @Produces("text/plain")
    public String getManagerByUser(@PathParam("manager") String manager) {
        //return storeID;
        return dataModel.getManager(manager).getName();
    }

    @GET
    @Path("store/{store}/manager/{manager}")
    @Produces("text/plain")
    public String addStoreManager(@PathParam("manager") String manager, @PathParam("store") String store) {
        dataModel.addManager(store,manager);

        List<Store> stores =  dataModel.getManager(manager).getStores();
        List<Manager> managers =  dataModel.getStore(store).getManagers();
        StringBuilder toRet  = new StringBuilder();
        toRet.append("Manager \n");
        for (Manager store1: managers) {

            toRet.append(store1.getUsername()).append(" ");

        }
        toRet.append("\nStore \n");

        for (Store store1 : stores){
            toRet.append(store1.getName()).append(" ");
        }

        return toRet.toString();

    }

    @GET
    @Path("store/{storeName}/chain/{chain}/manager/{managerUsername}")
    @Produces("text/plain")
    public String insertStore(@PathParam("storeName") String storeName, @PathParam("chain") String chain, @PathParam("managerUsername") String managerUsername){
        dataModel.insertNewStore(storeName, chain, 1,2,3, managerUsername);
        return "OK!";
    }

    @POST
    @Path("access/request")
    @Produces(MediaType.APPLICATION_JSON)
    public Response makeAccessRequest(@FormParam("token") String token, @FormParam("storeID") String storeID){
        System.out.println(token + "\npoi\n" + storeID);
        int numOfPeople = visitManager.validateAccess(token, storeID);
        System.out.println("Ho ricevuto la richiesta, num:" + numOfPeople);
        if (numOfPeople>0){
            return Response.status(Response.Status.OK).entity("{\" validated \" : \"true\", \"numberOfPeople\" : " + numOfPeople + "}")
                    .type(MediaType.APPLICATION_JSON).build();
        }else {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("exit/request")
    @Produces(MediaType.APPLICATION_JSON)
    public Response makeExitRequest(@FormParam("token") String token, @FormParam("storeID") String storeID){
        System.out.println(token + "\npoi\n" + storeID);
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmAccessRequest(@FormParam("token") String token, @FormParam("storeID") String storeID, @FormParam("numberOfPeople") String numOfPeople) {
        int numberOfP = Integer.parseInt(numOfPeople);
        return numberOfP > 0 && visitManager.confirmAccess(token, storeID, numberOfP) ? Response.status(Response.Status.OK).build() : Response.serverError().build();
    }

    @POST
    @Path("exit/confirm")
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmExitRequest(@FormParam("token") String token, @FormParam("storeID") String storeID, @FormParam("numberOfPeople") String numOfPeople) {
        int numberOfP = Integer.parseInt(numOfPeople);
        return numberOfP > 0 && visitManager.confirmExit(token, storeID, numberOfP) ? Response.status(Response.Status.OK).build() : Response.serverError().build();
    }
}