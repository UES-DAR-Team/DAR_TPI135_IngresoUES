package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;

/**
 * Clase base para todos los Resources REST del proyecto.
 *
 * <p>Centraliza la construcción de respuestas de error de negocio comunes,
 * garantizando que los headers y códigos de estado sean consistentes en
 * toda la API sin duplicar código en cada Resource.</p>
 *
 * <p><b>Cómo usarla:</b> todo Resource debe extender esta clase.</p>
 * <pre>
 *   {@literal @}Path("miRecurso")
 *   public class MiResource extends AbstractResource implements Serializable {
 *
 *       {@literal @}GET {@literal @}Path("{id}")
 *       public Response findById({@literal @}PathParam("id") UUID id) {
 *           if (id == null) return unprocessable("id");         // 422
 *           MiEntidad e = dao.findById(id);
 *           if (e == null)  return notFound(id.toString(), "MiEntidad"); // 404
 *           return Response.ok(e).build();
 *       }
 *   }
 * </pre>
 *
 * <p><b>División de responsabilidades con {@link GlobalExceptionMapper}:</b></p>
 * <ul>
 *   <li>{@code AbstractResource} maneja errores de <b>negocio</b> (input inválido,
 *       entidad no encontrada, conflicto de integridad) → 422, 404, 409.</li>
 *   <li>{@code GlobalExceptionMapper} maneja errores de <b>infraestructura</b>
 *       (excepciones JPA/SQL no controladas) → 500.</li>
 * </ul>
 */
public abstract class AbstractResource {

    /**
     * Respuesta estándar para parámetros faltantes o inválidos en la petición.
     *
     * <p>Usar cuando la entrada del cliente no cumple las precondiciones del
     * método: parámetro nulo, campo que debería ser nulo pero no lo es, etc.</p>
     *
     * <p>Retorna HTTP <b>422 Unprocessable Entity</b> con el header
     * {@code Missing-parameter} indicando qué campo causó el problema.</p>
     *
     * <p>Ejemplos de uso:</p>
     * <pre>
     *   if (id == null)     return unprocessable("id");
     *   if (entity == null) return unprocessable("entity must not be null");
     *   if (entity.getId() != null) return unprocessable("entity.id must be null");
     * </pre>
     *
     * @param missingParam nombre del parámetro o descripción del problema
     * @return Response HTTP 422 con header {@code Missing-parameter}
     */
    protected Response unprocessable(String missingParam) {
        return Response.status(422)
                .header("Missing-parameter", missingParam)
                .build();
    }

    /**
     * Respuesta estándar cuando una entidad buscada por ID no existe en la base de datos.
     *
     * <p>Usar cuando {@code dao.findById(...)} retorna {@code null}.</p>
     *
     * <p>Retorna HTTP <b>404 Not Found</b> con el header {@code Not-found-id}
     * que incluye el nombre de la entidad y el ID buscado para facilitar el diagnóstico.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     *   AreaConocimiento encontrado = dao.findById(id);
     *   if (encontrado == null) return notFound(id.toString(), "AreaConocimiento");
     * </pre>
     *
     * @param id         representación en texto del ID buscado
     * @param entityName nombre de la entidad (para el mensaje del header)
     * @return Response HTTP 404 con header {@code Not-found-id}
     */
    protected Response notFound(String id, String entityName) {
        return Response.status(Response.Status.NOT_FOUND)
                .header("Not-found-id", entityName + " with id " + id + " not found")
                .build();
    }

    /**
     * Respuesta estándar cuando una operación viola una regla de integridad de negocio.
     *
     * <p>Usar cuando la operación es sintácticamente correcta y la entidad existe,
     * pero el estado actual del sistema impide ejecutarla. El caso más común es
     * intentar eliminar un registro padre que todavía tiene hijos asociados.</p>
     *
     * <p>Retorna HTTP <b>409 Conflict</b> con el header {@code Conflict-id}
     * que incluye el ID involucrado y la razón del conflicto.</p>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>
     *   List&lt;AreaConocimiento&gt; hijos = dao.findHijosByPadre(id);
     *   if (!hijos.isEmpty())
     *       return conflict(id.toString(), "has child records and cannot be deleted");
     * </pre>
     *
     * @param id     representación en texto del ID involucrado en el conflicto
     * @param reason descripción corta de la razón del conflicto
     * @return Response HTTP 409 con header {@code Conflict-id}
     */
    protected Response conflict(String id, String reason) {
        return Response.status(Response.Status.CONFLICT)
                .header("Conflict-id", "Record with id " + id + " " + reason)
                .build();
    }
}