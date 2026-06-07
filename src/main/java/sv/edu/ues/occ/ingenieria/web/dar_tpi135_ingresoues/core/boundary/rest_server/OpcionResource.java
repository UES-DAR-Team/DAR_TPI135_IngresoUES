package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.OpcionDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Opcion;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("opcion")
public class OpcionResource extends AbstractResource implements Serializable {
    @Inject
    OpcionDAO opcionDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {
        if (first >= 0 && max > 0 && max <= 10) {
            List<Opcion> encontrados = opcionDAO.findRange(first, max);
            int total = opcionDAO.count();
            return Response.ok(encontrados).header(X_TOTAL_COUNT, total).build();
        }
        return unprocessable("first, max");
    }


    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") UUID id) {
        if (id == null) return unprocessable("id");
        Opcion encontrados = opcionDAO.findById(id);
        if (encontrados == null) return notFound(id.toString(), "Opcion");
        return Response.ok(encontrados).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Opcion entity, @Context UriInfo uriInfo) {
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        opcionDAO.create(entity);
        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }


    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, Opcion entity) {
        if (id == null) return unprocessable("id");
        if (entity == null) return unprocessable("entity must not be null");
        Opcion existing = opcionDAO.findById(id);
        if (existing == null) return notFound(id.toString(), "Opcion");
        entity.setId(id);
        Opcion updated = opcionDAO.update(entity);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@PathParam("id") UUID id) {
        if (id == null) return unprocessable("id");
        Opcion encontrados = opcionDAO.findById(id);
        if (encontrados == null) return notFound(id.toString(), "Opcion");
        opcionDAO.delete(encontrados);
        return Response.noContent().build();
    }
}