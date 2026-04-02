package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;

import java.io.Serializable;
import java.util.UUID;

@Path("jornada")
public class JornadaResource implements Serializable {

    @Inject
    JornadaDAO jornadaDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(100) @DefaultValue("100") @QueryParam("max") int max
    ) {

        if (first >= 0 && max <= 100) {
            try {
                int total = jornadaDAO.count();

                return Response.ok(jornadaDAO.findRange(first, max))
                        .header("Total-records", total)
                        .build();

            } catch (Exception e) {
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
                Jornada resp = jornadaDAO.findById(id);

                if (resp != null) {
                    return Response.ok(resp).build();
                }

                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Record with id " + id + " not found")
                        .build();

            } catch (Exception e) {
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
                Jornada resp = jornadaDAO.findById(id);

                if (resp != null) {
                    jornadaDAO.delete(resp);
                    return Response.noContent().build();
                }

                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Record with id " + id + " not found")
                        .build();

            } catch (Exception e) {
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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Jornada entity, @Context UriInfo uriInfo) {

        if (entity == null || entity.getId() != null) {
            return Response.status(422)
                    .header("Missing-parameter", "entity must not be null and id must be null")
                    .build();
        }

        try {
            jornadaDAO.create(entity);

            return Response.created(
                    uriInfo.getAbsolutePathBuilder()
                            .path(String.valueOf(entity.getId()))
                            .build()
            ).entity(entity).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, Jornada entity) {

        if (id == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "id,entity")
                    .build();
        }

        try {
            Jornada existing = jornadaDAO.findById(id);

            if (existing == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Record with id " + id + " not found")
                        .build();
            }

            entity.setId(id);
            Jornada updated = jornadaDAO.update(entity);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", e.getMessage())
                    .build();
        }
    }
}