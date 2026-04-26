package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AulaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aula;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("aula")
public class AulaResource implements Serializable {

    @Inject
    AulaDAO aulaDAO;

    private static final Logger LOG = Logger.getLogger(AulaResource.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {
        if (first >= 0 && max > 0 && max <= 10) {
            try {
                List<Aula> list = aulaDAO.findRange(first, max);
                int total = aulaDAO.count();
                return Response.ok(list)
                        .header("Total-records", total)
                        .build();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error retrieving Aula range", ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db")
                        .build();
            }
        }
        return Response.status(422)
                .header("Missing-parameter", "first,max")
                .build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") UUID id) {

        if (id != null) {
            try {
                Aula entity = aulaDAO.findById(id);

                if (entity != null) {
                    return Response.ok(entity).build();
                }

                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record with id " + id + " not found")
                        .build();

            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error retrieving Aula by id", ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db")
                        .build();
            }
        }

        return Response.status(422)
                .header("Missing-parameter", "id")
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {

        if (id != null) {
            try {
                Aula entity = aulaDAO.findById(id);

                if (entity != null) {
                    aulaDAO.delete(entity);
                    return Response.noContent().build();
                }

                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record with id " + id + " not found")
                        .build();

            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error deleting Aula", ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db")
                        .build();
            }
        }

        return Response.status(422)
                .header("Missing-parameter", "id")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Aula entity, @Context UriInfo uriInfo) {

        if (entity != null) {

            if (entity.getId() == null) {
                try {
                    aulaDAO.create(entity);

                    return Response.created(
                            uriInfo.getAbsolutePathBuilder()
                                    .path(entity.getId().toString())
                                    .build()
                    ).entity(entity).build();

                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Error creating Aula", ex);
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
    public Response update(@PathParam("id") UUID id, Aula entity) {

        if (id != null) {

            if (entity != null) {
                try {
                    Aula existing = aulaDAO.findById(id);

                    if (existing != null) {
                        entity.setId(id);
                        aulaDAO.update(entity);
                        return Response.ok(entity).build();
                    }

                    return Response.status(Response.Status.NOT_FOUND)
                            .header("Not-found-id", "Record with id " + id + " not found")
                            .build();

                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Error updating Aula", ex);
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