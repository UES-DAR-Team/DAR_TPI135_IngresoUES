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

@Path("pruebaAreaPregunta/{idPruebaAreaPregunta}/distractor")
public class PruebaAreaPreguntaDistractorResource extends AbstractResource implements Serializable {

    @Inject
    PruebaAreaPreguntaDistractorDAO papdDAO;

    @Inject
    PruebaAreaPreguntaDAO pruebaAreaPreguntaDAO;

    @Inject
    DistractorDAO distractorDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {

        if (idPruebaAreaPregunta == null) {
            return unprocessable("idPruebaAreaPregunta");
        }
        if (first < 0) {
            return unprocessable("first");
        }
        if (max <= 0 || max > 10) {
            return unprocessable("max");
        }
        PruebaAreaPregunta padre = pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta);
        if (padre == null) {
            return notFound(idPruebaAreaPregunta.toString(), "PruebaAreaPregunta");
        }
        List<PruebaAreaPreguntaDistractor> encontrados = papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, first, max);
        int total = papdDAO.count();

        return Response.ok(encontrados)
                .header("X-Total-Count", total)
                .build();
    }

    @GET
    @Path("{idDistractor}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
            @PathParam("idDistractor") UUID idDistractor) {

        if (idPruebaAreaPregunta == null) return unprocessable("idPruebaAreaPregunta");
        if (idDistractor == null) return unprocessable("idDistractor");

        List<PruebaAreaPreguntaDistractor> list = papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
        Optional<PruebaAreaPreguntaDistractor> found = list.stream()
                .filter(d -> d.getIdDistractor() != null && idDistractor.equals(d.getIdDistractor().getId()))
                .findFirst();

        if (found.isPresent()) {
            return Response.ok(found.get()).build();
        }

        return notFound("linking pruebaAreaPregunta " + idPruebaAreaPregunta + " and distractor " + idDistractor, "Record");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
            PruebaAreaPreguntaDistractor entity,
            @Context UriInfo uriInfo) {

        if (idPruebaAreaPregunta == null) return unprocessable("idPruebaAreaPregunta");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdDistractor() == null || entity.getIdDistractor().getId() == null) {
            return unprocessable("idDistractor must be provided in body");
        }
        PruebaAreaPregunta padre = pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta);
        if (padre == null) {
            return notFound(idPruebaAreaPregunta.toString(), "PruebaAreaPregunta");
        }
        Distractor dist = distractorDAO.findById(entity.getIdDistractor().getId());
        if (dist == null) {
            return notFound(entity.getIdDistractor().getId().toString(), "Distractor");
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
    }

    @PUT
    @Path("{idDistractor}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
            @PathParam("idDistractor") UUID idDistractor,
            PruebaAreaPreguntaDistractor entity) {

        if (idPruebaAreaPregunta == null) return unprocessable("idPruebaAreaPregunta");
        if (idDistractor == null) return unprocessable("idDistractor");
        if (entity == null) return unprocessable("entity must not be null");

        List<PruebaAreaPreguntaDistractor> list = papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
        Optional<PruebaAreaPreguntaDistractor> foundOpt = list.stream()
                .filter(d -> d.getIdDistractor() != null && idDistractor.equals(d.getIdDistractor().getId()))
                .findFirst();

        if (foundOpt.isEmpty()) {
            return notFound("linking pruebaAreaPregunta " + idPruebaAreaPregunta + " and distractor " + idDistractor, "Record");
        }
        PruebaAreaPreguntaDistractor existing = foundOpt.get();
        existing.setEsRespuestaCorrecta(entity.getEsRespuestaCorrecta());

        PruebaAreaPreguntaDistractor updated = papdDAO.update(existing);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{idDistractor}")
    public Response delete(
            @PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
            @PathParam("idDistractor") UUID idDistractor) {

        if (idPruebaAreaPregunta == null) return unprocessable("idPruebaAreaPregunta");
        if (idDistractor == null) return unprocessable("idDistractor");
        List<PruebaAreaPreguntaDistractor> list = papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
        Optional<PruebaAreaPreguntaDistractor> found = list.stream()
                .filter(d -> d.getIdDistractor() != null && idDistractor.equals(d.getIdDistractor().getId()))
                .findFirst();
        if (found.isEmpty()) {
            return notFound("linking pruebaAreaPregunta " + idPruebaAreaPregunta + " and distractor " + idDistractor, "Record");
        }
        papdDAO.delete(found.get());
        return Response.noContent().build();
    }
}