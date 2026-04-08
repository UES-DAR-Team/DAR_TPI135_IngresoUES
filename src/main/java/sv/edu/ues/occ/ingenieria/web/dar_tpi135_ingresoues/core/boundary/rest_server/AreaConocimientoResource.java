package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("areaConocimiento")
public class AreaConocimientoResource implements Serializable {
    @Inject
    AreaConocimientoDAO areaConocimientoDAO;

    private static final Logger LOG = Logger.getLogger(AreaConocimientoResource.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        //validacion duplicada para especificar el status
        //en un futuro interceptar la Bean validation para customizar el mensaje de error y status
        if(first>=0 && max > 0 && max <= 10 ){
            try{
                List<AreaConocimiento> encontrados = areaConocimientoDAO.findRange(first, max);
                int total = areaConocimientoDAO.count();
                return Response.ok(encontrados)
                        .header("X-Total-Count", total)
                        .build();
            }catch (Exception ex){
                LOG.log(Level.SEVERE, "Error retrieving AreaConocimiento range", ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db")
                        .build();
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
                AreaConocimiento encontrados = areaConocimientoDAO.findById(id);
                if (encontrados != null) {
                    return Response.ok(encontrados).build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-found-id", "Record with id "+id+" not found")
                        .build();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error retrieving AreaConocimiento by id", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        header("Server-exception", "Cannot access db").build();
            }
        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

    //verificar que no tenga hijos, osea no se puede eliminar a un padre(luego hacerlo dentro de un service especifico)
    @DELETE
    @Path("{id}")
    public Response deleteById(@PathParam("id") UUID id) {
        if (id != null) {
            try {
                AreaConocimiento encontrados = areaConocimientoDAO.findById(id);
                if (encontrados != null) {
                    AreaConocimiento padre = encontrados.getIdAutoReferenciaArea();
                    areaConocimientoDAO.delete(encontrados);
                    return Response.noContent().build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Not-Found-id", "Record with id " + id + " not found")
                        .build();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error deleting AreaConocimiento", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .header("Server-exception", "Cannot access db")
                        .build();
            }
        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(AreaConocimiento entity, @Context UriInfo uriInfo) {
        if(entity != null){
            if(entity.getId() == null){
                try{
                    areaConocimientoDAO.create(entity);
                    return Response.created(uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build())
                            .entity(entity)
                            .build();
                }catch (Exception ex){
                    LOG.log(Level.SEVERE, "Error creating AreaConocimiento", ex);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .header("Server-exception", "Cannot access db")
                            .build();
                }
            }
            return Response.status(422)
                    .header("Missing-parameter", "entity.id must be null")
                    .build();
        }
        return Response.status(422).header("Missing-parameter", "entity must not be null").build();
    }

    //si tiene area padre y ademas tiene areas hijas que dependen de el
    //no se puede cambiar el padre al que pertenece, porque se perderia la relacion con sus hijos
    //(luego hacerlo dentro de un service especifico)
    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, AreaConocimiento entity) {
        if(id != null){
            if(entity != null){
                try{
                    AreaConocimiento existing = areaConocimientoDAO.findById(id);
                    if(existing != null){
                        entity.setId(id);
                        AreaConocimiento update = areaConocimientoDAO.update(entity);
                        return Response.ok(update).build();
                    }
                    return Response.status(Response.Status.NOT_FOUND)
                            .header("Not-found-id", "Record with id "+id+" not found")
                            .build();
                }catch (Exception ex){
                    LOG.log(Level.SEVERE, "Error updating AreaConocimiento", ex);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .header("Server-exception", "Cannot access db")
                            .build();
                }
            }
            return Response.status(422).header("Missing-parameter", "entity must not be null").build();
        }
        return Response.status(422).header("Missing-parameter", "id").build();
    }

}
