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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorAreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.DistractorAreaConocimiento;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("areaConocimiento/{idAreaConocimiento}/distractor")
public class DistractorAreaConocimientoResource implements Serializable {
    @Inject
    DistractorAreaConocimientoDAO distractorAreaConocimientoDAO;

    @Inject
    DistractorDAO distractorDAO;

    @Inject
    AreaConocimientoDAO areaConocimientoDAO;

    private static final Logger LOG = Logger.getLogger(DistractorAreaConocimientoResource.class.getName());

    //Listar distractores asignados a una area conocimiento (paginado)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {
        if (idAreaConocimiento == null) {
            return Response.status(422).header("Missing-parameter", "idAreaConocimiento").build();
        }
        if (first < 0 || max <= 0 || max > 10) {
            return Response.status(422).header("Missing-parameter", "first,max").build();
        }
        try {
            //validar existencia de area conocimiento padre
            AreaConocimiento areaConocimiento = areaConocimientoDAO.findById(idAreaConocimiento);
            if (areaConocimiento == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "AreaConocimiento with id " + idAreaConocimiento + " not found")
                        .build();
            }
            List<DistractorAreaConocimiento> encontrados = distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, first, max);
            int total = distractorAreaConocimientoDAO.count();
            return Response.ok(encontrados)
                    .header("X-Total-Count", total)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving Distractor range", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    //Obtener un distractor asignado a una area conocimiento por id
    @GET
    @Path("{idDistractor}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            @PathParam("idDistractor") UUID idDistractor) {
        if (idAreaConocimiento == null || idDistractor == null) {
            return Response.status(422).header("Missing-parameter", "idAreaConocimiento,idDistractor").build();
        }
        try {
            List<DistractorAreaConocimiento> list = distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
            Optional<DistractorAreaConocimiento> found = list.stream()
                    .filter(dac -> dac.getIdDistractor() != null && idDistractor
                            .equals(dac.getIdDistractor().getId())).findFirst();
            if (found.isPresent()) {
                return Response.ok(found.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking area " + idAreaConocimiento + " and distractor " + idDistractor + "not found")
                        .build();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving DistractorAreaConocimiento", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            DistractorAreaConocimiento entity,
            @Context UriInfo uriInfo) {
        if (idAreaConocimiento == null) {
            return Response.status(422).header("Missing-parameter", "idAreaConocimiento").build();
        }
        if (entity == null) {
            return Response.status(422).header("Missing-parameter", "entity must not be null").build();
        }
        if (entity.getId() != null) {
            return Response.status(422).header("Missing-parameter", "entity.id must be null").build();
        }
        if (entity.getIdDistractor() == null || entity.getIdDistractor().getId() == null) {
            return Response.status(422).header("Missing-parameter", "entity.id must be provider in body").build();
        }
        try {
            AreaConocimiento areaConocimiento = areaConocimientoDAO.findById(idAreaConocimiento);
            if (areaConocimiento == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "AreaConocimiento with id " + idAreaConocimiento + " not found")
                        .build();
            }
            Distractor distractor = distractorDAO.findById(entity.getIdDistractor().getId());
            if (distractor == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Distractor with id " + entity.getIdDistractor().getId() + " not found")
                        .build();
            }
            entity.setIdAreaConocimiento(areaConocimiento);
            entity.setIdDistractor(distractor);
            distractorAreaConocimientoDAO.create(entity);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build())
                    .entity(entity)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating DistractorAreaConocimiento", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    //Eliminar asociasion de distractor a area conocimiento
    @DELETE
    @Path("{idDistractor}")
    public Response delete(
            @PathParam("idAreaConocimiento") UUID idAreaConocimiento,
            @PathParam("idDistractor") UUID idDistractor) {
        if (idAreaConocimiento == null || idDistractor == null) {
            return Response.status(422).header("Missing-parameter", "idAreaConocimiento,idDistractor").build();
        }
        try {
            List<DistractorAreaConocimiento> list = distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
            Optional<DistractorAreaConocimiento> found = list.stream()
                    .filter(dac -> dac.getIdDistractor() != null && idDistractor
                            .equals(dac.getIdDistractor().getId())).findFirst();
            if (found.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking area " + idAreaConocimiento + " and distractor " + idDistractor + "not found")
                        .build();
            }
            distractorAreaConocimientoDAO.delete(found.get());
            return Response.noContent().build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error deleting DistractorAreaConocimiento", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

}
