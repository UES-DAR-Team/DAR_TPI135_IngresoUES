package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaJornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaDistractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaJornada;

import java.io.Serializable;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("prueba/{idPrueba}/jornada")
public class PruebaJornadaResource extends AbstractResource implements Serializable {

    @Inject
    PruebaJornadaDAO pruebaJornadaDAO;

    @Inject
    PruebaDAO pruebaDAO;

    @Inject
    JornadaDAO jornadaDAO;

    @GET
    @Path("{idJornada}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idJornada") UUID idJornada) {
        if (idPrueba == null) return unprocessable("idPrueba");
        if (idJornada == null) return unprocessable("idJornada");

        List<PruebaJornada> list =
                pruebaJornadaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE);

        Optional<PruebaJornada> found = list.stream()
                .filter(pj -> pj.getIdJornada() != null
                        && idJornada.equals(pj.getIdJornada().getId())).findFirst();

        if (found.isEmpty()) {
            return notFound("prueba=" + idPrueba + ", jornada=" + idJornada, "PruebaJornada");
        }
        return Response.ok(found.get()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("idPrueba") UUID idPrueba,
            PruebaJornada entity,
            @Context UriInfo uriInfo) {
        if (idPrueba == null) return unprocessable("idPrueba");
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");
        if (entity.getIdJornada() == null) return unprocessable("entity.idJornada must be provided in body");

        Prueba prueba = pruebaDAO.findById(idPrueba);
        if (prueba == null) return notFound(idPrueba.toString(), "Prueba");

        Jornada jornada = jornadaDAO.findById(entity.getIdJornada().getId());
        if (jornada == null) return notFound(entity.getIdJornada().getId().toString(), "Jornada");

        entity.setIdPrueba(prueba);
        entity.setIdJornada(jornada);
        pruebaJornadaDAO.create(entity);

        URI created = uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build();
        return Response.created(created).build();
    }

    @PUT
    @Path("{idJornada}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idJornada") UUID idJornada,
            PruebaJornada entity) {
        if (idPrueba == null) return unprocessable("idPrueba");
        if (idJornada == null) return unprocessable("idJornada");
        if (entity == null) return unprocessable("entity must not be null");
        
        List<PruebaJornada> list =
                pruebaJornadaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
  
        Optional<PruebaJornada> found = list.stream()
                .filter(pj -> pj.getIdJornada() != null
                        && idJornada.equals(pj.getIdJornada().getId())).findFirst();

        if(found.isEmpty()){
            return notFound("prueba=" + idPrueba + ", jornada=" + idJornada, "PruebaJornada");
        }
        
        PruebaJornada existing = found.get();
      //  existing.setIdPrueba( );
        // existing.setIdJornada();
        PruebaJornada updated = pruebaJornadaDAO.update(existing);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{idJornada}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idPrueba") UUID idPrueba,
            @PathParam("idJornada") UUID idJornada) {
        if (idPrueba == null) return unprocessable("idPrueba");
        if (idJornada == null) return unprocessable("idJornada");

        List<PruebaJornada> list =
                pruebaJornadaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE);

        Optional<PruebaJornada> found = list.stream()
                .filter(pj -> pj.getIdJornada() != null
                        && idJornada.equals(pj.getIdJornada().getId())).findFirst();

        if (found.isEmpty()){
            return notFound("prueba=" + idPrueba + ", jornada=" + idJornada, "PruebaJornada");
        }
        pruebaJornadaDAO.delete(found.get());
        return Response.noContent().build();
    }

}