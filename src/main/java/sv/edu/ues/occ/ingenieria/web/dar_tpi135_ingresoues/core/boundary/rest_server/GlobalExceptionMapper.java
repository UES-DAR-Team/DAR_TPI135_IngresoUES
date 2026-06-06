package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.persistence.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;


@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception e) {
        LOG.log(Level.SEVERE, "Excepción global capturada por ExceptionMapper", e);

        return Response.serverError()
                .header("Server-exception", buildHeaderError(e))
                .build();
    }

    private String buildHeaderError(Exception e) {
        Throwable cause = getRootCause(e);
        if (cause instanceof java.sql.SQLException sqlEx) {
            return sanitizeForHeader("DB-Error: SQLState=" + sqlEx.getSQLState() + " - " + sqlEx.getMessage());
        }
        if (e instanceof EntityExistsException) {
            return "DB-Error: Entity already exists";
        }
        if (e instanceof OptimisticLockException) {
            return "DB-Error: Concurrent modification detected";
        }
        if (e instanceof TransactionRequiredException) {
            return "DB-Error: No active transaction";
        }
        if (e instanceof NoResultException) {
            return "DB-Error: No result found";
        }
        if (e instanceof PersistenceException) {
            return sanitizeForHeader("DB-Error: " + e.getMessage());
        }
        return sanitizeForHeader("Unexpected error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
    }

    private Throwable getRootCause(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }


    private String sanitizeForHeader(String value) {
        if (value == null) return "Unknown error";
        String sanitized = value.replaceAll("[\\r\\n\\t]", " ")
                .replaceAll("[^\\x20-\\x7E]", "");
        return sanitized.substring(0, Math.min(sanitized.length(), 150));
    }
}