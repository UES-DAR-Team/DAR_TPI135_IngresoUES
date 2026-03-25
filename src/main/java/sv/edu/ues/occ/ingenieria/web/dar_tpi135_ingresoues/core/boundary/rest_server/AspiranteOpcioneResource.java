package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteOpcioneDAO;


import java.io.Serializable;
import java.util.UUID;

//modificar el path a conveniencia de la logica que debe seguir
@Path("aspirante/{idAspirante}/opciones")
public class AspiranteOpcioneResource implements Serializable {
    @Inject
    AspiranteOpcioneDAO aspiranteOpcioneDAO;

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
            @PathParam("idAspirante")
            UUID idAspirante
    ) {
        //Desarrollar con con el flujo de TDD
        //validacion de parametros
        //captura de excepciones
        //logica

        return Response.status(422).header("Missing-parameter", "first,max").build();
    }
}
