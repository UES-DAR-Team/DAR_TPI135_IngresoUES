package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.TurnoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Turno;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.UUID;


@Path("turno")
public class TurnoResource extends AbstractResource implements Serializable {
    @Inject
    TurnoDAO turnoDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {

        if (first >= 0 && max > 0 && max <= 10) {
            List<Turno> encontrados = turnoDAO.findRange(first, max);
            int total = turnoDAO.count();
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

        Turno encontrados = turnoDAO.findById(id);
        if (encontrados == null) return notFound(id.toString(), "Turno");
        return Response.ok(encontrados).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Turno entity, @Context UriInfo uriInfo) {
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");

        turnoDAO.create(entity);
        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, Turno entity) {
        if (id == null) return unprocessable("id");
        if (entity == null) return unprocessable("entity must not be null");

        Turno existing = turnoDAO.findById(id);
        if (existing == null) return notFound(id.toString(), "Turno");

        entity.setId(id);
        Turno updated = turnoDAO.update(entity);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@PathParam("id") UUID id) {
        if (id == null) return unprocessable("id");

        Turno encontrados = turnoDAO.findById(id);
        if (encontrados == null) return notFound(id.toString(), "Turno");
        turnoDAO.delete(encontrados);
        return Response.noContent().build();
    }
}
