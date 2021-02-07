package it.polimi.se2.ricciosorrentinotriuzzi.rest.api;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/")
public class Home {
    @GET
    @Path("")
    @Produces("text/plain")
    public Response home() {
        return Response.ok().entity("CLup project\n\n" +
                "Group members:\n" +
                "Riccio Vincenzo (C.P. 10804402)\n" +
                "Sorrentino Giancarlo (C.P. 10800065)\n" +
                "Triuzzi Emanuele (C.P. 10794440)").build();
    }
}