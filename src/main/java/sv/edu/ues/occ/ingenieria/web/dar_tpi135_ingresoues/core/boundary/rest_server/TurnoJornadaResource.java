package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.TurnoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.TurnoJornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Turno;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.TurnoJornada;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("jornada/{idJornada}/turno")
public class TurnoJornadaResource extends AbstractResource implements Serializable {
    @Inject
    TurnoJornadaDAO turnoJornadaDAO;

    @Inject
    TurnoDAO turnoDAO;

    @Inject
    JornadaDAO jornadaDAO;

    //un findrange por jornada
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idJornada") UUID idJornada,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (idJornada == null) return unprocessable("idJornada");
        if (first < 0 || max <= 0 || max > 10) return unprocessable("first,max");

        Jornada jornada = jornadaDAO.findById(idJornada);
        if (jornada == null) return notFound(idJornada.toString(), "Jornada");

        List<TurnoJornada> list = turnoJornadaDAO.findTurnoByIdJornada(idJornada, first, max);
        int total = turnoJornadaDAO.count();
        return Response.ok(list).header(X_TOTAL_COUNT, total).build();
    }

    //busca una asociacion especifica por turno
    @GET
    @Path("{idTurno}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idTurno") UUID idTurno) {

        if (idJornada == null) return unprocessable("idJornada");
        if (idTurno == null) return unprocessable("idTurno");

        List<TurnoJornada> list =
                turnoJornadaDAO.findTurnoByIdJornada(idJornada, 0, Integer.MAX_VALUE);
        Optional<TurnoJornada> found = list.stream()
                .filter(t -> t.getIdTurno() != null && idTurno.equals(t.getIdTurno().getId()))
                .findFirst();
        if (found.isEmpty()) {
            return notFound("jornada=" + idJornada + ", turno=" + idTurno, "TurnoJornada");
        }

        return Response.ok(found.get()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idJornada") UUID idJornada,
            TurnoJornada entity,
            @Context UriInfo uriInfo) {

        if (idJornada == null) return unprocessable("idJornada");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdTurno() == null) return unprocessable("entity.idTurno must be provided in body");

        Jornada jornada = jornadaDAO.findById(idJornada);
        if (jornada == null) return notFound(idJornada.toString(), "Jornada");

        Turno turno = turnoDAO.findById(entity.getIdTurno().getId());
        if (turno == null) return notFound(entity.getIdTurno().getId().toString(), "Turno");

        entity.setIdJornada(jornada);
        entity.setIdTurno(turno);

        turnoJornadaDAO.create(entity);
        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }


    @DELETE
    @Path("{idTurno}")
    public Response delete(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idTurno") UUID idTurno) {
        if (idJornada == null) return unprocessable("idJornada");
        if (idTurno == null) return unprocessable("idTurno");


        List<TurnoJornada> list = turnoJornadaDAO.findTurnoByIdJornada(idJornada, 0, Integer.MAX_VALUE);
        Optional<TurnoJornada> found = list.stream()
                .filter(t -> t.getIdTurno() != null && idTurno.equals(t.getIdTurno().getId()))
                .findFirst();
        if (found.isEmpty()) {
            return notFound("jornada=" + idJornada + ", turno=" + idTurno, "TurnoJornada");
        }

        turnoJornadaDAO.delete(found.get());
        return Response.noContent().build();
    }
}
