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


/**
 * Resource REST para la entidad {@link AreaConocimiento}.
 * Expone el endpoint base {@code /areaConocimiento}.
 *
 * <p><b>CAMBIO DE PATRÓN:</b> esta clase fue migrada al nuevo esquema centralizado
 * de manejo de errores. Los cambios principales respecto a la versión anterior son:</p>
 * <ul>
 *   <li><b>Extiende {@link AbstractResource}</b> en lugar de solo implementar
 *       {@code Serializable}. Esto provee los métodos {@code unprocessable()},
 *       {@code notFound()} y {@code conflict()} para construir respuestas de
 *       error de negocio de forma uniforme.</li>
 *   <li><b>Se eliminó el {@code Logger} local</b> y todos los bloques
 *       {@code try/catch} de infraestructura. El {@link GlobalExceptionMapper}
 *       captura automáticamente cualquier excepción no controlada (JPA, SQL, etc.)
 *       y retorna HTTP 500 con el header {@code Server-exception}.</li>
 *   <li><b>Se eliminaron los {@code Response.status(...).header(...).build()}
 *       inline</b> para los casos de error comunes, reemplazados por las llamadas
 *       a los métodos heredados de {@code AbstractResource}.</li>
 *   <li><b>Estructura de control simplificada</b>: se usa early return en lugar
 *       de lógica principal anidada dentro de bloques {@code if} positivos.</li>
 * </ul>
 */
@Path("areaConocimiento")
// CAMBIO: se agrega "extends AbstractResource" para heredar los métodos de error comunes.
// Antes: public class AreaConocimientoResource implements Serializable
public class AreaConocimientoResource extends AbstractResource implements Serializable {

    @Inject
    AreaConocimientoDAO areaConocimientoDAO;

    // CAMBIO: se eliminó el Logger local.
    // Antes existía: private static final Logger LOG = Logger.getLogger(...)
    // El GlobalExceptionMapper ya realiza LOG.log(Level.SEVERE, ...) para toda
    // excepción no controlada, por lo que mantenerlo aquí sería redundante.

