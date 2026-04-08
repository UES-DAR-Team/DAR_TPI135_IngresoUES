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
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("prueba/{idPrueba}/areaConocimiento")
public class PruebaAreaResource implements Serializable {
    @Inject
    PruebaAreaDAO pruebaAreaDAO;

    @Inject
    PruebaDAO pruebaDAO;

    @Inject
    AreaConocimientoDAO areaConocimientoDAO;

    private static final Logger LOG = Logger.getLogger(PruebaAreaResource.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idPrueba") UUID idPrueba,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (idPrueba == null) {
            return Response.status(422).header("Missing-parameter", "idPrueba").build();
        }
        if (first < 0 || max <= 0 || max > 10) {
            return Response.status(422).header("Missing-parameter", "first,max").build();
        }
        try {
            Prueba prueba = pruebaDAO.findById(idPrueba);
            if (prueba == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Prueba with id " + idPrueba + " not found")
                        .build();
            }
            List<PruebaArea> encontrados = pruebaAreaDAO.findByPrueba(idPrueba, first, max);
            int total = pruebaAreaDAO.count();
            return Response.ok(encontrados)
                    .header("X-Total-Count", total)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving PruebaArea range", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @GET
    @Path("{idAreaConocimiento}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento) {
        if (idPrueba == null || idAreaConocimiento == null) {
            return Response.status(422).header("Missing-parameter", "idPrueba,idAreaConocimiento").build();
        }
        try {
            List<PruebaArea> list = pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
            Optional<PruebaArea> found = list.stream()
                    .filter(pa -> pa.getIdAreaConocimiento() != null && idAreaConocimiento.equals(pa.getIdAreaConocimiento().getId()))
                    .findFirst();
            if (found.isPresent()) {
                return Response.ok(found.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking prueba " + idPrueba + " and area " + idAreaConocimiento + " not found")
                        .build();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving PruebaArea", ex);
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
            PruebaArea entity,
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
        if (entity.getIdAreaConocimiento() == null || entity.getIdAreaConocimiento().getId() == null) {
            return Response.status(422).header("Missing-parameter", "idAreaConocimiento must be provided in body").build();
        }
        try {
            Prueba prueba = pruebaDAO.findById(idPrueba);
            if (prueba == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Prueba with id " + idPrueba + " not found")
                        .build();
            }
            AreaConocimiento area = areaConocimientoDAO.findById(entity.getIdAreaConocimiento().getId());
            if (area == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "AreaConocimiento with id " + entity.getIdAreaConocimiento().getId() + " not found")
                        .build();
            }
            entity.setIdPrueba(prueba);
            entity.setIdAreaConocimiento(area);
            entity.setFechaAsignacion(OffsetDateTime.now());
            pruebaAreaDAO.create(entity);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(entity.getIdAreaConocimiento().getId().toString()).build())
                    .entity(entity)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating PruebaArea", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    // Actualizar asociación: permite actualizar numPreguntas
    @PUT
    @Path("{idAreaConocimiento}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            PruebaArea entity) {
        if (idPrueba == null || idAreaConocimiento == null) {
            return Response.status(422).header("Missing-parameter", "idPrueba,idAreaConocimiento").build();
        }
        if (entity == null) {
            return Response.status(422).header("Missing-parameter", "entity must not be null").build();
        }
        try {
            List<PruebaArea> list = pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
            Optional<PruebaArea> foundOpt = list.stream()
                    .filter(pa -> pa.getIdAreaConocimiento() != null && idAreaConocimiento.equals(pa.getIdAreaConocimiento().getId()))
                    .findFirst();
            if (foundOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking prueba " + idPrueba + " and area " + idAreaConocimiento + " not found")
                        .build();
            }
            PruebaArea existing = foundOpt.get();
            // Actualizar solo campos permitidos: numPreguntas
            existing.setNumPreguntas(entity.getNumPreguntas());
            PruebaArea updated = pruebaAreaDAO.update(existing);
            return Response.ok(updated).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error updating PruebaArea", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @DELETE
    @Path("{idAreaConocimiento}")
    public Response delete(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento) {
        if (idPrueba == null || idAreaConocimiento == null) {
            return Response.status(422).header("Missing-parameter", "idPrueba,idAreaConocimiento").build();
        }
        try {
            List<PruebaArea> list = pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
            Optional<PruebaArea> found = list.stream()
                    .filter(pa -> pa.getIdAreaConocimiento() != null && idAreaConocimiento.equals(pa.getIdAreaConocimiento().getId()))
                    .findFirst();
            if (found.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking prueba " + idPrueba + " and area " + idAreaConocimiento + " not found")
                        .build();
            }
            pruebaAreaDAO.delete(found.get());
            return Response.noContent().build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error deleting PruebaArea", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }
}
