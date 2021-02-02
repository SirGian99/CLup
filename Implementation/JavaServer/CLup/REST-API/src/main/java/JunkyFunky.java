import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import it.polimi.se2.ricciosorrentinotriuzzi.Manager;
import it.polimi.se2.ricciosorrentinotriuzzi.Store;

import it.polimi.se2.ricciosorrentinotriuzzi.*;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;

import java.io.Serializable;
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
        return dataModel.getStore(storeID).getName();
    }


    @GET
    @Path("store/{storeID}/generalInfo")
    @Produces("application/json")
    public StoreInfo getStoreGeneralInfo(@PathParam("storeID") String storeID) {
        Chain chain = dataModel.getChain(storeID);
        Store store = dataModel.getStore(storeID);
        Address address = null;
        if(store.getAddress() != null) {
            address = dataModel.getAddress(store.getAddress().longValue());
        }
        return new StoreInfo(chain, store, address);
    }

    class StoreInfo implements Serializable {
        public String chainName;
        public String name;
        public Address address;
        public String description;
        public byte[] image;
        public Integer currentOccupancy;
        public Integer maximumOccupancy;
        public Double safetyThreshold;
        public ArrayList<DayIntervalJson> workingHours;
        public ArrayList<ProductsectionJson> productSections;

        public StoreInfo(Chain chain, Store store, Address address){
            if (chain != null) {
                this.chainName = chain.getName();
            }
            this.name = store.getName();
            this.description = store.getDescription();
            this.image = store.getImage();
            this.currentOccupancy = store.getCurrentOccupancy();
            this.maximumOccupancy = store.getMaximumOccupancy();
            this.safetyThreshold = store.getSafetyThreshold();
            if (address != null) {
                this.address = address;
            }
            workingHours = new ArrayList<>();
            for (Dayinterval di:store.getWorkingHours()) {
                workingHours.add(new DayIntervalJson(di));
            }
            productSections = new ArrayList<>();
            for (Productsection ps:store.getProductSections()) {
                productSections.add(new ProductsectionJson(ps));
            }
        }
    }

    class ProductsectionJson implements Serializable {
        public String name;
        public Double currentOccupancy;
        public Integer maximumOccupancy;
        public ProductsectionJson(Productsection ps) {
            this.name = ps.getName();
            this.currentOccupancy = ps.getCurrentOccupancy();
            this.maximumOccupancy = ps.getMaximumOccupancy();
        }
    }


    class DayIntervalJson implements Serializable {
        public long id;
        public int dayOfTheWeek;
        public String start;
        public String end;
        public DayIntervalJson(Dayinterval di){
            this.id = di.getId();
            this.dayOfTheWeek = di.getDayOfTheWeek();
            this.start = di.getStart().toString();
            this.end = di.getEnd().toString();
        }
    }
}