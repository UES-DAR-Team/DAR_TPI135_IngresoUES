package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaJornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaJornada;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Path("prueba/{idPrueba}/jornada")
public class PruebaJornadaResource extends AbstractResource implements Serializable {

    @Inject
    PruebaJornadaDAO pruebaJornadaDAO;

    @Inject
    PruebaDAO pruebaDAO;

    @Inject
    JornadaDAO jornadaDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idPrueba") UUID idPrueba,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {

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
        List<PruebaJornada> encontrados = pruebaJornadaDAO.findByPrueba(idPrueba, first, max);
        int total = pruebaJornadaDAO.count();
        return Response.ok(encontrados)
                .header("X-Total-Count", total)
                .build();
    }

    @GET
    @Path("{idJornada}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idJornada") UUID idJornada) {

        if (idPrueba == null) return unprocessable("idPrueba");
        if (idJornada == null) return unprocessable("idJornada");

        List<PruebaJornada> list = pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1);
        if (list.isEmpty()) {
            return notFound("linking prueba " + idPrueba + " and jornada " + idJornada, "Record");
        }

        return Response.ok(list.get(0)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idPrueba") UUID idPrueba,
            PruebaJornada entity,
            @Context UriInfo uriInfo) {

        if (idPrueba == null) return unprocessable("idPrueba");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdJornada() == null || entity.getIdJornada().getId() == null) {
            return unprocessable("idJornada must be provided in body");
        }
        Prueba prueba = pruebaDAO.findById(idPrueba);
        if (prueba == null) {
            return notFound(idPrueba.toString(), "Prueba");
        }
        Jornada jornada = jornadaDAO.findById(entity.getIdJornada().getId());
        if (jornada == null) {
            return notFound(entity.getIdJornada().getId().toString(), "Jornada");
        }
        entity.setIdPrueba(prueba);
        entity.setIdJornada(jornada);
        if (entity.getFechaAsignacion() == null) {
            entity.setFechaAsignacion(OffsetDateTime.now());
        }
        pruebaJornadaDAO.create(entity);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(jornada.getId().toString()).build())
                .build();
    }

    @PUT
    @Path("{idJornada}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idJornada") UUID idJornada,
            PruebaJornada entity) {

        if (idPrueba == null) return unprocessable("idPrueba");
        if (idJornada == null) return unprocessable("idJornada");
        if (entity == null) return unprocessable("entity must not be null");

        List<PruebaJornada> list = pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1);
        if (list.isEmpty()) {
            return notFound("linking prueba " + idPrueba + " and jornada " + idJornada, "Record");
        }
        PruebaJornada existing = list.get(0);
        if (entity.getFechaAsignacion() != null) {
            existing.setFechaAsignacion(entity.getFechaAsignacion());
        }
        PruebaJornada updated = pruebaJornadaDAO.update(existing);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{idJornada}")
    public Response delete(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idJornada") UUID idJornada) {

        if (idPrueba == null) return unprocessable("idPrueba");
        if (idJornada == null) return unprocessable("idJornada");
        List<PruebaJornada> list = pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1);
        if (list.isEmpty()) {
            return notFound("linking prueba " + idPrueba + " and jornada " + idJornada, "Record");
        }
        pruebaJornadaDAO.delete(list.get(0));
        return Response.noContent().build();
    }
}