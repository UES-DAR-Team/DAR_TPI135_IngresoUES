package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.persistence.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Manejador global de excepciones para la capa REST.
 *
 * <p>Actúa como red de seguridad: captura cualquier excepción no controlada
 * que escape desde un Resource o capa de servicio, y la convierte en una
 * respuesta HTTP 500 estructurada con un header de diagnóstico.</p>
 *
 * <p><b>Con esta clase en su lugar, ningún Resource necesita un bloque
 * try/catch para excepciones de infraestructura (JPA, SQL)</b>
 * El contenedor JAX-RS delega automáticamente a este mapper antes de
 * devolver un error genérico al cliente.</p>
 *
 * <p><b>Patrón de uso:</b></p>
 * <pre>
 *   En el Resource, NO hacer esto:
 *   try {
 *       dao.create(entity);
 *   } catch (Exception ex) {
 *       return Response.serverError()...;  --> redundante
 *   }
 *
 *  Simplemente dejar que la excepción suba:
 *   dao.create(entity);
 *  Si falla, GlobalExceptionMapper la captura y retorna 500.
 * </pre>
 *
 * @see AbstractResource para el manejo de errores de negocio (422, 404, 409)
 */
@Provider // Registra esta clase en el runtime JAX-RS
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    /**
     * Punto de entrada del mapper. JAX-RS lo invoca automáticamente cuando
     * una excepción no controlada escapa desde cualquier Resource.
     *
     * <p>Loguea la excepción completa en nivel SEVERE (con stack trace)
     * y retorna siempre HTTP 500 con el header {@code Server-exception}
     * que contiene un mensaje corto y seguro para el cliente.</p>
     *
     * @param e la excepción capturada por el runtime de JAX-RS
     * @return Response HTTP 500 con header {@code Server-exception}
     */
    @Override
    public Response toResponse(Exception e) {
        LOG.log(Level.SEVERE, "Excepción global capturada por ExceptionMapper", e);

        return Response.serverError()
                .header("Server-exception", buildHeaderError(e))
                .build();
    }

    /**
     * Construye el mensaje de diagnóstico que se incluye en el header HTTP.
     *
     * <p>La evaluación sigue este orden deliberado:</p>
     * <ol>
     *   <li>Primero busca {@link java.sql.SQLException} en la <b>causa raíz</b>
     *       (usando {@link #getRootCause}), porque JPA frecuentemente envuelve
     *       SQLExceptions dentro de PersistenceException.</li>
     *   <li>Luego evalúa el tipo exacto de la excepción original {@code e}
     *       para los tipos JPA específicos.</li>
     *   <li>Si no coincide con ninguno, genera un mensaje genérico.</li>
     * </ol>
     *
     * <p>Todo mensaje pasa por {@link #sanitizeForHeader} antes de ser retornado.</p>
     *
     * @param e la excepción original recibida en {@link #toResponse}
     * @return mensaje de error sanitizado, listo para incluir en un header HTTP
     */
    private String buildHeaderError(Exception e) {
        Throwable cause = getRootCause(e);

        // Se evalúa la causa raíz primero para detectar SQLException,
        // ya que puede estar envuelta en varias capas de PersistenceException.
        if (cause instanceof java.sql.SQLException sqlEx) {
            return sanitizeForHeader("DB-Error: SQLState=" + sqlEx.getSQLState() + " - " + sqlEx.getMessage());
        }
        // A partir de aquí se evalúa 'e' (la excepción original),
        // que representa el tipo JPA específico lanzado por la capa de persistencia.
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
            // Captura cualquier otro PersistenceException no tipado anteriormente.
            return sanitizeForHeader("DB-Error: " + e.getMessage());
        }
        // Fallback para excepciones no relacionadas con persistencia.
        return sanitizeForHeader("Unexpected error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
    }

    /**
     * Recorre la cadena de causas encadenadas ({@link Throwable#getCause()})
     * hasta encontrar la excepción raíz (aquella que no tiene causa o cuya
     * causa se apunta a sí misma).
     *
     * <p>Necesario porque JPA envuelve las excepciones de la base de datos:
     * {@code PersistenceException → ... → SQLException}. Sin este metodo,
     * nunca se alcanzaría el {@code SQLException} real.</p>
     *
     * @param t la excepción desde la que comenzar el recorrido
     * @return la excepción más profunda de la cadena de causas
     */
    private Throwable getRootCause(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    /**
     * Limpia y trunca un mensaje para que sea seguro como valor de header HTTP.
     *
     * <p>Los headers HTTP no pueden contener caracteres de control (CR, LF, TAB)
     * ni caracteres fuera del rango ASCII imprimible. Un mensaje sin sanitizar
     * podría provocar un header malformado o una vulnerabilidad de inyección.</p>
     *
     * <p>Pasos aplicados:</p>
     * <ol>
     *   <li>Reemplaza {@code \r}, {@code \n}, {@code \t} con un espacio.</li>
     *   <li>Elimina cualquier carácter fuera del rango ASCII imprimible (0x20–0x7E).</li>
     *   <li>Trunca a 150 caracteres <b>después</b> de limpiar, para evitar
     *       que la eliminación de caracteres invalide el índice de corte.</li>
     * </ol>
     *
     * @param value el mensaje a sanitizar; puede ser {@code null}
     * @return cadena limpia y truncada, nunca {@code null}
     */
    private String sanitizeForHeader(String value) {
        if (value == null) return "Unknown error";

        //Primero limpiamos la cadena
        String sanitized = value.replaceAll("[\\r\\n\\t]", " ")
                .replaceAll("[^\\x20-\\x7E]", "");

        //Ahora aplicamos el límite usando el tamaño de la cadena YA limpia
        return sanitized.substring(0, Math.min(sanitized.length(), 150));
    }
}