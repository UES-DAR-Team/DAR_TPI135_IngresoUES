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

@Path("jornada/{idJornada}/aula/{idAula}")
public class JornadaAulaResource implements Serializable {

    @Inject
    JornadaAulaDAO jornadaAulaDAO;

    @Inject
    JornadaDAO jornadaDAO;

    @Inject
    AulaDAO aulaDAO;

    /**
     * GET jornada/{idJornada}/aula/{idAula}?first=0&max=100
     * Lista paginada de aulas asignadas a una jornada.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idAula") UUID idAula,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(100) @Min(1) @DefaultValue("100") @QueryParam("max") int max) {

        if (idJornada == null || idAula == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornada, idAula")
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

            Aula aula = aulaDAO.findById(idAula);
            if (aula == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Aula con id " + idAula + " no encontrada")
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

    /**
     * GET jornada/{idJornada}/aula/{idAula}/{id}
     * Obtiene un registro JornadaAula por su ID, verificando que pertenezca
     * a la jornada y aula indicadas.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idAula") UUID idAula,
            @PathParam("id") Integer id) {

        if (idJornada == null || idAula == null || id == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornada, idAula, id")
                    .build();
        }

        try {
            JornadaAula resp = jornadaAulaDAO.findById(id);

            if (resp == null
                    || !resp.getIdJornada().getId().equals(idJornada)
                    || !resp.getIdAula().getId().equals(idAula)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para la jornada y aula indicadas")
                        .build();
            }

            return Response.ok(resp).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    /**
     * POST jornada/{idJornada}/aula/{idAula}
     * Asigna un aula a una jornada. El id debe venir nulo.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idAula") UUID idAula,
            JornadaAula entity,
            @Context UriInfo uriInfo) {

        if (idJornada == null || idAula == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornada, idAula y entity son requeridos")
                    .build();
        }

        if (entity.getId() != null) {
            return Response.status(422)
                    .header("Missing-parameter", "entity.id must be null")
                    .build();
        }

        try {
            Jornada jornada = jornadaDAO.findById(idJornada);
            if (jornada == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Jornada con id " + idJornada + " no encontrada")
                        .build();
            }

            Aula aula = aulaDAO.findById(idAula);
            if (aula == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Aula con id " + idAula + " no encontrada")
                        .build();
            }

            entity.setIdJornada(jornada);
            entity.setIdAula(aula);

            jornadaAulaDAO.create(entity);

            return Response.created(
                    uriInfo.getAbsolutePathBuilder()
                            .build()
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

    /**
     * PUT jornada/{idJornada}/aula/{idAula}/{id}
     * Actualiza una asignacion de aula en una jornada existente.
     */
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idAula") UUID idAula,
            @PathParam("id") Integer id,
            JornadaAula entity) {

        if (idJornada == null || idAula == null || id == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornada, idAula, id y entity son requeridos")
                    .build();
        }

        try {
            JornadaAula existing = jornadaAulaDAO.findById(id);

            if (existing == null
                    || !existing.getIdJornada().getId().equals(idJornada)
                    || !existing.getIdAula().getId().equals(idAula)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para la jornada y aula indicadas")
                        .build();
            }

            Jornada jornada = jornadaDAO.findById(idJornada);
            if (jornada == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Jornada con id " + idJornada + " no encontrada")
                        .build();
            }

            Aula aula = aulaDAO.findById(idAula);
            if (aula == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Aula con id " + idAula + " no encontrada")
                        .build();
            }

            entity.setId(id);
            entity.setIdJornada(jornada);
            entity.setIdAula(aula);

            JornadaAula updated = jornadaAulaDAO.update(entity);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    /**
     * DELETE jornada/{idJornada}/aula/{idAula}/{id}
     * Elimina una asignacion verificando que pertenezca a la jornada y aula indicadas.
     */
    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idAula") UUID idAula,
            @PathParam("id") Integer id) {

        if (idJornada == null || idAula == null || id == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornada, idAula, id son requeridos")
                    .build();
        }

        try {
            JornadaAula existing = jornadaAulaDAO.findById(id);

            if (existing == null
                    || !existing.getIdJornada().getId().equals(idJornada)
                    || !existing.getIdAula().getId().equals(idAula)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para la jornada y aula indicadas")
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