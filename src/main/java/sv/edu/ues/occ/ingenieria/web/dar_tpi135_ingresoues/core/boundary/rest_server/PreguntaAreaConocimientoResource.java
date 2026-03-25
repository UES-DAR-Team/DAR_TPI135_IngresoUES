package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaAreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaDAO;

import java.io.Serializable;

//modificar el path a conveniencia de la logica que debe seguir
@Path("pregunta/{idPregunta}/{idAreaConocimiento}")
public class PreguntaAreaConocimientoResource implements Serializable {
    @Inject
    PreguntaAreaConocimientoDAO preguntaAreaConocimientoDAO;

    @Inject
    AreaConocimientoDAO areaConocimientoDAO;

    @Inject
    PreguntaDAO preguntaDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0)
            @DefaultValue("0")
            @QueryParam("first")
            int first,
            @Max(10)
            @DefaultValue("10")
            @QueryParam("max")
            int max,
            @PathParam("idAreaConocimiento") Long idAreaConocimiento,
            @PathParam("idPregunta") Long idPregunta
    ) {
        //Desarrollar con con el flujo de TDD
        //validacion de parametros
        //captura de excepciones
        //logica

        return Response.status(422).header("Missing-parameter", "first,max").build();
    }
}
