package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("jornada")
public class JornadaResource extends AbstractResource implements Serializable {

    @Inject
    JornadaDAO jornadaDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {

        if (first >= 0 && max > 0 && max <= 10) {
            List<Jornada> encontrados = jornadaDAO.findRange(first, max);
            int total = jornadaDAO.count();
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
        if (id == null) {
            return unprocessable("id");
        }
        Jornada resp = jornadaDAO.findById(id);
        if (resp == null) {
            return notFound(id.toString(), "Jornada");
        }
        return Response.ok(resp).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Jornada entity, @Context UriInfo uriInfo) {
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");

        jornadaDAO.create(entity);
        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, Jornada entity) {
        if (id == null) return unprocessable("id");
        if (entity == null) return unprocessable("entity must not be null");

        Jornada existing = jornadaDAO.findById(id);
        if (existing == null) {
            return notFound(id.toString(), "Jornada");
        }

        entity.setId(id);
        Jornada updated = jornadaDAO.update(entity);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {
        if (id == null) return unprocessable("id");

        Jornada encontrados = jornadaDAO.findById(id);
        if (encontrados == null) {
            return notFound(id.toString(), "Jornada");
        }
        jornadaDAO.delete(encontrados);
        return Response.noContent().build();
    }
}