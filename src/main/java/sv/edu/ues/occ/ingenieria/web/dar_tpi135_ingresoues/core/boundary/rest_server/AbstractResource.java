package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;

public abstract class AbstractResource {

    protected Response unprocessable(String missingParam) {
        return Response.status(422)
                .header("Missing-parameter", missingParam)
                .build();
    }

    protected Response notFound(String id, String entityName) {
        return Response.status(Response.Status.NOT_FOUND)
                .header("Not-found-id", entityName + " with id " + id + " not found")
                .build();
    }

    protected Response conflict(String id, String reason) {
        return Response.status(Response.Status.CONFLICT)
                .header("Conflict-id", "Record with id " + id + " " + reason)
                .build();
    }
}