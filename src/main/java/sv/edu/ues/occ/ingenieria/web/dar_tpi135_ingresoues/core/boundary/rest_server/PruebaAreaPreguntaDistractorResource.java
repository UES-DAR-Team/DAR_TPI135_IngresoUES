package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaPreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaPreguntaDistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPreguntaDistractor;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("pruebaAreaPregunta/{idPruebaAreaPregunta}/distractor")
public class PruebaAreaPreguntaDistractorResource extends AbstractResource implements Serializable {

    @Inject
    PruebaAreaPreguntaDistractorDAO pruebaAreaPreguntaDistractorDAO;

    @Inject
    PruebaAreaPreguntaDAO pruebaAreaPreguntaDAO;

    @Inject
    DistractorDAO distractorDAO;

    //falta findRange

    //buscar respecto a idPruebaAreaPregunta
    @GET
    @Path("{idDistractor}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
                             @PathParam("idDistractor") UUID idDistractor) {

        if (idPruebaAreaPregunta == null) return unprocessable("idPruebaAreaPregunta");
        if (idDistractor == null) return unprocessable("idDistractor");

        List<PruebaAreaPreguntaDistractor> list =
                pruebaAreaPreguntaDistractorDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);

        Optional<PruebaAreaPreguntaDistractor> found = list.stream()
                .filter(papd -> papd.getIdDistractor() != null
                        && idDistractor.equals(papd.getIdDistractor().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("pruebaAreaPregunta=" + idPruebaAreaPregunta + ", distractor=" + idDistractor, "PuebaAreaPreguntaDistractor");
        }
        return Response.ok(found.get()).build();

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
                           PruebaAreaPreguntaDistractor entity,
                           @Context UriInfo uriInfo) {

        if (idPruebaAreaPregunta == null) return unprocessable("idPruebaAreaPregunta");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdDistractor() == null || entity.getIdDistractor().getId() == null)
            return unprocessable("entity.idDistractor.id must be provided in body");

        PruebaAreaPregunta pap = pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta);
        if (pap == null) return notFound(idPruebaAreaPregunta.toString(), "PruebaAreaPreguntaDistractor");

        Distractor distractor = distractorDAO.findById(entity.getIdDistractor().getId());
        if (distractor == null) return notFound(entity.getIdDistractor().getId().toString(), "Distractor");

        entity.setIdPruebaAreaPregunta(pap);
        entity.setIdDistractor(distractor);

        pruebaAreaPreguntaDistractorDAO.create(entity);

        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }

    @PUT
    @Path("{idDistractor}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
                           @PathParam("idDistractor") UUID idDistractor,
                           PruebaAreaPreguntaDistractor entity) {
        if (idPruebaAreaPregunta == null) return unprocessable("idPruebaAreaPregunta");
        if (idDistractor == null) return unprocessable("idDistractor");
        if (entity == null) return unprocessable("entity must not be null");
        
        List<PruebaAreaPreguntaDistractor> list =
                pruebaAreaPreguntaDistractorDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
        
        Optional<PruebaAreaPreguntaDistractor> foundPap = list.stream()
                .filter(papd -> papd.getIdDistractor() != null
                        && idDistractor.equals(papd.getIdDistractor().getId())).findFirst();

        if (foundPap.isEmpty()) {
            return notFound("pruebaAreaPregunta=" + idPruebaAreaPregunta + ", distractor=" + idDistractor, "PuebaAreaPreguntaDistractor");
        }
        
        PruebaAreaPreguntaDistractor existing = foundPap.get();
        existing.setEsRespuestaCorrecta(entity.getEsRespuestaCorrecta());
        PruebaAreaPreguntaDistractor updated = pruebaAreaPreguntaDistractorDAO.update(entity);
        return Response.ok(updated).build();
    }
    
    @DELETE
    @Path("{idDistractor}")
    public Response delete(
            @PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
            @PathParam("idDistractor") UUID idDistractor
    ){

        if (idPruebaAreaPregunta == null) return unprocessable("idPruebaAreaPregunta");
        if (idDistractor == null) return unprocessable("idDistractor");

        List<PruebaAreaPreguntaDistractor> list =
                pruebaAreaPreguntaDistractorDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);

        Optional<PruebaAreaPreguntaDistractor> found = list.stream()
                .filter(papd -> papd.getIdDistractor() != null
                        && idDistractor.equals(papd.getIdDistractor().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("pruebaAreaPregunta=" + idPruebaAreaPregunta + ", distractor=" + idDistractor, "PuebaAreaPreguntaDistractor");
        }

        pruebaAreaPreguntaDistractorDAO.delete(found.get());
        return Response.noContent().build();
    }
}