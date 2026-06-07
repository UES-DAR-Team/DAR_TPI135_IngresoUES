package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;



@Path("areaConocimiento")
public class AreaConocimientoResource extends AbstractResource implements Serializable {

    @Inject
    AreaConocimientoDAO areaConocimientoDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (first >= 0 && max > 0 && max <= 10) {
          List<AreaConocimiento> encontrados = areaConocimientoDAO.findRange(first, max);
            int total = areaConocimientoDAO.count();
            return Response.ok(encontrados)
                    .header(X_TOTAL_COUNT, total)
                    .build();
        }
       return unprocessable("first,max");
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") UUID id) {
       if (id == null) return unprocessable("id");

       AreaConocimiento encontrado = areaConocimientoDAO.findById(id);
        if (encontrado != null) {
            return Response.ok(encontrado).build();
        }

        return notFound(id.toString(), "AreaConocimiento");
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@PathParam("id") UUID id) {
        if (id == null) return unprocessable("id");

        AreaConocimiento encontrado = areaConocimientoDAO.findById(id);
        if (encontrado == null) {
            return notFound(id.toString(), "AreaConocimiento");
        }

        List<AreaConocimiento> hijos = areaConocimientoDAO.findHijosByPadre(id);
        if (!hijos.isEmpty()) {
            return conflict(id.toString(), "has child records and cannot be deleted");
        }

        areaConocimientoDAO.delete(encontrado);
        return Response.noContent().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(AreaConocimiento entity, @Context UriInfo uriInfo) {
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");

        if (entity.getIdAutoReferenciaArea() != null) {
            AreaConocimiento padre = areaConocimientoDAO.findById(entity.getIdAutoReferenciaArea().getId());
            if (padre == null) {
                return notFound(entity.getIdAutoReferenciaArea().getId().toString(), "idAutoReferenciaArea");
            }
        }

        areaConocimientoDAO.create(entity);
        return Response.created(
                uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build()).build();
    }

    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, AreaConocimiento entity) {
        if (id == null) return unprocessable("id");
        if (entity == null) return unprocessable("entity must not be null");

        AreaConocimiento existing = areaConocimientoDAO.findById(id);
        if (existing == null) {
            return notFound(id.toString(), "AreaConocimiento");
        }

        if (entity.getIdAutoReferenciaArea() != null) {
            AreaConocimiento padre = areaConocimientoDAO.findById(entity.getIdAutoReferenciaArea().getId());
            if (padre == null) {
                return notFound(entity.getIdAutoReferenciaArea().getId().toString(), "idAutoReferenciaArea");
            }
        }

        entity.setId(id);
        AreaConocimiento update = areaConocimientoDAO.update(entity);
        return Response.ok(update).build();
    }
}