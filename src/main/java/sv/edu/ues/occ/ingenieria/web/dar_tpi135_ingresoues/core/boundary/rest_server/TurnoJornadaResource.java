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
public class TurnoJornadaResource implements Serializable {
    @Inject
    TurnoJornadaDAO turnoJornadaDAO;

    @Inject
    TurnoDAO turnoDAO;

    @Inject
    JornadaDAO jornadaDAO;

    private static final Logger LOG = Logger.getLogger(TurnoResource.class.getName());

    //un findrange por jornada
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @PathParam("idJornada") UUID idJornada,
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (idJornada == null) {
            return Response.status(422).header("Missing-parameter", "idJornada").build();
        }
        if (first < 0 || max <= 0 || max > 10) {
            return Response.status(422).header("Missing-parameter", "first,max").build();
        }
        try {
            Jornada jornada = jornadaDAO.findById(idJornada);
            if (jornada == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Jornada with id " + idJornada + " not found")
                        .build();
            }
            List<TurnoJornada> list = turnoJornadaDAO.findTurnoByIdJornada(idJornada, first, max);
            int total = turnoJornadaDAO.count();
            return Response.ok(list)
                    .header("X-Total-Count", total)
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving TurnoJornada range", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db").build();
        }
    }

    //busca una asociacion especifica por turno
    @GET
    @Path("{idTurno}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findOne(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idTurno") UUID idTurno) {

        if (idJornada == null || idTurno == null) {
            return Response.status(422).header("Missing-parameter", "idJornada,idTurno").build();
        }
        try {
            List<TurnoJornada> list = turnoJornadaDAO.findTurnoByIdJornada(idJornada, 0, Integer.MAX_VALUE);
            //luego implementar logica para filtrar esto en el dao y aqui solo llamarlo
            Optional<TurnoJornada> found = list.stream()
                    .filter(t -> t.getIdTurno() != null && idTurno.equals(t.getIdTurno().getId()))
                    .findFirst();
            if (found.isPresent()) {
                return Response.ok(found.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking jornada " + idJornada + " and turno " + idTurno + "not found")
                        .build();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving TurnoJornada", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idJornada") UUID idJornada,
            TurnoJornada entity,
            @Context UriInfo uriInfo) {

        if (idJornada == null) {
            return Response.status(422).header("Missing-parameter", "idJornada").build();
        }
        if (entity == null) {
            return Response.status(422).header("Missing-parameter", "entity must not be null").build();
        }
        if (entity.getId() != null) {
            return Response.status(422).header("Missing-parameter", "entity.id must be null").build();
        }
        if (entity.getIdTurno() == null) {
            return Response.status(422).header("Missing-parameter", "idTurno must be provider in body").build();
        }
        try {
            Jornada jornada = jornadaDAO.findById(idJornada);
            if (jornada == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Jornada with id " + idJornada + " not found")
                        .build();
            }
            Turno turno = turnoDAO.findById(entity.getIdTurno().getId());
            if (turno == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Turno with id " + entity.getIdTurno().getId() + " not found")
                        .build();
            }

            entity.setIdJornada(jornada);
            entity.setIdTurno(turno);

            turnoJornadaDAO.create(entity);
            URI created = uriInfo.getAbsolutePathBuilder().path(turno.getId().toString()).build();
            return Response.created(created).entity(entity).build();

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating TurnoJornada", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db").build();
        }

    }


    @DELETE
    @Path("{idTurno}")
    public Response delete(
            @PathParam("idJornada") UUID idJornada,
            @PathParam("idTurno") UUID idTurno) {
        if(idJornada == null || idTurno == null) {
            return Response.status(422).header("Missing-parameter", "idJornada,idTurno").build();
        }
        try{
            List<TurnoJornada> list = turnoJornadaDAO.findTurnoByIdJornada(idJornada, 0, Integer.MAX_VALUE);
            Optional<TurnoJornada> found = list.stream()
                    .filter(t -> t.getIdTurno() != null && idTurno.equals(t.getIdTurno().getId()))
                    .findFirst();
            if(found.isEmpty()){
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record linking jornada " + idJornada + " and turno " + idTurno + "not found")
                        .build();
            }

            turnoJornadaDAO.delete(found.get());
            return Response.noContent().build();

        }catch (Exception ex){
            LOG.log(Level.SEVERE, "Error deleting TurnoJornada", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db").build();
        }
    }


}
