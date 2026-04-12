package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;


import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaAreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaDistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaDistractor;

import java.io.Serializable;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("pregunta/{idPregunta}/distractor")
public class PreguntaDistractorResource implements Serializable {
    @Inject
    PreguntaDistractorDAO preguntaDistractorDAO;

    @Inject
    PreguntaDAO preguntaDAO;

    @Inject
    DistractorDAO distractorDAO;

    private static final Logger LOG = Logger.getLogger(PreguntaDistractorResource.class.getName());


    // Listar distractores asignados a una pregunta (paginado)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByPregunta(
            @PathParam("idPregunta") UUID idPregunta,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {
        if (idPregunta == null) {
            return Response.status(422).header("Missing-parameter", "idPregunta").build();
        }
        if (first < 0 || max <= 0 || max > 10) {
            return Response.status(422).header("Missing-parameter", "first,max").build();
        }
        try {
            // validar existencia de pregunta padre
            Pregunta preg = preguntaDAO.findById(idPregunta);
            if (preg == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Pregunta with id " + idPregunta + " not found")
                        .build();
            }
            List<PreguntaDistractor> list = preguntaDistractorDAO.findByIdPregunta(idPregunta, first, max);
            int total = preguntaDistractorDAO.count(); // nota: count global (si necesita count filtrado, agregar DAO)
            return Response.ok(list)
                    .header("X-Total-Count", total)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving PreguntaDistractor range", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    // Obtener una asociación específica por idDistractor
    @GET
    @Path("{idDistractor}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idPregunta") UUID idPregunta,
            @PathParam("idDistractor") UUID idDistractor) {

        if (idPregunta == null || idDistractor == null) {
            return Response.status(422).header("Missing-parameter", "idPregunta,idDistractor").build();
        }

        try {
            List<PreguntaDistractor> list = preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);
            Optional<PreguntaDistractor> found = list.stream()
                    .filter(pd -> pd.getIdDistractor() != null && idDistractor.equals(pd.getIdDistractor().getId()))
                    .findFirst();
            if (found.isPresent()) {
                return Response.ok(found.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking pregunta " + idPregunta + " and distractor " + idDistractor + " not found")
                        .build();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving PreguntaDistractor", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }


    // Crear asociación: POST /pregunta/{idPregunta}/distractor
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idPregunta") UUID idPregunta,
            PreguntaDistractor entity,
            @Context UriInfo uriInfo) {

        if (idPregunta == null) {
            return Response.status(422).header("Missing-parameter", "idPregunta").build();
        }
        if (entity == null) {
            return Response.status(422).header("Missing-parameter", "entity must not be null").build();
        }
        if (entity.getId() != null) {
            return Response.status(422).header("Missing-parameter", "entity.id must be null").build();
        }
        if (entity.getIdDistractor() == null ) {
            return Response.status(422).header("Missing-parameter", "idDistractor must be provided in body").build();
        }

        try {
            Pregunta preg = preguntaDAO.findById(idPregunta);
            if (preg == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Pregunta with id " + idPregunta + " not found")
                        .build();
            }
            Distractor dist = distractorDAO.findById(entity.getIdDistractor().getId());
            if (dist == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Distractor with id " + entity.getIdDistractor().getId() + " not found")
                        .build();
            }

            // Setear referencias y campos necesarios
            entity.setIdPregunta(preg);
            entity.setIdDistractor(dist);
            entity.setFechaAsignacion(OffsetDateTime.now());

            preguntaDistractorDAO.create(entity);
            URI created = uriInfo.getAbsolutePathBuilder().path(dist.getId().toString()).build();
            return Response.created(created).entity(entity).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating PreguntaDistractor", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    // Actualizar asociación (por ejemplo: orden)
    @PUT
    @Path("{idDistractor}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idPregunta") UUID idPregunta,
            @PathParam("idDistractor") UUID idDistractor,
            PreguntaDistractor entity) {

        if (idPregunta == null || idDistractor == null) {
            return Response.status(422).header("Missing-parameter", "idPregunta,idDistractor").build();
        }
        if (entity == null) {
            return Response.status(422).header("Missing-parameter", "entity must not be null").build();
        }

        try {
            List<PreguntaDistractor> list = preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);
            Optional<PreguntaDistractor> foundOpt = list.stream()
                    .filter(pd -> pd.getIdDistractor() != null && idDistractor.equals(pd.getIdDistractor().getId()))
                    .findFirst();

            if (foundOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking pregunta " + idPregunta + " and distractor " + idDistractor + " not found")
                        .build();
            }

            PreguntaDistractor existing = foundOpt.get();
            // Solo actualizar campos permitidos (ejemplo: orden)
            existing.setOrden(entity.getOrden());
            PreguntaDistractor updated = preguntaDistractorDAO.update(existing);
            return Response.ok(updated).build();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error updating PreguntaDistractor", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    // Eliminar asociación entre pregunta y distractor
    @DELETE
    @Path("{idDistractor}")
    public Response delete(
            @PathParam("idPregunta") UUID idPregunta,
            @PathParam("idDistractor") UUID idDistractor) {

        if (idPregunta == null || idDistractor == null) {
            return Response.status(422).header("Missing-parameter", "idPregunta,idDistractor").build();
        }

        try {
            List<PreguntaDistractor> list = preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);
            Optional<PreguntaDistractor> found = list.stream()
                    .filter(pd -> pd.getIdDistractor() != null && idDistractor.equals(pd.getIdDistractor().getId()))
                    .findFirst();

            if (found.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking pregunta " + idPregunta + " and distractor " + idDistractor + " not found")
                        .build();
            }

            preguntaDistractorDAO.delete(found.get());
            return Response.noContent().build();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error deleting PreguntaDistractor", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

}
