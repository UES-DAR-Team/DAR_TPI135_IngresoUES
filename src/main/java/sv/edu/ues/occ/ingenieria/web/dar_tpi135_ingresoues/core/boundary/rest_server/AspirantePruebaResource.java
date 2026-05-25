package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspirantePruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspirantePrueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Path("aspirante/{idAspirante}/pruebas")
public class AspirantePruebaResource implements Serializable {

    @Inject
    AspirantePruebaDAO aspirantePruebaDAO;

    @Inject
    AspiranteDAO aspiranteDAO;

    @Inject
    PruebaDAO pruebaDAO;

    // ─────────────────────────────────────────────────────────────────────────
    // DTO — el cliente solo manda el UUID de la prueba, no el objeto completo
    //
    // POST/PUT body esperado:
    //   { "idPrueba": "07000000-0000-0000-0000-000000000001" }
    // ─────────────────────────────────────────────────────────────────────────
    public static class AspirantePruebaInput {
        private UUID idPrueba;

        public UUID getIdPrueba() { return idPrueba; }
        public void setIdPrueba(UUID idPrueba) { this.idPrueba = idPrueba; }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /aspirante/{idAspirante}/pruebas
    // ─────────────────────────────────────────────────────────────────────────
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idAspirante") UUID idAspirante,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(100) @Min(1) @DefaultValue("100") @QueryParam("max") int max) {

        if (idAspirante == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirante")
                    .build();
        }

        if (first < 0 || max <= 0 || max > 100) {
            return Response.status(422)
                    .header("Missing-parameter", "first, max")
                    .build();
        }

        try {
            Aspirante aspirante = aspiranteDAO.findById(idAspirante);
            if (aspirante == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Aspirante con id " + idAspirante + " no encontrado")
                        .build();
            }

            Long total = aspirantePruebaDAO.countByAspirante(idAspirante);
            List<AspirantePrueba> lista = aspirantePruebaDAO.findByAspirante(idAspirante, first, max);

            return Response.ok(lista)
                    .header("Total-records", total)
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /aspirante/{idAspirante}/pruebas/{idPrueba}
    // ─────────────────────────────────────────────────────────────────────────
    @GET
    @Path("{idPrueba}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @PathParam("idAspirante") UUID idAspirante,
            @PathParam("idPrueba") Integer idPrueba) {

        if (idAspirante == null || idPrueba == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirante, idPrueba")
                    .build();
        }

        try {
            AspirantePrueba resp = aspirantePruebaDAO.findById(idPrueba);

            if (resp == null || !resp.getIdAspirante().getId().equals(idAspirante)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para el aspirante indicado")
                        .build();
            }

            return Response.ok(resp).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /aspirante/{idAspirante}/pruebas
    // Body: { "idPrueba": "uuid-de-la-prueba" }
    // ─────────────────────────────────────────────────────────────────────────
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idAspirante") UUID idAspirante,
            AspirantePruebaInput input,
            @Context UriInfo uriInfo) {

        if (idAspirante == null || input == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirante e input no pueden ser nulos")
                    .build();
        }

        if (input.getIdPrueba() == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idPrueba es requerido")
                    .build();
        }

        try {
            Aspirante aspirante = aspiranteDAO.findById(idAspirante);
            if (aspirante == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Aspirante con id " + idAspirante + " no encontrado")
                        .build();
            }

            Prueba prueba = pruebaDAO.findById(input.getIdPrueba());
            if (prueba == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Prueba con id " + input.getIdPrueba() + " no encontrada")
                        .build();
            }

            AspirantePrueba entity = new AspirantePrueba();
            entity.setIdAspirante(aspirante);
            entity.setIdPrueba(prueba);
            entity.setFechaAsignacion(OffsetDateTime.now());

            aspirantePruebaDAO.create(entity);

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

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /aspirante/{idAspirante}/pruebas/{idPrueba}
    // Body: { "idPrueba": "uuid-de-la-nueva-prueba" }  ← idPrueba es opcional
    // ─────────────────────────────────────────────────────────────────────────
    @PUT
    @Path("{idPrueba}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idAspirante") UUID idAspirante,
            @PathParam("idPrueba") Integer idPrueba,
            AspirantePruebaInput input) {

        if (idAspirante == null || idPrueba == null || input == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirante, idPrueba y input son requeridos")
                    .build();
        }

        try {
            AspirantePrueba existing = aspirantePruebaDAO.findById(idPrueba);

            if (existing == null || !existing.getIdAspirante().getId().equals(idAspirante)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para el aspirante indicado")
                        .build();
            }

            Aspirante aspirante = aspiranteDAO.findById(idAspirante);
            if (aspirante == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Aspirante con id " + idAspirante + " no encontrado")
                        .build();
            }

            // Si el cliente manda idPrueba lo actualiza, sino conserva el existente
            if (input.getIdPrueba() != null) {
                Prueba prueba = pruebaDAO.findById(input.getIdPrueba());
                if (prueba == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .header("Not-found", "Prueba con id " + input.getIdPrueba() + " no encontrada")
                            .build();
                }
                existing.setIdPrueba(prueba);
            }

            existing.setIdAspirante(aspirante);

            AspirantePrueba updated = aspirantePruebaDAO.update(existing);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /aspirante/{idAspirante}/pruebas/{idPrueba}
    // ─────────────────────────────────────────────────────────────────────────
    @DELETE
    @Path("{idPrueba}")
    public Response delete(
            @PathParam("idAspirante") UUID idAspirante,
            @PathParam("idPrueba") Integer idPrueba) {

        if (idAspirante == null || idPrueba == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirante e idPrueba son requeridos")
                    .build();
        }

        try {
            AspirantePrueba existing = aspirantePruebaDAO.findById(idPrueba);

            if (existing == null || !existing.getIdAspirante().getId().equals(idAspirante)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado para el aspirante indicado")
                        .build();
            }

            aspirantePruebaDAO.delete(existing);

            return Response.noContent().build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }
}