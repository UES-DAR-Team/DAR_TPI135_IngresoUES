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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("pregunta/{idPregunta}/distractor")
public class PreguntaDistractorResource extends AbstractResource implements Serializable {
    @Inject
    PreguntaDistractorDAO preguntaDistractorDAO;

    @Inject
    PreguntaDAO preguntaDAO;

    @Inject
    DistractorDAO distractorDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByPregunta(
            @PathParam("idPregunta") UUID idPregunta,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {
        if (idPregunta == null) return unprocessable("idPregunta");
        if (first < 0 || max <= 0 || max > 10) return unprocessable("first, max");

        Pregunta preg = preguntaDAO.findById(idPregunta);
        if (preg == null) return notFound(idPregunta.toString(), "Pregunta");

        List<PreguntaDistractor> list = preguntaDistractorDAO.findByIdPregunta(idPregunta, first, max);
        int total = preguntaDistractorDAO.count();
        return Response.ok(list)
                .header(X_TOTAL_COUNT, total)
                .build();
    }

    @GET
    @Path("{idDistractor}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idPregunta") UUID idPregunta,
            @PathParam("idDistractor") UUID idDistractor) {
        if (idPregunta == null) return unprocessable("idPregunta");
        if (idDistractor == null) return unprocessable("idDistractor");

        List<PreguntaDistractor> list =
                preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);

        Optional<PreguntaDistractor> found = list.stream()
                .filter(pd -> pd.getIdDistractor() != null
                        && idDistractor.equals(pd.getIdDistractor().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("pregunta=" + idPregunta + ", distractor=" + idDistractor, "PreguntaDistractor");
        }
        return Response.ok(found.get()).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idPregunta") UUID idPregunta,
            PreguntaDistractor entity,
            @Context UriInfo uriInfo) {

        if (idPregunta == null) return unprocessable("idPregunta");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdDistractor() == null) return unprocessable("entity.idDistractor must be provided in body");

        Pregunta preg = preguntaDAO.findById(idPregunta);
        if (preg == null) return notFound(idPregunta.toString(), "Pregunta");

        Distractor dist = distractorDAO.findById(entity.getIdDistractor().getId());
        if (dist == null) return notFound(entity.getIdDistractor().getId().toString(), "Distractor");

        // Setear referencias y campos necesarios
        entity.setIdPregunta(preg);
        entity.setIdDistractor(dist);

        preguntaDistractorDAO.create(entity);

        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }

    @PUT
    @Path("{idDistractor}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idPregunta") UUID idPregunta,
            @PathParam("idDistractor") UUID idDistractor,
            PreguntaDistractor entity) {

        if (idPregunta == null) return unprocessable("idPregunta");
        if (idDistractor == null) return unprocessable("idDistractor");
        if (entity == null) return unprocessable("entity must not be null");

        List<PreguntaDistractor> list =
                preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);

        Optional<PreguntaDistractor> foundOpt = list.stream()
                .filter(pd -> pd.getIdDistractor() != null
                        && idDistractor.equals(pd.getIdDistractor().getId()))
                .findFirst();

        if (foundOpt.isEmpty()) {
            return notFound("pregunta=" + idPregunta + ", distractor=" + idDistractor,
                    "PreguntaDistractor");
        }

        PreguntaDistractor existing = foundOpt.get();
        // Solo actualizar campos permitidos (ejemplo: EsCorrecto)
        existing.setEsCorrecto(entity.getEsCorrecto());
        PreguntaDistractor updated = preguntaDistractorDAO.update(existing);
        return Response.ok(updated).build();

    }

    // Eliminar asociación entre pregunta y distractor
    @DELETE
    @Path("{idDistractor}")
    public Response delete(
            @PathParam("idPregunta") UUID idPregunta,
            @PathParam("idDistractor") UUID idDistractor) {

        if (idPregunta == null) return unprocessable("idPregunta");
        if (idDistractor == null) return unprocessable("idDistractor");

        List<PreguntaDistractor> list =
                preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);

        Optional<PreguntaDistractor> found = list.stream()
                .filter(pd -> pd.getIdDistractor() != null &&
                        idDistractor.equals(pd.getIdDistractor().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("pregunta="+idPregunta+", distractor="+idDistractor,
                    "PreguntaDistractor");
        }

        preguntaDistractorDAO.delete(found.get());
        return Response.noContent().build();
    }

}
