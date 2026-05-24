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

@Path("jornadaAula/{idJornadaAula}/aspirantePrueba/{idAspirantePrueba}")
public class JornadaAulaAspiranteResource implements Serializable {

    @Inject
    JornadaAulaAspiranteDAO jaaDAO;

    @Inject
    JornadaAulaDAO jornadaAulaDAO;

    @Inject
    AspirantePruebaDAO aspirantePruebaDAO;

    /**
     * GET jornadaAula/{idJornadaAula}/aspirantePrueba/{idAspirantePrueba}?first=0&max=100
     * Lista paginada de aspirantes asignados a un aula de jornada.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @PathParam("idAspirantePrueba") Integer idAspirantePrueba,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(100) @Min(1) @DefaultValue("100") @QueryParam("max") int max) {

        if (idJornadaAula == null || idAspirantePrueba == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAula, idAspirantePrueba")
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

            AspirantePrueba ap = aspirantePruebaDAO.findById(idAspirantePrueba);
            if (ap == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "AspirantePrueba con id " + idAspirantePrueba + " no encontrada")
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

    /**
     * GET jornadaAula/{idJornadaAula}/aspirantePrueba/{idAspirantePrueba}/{id}
     * Obtiene un registro por su ID verificando que pertenezca a la jornadaAula
     * y aspirantePrueba indicadas.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @PathParam("idAspirantePrueba") Integer idAspirantePrueba,
            @PathParam("id") Integer id) {

        if (idJornadaAula == null || idAspirantePrueba == null || id == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAula, idAspirantePrueba, id")
                    .build();
        }

        try {
            JornadaAulaAspirante resp = jaaDAO.findById(id);

            if (resp == null
                    || !resp.getIdJornadaAula().getId().equals(idJornadaAula)
                    || !resp.getIdAspirantePrueba().getId().equals(idAspirantePrueba)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para la jornadaAula y aspirantePrueba indicadas")
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
     * POST jornadaAula/{idJornadaAula}/aspirantePrueba/{idAspirantePrueba}
     * Asigna un aspirante a un aula de jornada. El id debe venir nulo.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @PathParam("idAspirantePrueba") Integer idAspirantePrueba,
            JornadaAulaAspirante entity,
            @Context UriInfo uriInfo) {

        if (idJornadaAula == null || idAspirantePrueba == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAula, idAspirantePrueba y entity son requeridos")
                    .build();
        }

        if (entity.getId() != null) {
            return Response.status(422)
                    .header("Missing-parameter", "entity.id debe ser nulo para creacion")
                    .build();
        }

        try {
            JornadaAula ja = jornadaAulaDAO.findById(idJornadaAula);
            if (ja == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "JornadaAula con id " + idJornadaAula + " no encontrada")
                        .build();
            }

            AspirantePrueba ap = aspirantePruebaDAO.findById(idAspirantePrueba);
            if (ap == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "AspirantePrueba con id " + idAspirantePrueba + " no encontrada")
                        .build();
            }

            entity.setIdJornadaAula(ja);
            entity.setIdAspirantePrueba(ap);

            jaaDAO.create(entity);

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
     * PUT jornadaAula/{idJornadaAula}/aspirantePrueba/{idAspirantePrueba}/{id}
     * Actualiza una asignacion existente verificando que pertenezca a la
     * jornadaAula y aspirantePrueba indicadas.
     */
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @PathParam("idAspirantePrueba") Integer idAspirantePrueba,
            @PathParam("id") Integer id,
            JornadaAulaAspirante entity) {

        if (idJornadaAula == null || idAspirantePrueba == null || id == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAula, idAspirantePrueba, id y entity son requeridos")
                    .build();
        }

        try {
            JornadaAulaAspirante existing = jaaDAO.findById(id);

            if (existing == null
                    || !existing.getIdJornadaAula().getId().equals(idJornadaAula)
                    || !existing.getIdAspirantePrueba().getId().equals(idAspirantePrueba)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para la jornadaAula y aspirantePrueba indicadas")
                        .build();
            }

            JornadaAula ja = jornadaAulaDAO.findById(idJornadaAula);
            if (ja == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "JornadaAula con id " + idJornadaAula + " no encontrada")
                        .build();
            }

            AspirantePrueba ap = aspirantePruebaDAO.findById(idAspirantePrueba);
            if (ap == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "AspirantePrueba con id " + idAspirantePrueba + " no encontrada")
                        .build();
            }

            entity.setId(id);
            entity.setIdJornadaAula(ja);
            entity.setIdAspirantePrueba(ap);

            JornadaAulaAspirante updated = jaaDAO.update(entity);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    /**
     * DELETE jornadaAula/{idJornadaAula}/aspirantePrueba/{idAspirantePrueba}/{id}
     * Elimina una asignacion verificando que pertenezca a la jornadaAula
     * y aspirantePrueba indicadas.
     */
    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @PathParam("idAspirantePrueba") Integer idAspirantePrueba,
            @PathParam("id") Integer id) {

        if (idJornadaAula == null || idAspirantePrueba == null || id == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAula, idAspirantePrueba, id son requeridos")
                    .build();
        }

        try {
            JornadaAulaAspirante existing = jaaDAO.findById(id);

            if (existing == null
                    || !existing.getIdJornadaAula().getId().equals(idJornadaAula)
                    || !existing.getIdAspirantePrueba().getId().equals(idAspirantePrueba)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para la jornadaAula y aspirantePrueba indicadas")
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