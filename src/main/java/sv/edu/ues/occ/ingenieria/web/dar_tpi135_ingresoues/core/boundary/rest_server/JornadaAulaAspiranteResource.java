package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.*;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.*;

import java.io.Serializable;
import java.util.UUID;

@Path("jornadaAula/{idJornadaAula}/aspirantePrueba/{idAspirantePrueba}")
public class JornadaAulaAspiranteResource implements Serializable {

    @Inject
    JornadaAulaAspiranteDAO jaaDAO;

    @Inject
    JornadaAulaDAO jornadaAulaDAO;

    @Inject
    AspirantePruebaDAO aspirantePruebaDAO;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Integer id) {

        if (id != null) {
            try {
                JornadaAulaAspirante resp = jaaDAO.findById(id);

                if (resp != null) {
                    return Response.ok(resp).build();
                }

                return Response.status(404).build();

            } catch (Exception e) {
                return Response.status(500).build();
            }
        }

        return Response.status(422).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("idJornadaAula") UUID idJornadaAula,
                           @PathParam("idAspirantePrueba") Integer idAspirantePrueba,
                           JornadaAulaAspirante entity,
                           @Context UriInfo uriInfo) {

        if (idJornadaAula == null || idAspirantePrueba == null || entity == null || entity.getId() != null) {
            return Response.status(422).build();
        }

        try {

            JornadaAula ja = jornadaAulaDAO.findById(idJornadaAula);
            AspirantePrueba ap = aspirantePruebaDAO.findById(idAspirantePrueba);

            if (ja == null || ap == null) {
                return Response.status(404).build();
            }

            entity.setIdJornadaAula(ja);
            entity.setIdAspirantePrueba(ap);

            jaaDAO.create(entity);

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
                           @PathParam("idJornadaAula") UUID idJornadaAula,
                           @PathParam("idAspirantePrueba") Integer idAspirantePrueba,
                           JornadaAulaAspirante entity) {

        if (id == null || idJornadaAula == null || idAspirantePrueba == null || entity == null) {
            return Response.status(422).build();
        }

        try {
            JornadaAulaAspirante existing = jaaDAO.findById(id);

            if (existing == null) {
                return Response.status(404).build();
            }

            JornadaAula ja = jornadaAulaDAO.findById(idJornadaAula);
            AspirantePrueba ap = aspirantePruebaDAO.findById(idAspirantePrueba);

            if (ja == null || ap == null) {
                return Response.status(404).build();
            }

            entity.setId(id);
            entity.setIdJornadaAula(ja);
            entity.setIdAspirantePrueba(ap);

            JornadaAulaAspirante updated = jaaDAO.update(entity);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(500).build();
        }
    }
}