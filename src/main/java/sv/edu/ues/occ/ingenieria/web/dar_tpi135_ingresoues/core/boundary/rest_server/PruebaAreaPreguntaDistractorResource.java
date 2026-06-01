package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaPreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaPreguntaDistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPreguntaDistractor;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("pruebaAreaPregunta/{idPruebaAreaPregunta}/distractor")
public class PruebaAreaPreguntaDistractorResource implements Serializable {

    @Inject
    PruebaAreaPreguntaDistractorDAO papdDAO;

    @Inject
    PruebaAreaPreguntaDAO pruebaAreaPreguntaDAO;

    @Inject
    DistractorDAO distractorDAO;

    private static final Logger LOG = Logger.getLogger(PruebaAreaPreguntaDistractorResource.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {

        if (idPruebaAreaPregunta == null) {
            return Response.status(422).header("Missing-parameter", "idPruebaAreaPregunta").build();
        }
        if (first < 0 || max <= 0 || max > 10) {
            return Response.status(422).header("Missing-parameter", "first,max").build();
        }
        try {
            PruebaAreaPregunta padre = pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta);
            if (padre == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "PruebaAreaPregunta with id " + idPruebaAreaPregunta + " not found")
                        .build();
            }
            List<PruebaAreaPreguntaDistractor> encontrados = papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, first, max);
            int total = papdDAO.count();
            return Response.ok(encontrados)
                    .header("X-Total-Count", total)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving PruebaAreaPreguntaDistractor range", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @GET
    @Path("{idDistractor}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
            @PathParam("idDistractor") UUID idDistractor) {

        if (idPruebaAreaPregunta == null || idDistractor == null) {
            return Response.status(422).header("Missing-parameter", "idPruebaAreaPregunta,idDistractor").build();
        }

        try {
            List<PruebaAreaPreguntaDistractor> list = papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
            Optional<PruebaAreaPreguntaDistractor> found = list.stream()
                    .filter(d -> d.getIdDistractor() != null && idDistractor.equals(d.getIdDistractor().getId()))
                    .findFirst();

            if (found.isPresent()) {
                return Response.ok(found.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking pruebaAreaPregunta " + idPruebaAreaPregunta + " and distractor " + idDistractor + " not found")
                        .build();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving PruebaAreaPreguntaDistractor", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
            PruebaAreaPreguntaDistractor entity,
            @Context UriInfo uriInfo) {

        if (idPruebaAreaPregunta == null) {
            return Response.status(422).header("Missing-parameter", "idPruebaAreaPregunta").build();
        }
        if (entity == null) {
            return Response.status(422).header("Missing-parameter", "entity must not be null").build();
        }
        if (entity.getId() != null) {
            return Response.status(422).header("Missing-parameter", "entity.id must be null").build();
        }
        if (entity.getIdDistractor() == null || entity.getIdDistractor().getId() == null) {
            return Response.status(422).header("Missing-parameter", "idDistractor must be provided in body").build();
        }

        try {
            PruebaAreaPregunta padre = pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta);
            if (padre == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "PruebaAreaPregunta with id " + idPruebaAreaPregunta + " not found")
                        .build();
            }
            Distractor dist = distractorDAO.findById(entity.getIdDistractor().getId());
            if (dist == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Distractor with id " + entity.getIdDistractor().getId() + " not found")
                        .build();
            }
            entity.setIdPruebaAreaPregunta(padre);
            entity.setIdDistractor(dist);
            if (entity.getFechaRegistro() == null) {
                entity.setFechaRegistro(OffsetDateTime.now());
            }
            papdDAO.create(entity);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(dist.getId().toString()).build())
                    .entity(entity)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating PruebaAreaPreguntaDistractor", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @PUT
    @Path("{idDistractor}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
            @PathParam("idDistractor") UUID idDistractor,
            PruebaAreaPreguntaDistractor entity) {

        if (idPruebaAreaPregunta == null || idDistractor == null) {
            return Response.status(422).header("Missing-parameter", "idPruebaAreaPregunta,idDistractor").build();
        }
        if (entity == null) {
            return Response.status(422).header("Missing-parameter", "entity must not be null").build();
        }

        try {
            List<PruebaAreaPreguntaDistractor> list = papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
            Optional<PruebaAreaPreguntaDistractor> foundOpt = list.stream()
                    .filter(d -> d.getIdDistractor() != null && idDistractor.equals(d.getIdDistractor().getId()))
                    .findFirst();

            if (foundOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking pruebaAreaPregunta " + idPruebaAreaPregunta + " and distractor " + idDistractor + " not found")
                        .build();
            }
            PruebaAreaPreguntaDistractor existing = foundOpt.get();
            existing.setEsRespuestaCorrecta(entity.getEsRespuestaCorrecta());
            PruebaAreaPreguntaDistractor updated = papdDAO.update(existing);
            return Response.ok(updated).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error updating PruebaAreaPreguntaDistractor", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @DELETE
    @Path("{idDistractor}")
    public Response delete(
            @PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
            @PathParam("idDistractor") UUID idDistractor) {

        if (idPruebaAreaPregunta == null || idDistractor == null) {
            return Response.status(422).header("Missing-parameter", "idPruebaAreaPregunta,idDistractor").build();
        }
        try {
            List<PruebaAreaPreguntaDistractor> list = papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
            Optional<PruebaAreaPreguntaDistractor> found = list.stream()
                    .filter(d -> d.getIdDistractor() != null && idDistractor.equals(d.getIdDistractor().getId()))
                    .findFirst();
            if (found.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking pruebaAreaPregunta " + idPruebaAreaPregunta + " and distractor " + idDistractor + " not found")
                        .build();
            }
            papdDAO.delete(found.get());
            return Response.noContent().build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error deleting PruebaAreaPreguntaDistractor", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }
}