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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorAreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.DistractorAreaConocimiento;

import java.io.Serializable;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("areaConocimiento/{idAreaConocimiento}/distractor")
public class DistractorAreaConocimientoResource extends AbstractResource implements Serializable {

    @Inject
    DistractorAreaConocimientoDAO distractorAreaConocimientoDAO;

    @Inject
    DistractorDAO distractorDAO;

    @Inject
    AreaConocimientoDAO areaConocimientoDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {

        if (idAreaConocimiento == null) return unprocessable("idAreaConocimiento");
        if (first < 0 || max <= 0 || max > 10) return unprocessable("first, max");

        AreaConocimiento areaConocimiento = areaConocimientoDAO.findById(idAreaConocimiento);
        if (areaConocimiento == null) return notFound(idAreaConocimiento.toString(), "AreaConocimiento");

        List<DistractorAreaConocimiento> encontrados =
                distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, first, max);
        int total = distractorAreaConocimientoDAO.count();

        return Response.ok(encontrados)
                .header(X_TOTAL_COUNT, total)
                .build();
    }

    @GET
    @Path("{idDistractor}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            @PathParam("idDistractor") UUID idDistractor) {

        if (idAreaConocimiento == null) return unprocessable("idAreaConocimiento");
        if (idDistractor == null) return unprocessable("idDistractor");

        List<DistractorAreaConocimiento> list =
                distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);

        Optional<DistractorAreaConocimiento> found = list.stream()
                .filter(dac -> dac.getIdDistractor() != null
                        && idDistractor.equals(dac.getIdDistractor().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("area=" + idAreaConocimiento + ", distractor=" + idDistractor,
                    "DistractorAreaConocimiento");
        }

        return Response.ok(found.get()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            DistractorAreaConocimiento entity,
            @Context UriInfo uriInfo) {

        if (idAreaConocimiento == null) return unprocessable("idAreaConocimiento");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdDistractor() == null) return unprocessable("entity.idDistractor must be provided in body");

        AreaConocimiento areaConocimiento = areaConocimientoDAO.findById(idAreaConocimiento);
        if (areaConocimiento == null) return notFound(idAreaConocimiento.toString(), "AreaConocimiento");

        Distractor distractor = distractorDAO.findById(entity.getIdDistractor().getId());
        if (distractor == null) return notFound(entity.getIdDistractor().getId().toString(), "Distractor");

        entity.setIdAreaConocimiento(areaConocimiento);
        entity.setIdDistractor(distractor);
        entity.setFechaAsignacion(OffsetDateTime.now());

        distractorAreaConocimientoDAO.create(entity);

        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }

    @DELETE
    @Path("{idDistractor}")
    public Response delete(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            @PathParam("idDistractor") UUID idDistractor) {

        if (idAreaConocimiento == null) return unprocessable("idAreaConocimiento");
        if (idDistractor == null) return unprocessable("idDistractor");

        List<DistractorAreaConocimiento> list =
                distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);

        Optional<DistractorAreaConocimiento> found = list.stream()
                .filter(dac -> dac.getIdDistractor() != null
                        && idDistractor.equals(dac.getIdDistractor().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("area=" + idAreaConocimiento + ", distractor=" + idDistractor,
                    "DistractorAreaConocimiento");
        }

        distractorAreaConocimientoDAO.delete(found.get());
        return Response.noContent().build();
    }
}