package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteOpcioneDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspiranteOpcione;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;

import java.io.Serializable;
import java.util.UUID;

@Path("aspirante/{idAspirante}/opciones")
public class AspiranteOpcioneResource implements Serializable {

    @Inject
    AspiranteOpcioneDAO aspiranteOpcioneDAO;

    @Inject
    AspiranteDAO aspiranteDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(100) @DefaultValue("100") @QueryParam("max") int max,
            @PathParam("idAspirante") UUID idAspirante
    ) {

        if (idAspirante != null && first >= 0 && max <= 100) {
            try {
                long total = aspiranteOpcioneDAO.countByAspirante(idAspirante);

                return Response.ok(
                                aspiranteOpcioneDAO.findByAspirante(idAspirante, first, max)
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
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("idAspirante") UUID idAspirante,
                             @PathParam("id") UUID id) {

        if (idAspirante != null && id != null) {
            try {
                AspiranteOpcione resp = aspiranteOpcioneDAO.findById(id);

                if (resp != null && resp.getIdAspirante().getId().equals(idAspirante)) {
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
                .header("Missing-parameter", "idAspirante,id")
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("idAspirante") UUID idAspirante,
                           @PathParam("id") UUID id) {

        if (idAspirante != null && id != null) {
            try {
                AspiranteOpcione resp = aspiranteOpcioneDAO.findById(id);

                if (resp != null && resp.getIdAspirante().getId().equals(idAspirante)) {
                    aspiranteOpcioneDAO.delete(resp);
                    return Response.noContent().build();
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
                .header("Missing-parameter", "idAspirante,id")
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("idAspirante") UUID idAspirante,
                           AspiranteOpcione entity,
                           @Context UriInfo uriInfo) {

        if (idAspirante == null || entity == null || entity.getId() != null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirante,entity")
                    .build();
        }

        try {
            Aspirante aspirante = aspiranteDAO.findById(idAspirante);

            if (aspirante == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Aspirante no existe")
                        .build();
            }

            entity.setIdAspirante(aspirante);

            aspiranteOpcioneDAO.create(entity);

            return Response.created(
                    uriInfo.getAbsolutePathBuilder()
                            .path(String.valueOf(entity.getId()))
                            .build()
            ).entity(entity).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("idAspirante") UUID idAspirante,
                           @PathParam("id") Integer id,
                           AspiranteOpcione entity) {

        if (idAspirante == null || id == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idAspirante,id,entity")
                    .build();
        }

        try {
            AspiranteOpcione existing = aspiranteOpcioneDAO.findById(id);

            if (existing == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Registro no encontrado")
                        .build();
            }

            Aspirante aspirante = aspiranteDAO.findById(idAspirante);

            if (aspirante == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Aspirante no existe")
                        .build();
            }

            entity.setId(id);
            entity.setIdAspirante(aspirante);

            AspiranteOpcione updated = aspiranteOpcioneDAO.update(entity);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", e.getMessage())
                    .build();
        }
    }
}