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
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("jornadaAulaAspirante/{idJornadaAulaAspirante}/resultado")
public class JornadaAulaAspiranteResultadoResource extends AbstractResource implements Serializable {

    @Inject
    JornadaAulaAspiranteResultadoDAO jaarDAO;// resultado

    @Inject
    JornadaAulaAspiranteDAO jornadaAulaAspiranteDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max) {
        if (idJornadaAulaAspirante == null) return unprocessable("idJornadaAulaAspirante");
        if (first < 0 || max <= 0 || max > 10) return unprocessable("first,max");

        JornadaAulaAspirante jaa = jornadaAulaAspiranteDAO.findById(idJornadaAulaAspirante);
        if (jaa == null) return notFound(idJornadaAulaAspirante.toString(), "JornadaAulaAspirante");

        List<JornadaAulaAspiranteResultado> lista =
                jaarDAO.findByJornadaAulaAspirante(idJornadaAulaAspirante, first, max);
        int total = jaarDAO.count();
        return Response.ok(lista)
                .header(X_TOTAL_COUNT, lista.size())
                .build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            @PathParam("id") Integer id) {
        if (idJornadaAulaAspirante == null) return unprocessable("idJornadaAulaAspirante");
        if (id == null) return unprocessable("id");

        List<JornadaAulaAspiranteResultado> list =
                jaarDAO.findByJornadaAulaAspirante(idJornadaAulaAspirante, 0, Integer.MAX_VALUE);

        Optional<JornadaAulaAspiranteResultado> found = list.stream()
                .filter(jaar -> jaar.getId() != null
                        && id.equals(jaar.getId())).findFirst();

        if (found.isEmpty()) {
            return notFound("Resultado con id " + id + ", jornadaAulaAspirante " + idJornadaAulaAspirante, "JornadaAulaAspiranteResultado");
        }
        return Response.ok(found.get()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            JornadaAulaAspiranteResultado entity,
            @Context UriInfo uriInfo) {

        if (idJornadaAulaAspirante == null) return unprocessable("idJornadaAulaAspirante");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");

        JornadaAulaAspirante jaa = jornadaAulaAspiranteDAO.findById(idJornadaAulaAspirante);
        if (jaa == null) return notFound(idJornadaAulaAspirante.toString(), "JornadaAulaAspirante");

        entity.setIdJornadaAulaAspirante(jaa);
        jaarDAO.create(entity);

        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            @PathParam("id") Integer id,
            JornadaAulaAspiranteResultado entity) {
        if (idJornadaAulaAspirante == null) return unprocessable("idJornadaAulaAspirante");
        if (id == null) return unprocessable("id");
        if (entity == null) return unprocessable("entity must not be null");

        List<JornadaAulaAspiranteResultado> list =
                jaarDAO.findByJornadaAulaAspirante(idJornadaAulaAspirante, 0, Integer.MAX_VALUE);

        Optional<JornadaAulaAspiranteResultado> found = list.stream()
                .filter(jaar -> jaar.getId() != null
                        && id.equals(jaar.getId())).findFirst();

        if (found.isEmpty()) {
            return notFound("resultado=" + id + ", jornadaAulaAspirante=" + idJornadaAulaAspirante,
                    "JornadaAulaAspiranteResultado");
        }

        JornadaAulaAspiranteResultado existing = found.get();
        existing.setAprobado(entity.getAprobado());
        existing.setPuntajeObtenido(entity.getPuntajeObtenido());
        JornadaAulaAspiranteResultado updated = jaarDAO.update(existing);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
            @PathParam("id") Integer id) {
        if (idJornadaAulaAspirante == null) return unprocessable("idJornadaAulaAspirante");
        if (id == null) return unprocessable("id");


        JornadaAulaAspiranteResultado existing = jaarDAO.findById(id);

        if (existing == null
                || !existing.getIdJornadaAulaAspirante().getId().equals(idJornadaAulaAspirante)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Not-found", "Resultado no encontrado para el aspirante indicado")
                    .build();
        }

        jaarDAO.delete(existing);
        return Response.noContent().build();

    }
}