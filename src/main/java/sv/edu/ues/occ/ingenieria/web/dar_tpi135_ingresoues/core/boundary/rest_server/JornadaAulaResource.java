package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AulaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaAulaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aula;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("jornada/{idJornada}/aula")
public class JornadaAulaResource extends AbstractResource implements Serializable {

    @Inject
    JornadaAulaDAO jornadaAulaDAO;

    @Inject
    JornadaDAO jornadaDAO;

    @Inject
    AulaDAO aulaDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idJornada") UUID idJornada,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(100) @Min(1) @DefaultValue("100") @QueryParam("max") int max) {
        if (idJornada == null) return unprocessable("idJornada");
        if (first < 0 || max <= 0 || max > 100) return unprocessable("first, max");

        Jornada jornada = jornadaDAO.findById(idJornada);
        if (jornada == null) return notFound(idJornada.toString(), "Jornada");

        List<JornadaAula> list = jornadaAulaDAO.findByJornada(idJornada, first, max);
        int total = jornadaAulaDAO.count();
        return Response.ok(list)
                .header(X_TOTAL_COUNT, total)
                .build();
    }

    @GET
    @Path("{idAula}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idAula") UUID idAula) {
        if (idJornada == null) return unprocessable("idJornada");
        if (idAula == null) return unprocessable("idAula");

        List<JornadaAula> list =
                jornadaAulaDAO.findByJornada(idJornada, 0, Integer.MAX_VALUE);

        Optional<JornadaAula> found = list.stream()
                .filter(ja -> ja.getIdAula() != null
                        && idAula.equals(ja.getIdAula().getId()))
                .findFirst();
        if (found.isEmpty()){
            return notFound("jornada="+idJornada+", aula="+idAula, "JornadaAula");
        }
        return  Response.ok(found.get()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idJornada") UUID idJornada,
            JornadaAula entity,
            @Context UriInfo uriInfo) {
            if (idJornada == null) return unprocessable("idJornada");
            if (entity == null) return unprocessable("entity must not be null");
            if (entity.getId() != null) return unprocessable("entity.id must be null");
            if (entity.getIdAula() == null) return unprocessable("entity.idAula must be provided in body");

            Jornada jornada = jornadaDAO.findById(idJornada);
            if (jornada == null) return notFound(idJornada.toString(), "Jornada");

            Aula aula = aulaDAO.findById(entity.getIdAula().getId());
            if (aula == null) return notFound(entity.getIdAula().getId().toString(), "Aula");

            entity.setIdJornada(jornada);
            entity.setIdAula(aula);
            jornadaAulaDAO.create(entity);

            URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
            return Response.created(created).build();

    }

    @DELETE
    @Path("{idAula}")
    public Response delete(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idAula") UUID idAula) {
        if (idJornada == null) return unprocessable("idJornada");
        if (idAula == null) return unprocessable("idAula");

        List<JornadaAula> list =
                jornadaAulaDAO.findByJornada(idJornada, 0, Integer.MAX_VALUE);

        Optional<JornadaAula> found = list.stream()
                .filter(ja -> ja.getIdAula() != null
                        && idAula.equals(ja.getIdAula().getId()))
                .findFirst();
        if (found.isEmpty()){
            return notFound("jornada="+idJornada+", aula="+idAula, "JornadaAula");
        }

        jornadaAulaDAO.delete(found.get());
        return Response.noContent().build();
    }
}