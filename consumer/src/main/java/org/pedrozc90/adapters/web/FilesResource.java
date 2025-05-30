package org.pedrozc90.adapters.web;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestHeader;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.pedrozc90.adapters.web.dtos.FileStorageDto;
import org.pedrozc90.application.FileStorageService;
import org.pedrozc90.core.exceptions.AppException;
import org.pedrozc90.core.mappers.FileStorageMapper;
import org.pedrozc90.domain.FileStorage;

@Path("/files")
public class FilesResource {

    @Inject
    protected FileStorageService service;

    @Inject
    protected FileStorageMapper mapper;

    @Transactional
    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@RestHeader("Content-Disposition") final String contentDisposition,
                           @RestForm(value = "file") FileUpload file) throws AppException {
        final FileStorage fs = service.create(file);
        final FileStorageDto dto = mapper.toDto(fs);
        return Response.status(Response.Status.CREATED).entity(dto).build();
    }

    @Transactional
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@RestPath(value = "id") final Long id) throws AppException {
        final FileStorage fs = service.get(id);
        final FileStorageDto dto = mapper.toDto(fs);
        return Response.ok(dto).build();
    }

}
