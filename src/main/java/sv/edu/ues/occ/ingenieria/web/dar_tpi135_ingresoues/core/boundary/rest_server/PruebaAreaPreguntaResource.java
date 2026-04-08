package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaPreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaArea;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPregunta;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("pruebaArea/{idPruebaArea}/pregunta")
public class PruebaAreaPreguntaResource implements Serializable {
    @Inject
    PruebaAreaPreguntaDAO pruebaAreaPreguntaDAO;

    @Inject
    PruebaAreaDAO pruebaAreaDAO;

    @Inject
    PreguntaDAO preguntaDAO;

    private static final Logger LOG = Logger.getLogger(PruebaAreaPreguntaResource.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idPruebaArea") Integer idPruebaArea,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (idPruebaArea == null) {
            return Response.status(422).header("Missing-parameter", "idPruebaArea").build();
        }
        if (first < 0 || max <= 0 || max > 10) {
            return Response.status(422).header("Missing-parameter", "first,max").build();
        }
        try {
            // validar existencia de pruebaArea padre
            PruebaArea pruebaArea = pruebaAreaDAO.findById(idPruebaArea);
            if (pruebaArea == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "PruebaArea with id " + idPruebaArea + " not found")
                        .build();
            }
            List<PruebaAreaPregunta> encontrados = pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, first, max);
            int total = pruebaAreaPreguntaDAO.count();
            return Response.ok(encontrados)
                    .header("X-Total-Count", total)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving PruebaAreaPregunta range", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @GET
    @Path("{idPregunta}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idPruebaArea") Integer idPruebaArea,
            @PathParam("idPregunta") UUID idPregunta) {
        if (idPruebaArea == null || idPregunta == null) {
            return Response.status(422).header("Missing-parameter", "idPruebaArea,idPregunta").build();
        }
        try {
            List<PruebaAreaPregunta> list = pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
            Optional<PruebaAreaPregunta> found = list.stream()
                    .filter(pap -> pap.getIdPregunta() != null && idPregunta.equals(pap.getIdPregunta().getId()))
                    .findFirst();
            if (found.isPresent()) {
                return Response.ok(found.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking pruebaArea " + idPruebaArea + " and pregunta " + idPregunta + " not found")
                        .build();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving PruebaAreaPregunta", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idPruebaArea") Integer idPruebaArea,
            PruebaAreaPregunta entity,
            @Context UriInfo uriInfo) {
        if (idPruebaArea == null) {
            return Response.status(422).header("Missing-parameter", "idPruebaArea").build();
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
            PruebaArea pruebaArea = pruebaAreaDAO.findById(idPruebaArea);
            if (pruebaArea == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "PruebaArea with id " + idPruebaArea + " not found")
                        .build();
            }
            Pregunta pregunta = preguntaDAO.findById(entity.getIdPregunta().getId());
            if (pregunta == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Pregunta with id " + entity.getIdPregunta().getId() + " not found")
                        .build();
            }
            entity.setIdPruebaArea(pruebaArea);
            entity.setIdPregunta(pregunta);
            entity.setFechaAsignacion(OffsetDateTime.now());
            pruebaAreaPreguntaDAO.create(entity);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(entity.getIdPregunta().getId().toString()).build())
                    .entity(entity)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating PruebaAreaPregunta", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @PUT
    @Path("{idPregunta}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idPruebaArea") Integer idPruebaArea,
            @PathParam("idPregunta") UUID idPregunta,
            PruebaAreaPregunta entity) {
        if (idPruebaArea == null || idPregunta == null) {
            return Response.status(422).header("Missing-parameter", "idPruebaArea,idPregunta").build();
        }
        if (entity == null) {
            return Response.status(422).header("Missing-parameter", "entity must not be null").build();
        }
        try {
            List<PruebaAreaPregunta> list = pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
            Optional<PruebaAreaPregunta> foundOpt = list.stream()
                    .filter(pap -> pap.getIdPregunta() != null && idPregunta.equals(pap.getIdPregunta().getId()))
                    .findFirst();
            if (foundOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking pruebaArea " + idPruebaArea + " and pregunta " + idPregunta + " not found")
                        .build();
            }
            PruebaAreaPregunta existing = foundOpt.get();
            // Actualizar solo campos permitidos: orden
            existing.setOrden(entity.getOrden());
            PruebaAreaPregunta updated = pruebaAreaPreguntaDAO.update(existing);
            return Response.ok(updated).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error updating PruebaAreaPregunta", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @DELETE
    @Path("{idPregunta}")
    public Response delete(
            @PathParam("idPruebaArea") Integer idPruebaArea,
            @PathParam("idPregunta") UUID idPregunta) {
        if (idPruebaArea == null || idPregunta == null) {
            return Response.status(422).header("Missing-parameter", "idPruebaArea,idPregunta").build();
        }
        try {
            List<PruebaAreaPregunta> list = pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
            Optional<PruebaAreaPregunta> found = list.stream()
                    .filter(pap -> pap.getIdPregunta() != null && idPregunta.equals(pap.getIdPregunta().getId()))
                    .findFirst();
            if (found.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking pruebaArea " + idPruebaArea + " and pregunta " + idPregunta + " not found")
                        .build();
            }
            pruebaAreaPreguntaDAO.delete(found.get());
            return Response.noContent().build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error deleting PruebaAreaPregunta", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }
}
