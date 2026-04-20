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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("areaConocimiento/{idAreaConocimiento}/pregunta")
public class PreguntaAreaConocimientoResource implements Serializable {
    @Inject
    PreguntaAreaConocimientoDAO preguntaAreaConocimientoDAO;

    @Inject
    AreaConocimientoDAO areaConocimientoDAO;

    @Inject
    PreguntaDAO preguntaDAO;

    private static final Logger LOG= Logger.getLogger(PreguntaAreaConocimientoResource.class.getName());

    // Obtener preguntas asociadas a un area de conocimiento con paginacion
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (idAreaConocimiento == null) {
            return Response.status(422).header("Missing-parameter", "idAreaConocimiento").build();
        }
        if (first < 0 || max <= 0 || max > 10) {
            return Response.status(422).header("Missing-parameter", "first,max").build();
        }
        try {
            // validar existencia de area conocimiento padre
            AreaConocimiento area = areaConocimientoDAO.findById(idAreaConocimiento);
            if (area == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "AreaConocimiento with id " + idAreaConocimiento + " not found")
                        .build();
            }
            List<PreguntaAreaConocimiento> encontrados = preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, first, max);
            int total = preguntaAreaConocimientoDAO.count();
            return Response.ok(encontrados)
                    .header("X-Total-Count", total)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving PreguntaAreaConocimiento range", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    // Obtener una asociación pregunta-area por idPregunta
    @GET
    @Path("{idPregunta}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            @PathParam("idPregunta") UUID idPregunta) {
        if (idAreaConocimiento == null || idPregunta == null) {
            return Response.status(422).header("Missing-parameter", "idAreaConocimiento,idPregunta").build();
        }
        try {
            List<PreguntaAreaConocimiento> list = preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
            Optional<PreguntaAreaConocimiento> found = list.stream()
                    .filter(pac -> pac.getIdPregunta() != null && idPregunta.equals(pac.getIdPregunta().getId()))
                    .findFirst();
            if (found.isPresent()) {
                return Response.ok(found.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking area " + idAreaConocimiento + " and pregunta " + idPregunta + " not found")
                        .build();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving PreguntaAreaConocimiento", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    // Crear asociación: POST /areaConocimiento/{idAreaConocimiento}/pregunta
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            PreguntaAreaConocimiento entity,
            @Context UriInfo uriInfo) {

        if (idAreaConocimiento == null) {
            return Response.status(422).header("Missing-parameter", "idAreaConocimiento").build();
        }
        if (entity == null) {
            return Response.status(422).header("Missing-parameter", "entity must not be null").build();
        }
        if (entity.getId() != null) {
            return Response.status(422).header("Missing-parameter", "entity.id must be null").build();
        }
        if (entity.getIdPregunta() == null || entity.getIdPregunta().getId() == null) {
            return Response.status(422).header("Missing-parameter", "idPregunta must be provided in body").build();
        }
        try {
            AreaConocimiento area = areaConocimientoDAO.findById(idAreaConocimiento);
            if (area == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "AreaConocimiento with id " + idAreaConocimiento + " not found")
                        .build();
            }
            Pregunta pregunta = preguntaDAO.findById(entity.getIdPregunta().getId());
            if (pregunta == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Pregunta with id " + entity.getIdPregunta().getId() + " not found")
                        .build();
            }
            entity.setIdAreaConocimiento(area);
            entity.setIdPregunta(pregunta);
            entity.setFechaAsignacion(OffsetDateTime.now());
            preguntaAreaConocimientoDAO.create(entity);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(entity.getIdPregunta().getId().toString()).build())
                    .entity(entity)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating PreguntaAreaConocimiento", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    // Eliminar asociación pregunta-area
    @DELETE
    @Path("{idPregunta}")
    public Response delete(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            @PathParam("idPregunta") UUID idPregunta) {
        if (idAreaConocimiento == null || idPregunta == null) {
            return Response.status(422).header("Missing-parameter", "idAreaConocimiento,idPregunta").build();
        }
        try {
            List<PreguntaAreaConocimiento> list = preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
            Optional<PreguntaAreaConocimiento> found = list.stream()
                    .filter(pac -> pac.getIdPregunta() != null && idPregunta.equals(pac.getIdPregunta().getId()))
                    .findFirst();
            if (found.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking area " + idAreaConocimiento + " and pregunta " + idPregunta + " not found")
                        .build();
            }
            preguntaAreaConocimientoDAO.delete(found.get());
            return Response.noContent().build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error deleting PreguntaAreaConocimiento", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }
}
