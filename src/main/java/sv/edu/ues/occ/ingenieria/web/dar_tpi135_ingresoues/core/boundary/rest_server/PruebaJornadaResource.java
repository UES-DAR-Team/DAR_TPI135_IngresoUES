package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaJornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaJornada;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Path("prueba/{idPrueba}/jornada/{idJornada}")
public class PruebaJornadaResource implements Serializable {

    @Inject
    PruebaJornadaDAO pruebaJornadaDAO;

    @Inject
    PruebaDAO pruebaDAO;

    @Inject
    JornadaDAO jornadaDAO;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idJornada") UUID idJornada,
            @PathParam("id") Integer id) {
        if (idPrueba == null || idJornada == null || id == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idPrueba,idJornada,id")
                    .build();
        }
        try {
            PruebaJornada resp = pruebaJornadaDAO.findById(id);
            if (resp == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Record not found")
                        .build();
            }
            if (resp.getIdPrueba() == null || resp.getIdJornada() == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Record not found")
                        .build();
            }
            if (!resp.getIdPrueba().getId().equals(idPrueba) ||
                    !resp.getIdJornada().getId().equals(idJornada)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Record not found")
                        .build();
            }
            return Response.ok(resp).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idJornada") UUID idJornada,
            PruebaJornada entity,
            @Context UriInfo uriInfo) {
        if (idPrueba == null || idJornada == null || entity == null || entity.getId() != null) {
            return Response.status(422)
                    .header("Missing-parameter", "idPrueba,idJornada,entity")
                    .build();
        }
        if (entity.getFechaAsignacion() == null) {
            entity.setFechaAsignacion(OffsetDateTime.now());
        }
        try {
            Prueba prueba = pruebaDAO.findById(idPrueba);
            if (prueba == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Prueba not found")
                        .build();
            }
            Jornada jornada = jornadaDAO.findById(idJornada);
            if (jornada == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Jornada not found")
                        .build();
            }
            entity.setIdPrueba(prueba);
            entity.setIdJornada(jornada);
            pruebaJornadaDAO.create(entity);
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
    public Response update(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idJornada") UUID idJornada,
            @PathParam("id") Integer id,
            PruebaJornada entity) {
        if (idPrueba == null || idJornada == null || id == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idPrueba,idJornada,id,entity")
                    .build();
        }
        try {
            PruebaJornada existing = pruebaJornadaDAO.findById(id);

            if (existing == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Record not found")
                        .build();
            }

            if (existing.getIdPrueba() == null || existing.getIdJornada() == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Record not found")
                        .build();
            }
            if (!existing.getIdPrueba().getId().equals(idPrueba) ||
                    !existing.getIdJornada().getId().equals(idJornada)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Record not found")
                        .build();
            }
            Prueba prueba = pruebaDAO.findById(idPrueba);
            Jornada jornada = jornadaDAO.findById(idJornada);

            if (prueba == null || jornada == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Prueba or Jornada not found")
                        .build();
            }
            if (entity.getFechaAsignacion() != null) {
                existing.setFechaAsignacion(entity.getFechaAsignacion());
            }
            existing.setIdPrueba(prueba);
            existing.setIdJornada(jornada);

            PruebaJornada updated = pruebaJornadaDAO.update(existing);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }
}