package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AulaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaAulaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aula;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Path("jornada/{idJornada}/aula")
public class JornadaAulaResource implements Serializable {

    @Inject
    JornadaAulaDAO jornadaAulaDAO;

    @Inject
    JornadaDAO jornadaDAO;

    @Inject
    AulaDAO aulaDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idJornada") UUID idJornada,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(100) @Min(1) @DefaultValue("100") @QueryParam("max") int max) {

        if (idJornada == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornada")
                    .build();
        }

        if (first < 0 || max <= 0 || max > 100) {
            return Response.status(422)
                    .header("Missing-parameter", "first, max")
                    .build();
        }

        try {
            Jornada jornada = jornadaDAO.findById(idJornada);
            if (jornada == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Jornada con id " + idJornada + " no encontrada")
                        .build();
            }

            Long total = jornadaAulaDAO.countByJornada(idJornada);
            List<JornadaAula> lista = jornadaAulaDAO.findByJornada(idJornada, first, max);

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
    @Path("{idAula}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idAula") Integer idAula) {

        if (idJornada == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornada")
                    .build();
        }

        if (idAula == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAula")
                    .build();
        }

        try {
            JornadaAula resp = jornadaAulaDAO.findById(idAula);

            if (resp == null
                    || !resp.getIdJornada().getId().equals(idJornada)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para la jornada indicada")
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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idJornada") UUID idJornada,
            JornadaAula entity,
            @Context UriInfo uriInfo) {

        if (idJornada == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornada")
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

        if (entity.getIdAula() == null || entity.getIdAula().getId() == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAula")
                    .build();
        }

        try {
            Jornada jornada = jornadaDAO.findById(idJornada);
            if (jornada == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Jornada con id " + idJornada + " no encontrada")
                        .build();
            }

            Aula aula = aulaDAO.findById(entity.getIdAula().getId());
            if (aula == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Aula con id " + entity.getIdAula().getId() + " no encontrada")
                        .build();
            }

            entity.setIdJornada(jornada);
            entity.setIdAula(aula);

            jornadaAulaDAO.create(entity);

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
    @Path("{idAula}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idAula") Integer idAula,
            JornadaAula entity) {

        if (idJornada == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornada")
                    .build();
        }

        if (idAula == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAula")
                    .build();
        }

        if (entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "entity")
                    .build();
        }

        try {
            JornadaAula existing = jornadaAulaDAO.findById(idAula);

            if (existing == null
                    || !existing.getIdJornada().getId().equals(idJornada)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para la jornada indicada")
                        .build();
            }

            existing.setFechaAsignacion(entity.getFechaAsignacion());

            JornadaAula updated = jornadaAulaDAO.update(existing);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @DELETE
    @Path("{idAula}")
    public Response delete(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idAula") Integer idAula) {

        if (idJornada == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornada")
                    .build();
        }

        if (idAula == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAula")
                    .build();
        }

        try {
            JornadaAula existing = jornadaAulaDAO.findById(idAula);

            if (existing == null
                    || !existing.getIdJornada().getId().equals(idJornada)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para la jornada indicada")
                        .build();
            }

            jornadaAulaDAO.delete(existing);
            return Response.noContent().build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }
}