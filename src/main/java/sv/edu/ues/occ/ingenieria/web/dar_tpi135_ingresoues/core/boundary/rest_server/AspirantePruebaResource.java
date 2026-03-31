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
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(100) @DefaultValue("100") @QueryParam("max") int max,
            @PathParam("idAspirante") UUID idAspirante
    ) {

        if (idAspirante != null && first >= 0 && max <= 100) {
            try {
                long total = aspirantePruebaDAO.countByAspirante(idAspirante);

                return Response.ok(
                                aspirantePruebaDAO.findByAspirante(idAspirante, first, max)
                        )
                        .header("Total-records", total)
                        .build();

            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db")
                        .build();
            }
        }

        return Response.status(422)
                .header("Missing-parameter", "idAspirante,first,max")
                .build();
    }

    @GET
    @Path("{idPrueba}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("idAspirante") UUID idAspirante,
                             @PathParam("idPrueba") Integer idPrueba) {

        if (idAspirante != null && idPrueba != null) {
            try {
                AspirantePrueba resp = aspirantePruebaDAO.findById(idPrueba);

                if (resp != null &&
                        resp.getIdAspirante().getId().equals(idAspirante)) {

                    return Response.ok(resp).build();
                }

                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado")
                        .build();

            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db")
                        .build();
            }
        }

        return Response.status(422)
                .header("Missing-parameter", "idAspirante,idPrueba")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("idAspirante") UUID idAspirante,
                           AspirantePrueba entity,
                           @Context UriInfo uriInfo) {

        if (idAspirante == null || entity == null || entity.getId() != null) {
            return Response.status(422).build();
        }

        try {
            Aspirante aspirante = aspiranteDAO.findById(idAspirante);
            if (aspirante == null) {
                return Response.status(404)
                        .header("Not-found", "Aspirante no existe")
                        .build();
            }

            if (entity.getIdPrueba() == null || entity.getIdPrueba().getId() == null) {
                return Response.status(422)
                        .header("Missing-parameter", "idPrueba.id")
                        .build();
            }

            Prueba prueba = pruebaDAO.findById(entity.getIdPrueba().getId());
            if (prueba == null) {
                return Response.status(404)
                        .header("Not-found", "Prueba no existe")
                        .build();
            }

            entity.setIdAspirante(aspirante);
            entity.setIdPrueba(prueba);

            aspirantePruebaDAO.create(entity);

            return Response.created(
                    uriInfo.getAbsolutePathBuilder()
                            .path(String.valueOf(entity.getId()))
                            .build()
            ).entity(entity).build();

        } catch (Exception e) {
            return Response.status(500)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @PUT
    @Path("{idPrueba}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("idAspirante") UUID idAspirante,
                           @PathParam("idPrueba") Integer idPrueba,
                           AspirantePrueba entity) {

        if (idAspirante == null || idPrueba == null || entity == null) {
            return Response.status(422).build();
        }

        try {
            AspirantePrueba existing = aspirantePruebaDAO.findById(idPrueba);

            if (existing == null) {
                return Response.status(404).build();
            }

            Aspirante aspirante = aspiranteDAO.findById(idAspirante);
            if (aspirante == null) {
                return Response.status(404).build();
            }

            entity.setId(idPrueba);
            entity.setIdAspirante(aspirante);

            AspirantePrueba updated = aspirantePruebaDAO.update(entity);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    @DELETE
    @Path("{idPrueba}")
    public Response delete(@PathParam("idAspirante") UUID idAspirante,
                           @PathParam("idPrueba") Integer idPrueba) {

        if (idAspirante != null && idPrueba != null) {
            try {
                AspirantePrueba resp = aspirantePruebaDAO.findById(idPrueba);

                if (resp != null &&
                        resp.getIdAspirante().getId().equals(idAspirante)) {

                    aspirantePruebaDAO.delete(resp);
                    return Response.noContent().build();
                }

                return Response.status(404).build();

            } catch (Exception e) {
                return Response.status(500).build();
            }
        }

        return Response.status(422).build();
    }
}