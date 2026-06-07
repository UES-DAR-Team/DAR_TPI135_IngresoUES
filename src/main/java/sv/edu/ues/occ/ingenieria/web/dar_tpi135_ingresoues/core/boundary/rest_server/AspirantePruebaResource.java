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
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("aspirante/{idAspirante}/prueba")
public class AspirantePruebaResource extends AbstractResource implements Serializable {

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
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {
        if (idAspirante == null) return unprocessable("idAspirante");
        if (first < 0 || max <= 0 || max > 10) return unprocessable("first,max");

        Aspirante aspirante = aspiranteDAO.findById(idAspirante);
        if (aspirante == null) return notFound(idAspirante.toString(), "Aspirante");

        List<AspirantePrueba> lista =
                aspirantePruebaDAO.findByAspirante(idAspirante, first, max);
        int total = aspirantePruebaDAO.count();
        return Response.ok(lista)
                .header(X_TOTAL_COUNT, total)
                .build();
    }

    @GET
    @Path("{idPrueba}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idAspirante") UUID idAspirante,
            @PathParam("idPrueba") UUID idPrueba) {
        if (idAspirante == null) return unprocessable("idAspirante");
        if (idPrueba == null) return unprocessable("idPrueba");

        List<AspirantePrueba> list =
                aspirantePruebaDAO.findByAspirante(idAspirante, 0, Integer.MAX_VALUE);

        Optional<AspirantePrueba> found = list.stream()
                .filter(ap -> ap.getIdPrueba() != null
                        && idPrueba.equals(ap.getIdPrueba().getId())).findFirst();

        if (found.isEmpty()) {
            return notFound("prueba= " + idPrueba + ", aspirante=" + idAspirante, "AspirantePrueba");
        }
        return Response.ok(found.get()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idAspirante") UUID idAspirante,
            AspirantePrueba entity,
            @Context UriInfo uriInfo) {
        if (idAspirante == null) return unprocessable("idAspirante");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdPrueba() == null) return unprocessable("entity.idPrueba must be provided in body");

        Aspirante aspirante = aspiranteDAO.findById(idAspirante);
        if (aspirante == null) return notFound(idAspirante.toString(), "Aspirante");

        Prueba prueba = pruebaDAO.findById(entity.getIdPrueba().getId());
        if (prueba == null) return notFound(entity.getIdPrueba().getId().toString(), "Prueba");

        entity.setIdAspirante(aspirante);
        entity.setIdPrueba(prueba);

        aspirantePruebaDAO.create(entity);
        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();

    }


    @DELETE
    @Path("{idPrueba}")
    public Response delete(
            @PathParam("idAspirante") UUID idAspirante,
            @PathParam("idPrueba") UUID idPrueba) {
        if (idAspirante == null) return unprocessable("idAspirante");
        if (idPrueba == null) return unprocessable("idPrueba");

        List<AspirantePrueba> list =
                aspirantePruebaDAO.findByAspirante(idAspirante, 0, Integer.MAX_VALUE);

        Optional<AspirantePrueba> found = list.stream()
                .filter(ap -> ap.getIdPrueba() != null
                        && idPrueba.equals(ap.getIdPrueba().getId())).findFirst();

        if (found.isEmpty()) {
            return notFound("prueba= " + idPrueba + ", aspirante=" + idAspirante, "AspirantePrueba");
        }

        aspirantePruebaDAO.delete(found.get());
        return Response.noContent().build();
    }
}