    /**
     * Lista paginada de áreas de conocimiento.
     *
     * <p>Retorna HTTP 200 con la lista y el header {@code X-Total-Count}
     * indicando el total de registros en la tabla.</p>
     *
     * <p>Retorna HTTP 422 si los parámetros de paginación están fuera de rango
     * (aunque Bean Validation ya restringe los valores, la condición manual
     * actúa como segunda línea de defensa).</p>
     *
     * <p><b>CAMBIO:</b> se eliminó el bloque {@code try/catch} que antes envolvía
     * la llamada al DAO y retornaba HTTP 500 manualmente. Si el DAO lanza una
     * excepción, el {@link GlobalExceptionMapper} la captura y retorna 500.</p>
     *
     * <p><b>CAMBIO:</b> el {@code Response.status(422).header(...).build()} inline
     * fue reemplazado por {@code unprocessable("first,max")}.</p>
     *
     * @param first índice de inicio (mínimo 0, por defecto 0)
     * @param max   cantidad máxima de resultados (entre 1 y 10, por defecto 10)
     * @return 200 con lista + X-Total-Count, o 422 si los parámetros son inválidos
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @Min(0) @DefaultValue("0") @QueryParam("first") int first,
            @Max(10) @Min(1) @DefaultValue("10") @QueryParam("max") int max
    ) {
        if (first >= 0 && max > 0 && max <= 10) {
            // CAMBIO: sin try/catch. Si areaConocimientoDAO.findRange() o .count()
            // lanzan una excepción, el GlobalExceptionMapper retorna HTTP 500.
            List<AreaConocimiento> encontrados = areaConocimientoDAO.findRange(first, max);
            int total = areaConocimientoDAO.count();
            return Response.ok(encontrados)
                    .header("X-Total-Count", total)
                    .build();
        }
        // CAMBIO: antes era Response.status(422).header("Missing-parameter", "first,max").build()
        return unprocessable("first,max");
    }

    /**
     * Busca un área de conocimiento por su UUID.
     *
     * <p>Retorna HTTP 200 con la entidad si existe, 404 si no se encuentra,
     * o 422 si el ID es nulo (aunque JAX-RS normalmente no permite esto con
     * {@code @PathParam UUID}, se mantiene como defensa).</p>
     *
     * <p><b>CAMBIO:</b> se eliminó el bloque {@code try/catch} y el {@code Logger}.
     * Las excepciones de infraestructura las maneja {@link GlobalExceptionMapper}.</p>
     *
     * <p><b>CAMBIO:</b> la estructura pasó de {@code if (id != null) { ... }}
     * anidado a early return: {@code if (id == null) return unprocessable(...)}.</p>
     *
     * <p><b>CAMBIO:</b> los {@code Response.status(...).header(...).build()} inline
     * para 404 y 422 fueron reemplazados por {@code notFound()} y {@code unprocessable()}.</p>
     *
     * @param id UUID del área de conocimiento a buscar
     * @return 200 con la entidad, 404 si no existe, 422 si el id es nulo
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") UUID id) {
        // CAMBIO: early return en lugar de if (id != null) { ... lógica principal ... }
        if (id == null) return unprocessable("id");

        // CAMBIO: sin try/catch. Las excepciones del DAO suben al GlobalExceptionMapper.
        AreaConocimiento encontrado = areaConocimientoDAO.findById(id);
        if (encontrado != null) {
            return Response.ok(encontrado).build();
        }

        // CAMBIO: antes era Response.status(NOT_FOUND).header("Not-found-id", ...).build()
        return notFound(id.toString(), "AreaConocimiento");
    }

    /**
     * Elimina un área de conocimiento por su UUID.
     *
     * <p>Antes de eliminar verifica dos precondiciones:</p>
     * <ol>
     *   <li>Que la entidad exista (404 si no).</li>
     *   <li>Que no tenga áreas hijas dependientes (409 si tiene).</li>
     * </ol>
     *
     * <p>Retorna HTTP 204 No Content si la eliminación fue exitosa.</p>
     *
     * <p><b>CAMBIO:</b> se eliminaron el {@code try/catch}, el {@code Logger} y
     * los {@code Response} inline para 404, 409 y 422. Las excepciones de
     * infraestructura las maneja {@link GlobalExceptionMapper}.</p>
     *
     * <p><b>CAMBIO:</b> estructura simplificada con early return.</p>
     *
     * @param id UUID del área de conocimiento a eliminar
     * @return 204 si se eliminó, 404 si no existe, 409 si tiene hijos, 422 si id es nulo
     */
    @DELETE
    @Path("{id}")
    public Response deleteById(@PathParam("id") UUID id) {
        // CAMBIO: early return en lugar de if (id != null) { ... }
        if (id == null) return unprocessable("id");

        // CAMBIO: sin try/catch. Excepciones del DAO → GlobalExceptionMapper.
        AreaConocimiento encontrado = areaConocimientoDAO.findById(id);
        if (encontrado == null) {
            // CAMBIO: antes era Response.status(NOT_FOUND).header("Not-Found-id", ...).build()
            return notFound(id.toString(), "AreaConocimiento");
        }

        List<AreaConocimiento> hijos = areaConocimientoDAO.findHijosByPadre(id);
        if (!hijos.isEmpty()) {
            // CAMBIO: antes era Response.status(CONFLICT).header("Conflict-id", ...).build()
            return conflict(id.toString(), "has child records and cannot be deleted");
        }

        areaConocimientoDAO.delete(encontrado);
        return Response.noContent().build();
    }

