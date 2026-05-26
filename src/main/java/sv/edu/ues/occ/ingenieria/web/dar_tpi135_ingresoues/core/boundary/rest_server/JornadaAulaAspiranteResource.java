package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspirantePruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaAulaAspiranteDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaAulaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspirantePrueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspirante;

import java.io.Serializable;
import java.util.List;

@Path("jornadaAula/{idJornadaAula}/aspirantePrueba")
public class JornadaAulaAspiranteResource implements Serializable {

    @Inject
    JornadaAulaAspiranteDAO jaaDAO;

    @Inject
    JornadaAulaDAO jornadaAulaDAO;

    @Inject
    AspirantePruebaDAO aspirantePruebaDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(100) @Min(1) @DefaultValue("100") @QueryParam("max") int max) {

        if (idJornadaAula == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAula")
                    .build();
        }

        if (first < 0 || max <= 0 || max > 100) {
            return Response.status(422)
                    .header("Missing-parameter", "first, max")
                    .build();
        }

        try {
            JornadaAula ja = jornadaAulaDAO.findById(idJornadaAula);
            if (ja == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "JornadaAula con id " + idJornadaAula + " no encontrada")
                        .build();
            }

            Long total = jaaDAO.countByJornadaAula(idJornadaAula);
            List<JornadaAulaAspirante> lista = jaaDAO.findByJornadaAula(idJornadaAula, first, max);

            return Response.ok(lista)
                    .header("Total-records", total)
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @GET
    @Path("{idAspirantePrueba}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @PathParam("idAspirantePrueba") Integer idAspirantePrueba) {

        if (idJornadaAula == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAula")
                    .build();
        }

        if (idAspirantePrueba == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirantePrueba")
                    .build();
        }

        try {
            JornadaAulaAspirante resp = jaaDAO.findById(idAspirantePrueba);

            if (resp == null
                    || !resp.getIdJornadaAula().getId().equals(idJornadaAula)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para la jornadaAula indicada")
                        .build();
            }

            return Response.ok(resp).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            JornadaAulaAspirante entity,
            @Context UriInfo uriInfo) {

        if (idJornadaAula == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAula")
                    .build();
        }

        if (entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "entity")
                    .build();
        }

        if (entity.getId() != null) {
            return Response.status(422)
                    .header("Missing-parameter", "entity.id debe ser nulo para creacion")
                    .build();
        }

        if (entity.getIdAspirantePrueba() == null
                || entity.getIdAspirantePrueba().getId() == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirantePrueba")
                    .build();
        }

        try {
            JornadaAula ja = jornadaAulaDAO.findById(idJornadaAula);
            if (ja == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "JornadaAula con id " + idJornadaAula + " no encontrada")
                        .build();
            }

            AspirantePrueba ap = aspirantePruebaDAO.findById(
                    entity.getIdAspirantePrueba().getId());
            if (ap == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "AspirantePrueba con id "
                                + entity.getIdAspirantePrueba().getId() + " no encontrada")
                        .build();
            }

            entity.setIdJornadaAula(ja);
            entity.setIdAspirantePrueba(ap);

            jaaDAO.create(entity);

            return Response.created(
                    uriInfo.getAbsolutePathBuilder().build()
            ).entity(entity).build();

        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            String msg = cause.getMessage() != null ? cause.getMessage() : "";

            if (msg.contains("duplicate key")) {
                return Response.status(Response.Status.CONFLICT)
                        .header("Conflict", "Ya existe una asignacion con esos datos")
                        .build();
            }

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @PUT
    @Path("{idAspirantePrueba}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @PathParam("idAspirantePrueba") Integer idAspirantePrueba,
            JornadaAulaAspirante entity) {

        if (idJornadaAula == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAula")
                    .build();
        }

        if (idAspirantePrueba == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirantePrueba")
                    .build();
        }

        if (entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "entity")
                    .build();
        }

        try {
            JornadaAulaAspirante existing = jaaDAO.findById(idAspirantePrueba);

            if (existing == null
                    || !existing.getIdJornadaAula().getId().equals(idJornadaAula)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para la jornadaAula indicada")
                        .build();
            }

            existing.setHoraLlegada(entity.getHoraLlegada());
            existing.setAsistio(entity.getAsistio());

            JornadaAulaAspirante updated = jaaDAO.update(existing);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @DELETE
    @Path("{idAspirantePrueba}")
    public Response delete(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @PathParam("idAspirantePrueba") Integer idAspirantePrueba) {

        if (idJornadaAula == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAula")
                    .build();
        }

        if (idAspirantePrueba == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirantePrueba")
                    .build();
        }

        try {
            JornadaAulaAspirante existing = jaaDAO.findById(idAspirantePrueba);

            if (existing == null
                    || !existing.getIdJornadaAula().getId().equals(idJornadaAula)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para la jornadaAula indicada")
                        .build();
            }

            jaaDAO.delete(existing);
            return Response.noContent().build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }
}