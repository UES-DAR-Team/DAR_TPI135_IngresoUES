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
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("pruebaArea/{idPruebaArea}/pregunta")
public class PruebaAreaPreguntaResource extends AbstractResource implements Serializable {
    @Inject
    PruebaAreaPreguntaDAO pruebaAreaPreguntaDAO;

    @Inject
    PruebaAreaDAO pruebaAreaDAO;

    @Inject
    PreguntaDAO preguntaDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idPruebaArea") Integer idPruebaArea,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (idPruebaArea == null) return unprocessable("idPruebaArea");
        if (first < 0 || max <= 0 || max > 10) return unprocessable("first, max");


        // validar existencia de pruebaArea padre
        PruebaArea pruebaArea = pruebaAreaDAO.findById(idPruebaArea);
        if (pruebaArea == null) return notFound(idPruebaArea.toString(), "PruebaArea");

        List<PruebaAreaPregunta> encontrados = pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, first, max);
        int total = pruebaAreaPreguntaDAO.count();
        return Response.ok(encontrados)
                .header(X_TOTAL_COUNT, total)
                .build();
    }

    @GET
    @Path("{idPregunta}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idPruebaArea") Integer idPruebaArea,
            @PathParam("idPregunta") UUID idPregunta) {
        if (idPruebaArea == null) return unprocessable("idPruebaArea");
        if (idPregunta == null) return unprocessable("idPregunta");


        List<PruebaAreaPregunta> list =
                pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);

        Optional<PruebaAreaPregunta> found = list.stream()
                .filter(pap -> pap.getIdPregunta() != null && idPregunta.equals(pap.getIdPregunta().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("pruebaArea=" + idPruebaArea + ", pregunta=" + idPregunta, "PruebaAreaPregunta");
        }
        return Response.ok(found.get()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idPruebaArea") Integer idPruebaArea,
            PruebaAreaPregunta entity,
            @Context UriInfo uriInfo) {
        if (idPruebaArea == null) return unprocessable("idPruebaArea");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdPregunta() == null || entity.getIdPregunta().getId() == null)
            return unprocessable("entity.idPregunta must be provided in body");

        PruebaArea pruebaArea = pruebaAreaDAO.findById(idPruebaArea);
        if (pruebaArea == null) return notFound(idPruebaArea.toString(), "PruebaArea");

        Pregunta pregunta = preguntaDAO.findById(entity.getIdPregunta().getId());
        if (pregunta == null) return notFound(entity.getIdPregunta().getId().toString(), "Pregunta");

        entity.setIdPruebaArea(pruebaArea);
        entity.setIdPregunta(pregunta);
        entity.setFechaAsignacion(OffsetDateTime.now());

        pruebaAreaPreguntaDAO.create(entity);
        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }

    @PUT
    @Path("{idPregunta}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idPruebaArea") Integer idPruebaArea,
            @PathParam("idPregunta") UUID idPregunta,
            PruebaAreaPregunta entity) {
        if (idPruebaArea == null) return unprocessable("idPruebaArea");
        if (idPregunta == null) return unprocessable("idPregunta");
        if (entity == null) return unprocessable("entity must not be null");


        List<PruebaAreaPregunta> list =
                pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);

        Optional<PruebaAreaPregunta> foundOpt = list.stream()
                .filter(pap -> pap.getIdPregunta() != null
                        && idPregunta.equals(pap.getIdPregunta().getId()))
                .findFirst();

        if (foundOpt.isEmpty()) {
            return notFound("pruebaArea=" + idPruebaArea + ", pregunta=" + idPregunta, "PruebaAreaPregunta");
        }

        PruebaAreaPregunta existing = foundOpt.get();
        // Actualizar solo campos permitidos: orden
        existing.setOrden(entity.getOrden());
        PruebaAreaPregunta updated = pruebaAreaPreguntaDAO.update(existing);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{idPregunta}")
    public Response delete(
            @PathParam("idPruebaArea") Integer idPruebaArea,
            @PathParam("idPregunta") UUID idPregunta) {
        if (idPruebaArea == null) return unprocessable("idPruebaArea");
        if (idPregunta == null) return unprocessable("idPregunta");

        List<PruebaAreaPregunta> list =
                pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);

        Optional<PruebaAreaPregunta> found = list.stream()
                .filter(pap -> pap.getIdPregunta() != null
                        && idPregunta.equals(pap.getIdPregunta().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("pruebaArea=" + idPruebaArea + ", pregunta=" + idPregunta, "PruebaAreaPregunta");
        }
        pruebaAreaPreguntaDAO.delete(found.get());
        return Response.noContent().build();
    }
}