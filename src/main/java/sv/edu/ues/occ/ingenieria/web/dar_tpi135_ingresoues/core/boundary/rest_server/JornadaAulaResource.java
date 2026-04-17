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
import java.util.UUID;

@Path("jornada/{idJornada}/aula/{idAula}")
public class JornadaAulaResource implements Serializable {

    @Inject
    JornadaAulaDAO jornadaAulaDAO;

    @Inject
    JornadaDAO jornadaDAO;

    @Inject
    AulaDAO aulaDAO;


    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") UUID id) {

        if (id != null) {
            try {
                JornadaAula resp = jornadaAulaDAO.findById(id);

                if (resp != null) {
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
                .header("Missing-parameter", "id")
                .build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("idJornada") UUID idJornada,
                           @PathParam("idAula") UUID idAula,
                           JornadaAula entity,
                           @Context UriInfo uriInfo) {

        if (idJornada == null || idAula == null || entity == null || entity.getId() != null) {
            return Response.status(422).build();
        }

        try {
            Jornada jornada = jornadaDAO.findById(idJornada);
            if (jornada == null) {
                return Response.status(404)
                        .header("Not-found", "Jornada no existe")
                        .build();
            }

            Aula aula = aulaDAO.findById(idAula);
            if (aula == null) {
                return Response.status(404)
                        .header("Not-found", "Aula no existe")
                        .build();
            }

            entity.setIdJornada(jornada);
            entity.setIdAula(aula);

            jornadaAulaDAO.create(entity);

            return Response.created(
                    uriInfo.getAbsolutePathBuilder()
                            .path(String.valueOf(entity.getId()))
                            .build()
            ).entity(entity).build();

        } catch (Exception e) {
            return Response.status(500)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Integer id,
                           @PathParam("idJornada") UUID idJornada,
                           @PathParam("idAula") UUID idAula,
                           JornadaAula entity) {

        if (id == null || idJornada == null || idAula == null || entity == null) {
            return Response.status(422).build();
        }

        try {
            JornadaAula existing = jornadaAulaDAO.findById(id);

            if (existing == null) {
                return Response.status(404).build();
            }

            Jornada jornada = jornadaDAO.findById(idJornada);
            Aula aula = aulaDAO.findById(idAula);

            if (jornada == null || aula == null) {
                return Response.status(404).build();
            }

            entity.setId(id);
            entity.setIdJornada(jornada);
            entity.setIdAula(aula);

            JornadaAula updated = jornadaAulaDAO.update(entity);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(500).build();
        }
    }
}