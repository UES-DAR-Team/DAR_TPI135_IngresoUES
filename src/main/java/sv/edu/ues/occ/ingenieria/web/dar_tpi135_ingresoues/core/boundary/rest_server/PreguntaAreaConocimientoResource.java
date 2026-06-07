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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaAreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaAreaConocimiento;

import java.io.Serializable;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("areaConocimiento/{idAreaConocimiento}/pregunta")
public class PreguntaAreaConocimientoResource extends AbstractResource implements Serializable {
    @Inject
    PreguntaAreaConocimientoDAO preguntaAreaConocimientoDAO;

    @Inject
    AreaConocimientoDAO areaConocimientoDAO;

    @Inject
    PreguntaDAO preguntaDAO;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (idAreaConocimiento == null) return unprocessable("idAreaConocimiento");
        if (first < 0 || max <= 0 || max > 10) return unprocessable("first,max");

        // validar existencia de area conocimiento padre
        AreaConocimiento area = areaConocimientoDAO.findById(idAreaConocimiento);
        if (area == null) return notFound(idAreaConocimiento.toString(), "AreaConocimiento");

        List<PreguntaAreaConocimiento> encontrados = preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, first, max);
        int total = preguntaAreaConocimientoDAO.count();
        return Response.ok(encontrados)
                .header(X_TOTAL_COUNT, total)
                .build();
    }

    // Obtener una asociación pregunta-area por idPregunta
    @GET
    @Path("{idPregunta}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            @PathParam("idPregunta") UUID idPregunta) {
        if (idAreaConocimiento == null) return unprocessable("idAreaConocimiento");
        if (idPregunta == null) return unprocessable("idPregunta");

        List<PreguntaAreaConocimiento> list =
                preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);

        Optional<PreguntaAreaConocimiento> found = list.stream()
                .filter(pac -> pac.getIdPregunta() != null && idPregunta.equals(pac.getIdPregunta().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("areaConocimiento=" + idAreaConocimiento + ", pregunta=" + idPregunta, "PreguntaAreaConocimiento");
        }
        return Response.ok(found.get()).build();

    }

    // Crear asociación: POST /areaConocimiento/{idAreaConocimiento}/pregunta
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            PreguntaAreaConocimiento entity,
            @Context UriInfo uriInfo) {

        if (idAreaConocimiento == null) return unprocessable("idAreaConocimiento");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdPregunta() == null || entity.getIdPregunta().getId() == null)
            return unprocessable("entity.idPregunta must be provided in body");

        AreaConocimiento area = areaConocimientoDAO.findById(idAreaConocimiento);
        if (area == null) return notFound(idAreaConocimiento.toString(), "AreaConocimiento");

        Pregunta pregunta = preguntaDAO.findById(entity.getIdPregunta().getId());
        if (pregunta == null) return notFound(entity.getIdPregunta().getId().toString(), "Pregunta");

        entity.setIdAreaConocimiento(area);
        entity.setIdPregunta(pregunta);
        entity.setFechaAsignacion(OffsetDateTime.now());
        preguntaAreaConocimientoDAO.create(entity);

        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }

    // Eliminar asociación pregunta-area
    @DELETE
    @Path("{idPregunta}")
    public Response delete(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            @PathParam("idPregunta") UUID idPregunta) {
        if (idAreaConocimiento == null) return unprocessable("idAreaConocimiento");
        if (idPregunta == null) return unprocessable("idPregunta");


        List<PreguntaAreaConocimiento> list =
                preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);

        Optional<PreguntaAreaConocimiento> found = list.stream()
                .filter(pac -> pac.getIdPregunta() != null && idPregunta.equals(pac.getIdPregunta().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("areaConocimiento=" + idAreaConocimiento + ", pregunta=" + idPregunta, "PreguntaAreaConocimiento");
        }

        preguntaAreaConocimientoDAO.delete(found.get());
        return Response.noContent().build();

    }
}
