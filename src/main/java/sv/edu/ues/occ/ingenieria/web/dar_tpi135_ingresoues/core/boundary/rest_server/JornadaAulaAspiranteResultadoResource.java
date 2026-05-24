package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaAulaAspiranteDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaAulaAspiranteResultadoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspirante;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspiranteResultado;

import java.io.Serializable;
import java.util.List;

@Path("jornadaAulaAspirante/{idJornadaAulaAspirante}/resultado")
public class JornadaAulaAspiranteResultadoResource implements Serializable {

    @Inject
    JornadaAulaAspiranteResultadoDAO resultadoDAO;

    @Inject
    JornadaAulaAspiranteDAO jornadaAulaAspiranteDAO;

    /**
     * GET jornadaAulaAspirante/{idJornadaAulaAspirante}/resultado?first=0&max=100
     * Lista paginada de resultados de un aspirante en un aula de jornada.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(100) @Min(1) @DefaultValue("100") @QueryParam("max") int max) {

        if (idJornadaAulaAspirante == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAulaAspirante")
                    .build();
        }

        if (first < 0 || max <= 0 || max > 100) {
            return Response.status(422)
                    .header("Missing-parameter", "first, max")
                    .build();
        }

        try {
            JornadaAulaAspirante jaa = jornadaAulaAspiranteDAO.findById(idJornadaAulaAspirante);
            if (jaa == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "JornadaAulaAspirante con id " + idJornadaAulaAspirante + " no encontrada")
                        .build();
            }

            List<JornadaAulaAspiranteResultado> lista =
                    resultadoDAO.findByJornadaAulaAspirante(idJornadaAulaAspirante, first, max);

            return Response.ok(lista)
                    .header("Total-records", lista.size())
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    /**
     * GET jornadaAulaAspirante/{idJornadaAulaAspirante}/resultado/{id}
     * Obtiene un resultado por su ID verificando que pertenezca al aspirante indicado.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            @PathParam("id") Integer id) {

        if (idJornadaAulaAspirante == null || id == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAulaAspirante, id")
                    .build();
        }

        try {
            JornadaAulaAspiranteResultado resp = resultadoDAO.findById(id);

            if (resp == null
                    || !resp.getIdJornadaAulaAspirante().getId().equals(idJornadaAulaAspirante)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Resultado no encontrado para el aspirante indicado")
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
     * POST jornadaAulaAspirante/{idJornadaAulaAspirante}/resultado
     * Registra el resultado de un aspirante. El id debe venir nulo.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            JornadaAulaAspiranteResultado entity,
            @Context UriInfo uriInfo) {

        if (idJornadaAulaAspirante == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAulaAspirante y entity son requeridos")
                    .build();
        }

        if (entity.getId() != null) {
            return Response.status(422)
                    .header("Missing-parameter", "entity.id debe ser nulo para creacion")
                    .build();
        }

        try {
            JornadaAulaAspirante jaa = jornadaAulaAspiranteDAO.findById(idJornadaAulaAspirante);
            if (jaa == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "JornadaAulaAspirante con id " + idJornadaAulaAspirante + " no encontrada")
                        .build();
            }

            entity.setIdJornadaAulaAspirante(jaa);

            resultadoDAO.create(entity);

            return Response.created(
                    uriInfo.getAbsolutePathBuilder()
                            .build()
            ).entity(entity).build();

        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            String msg = cause.getMessage() != null ? cause.getMessage() : "";

            if (msg.contains("duplicate key")) {
                return Response.status(Response.Status.CONFLICT)
                        .header("Conflict", "Ya existe un resultado registrado con esos datos")
                        .build();
            }

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    /**
     * PUT jornadaAulaAspirante/{idJornadaAulaAspirante}/resultado/{id}
     * Actualiza el resultado de un aspirante verificando que pertenezca
     * al aspirante indicado.
     */
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            @PathParam("id") Integer id,
            JornadaAulaAspiranteResultado entity) {

        if (idJornadaAulaAspirante == null || id == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAulaAspirante, id y entity son requeridos")
                    .build();
        }

        try {
            JornadaAulaAspiranteResultado existing = resultadoDAO.findById(id);

            if (existing == null
                    || !existing.getIdJornadaAulaAspirante().getId().equals(idJornadaAulaAspirante)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Resultado no encontrado para el aspirante indicado")
                        .build();
            }

            JornadaAulaAspirante jaa = jornadaAulaAspiranteDAO.findById(idJornadaAulaAspirante);
            if (jaa == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "JornadaAulaAspirante con id " + idJornadaAulaAspirante + " no encontrada")
                        .build();
            }

            entity.setId(id);
            entity.setIdJornadaAulaAspirante(jaa);

            JornadaAulaAspiranteResultado updated = resultadoDAO.update(entity);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    /**
     * DELETE jornadaAulaAspirante/{idJornadaAulaAspirante}/resultado/{id}
     * Elimina un resultado verificando que pertenezca al aspirante indicado.
     */
    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            @PathParam("id") Integer id) {

        if (idJornadaAulaAspirante == null || id == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAulaAspirante, id son requeridos")
                    .build();
        }

        try {
            JornadaAulaAspiranteResultado existing = resultadoDAO.findById(id);

            if (existing == null
                    || !existing.getIdJornadaAulaAspirante().getId().equals(idJornadaAulaAspirante)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Resultado no encontrado para el aspirante indicado")
                        .build();
            }

            resultadoDAO.delete(existing);
            return Response.noContent().build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }
}