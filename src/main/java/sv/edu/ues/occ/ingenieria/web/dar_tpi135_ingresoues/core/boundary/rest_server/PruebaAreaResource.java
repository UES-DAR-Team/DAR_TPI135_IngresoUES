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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaArea;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("prueba/{idPrueba}/areaConocimiento")
public class PruebaAreaResource extends AbstractResource implements Serializable {

    @Inject
    PruebaAreaDAO pruebaAreaDAO;

    @Inject
    PruebaDAO pruebaDAO;

    @Inject
    AreaConocimientoDAO areaConocimientoDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idPrueba") UUID idPrueba,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    )
    {
        if (idPrueba == null) {
            return unprocessable("idPrueba");
        }
        if (first < 0) {
            return unprocessable("first");
        }
        if (max <= 0 || max > 10) {
            return unprocessable("max");
        }
        Prueba prueba = pruebaDAO.findById(idPrueba);
        if (prueba == null) {
            return notFound(idPrueba.toString(), "Prueba");
        }
        List<PruebaArea> encontrados = pruebaAreaDAO.findByPrueba(idPrueba, first, max);
        int total = pruebaAreaDAO.count();
        return Response.ok(encontrados)
                .header("X-Total-Count", total)
                .build();
    }


    @GET
    @Path("{idAreaConocimiento}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento) {

        if (idPrueba == null || idAreaConocimiento == null) {
            return unprocessable("idPrueba,idAreaConocimiento");
        }
        List<PruebaArea> list = pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        Optional<PruebaArea> found = list.stream()
                .filter(pa -> pa.getIdAreaConocimiento() != null && idAreaConocimiento.equals(pa.getIdAreaConocimiento().getId()))
                .findFirst();
        if (found.isPresent()) {
            return Response.ok(found.get()).build();
        }
        return notFound("linking prueba " + idPrueba + " and area " + idAreaConocimiento, "Record");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idPrueba") UUID idPrueba,
            PruebaArea entity,
            @Context UriInfo uriInfo) {

        if (idPrueba == null) return unprocessable("idPrueba");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdAreaConocimiento() == null || entity.getIdAreaConocimiento().getId() == null) {
            return unprocessable("idAreaConocimiento must be provided in body");
        }
        Prueba prueba = pruebaDAO.findById(idPrueba);
        if (prueba == null) {
            return notFound(idPrueba.toString(), "Prueba");
        }
        AreaConocimiento area = areaConocimientoDAO.findById(entity.getIdAreaConocimiento().getId());
        if (area == null) {
            return notFound(entity.getIdAreaConocimiento().getId().toString(), "AreaConocimiento");
        }
        entity.setIdPrueba(prueba);
        entity.setIdAreaConocimiento(area);
        if (entity.getFechaAsignacion() == null) {
            entity.setFechaAsignacion(OffsetDateTime.now());
        }
        pruebaAreaDAO.create(entity);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(entity.getIdAreaConocimiento().getId().toString()).build())
                .build();
    }

    @PUT
    @Path("{idAreaConocimiento}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            PruebaArea entity) {

        if (idPrueba == null || idAreaConocimiento == null) {
            return unprocessable("idPrueba,idAreaConocimiento");
        }
        if (entity == null) {
            return unprocessable("entity must not be null");
        }
        List<PruebaArea> list = pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        Optional<PruebaArea> foundOpt = list.stream()
                .filter(pa -> pa.getIdAreaConocimiento() != null && idAreaConocimiento.equals(pa.getIdAreaConocimiento().getId()))
                .findFirst();
        if (foundOpt.isEmpty()) {
            return notFound("linking prueba " + idPrueba + " and area " + idAreaConocimiento, "Record");
        }
        PruebaArea existing = foundOpt.get();
        existing.setNumPreguntas(entity.getNumPreguntas());
        PruebaArea updated = pruebaAreaDAO.update(existing);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{idAreaConocimiento}")
    public Response delete(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento) {

        if (idPrueba == null || idAreaConocimiento == null) {
            return unprocessable("idPrueba,idAreaConocimiento");
        }
        List<PruebaArea> list = pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        Optional<PruebaArea> found = list.stream()
                .filter(pa -> pa.getIdAreaConocimiento() != null && idAreaConocimiento.equals(pa.getIdAreaConocimiento().getId()))
                .findFirst();
        if (found.isEmpty()) {
            return notFound("linking prueba " + idPrueba + " and area " + idAreaConocimiento, "Record");
        }
        pruebaAreaDAO.delete(found.get());
        return Response.noContent().build();
    }
}