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
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("jornadaAula/{idJornadaAula}/aspirantePrueba")
public class JornadaAulaAspiranteResource extends AbstractResource implements Serializable {

    @Inject
    JornadaAulaAspiranteDAO jaaDAO;

    @Inject
    JornadaAulaDAO jornadaAulaDAO;

    @Inject
    AspirantePruebaDAO aspirantePruebaDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {

        if (idJornadaAula == null) return unprocessable("idJornadaAula");

        if (first < 0 || max <= 0 || max > 10) return unprocessable("first,max");


        JornadaAula ja = jornadaAulaDAO.findById(idJornadaAula);
        if (ja == null) return notFound(idJornadaAula.toString(), "JornadaAula");

        List<JornadaAulaAspirante> lista = jaaDAO.findByJornadaAula(idJornadaAula, first, max);
        int total = jaaDAO.count();

        return Response.ok(lista)
                .header(X_TOTAL_COUNT, total)
                .build();
    }

    @GET
    @Path("{idAspirantePrueba}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @PathParam("idAspirantePrueba") Integer idAspirantePrueba) {

        if (idJornadaAula == null) return unprocessable("idJornadaAula");
        if (idAspirantePrueba == null) return unprocessable("idAspirantePrueba");

        List<JornadaAulaAspirante> list =
                jaaDAO.findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE);

        Optional<JornadaAulaAspirante> found = list.stream()
                .filter(jaa -> jaa.getIdAspirantePrueba() != null
                        && idAspirantePrueba.equals(jaa.getIdAspirantePrueba().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("jornadaAula=" + idJornadaAula + ", aspirantePrueba=" + idAspirantePrueba, "JornadaAula");
        }
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            JornadaAulaAspirante entity,
            @Context UriInfo uriInfo) {

        if (idJornadaAula == null) return unprocessable("idJornadaAula");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdAspirantePrueba() == null || entity.getIdAspirantePrueba().getId() == null)
            return unprocessable("entity.idAspirantePrueba.id must be provided in body");

        JornadaAula ja = jornadaAulaDAO.findById(idJornadaAula);
        if (ja == null) return notFound(idJornadaAula.toString(), "JornadaAula");

        AspirantePrueba ap = aspirantePruebaDAO.findById(entity.getIdAspirantePrueba().getId());
        if (ap == null) return notFound(entity.getIdAspirantePrueba().getId().toString(), "AspirantePrueba");

        entity.setIdJornadaAula(ja);
        entity.setIdAspirantePrueba(ap);

        jaaDAO.create(entity);

        URI crated = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(crated).build();
    }

    @PUT
    @Path("{idAspirantePrueba}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @PathParam("idAspirantePrueba") Integer idAspirantePrueba,
            JornadaAulaAspirante entity) {

        if (idJornadaAula == null) return unprocessable("idJornadaAula");
        if (idAspirantePrueba == null) return unprocessable("idAspirantePrueba");
        if (entity == null) return unprocessable("entity must not be null");

        List<JornadaAulaAspirante> list =
                jaaDAO.findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE);

        Optional<JornadaAulaAspirante> found = list.stream()
                .filter(jaa -> jaa.getIdAspirantePrueba() != null
                        && idAspirantePrueba.equals(jaa.getIdAspirantePrueba().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("jornadaAula=" + idJornadaAula + ", aspirantePrueba=" + idAspirantePrueba, "JornadaAulaAspirante");
        }

        JornadaAulaAspirante existing = found.get();
        existing.setHoraLlegada(entity.getHoraLlegada());
        existing.setAsistio(entity.getAsistio());

        JornadaAulaAspirante updated = jaaDAO.update(existing);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{idAspirantePrueba}")
    public Response delete(
            @PathParam("idJornadaAula") Integer idJornadaAula,
            @PathParam("idAspirantePrueba") Integer idAspirantePrueba) {

        if (idJornadaAula == null) return unprocessable("idJornadaAula");
        if (idAspirantePrueba == null) return unprocessable("idAspirantePrueba");

        List<JornadaAulaAspirante> list =
                jaaDAO.findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE);

        Optional<JornadaAulaAspirante> found = list.stream()
                .filter(jaa -> jaa.getIdAspirantePrueba() != null
                        && idAspirantePrueba.equals(jaa.getIdAspirantePrueba().getId()))
                .findFirst();

        if (found.isEmpty()) {
            return notFound("jornadaAula=" + idJornadaAula + ", aspirantePrueba=" + idAspirantePrueba, "JornadaAulaAspirante");
        }

        jaaDAO.delete(found.get());
        return Response.noContent().build();
    }
}