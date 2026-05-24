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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idAspirante") UUID idAspirante,
            AspirantePrueba entity,
            @Context UriInfo uriInfo) {

        if (idAspirante == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirante y entity no pueden ser nulos")
                    .build();
        }

        if (entity.getId() != null) {
            return Response.status(422)
                    .header("Missing-parameter", "entity.id debe ser nulo para creacion")
                    .build();
        }

        try {
            Aspirante aspirante = aspiranteDAO.findById(idAspirante);
            if (aspirante == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Aspirante con id " + idAspirante + " no encontrado")
                        .build();
            }

            if (entity.getIdPrueba() == null || entity.getIdPrueba().getId() == null) {
                return Response.status(422)
                        .header("Missing-parameter", "idPrueba.id es requerido")
                        .build();
            }

            Prueba prueba = pruebaDAO.findById(entity.getIdPrueba().getId());
            if (prueba == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Prueba con id " + entity.getIdPrueba().getId() + " no encontrada")
                        .build();
            }

            entity.setIdAspirante(aspirante);
            entity.setIdPrueba(prueba);

            aspirantePruebaDAO.create(entity);

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

    @PUT
    @Path("{idPrueba}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idAspirante") UUID idAspirante,
            @PathParam("idPrueba") Integer idPrueba,
            AspirantePrueba entity) {

        if (idAspirante == null || idPrueba == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirante, idPrueba y entity son requeridos")
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

            if (entity.getIdPrueba() != null && entity.getIdPrueba().getId() != null) {
                Prueba prueba = pruebaDAO.findById(entity.getIdPrueba().getId());
                if (prueba == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .header("Not-found", "Prueba con id " + entity.getIdPrueba().getId() + " no encontrada")
                            .build();
                }
                entity.setIdPrueba(prueba);
            } else {
                entity.setIdPrueba(existing.getIdPrueba());
            }

            entity.setId(idPrueba);
            entity.setIdAspirante(aspirante);

            AspirantePrueba updated = aspirantePruebaDAO.update(entity);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

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