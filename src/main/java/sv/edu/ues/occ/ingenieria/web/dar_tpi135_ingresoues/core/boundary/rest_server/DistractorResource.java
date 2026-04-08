package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;


import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("distractor")
public class DistractorResource implements Serializable {
    @Inject
    DistractorDAO distractorDAO;

    private static final Logger LOG = Logger.getLogger(DistractorResource.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {
        if (first >= 0 && max > 0 && max <= 10) {
            try {
                List<Distractor> encontrados = distractorDAO.findRange(first, max);
                int total = distractorDAO.count();
                return Response.ok(encontrados)
                        .header("X-Total-Count", total)
                        .build();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error retrieving Distractor range", ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db")
                        .build();
            }
        }
        return Response.status(422).header("Missing-parameter", "first,max").build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") UUID id) {
        if (id != null) {
            try {
                Distractor encontrados = distractorDAO.findById(id);
                if (encontrados != null) {
                    return Response.ok(encontrados).build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        //.header("Not-found-id", id.toString())
                        .header("Not-found-id", "Record with id " + id + " not found")
                        .build();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error retrieving Distractor by id", ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db")
                        .build();
            }
        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@PathParam("id") UUID id) {
        if (id != null) {
            try {
                Distractor encontrados = distractorDAO.findById(id);
                if (encontrados != null) {
                    distractorDAO.delete(encontrados);
                    return Response.noContent().build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record with id " + id + " not found")
                        .build();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error deleting Distractor by id", ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db")
                        .build();
            }
        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Distractor entity, @Context UriInfo uriInfo) {
        if (entity != null) {
            if (entity.getId() == null) {
                try {
                    distractorDAO.create(entity);
                    return Response.created(
                                    uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build())
                            .entity(entity)
                            .build();
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Error creating Distractor", ex);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .header("Server-exception", "Cannot access db")
                            .build();
                }
            }
            return Response.status(422)
                    .header("Missing-parameter", "entity.id must be null")
                    .build();
        }
        return Response.status(422)
                .header("Missing-parameter", "entity must not be null")
                .build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, Distractor entity) {
        if (id != null) {
            if (entity != null) {
                try {
                    Distractor existing = distractorDAO.findById(id);
                    if (existing != null) {
                        entity.setId(id);
                        Distractor updated = distractorDAO.update(entity);
                        return Response.ok(updated).build();
                    }
                    return Response.status(Response.Status.NOT_FOUND)
                            .header("Not-found-id", "Record with id " + id + " not found")
                            .build();
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Error updating Distractor", ex);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .header("Server-exception", "Cannot access db")
                            .build();
                }
            }
            return Response.status(422)
                    .header("Missing-parameter", "entity must not be null")
                    .build();
        }
        return Response.status(422)
                .header("Missing-parameter", "id")
                .build();
    }

}
