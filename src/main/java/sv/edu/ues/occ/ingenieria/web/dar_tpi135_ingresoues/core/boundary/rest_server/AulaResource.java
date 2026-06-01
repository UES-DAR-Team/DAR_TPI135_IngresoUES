package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AulaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aula;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Path("aula")
public class AulaResource extends AbstractResource implements Serializable {

    @Inject
    AulaDAO aulaDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {

        if (first < 0 || max <= 0 || max > 10) {
            return unprocessable("first,max");
        }
        List<Aula> list = aulaDAO.findRange(first, max);
        int total = aulaDAO.count();
        return Response.ok(list)
                .header("X-Total-Count", total)
                .build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") UUID id) {
        if (id == null) {
            return unprocessable("id");
        }
        Aula entity = aulaDAO.findById(id);
        if (entity == null) {
            return notFound(id.toString(), "Aula");
        }
        return Response.ok(entity).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {
        if (id == null) {
            return unprocessable("id");
        }
        Aula entity = aulaDAO.findById(id);
        if (entity == null) {
            return notFound(id.toString(), "Aula");
        }
        aulaDAO.delete(entity);
        return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Aula entity, @Context UriInfo uriInfo) {
        if (entity == null) {
            return unprocessable("entity must not be null");
        }
        if (entity.getId() != null) {
            return unprocessable("entity.id must be null");
        }
        aulaDAO.create(entity);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build())
                .entity(entity)
                .build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, Aula entity) {
        if (id == null) {
            return unprocessable("id");
        }
        if (entity == null) {
            return unprocessable("entity must not be null");
        }
        Aula existing = aulaDAO.findById(id);
        if (existing == null) {
            return notFound(id.toString(), "Aula");
        }
        entity.setId(id);
        Aula updated = aulaDAO.update(entity);

        return Response.ok(updated).build();
    }
}