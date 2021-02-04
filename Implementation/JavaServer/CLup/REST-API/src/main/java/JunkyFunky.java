import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polimi.se2.ricciosorrentinotriuzzi.Manager;
import it.polimi.se2.ricciosorrentinotriuzzi.Store;

import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Path("/JunkyFunky/")

public class JunkyFunky {
    //TODO no response.serverError ma status 500 tipo
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi/VisitManager")
    private VisitManager visitManager;

    @GET
    @Path("manager/{manager}")
    @Produces("text/plain")
    public String getManagerByUser(@PathParam("manager") String manager) {
        return "Username: " + manager + "Name: " + dataModel.getManager(manager).getName();
    }

    @GET
    @Path("test/BookingsBetweenDate")
    @Produces("text/plain")
    public String testBookingBetweenDate() {
        StringBuilder string = new StringBuilder();
        List<Booking> bookings = dataModel.getBookings("a8224c0b-6552-11eb-a3e0-dca632747890", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now().plusMinutes(1)));
        for (Booking booking:bookings) {
            string.append("BookingID: ").append(booking.getUuid()).append("    HFID: ").append(booking.getHfid()).append("\n");
        }
        return string.toString();
    }

    @GET
    @Path("test/checkNewReadyRequest/{store}")
    @Produces("text/plain")
    public String checkNewReadyRequest(@PathParam("store") String storeID) {
        visitManager.checkNewReadyRequest(storeID);
        return "ok";
    }

    @GET
    @Path("test/checkGetQueue/{store}")
    @Produces("text/plain")
    public String checkGetQueue(@PathParam("store") String storeID) {
        List<Lineup> lineups = dataModel.getQueue(storeID);
        StringBuilder toRet = new StringBuilder();
        for (Lineup lur: lineups) {
            System.out.println(lur.getUuid());
            toRet.append(lur.getUuid());
        }
        return toRet.toString();
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
        System.out.println(storeName + chain + managerUsername);
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
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("access/confirm")
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmAccessRequest(@FormParam("token") String token, @FormParam("storeID") String storeID, @FormParam("numberOfPeople") String numOfPeople) {
        int numberOfP = Integer.parseInt(numOfPeople);
        return numberOfP > 0 && visitManager.confirmAccess(token, storeID, numberOfP) ? Response.status(Response.Status.OK).build() : Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Path("exit/confirm")
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmExitRequest(@FormParam("token") String token, @FormParam("storeID") String storeID, @FormParam("numberOfPeople") String numOfPeople) {
        int numberOfP = Integer.parseInt(numOfPeople);
        return numberOfP > 0 && visitManager.confirmExit(token, storeID, numberOfP) ? Response.status(Response.Status.OK).build() : Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @GET
    @Path("{store}")
    @Produces("text/plain")
    public String getStoreName(@PathParam("store") String storeID) {
        List<Productsection> prods = dataModel.getStore(storeID).getProductSections();
        return prods.toString();
    }

}