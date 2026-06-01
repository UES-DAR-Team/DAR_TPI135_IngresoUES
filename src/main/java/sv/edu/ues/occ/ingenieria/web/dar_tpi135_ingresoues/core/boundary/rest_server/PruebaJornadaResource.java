package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

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
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("prueba/{idPrueba}/jornada")
public class PruebaJornadaResource implements Serializable {

    @Inject
    PruebaJornadaDAO pruebaJornadaDAO;

    @Inject
    PruebaDAO pruebaDAO;

    @Inject
    JornadaDAO jornadaDAO;

    private static final Logger LOG = Logger.getLogger(PruebaJornadaResource.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idPrueba") UUID idPrueba,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {

        if (idPrueba == null) {
            return Response.status(422).header("Missing-parameter", "idPrueba").build();
        }
        try {
            Prueba prueba = pruebaDAO.findById(idPrueba);
            if (prueba == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Prueba with id " + idPrueba + " not found")
                        .build();
            }
            List<PruebaJornada> encontrados = pruebaJornadaDAO.findByPrueba(idPrueba, first, max);
            int total = pruebaJornadaDAO.count();
            return Response.ok(encontrados)
                    .header("X-Total-Count", total)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving PruebaJornada range", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }


    @GET
    @Path("{idJornada}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idJornada") UUID idJornada) {

        if (idPrueba == null || idJornada == null) {
            return Response.status(422).header("Missing-parameter", "idPrueba,idJornada").build();
        }
        try {
            List<PruebaJornada> list = pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1);
            if (!list.isEmpty()) {
                return Response.ok(list.get(0)).build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Not-found-id", "Record linking prueba " + idPrueba + " and jornada " + idJornada + " not found")
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving PruebaJornada", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idPrueba") UUID idPrueba,
            PruebaJornada entity,
            @Context UriInfo uriInfo) {
        if (idPrueba == null) {
            return Response.status(422).header("Missing-parameter", "idPrueba").build();
        }
        if (entity == null) {
            return Response.status(422).header("Missing-parameter", "entity must not be null").build();
        }
        if (entity.getId() != null) {
            return Response.status(422).header("Missing-parameter", "entity.id must be null").build();
        }
        if (entity.getIdJornada() == null || entity.getIdJornada().getId() == null) {
            return Response.status(422).header("Missing-parameter", "idJornada must be provided in body").build();
        }
        try {
            Prueba prueba = pruebaDAO.findById(idPrueba);
            if (prueba == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Prueba with id " + idPrueba + " not found")
                        .build();
            }
            Jornada jornada = jornadaDAO.findById(entity.getIdJornada().getId());
            if (jornada == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Jornada with id " + entity.getIdJornada().getId() + " not found")
                        .build();
            }
            entity.setIdPrueba(prueba);
            entity.setIdJornada(jornada);
            if (entity.getFechaAsignacion() == null) {
                entity.setFechaAsignacion(OffsetDateTime.now());
            }
            pruebaJornadaDAO.create(entity);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(jornada.getId().toString()).build())
                    .entity(entity)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating PruebaJornada", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @PUT
    @Path("{idJornada}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idJornada") UUID idJornada,
            PruebaJornada entity) {
        if (idPrueba == null || idJornada == null || entity == null) {
            return Response.status(422).header("Missing-parameter", "idPrueba,idJornada,entity").build();
        }
        try {
            List<PruebaJornada> list = pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1);
            if (list.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking prueba " + idPrueba + " and jornada " + idJornada + " not found")
                        .build();
            }
            PruebaJornada existing = list.get(0);
            if (entity.getFechaAsignacion() != null) {
                existing.setFechaAsignacion(entity.getFechaAsignacion());
            }
            PruebaJornada updated = pruebaJornadaDAO.update(existing);
            return Response.ok(updated).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error updating PruebaJornada", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @DELETE
    @Path("{idJornada}")
    public Response delete(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idJornada") UUID idJornada) {
        if (idPrueba == null || idJornada == null) {
            return Response.status(422).header("Missing-parameter", "idPrueba,idJornada").build();
        }
        try {
            List<PruebaJornada> list = pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1);
            if (list.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking prueba " + idPrueba + " and jornada " + idJornada + " not found")
                        .build();
            }
            pruebaJornadaDAO.delete(list.get(0));
            return Response.noContent().build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error deleting PruebaJornada", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }
}