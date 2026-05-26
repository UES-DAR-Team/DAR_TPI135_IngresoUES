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

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            @PathParam("id") Integer id) {

        if (idJornadaAulaAspirante == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAulaAspirante")
                    .build();
        }

        if (id == null) {
            return Response.status(422)
                    .header("Missing-parameter", "id")
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            JornadaAulaAspiranteResultado entity,
            @Context UriInfo uriInfo) {

        if (idJornadaAulaAspirante == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAulaAspirante")
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
                    uriInfo.getAbsolutePathBuilder().build()
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

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            @PathParam("id") Integer id,
            JornadaAulaAspiranteResultado entity) {

        if (idJornadaAulaAspirante == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAulaAspirante")
                    .build();
        }

        if (id == null) {
            return Response.status(422)
                    .header("Missing-parameter", "id")
                    .build();
        }

        if (entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "entity")
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

    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            @PathParam("id") Integer id) {

        if (idJornadaAulaAspirante == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idJornadaAulaAspirante")
                    .build();
        }

        if (id == null) {
            return Response.status(422)
                    .header("Missing-parameter", "id")
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