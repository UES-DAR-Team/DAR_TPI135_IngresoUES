package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;


import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("prueba")
public class PruebaResource extends AbstractResource implements Serializable {
    @Inject
    PruebaDAO pruebaDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {
        if (first >= 0 && max > 0 && max <= 10) {
            List<Prueba> encontrados = pruebaDAO.findRange(first, max);
            int total = pruebaDAO.count();
            return Response.ok(encontrados).header(X_TOTAL_COUNT, total).build();
        }
        return unprocessable("first, max");
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") UUID id) {
        if (id == null) return unprocessable("id");

        Prueba encontrados = pruebaDAO.findById(id);
        if (encontrados == null) return notFound(id.toString(), "Prueba");
        return Response.ok(encontrados).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Prueba entity, @Context UriInfo uriInfo) {
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");

        pruebaDAO.create(entity);
        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, Prueba entity) {
        if (id == null) return unprocessable("id");
        if (entity == null) return unprocessable("entity must not be null");

        Prueba existing = pruebaDAO.findById(id);
        if (existing == null) return notFound(id.toString(), "Prueba");
        entity.setId(id);
        Prueba update = pruebaDAO.update(entity);
        return Response.ok(update).build();

    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@PathParam("id") UUID id) {
        if (id == null) return unprocessable("id");
        Prueba encontrados = pruebaDAO.findById(id);
        if (encontrados == null) return notFound(id.toString(), "Prueba");
        pruebaDAO.delete(encontrados);
        return Response.noContent().build();
    }
}
