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

@Path("jornadaAulaAspirante/{idJornadaAulaAspirante}/resultado")
public class JornadaAulaAspiranteResultadoResource implements Serializable {

    @Inject
    JornadaAulaAspiranteResultadoDAO resultadoDAO;

    @Inject
    JornadaAulaAspiranteDAO jornadaAulaAspiranteDAO;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Integer id) {

        if (id != null) {
            try {
                JornadaAulaAspiranteResultado resp = resultadoDAO.findById(id);

                if (resp != null) {
                    return Response.ok(resp).build();
                }

                return Response.status(404)
                        .header("Not-found", "Registro no encontrado")
                        .build();

            } catch (Exception e) {
                return Response.status(500)
                        .build();
            }
        }

        return Response.status(422)
                .header("Missing-parameter", "id")
                .build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
                           JornadaAulaAspiranteResultado entity,
                           @Context UriInfo uriInfo) {

        if (idJornadaAulaAspirante == null || entity == null || entity.getId() != null) {
            return Response.status(422).build();
        }

        try {
            JornadaAulaAspirante jaa = jornadaAulaAspiranteDAO.findById(idJornadaAulaAspirante);

            if (jaa == null) {
                return Response.status(404)
                        .header("Not-found", "JornadaAulaAspirante no existe")
                        .build();
            }

            entity.setIdJornadaAulaAspirante(jaa);

            resultadoDAO.create(entity);

            return Response.created(
                    uriInfo.getAbsolutePathBuilder()
                            .path(String.valueOf(entity.getId()))
                            .build()
            ).entity(entity).build();

        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Integer id,
                           @PathParam("idJornadaAulaAspirante") Integer idJornadaAulaAspirante,
                           JornadaAulaAspiranteResultado entity) {

        if (id == null || idJornadaAulaAspirante == null || entity == null) {
            return Response.status(422).build();
        }

        try {
            JornadaAulaAspiranteResultado existing = resultadoDAO.findById(id);

            if (existing == null) {
                return Response.status(404).build();
            }

            JornadaAulaAspirante jaa = jornadaAulaAspiranteDAO.findById(idJornadaAulaAspirante);

            if (jaa == null) {
                return Response.status(404).build();
            }

            entity.setId(id);
            entity.setIdJornadaAulaAspirante(jaa);

            JornadaAulaAspiranteResultado updated = resultadoDAO.update(entity);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(500).build();
        }
    }
}