package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaPreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaPreguntaDistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPreguntaDistractor;

import java.io.Serializable;
import java.util.UUID;

@Path("pruebaAreaPregunta/{idPruebaAreaPregunta}/distractor/{idDistractor}")
public class PruebaAreaPreguntaDistractorResource implements Serializable {

    @Inject
    PruebaAreaPreguntaDistractorDAO pruebaAreaPreguntaDistractorDAO;

    @Inject
    PruebaAreaPreguntaDAO pruebaAreaPreguntaDAO;

    @Inject
    DistractorDAO distractorDAO;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
                             @PathParam("idDistractor") UUID idDistractor,
                             @PathParam("id") Integer id) {

        if (idPruebaAreaPregunta != null && idDistractor != null && id != null) {
            try {
                PruebaAreaPreguntaDistractor resp = pruebaAreaPreguntaDistractorDAO.findById(id);

                if (resp != null &&
                        resp.getIdPruebaAreaPregunta().getId().equals(idPruebaAreaPregunta) &&
                        resp.getIdDistractor().getId().equals(idDistractor)) {

                    return Response.ok(resp).build();
                }

                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Record not found")
                        .build();

            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db")
                        .build();
            }
        }

        return Response.status(422)
                .header("Missing-parameter", "idPruebaAreaPregunta,idDistractor,id")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
                           @PathParam("idDistractor") UUID idDistractor,
                           PruebaAreaPreguntaDistractor entity,
                           @Context UriInfo uriInfo) {

        if (idPruebaAreaPregunta == null || idDistractor == null || entity == null || entity.getId() != null) {
            return Response.status(422)
                    .header("Missing-parameter", "idPruebaAreaPregunta,idDistractor,entity")
                    .build();
        }

        try {
            PruebaAreaPregunta pap = pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta);
            if (pap == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "PruebaAreaPregunta not found")
                        .build();
            }

            Distractor distractor = distractorDAO.findById(idDistractor);
            if (distractor == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Distractor not found")
                        .build();
            }

            entity.setIdPruebaAreaPregunta(pap);
            entity.setIdDistractor(distractor);

            pruebaAreaPreguntaDistractorDAO.create(entity);

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
    public Response update(@PathParam("idPruebaAreaPregunta") Integer idPruebaAreaPregunta,
                           @PathParam("idDistractor") UUID idDistractor,
                           @PathParam("id") Integer id,
                           PruebaAreaPreguntaDistractor entity) {

        if (idPruebaAreaPregunta == null || idDistractor == null || id == null || entity == null) {
            return Response.status(422)
                    .header("Missing-parameter", "idPruebaAreaPregunta,idDistractor,id,entity")
                    .build();
        }

        try {
            PruebaAreaPreguntaDistractor existing = pruebaAreaPreguntaDistractorDAO.findById(id);

            if (existing == null ||
                    !existing.getIdPruebaAreaPregunta().getId().equals(idPruebaAreaPregunta) ||
                    !existing.getIdDistractor().getId().equals(idDistractor)) {

                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "Record not found")
                        .build();
            }

            PruebaAreaPregunta pap = pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta);
            Distractor distractor = distractorDAO.findById(idDistractor);

            if (pap == null || distractor == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found", "PruebaAreaPregunta or Distractor not found")
                        .build();
            }

            entity.setId(id);
            entity.setIdPruebaAreaPregunta(pap);
            entity.setIdDistractor(distractor);

            PruebaAreaPreguntaDistractor updated = pruebaAreaPreguntaDistractorDAO.update(entity);

            return Response.ok(updated).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header("Server-exception", "Cannot access db")
                    .build();
        }
    }
}