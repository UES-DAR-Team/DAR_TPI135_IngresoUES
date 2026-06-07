package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;

public abstract class AbstractResource {

    public static final String X_TOTAL_COUNT = "X-Total-Count";
    public static final String MISSING_PARAMETER = "Missing-parameter";
    public static final String NOT_FOUND_ID = "Not-found-id";
    public static final String CONFLICT_ID = "Conflict-id";

    protected Response unprocessable(String missingParam) {
        return Response.status(422)
                .header(MISSING_PARAMETER, missingParam)
                .build();
    }

    protected Response notFound(String id, String entityName) {
        return Response.status(Response.Status.NOT_FOUND)
                .header(NOT_FOUND_ID, entityName + " with id " + id + " not found")
                .build();
    }

    protected Response conflict(String id, String reason) {
        return Response.status(Response.Status.CONFLICT)
                .header(CONFLICT_ID, "Record with id " + id + " " + reason)
                .build();
    }
}