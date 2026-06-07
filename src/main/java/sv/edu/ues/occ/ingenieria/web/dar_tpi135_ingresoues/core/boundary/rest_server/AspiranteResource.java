package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("aspirante")
public class AspiranteResource extends AbstractResource implements Serializable {

    @Inject
    AspiranteDAO aspiranteDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {

        if (first >= 0 && max > 0 & max <= 10) {
            List<Aspirante> encontrados = aspiranteDAO.findRange(first, max);
            int total = aspiranteDAO.count();
            return Response.ok(encontrados)
                    .header(X_TOTAL_COUNT, total)
                    .build();
        }
        return unprocessable("first, max");

    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") UUID id) {
        if (id == null) return unprocessable("id");

        Aspirante resp = aspiranteDAO.findById(id);
        if (resp == null) return notFound(id.toString(), "Aspirante");
        return Response.ok(resp).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Aspirante entity, @Context UriInfo uriInfo) {
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");

        aspiranteDAO.create(entity);
        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, Aspirante entity) {
        if (id == null) return unprocessable("id");
        if (entity == null) return unprocessable("entity must not be null");

        Aspirante existing = aspiranteDAO.findById(id);
        if (existing == null) return notFound(id.toString(), "Aspirante");

        entity.setId(id);
        Aspirante updated = aspiranteDAO.update(entity);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {
        if (id == null) return unprocessable("id");

        Aspirante existing = aspiranteDAO.findById(id);
        if (existing == null) return notFound(id.toString(), "Aspirante");
        aspiranteDAO.delete(existing);
        return Response.noContent().build();
    }

}