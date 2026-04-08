package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;

import javax.print.attribute.standard.Media;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("pregunta")
public class PreguntaResource implements Serializable {
    @Inject
    PreguntaDAO preguntaDAO;

    private static final Logger LOG = Logger.getLogger(PreguntaResource.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (first >= 0 && max > 0 && max <= 10) {
            try {
                List<Pregunta> encontrados = preguntaDAO.findRange(first, max);
                int total = preguntaDAO.count();
                return Response.ok(encontrados)
                        .header("X-Total-Count", total)
                        .build();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error retrieving Pregunta range", ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422).header("Missing-parameter", "first,max").build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") UUID id) {
        if (id != null) {
            try {
                Pregunta encontrados = preguntaDAO.findById(id);
                if (encontrados != null) {
                    return Response.ok(encontrados).build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record with id " + id + " not found").build();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error retrieving Pregunta by id", ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@PathParam("id") UUID id) {
        if (id != null) {
            try {
                Pregunta encontrados = preguntaDAO.findById(id);
                if (encontrados != null) {
                    preguntaDAO.delete(encontrados);
                    return Response.noContent().build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record with id " + id + " not found").build();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error deleting Pregunta by id", ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Pregunta entity, @Context UriInfo uriInfo) {
        if(entity != null){
            if(entity.getId() == null){
                try {
                    preguntaDAO.create(entity);
                    return Response.created(uriInfo.getAbsolutePathBuilder()
                            .path(entity.getId().toString()).build())
                            .entity(entity).build();
                }catch (Exception ex){
                    LOG.log(Level.SEVERE, "Error creating Pregunta", ex);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .header("Server-exception", "Cannot access db").build();
                }
            }
            return Response.status(422)
                    .header("Missing-parameter", "entity.id must be null")
                    .build();
        }
        return Response.status(422)
                .header("Missing-parameter", "entity must not be null")
                .build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, Pregunta entity) {
        if(id != null){
            if(entity != null){
                try{
                    Pregunta existing = preguntaDAO.findById(id);
                    if(existing != null){
                        entity.setId(id);
                        preguntaDAO.update(entity);
                        return Response.ok(entity).build();
                    }
                    return Response.status(Response.Status.NOT_FOUND)
                            .header("Not-found-id", "Record with id " + id + " not found").build();
                }catch (Exception ex){
                    LOG.log(Level.SEVERE, "Error updating Pregunta", ex);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .header("Server-exception", "Cannot access db").build();
                }
            }
            return Response.status(422)
                    .header("Missing-parameter", "entity must not be null")
                    .build();
        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }
}
