package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.data.repository.Delete;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteOpcionDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.OpcionDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspiranteOpcion;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Opcion;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("aspirante/{idAspirante}/opcion")
public class AspiranteOpcionResource implements Serializable {
    @Inject
    AspiranteOpcionDAO aspiranteOpcionDAO;

    @Inject
    AspiranteDAO aspiranteDAO;

    @Inject
    OpcionDAO opcionDAO;

    private static final Logger LOG = Logger.getLogger(TurnoResource.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idAspirante") UUID idAspirante,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (idAspirante == null) {
            return Response.status(422).header("Missing-parameter", "idAspirante").build();
        }
        if (first < 0 || max <= 0 || max > 10) {
            return Response.status(422).header("Missing-parameter", "first,max").build();
        }
        try {
            Aspirante aspirante = aspiranteDAO.findById(idAspirante);
            if (aspirante == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Aspirante with id " + idAspirante + " not found").build();
            }
            List<AspiranteOpcion> List = aspiranteOpcionDAO.findOpcionByIdAspirante(idAspirante, first, max);
            int total = aspiranteOpcionDAO.count();
            return Response.ok(List)
                    .header("X-Total-Count", total)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving AspiranteOpcion range", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @GET
    @Path("{idOpcion}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idAspirante") UUID idAspirante,
            @PathParam("idOpcion") UUID idOpcion
    ) {
        if (idAspirante == null || idOpcion == null) {
            return Response.status(422).header("Missing-parameter", "idAspirante,idOpcion").build();
        }
        try {
            List<AspiranteOpcion> list = aspiranteOpcionDAO.findOpcionByIdAspirante(idAspirante, 0, Integer.MAX_VALUE);

            Optional<AspiranteOpcion> found = list.stream()
                    .filter(ao -> ao.getIdOpcion() != null && idOpcion.equals(ao.getIdOpcion().getId()))
                    .findFirst();
            if (found.isPresent()) {
                return Response.ok(found.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking aspirante " + idAspirante + " and opcion " + idOpcion + " not found").build();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving AspiranteOpcion", ex);
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
            AspiranteOpcion entity,
            @Context UriInfo uriInfo) {
        if (idAspirante == null) {
            return Response.status(422).header("Missing-parameter", "idAspirante").build();
        }
        if (entity == null) {
            return Response.status(422).header("Missing-parameter", "entity must not be null").build();
        }
        if (entity.getId() != null) {
            return Response.status(422).header("Missing-parameter", "entity.id must be null").build();
        }
        if (entity.getIdOpcion() == null) {
            return Response.status(422).header("Missing-parameter", "idOpcion must be provider in body").build();
        }
        try {
            Aspirante aspirante = aspiranteDAO.findById(idAspirante);
            if (aspirante == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Aspirante with id " + idAspirante + " not found").build();
            }
            Opcion opcion = opcionDAO.findById(entity.getIdOpcion().getId());
            if (opcion == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Opcion with id " + entity.getIdOpcion().getId() + " not found").build();
            }

            entity.setIdAspirante(aspirante);
            entity.setIdOpcion(opcion);

            aspiranteOpcionDAO.create(entity);
            URI created = uriInfo.getAbsolutePathBuilder().path(opcion.getId().toString()).build();
            return Response.created(created).entity(entity).build();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating AspiranteOpcion", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db").build();
        }
    }

    @Delete
    @Path("{idOpcion}")
    public Response delete(
            @PathParam("idAspirante") UUID idAspirante,
            @PathParam("idOpcion") UUID idOpcion) {
        if (idAspirante == null || idOpcion == null) {
            return Response.status(422).header("Missing-parameter", "idAspirante,idOpcion").build();
        }
        try {
            List<AspiranteOpcion> list = aspiranteOpcionDAO.findOpcionByIdAspirante(idAspirante, 0, Integer.MAX_VALUE);

            Optional<AspiranteOpcion> found = list.stream()
                    .filter(ao -> ao.getIdOpcion() != null && idOpcion.equals(ao.getIdOpcion().getId()))
                    .findFirst();
            if (found.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking aspirante " + idAspirante + " and opcion " + idOpcion + " not found").build();
            }
            aspiranteOpcionDAO.delete(found.get());
            return Response.noContent().build();

        }catch (Exception ex){
            LOG.log(Level.SEVERE, "Error deleting AspiranteOpcion", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db").build();
        }
    }


}
