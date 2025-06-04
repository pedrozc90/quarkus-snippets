package org.pedrozc90.adapters.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.pedrozc90.adapters.web.dtos.HealthResponse;
import org.pedrozc90.application.HealthService;

@Path("/")
public class RootResource {

    @Inject
    protected HealthService service;

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response health() {
        final HealthResponse entity = service.create();
        return Response.ok(entity, MediaType.APPLICATION_JSON).build();
    }

}