    /**
     * Crea una nueva área de conocimiento.
     *
     * <p>Precondiciones que se validan antes de persistir:</p>
     * <ul>
     *   <li>La entidad no debe ser nula.</li>
     *   <li>El campo {@code id} de la entidad debe ser nulo (el UUID lo genera la BD).</li>
     *   <li>Si se especifica {@code idAutoReferenciaArea}, el área padre debe existir.</li>
     * </ul>
     *
     * <p>Retorna HTTP 201 Created con la entidad persistida y el header
     * {@code Location} apuntando al nuevo recurso.</p>
     *
     * <p><b>CAMBIO:</b> se eliminaron el {@code try/catch}, el {@code Logger} y los
     * {@code Response} inline para 404 y 422. Las excepciones de infraestructura
     * las maneja {@link GlobalExceptionMapper}.</p>
     *
     * <p><b>CAMBIO:</b> la estructura pasó de dos {@code if} anidados
     * ({@code if (entity != null) { if (entity.getId() == null) { ... } }})
     * a dos early returns consecutivos, reduciendo el nivel de anidamiento.</p>
     *
     * @param entity  datos de la nueva área de conocimiento (sin ID)
     * @param uriInfo contexto de la URI para construir el header Location
     * @return 201 con la entidad creada, 404 si el padre no existe, 422 si los datos son inválidos
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(AreaConocimiento entity, @Context UriInfo uriInfo) {
        // CAMBIO: early returns en lugar de if (entity != null) { if (entity.getId() == null) {...}}
        if (entity == null) return unprocessable("entity must not be null");
        if (entity.getId() != null) return unprocessable("entity.id must be null");

        if (entity.getIdAutoReferenciaArea() != null) {
            // CAMBIO: sin try/catch. El notFound() reemplaza el Response inline de 404.
            AreaConocimiento padre = areaConocimientoDAO.findById(entity.getIdAutoReferenciaArea().getId());
            if (padre == null) {
                return notFound(entity.getIdAutoReferenciaArea().getId().toString(), "idAutoReferenciaArea");
            }
        }

        // CAMBIO: sin try/catch. Si create() lanza excepción → GlobalExceptionMapper → HTTP 500.
        areaConocimientoDAO.create(entity);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(entity.getId().toString()).build())
                .entity(entity)
                .build();
    }

    /**
     * Actualiza un área de conocimiento existente.
     *
     * <p>Precondiciones que se validan antes de actualizar:</p>
     * <ul>
     *   <li>El ID de ruta no debe ser nulo.</li>
     *   <li>La entidad del cuerpo no debe ser nula.</li>
     *   <li>La entidad con ese ID debe existir en la BD.</li>
     *   <li>Si se especifica {@code idAutoReferenciaArea}, el área padre debe existir.</li>
     * </ul>
     *
     * <p>El {@code id} de la entidad se sobrescribe con el ID de ruta para garantizar
     * consistencia, independientemente del valor que venga en el cuerpo JSON.</p>
     *
     * <p>Retorna HTTP 200 con la entidad actualizada.</p>
     *
     * <p><b>CAMBIO:</b> se eliminaron el {@code try/catch}, el {@code Logger} y los
     * {@code Response} inline para 404 y 422. Las excepciones de infraestructura
     * las maneja {@link GlobalExceptionMapper}.</p>
     *
     * <p><b>CAMBIO:</b> la estructura pasó de dos {@code if} anidados con
     * {@code try/catch} interior a early returns consecutivos sin anidamiento.</p>
     *
     * @param id     UUID del área de conocimiento a actualizar (tomado de la ruta)
     * @param entity datos actualizados de la entidad
     * @return 200 con la entidad actualizada, 404 si no existe, 422 si los datos son inválidos
     */
    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") UUID id, AreaConocimiento entity) {
        // CAMBIO: early returns en lugar de if (id != null) { if (entity != null) { try { ... } } }
        if (id == null) return unprocessable("id");
        if (entity == null) return unprocessable("entity must not be null");

        // CAMBIO: sin try/catch. Excepciones del DAO → GlobalExceptionMapper.
        AreaConocimiento existing = areaConocimientoDAO.findById(id);
        if (existing == null) {
            // CAMBIO: antes era Response.status(NOT_FOUND).header(...).build()
            return notFound(id.toString(), "AreaConocimiento");
        }

        if (entity.getIdAutoReferenciaArea() != null) {
            AreaConocimiento padre = areaConocimientoDAO.findById(entity.getIdAutoReferenciaArea().getId());
            if (padre == null) {
                return notFound(entity.getIdAutoReferenciaArea().getId().toString(), "idAutoReferenciaArea");
            }
        }

        // Se fuerza el id desde la ruta para evitar inconsistencias con el cuerpo JSON.
        entity.setId(id);
        AreaConocimiento update = areaConocimientoDAO.update(entity);
        return Response.ok(update).build();
    }
}