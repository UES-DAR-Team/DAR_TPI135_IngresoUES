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
public class AspiranteOpcionResource extends AbstractResource implements Serializable {
    @Inject
    AspiranteOpcionDAO aspiranteOpcionDAO;

    @Inject
    AspiranteDAO aspiranteDAO;

    @Inject
    OpcionDAO opcionDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idAspirante") UUID idAspirante,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (idAspirante == null) return unprocessable("idAspirante");
        if (first < 0 || max <= 0 || max > 10) return unprocessable("first, max");

        Aspirante aspirante = aspiranteDAO.findById(idAspirante);
        if (aspirante == null) return notFound(idAspirante.toString(), "Aspirante");

        List<AspiranteOpcion> List = aspiranteOpcionDAO.findOpcionByIdAspirante(idAspirante, first, max);
        int total = aspiranteOpcionDAO.count();
        return Response.ok(List)
                .header(X_TOTAL_COUNT, total)
                .build();
    }

    @GET
    @Path("{idOpcion}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idAspirante") UUID idAspirante,
            @PathParam("idOpcion") UUID idOpcion
    ) {
        if (idAspirante == null) return unprocessable("idAspirante");
        if (idOpcion == null) return unprocessable("idOpcion");

        List<AspiranteOpcion> list =
                aspiranteOpcionDAO.findOpcionByIdAspirante(idAspirante, 0, Integer.MAX_VALUE);

        Optional<AspiranteOpcion> found = list.stream()
                .filter(ao -> ao.getIdOpcion() != null
                        && idOpcion.equals(ao.getIdOpcion().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("aspirante=" + idAspirante + ", opcion=" + idOpcion, "AspiranteOpcion");
        }
        return Response.ok(found.get()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idAspirante") UUID idAspirante,
            AspiranteOpcion entity,
            @Context UriInfo uriInfo) {
        if (idAspirante == null) return unprocessable("idAspirante");
        if (entity == null)  return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdOpcion() == null) return unprocessable("entity.idOpcion must be provided in body");

            Aspirante aspirante = aspiranteDAO.findById(idAspirante);
            if (aspirante == null) return notFound(idAspirante.toString(), "Aspirante");

            Opcion opcion = opcionDAO.findById(entity.getIdOpcion().getId());
            if (opcion == null) return notFound(entity.getIdOpcion().getId().toString(), "Opcion");

            entity.setIdAspirante(aspirante);
            entity.setIdOpcion(opcion);

            aspiranteOpcionDAO.create(entity);

            URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
            return Response.created(created).build();
    }

    @Delete
    @Path("{idOpcion}")
    public Response delete(
            @PathParam("idAspirante") UUID idAspirante,
            @PathParam("idOpcion") UUID idOpcion) {
        if (idAspirante == null) return unprocessable("idAspirante");
        if (idOpcion == null) return unprocessable("idOpcion");

        List<AspiranteOpcion> list =
                aspiranteOpcionDAO.findOpcionByIdAspirante(idAspirante, 0, Integer.MAX_VALUE);

        Optional<AspiranteOpcion> found = list.stream()
                .filter(ao -> ao.getIdOpcion() != null
                        && idOpcion.equals(ao.getIdOpcion().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("aspirante=" + idAspirante + ", opcion=" + idOpcion, "AspiranteOpcion");
        }
        aspiranteOpcionDAO.delete(found.get());
        return Response.noContent().build();
    }
}
