package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class AspiranteDAO extends IngresoDefaultDataAcces<Aspirante> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    EntityManager em;

    public AspiranteDAO() {
        super(Aspirante.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Aspirante> getEntityClass() {
        return Aspirante.class;
    }

    public List<Aspirante> findByNombre(String nombre, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<Aspirante> q = getEntityManager().createNamedQuery(
                    "Aspirante.buscarAspirantePorNombre", Aspirante.class);
            q.setParameter("nombre", "%" + nombre.trim().toUpperCase() + "%");
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aspirantes por nombre", ex);
        }
    }

    public List<Aspirante> findActivos(int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<Aspirante> q = getEntityManager().createNamedQuery(
                    "Aspirante.findActivos", Aspirante.class);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aspirantes activos", ex);
        }
    }

    public List<Aspirante> findByDocumento(String documento, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("Documento inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<Aspirante> q = getEntityManager().createNamedQuery(
                    "Aspirante.findByDocumento", Aspirante.class);
            q.setParameter("documento", documento.trim());
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aspirantes por documento", ex);
        }
    }

    public List<Aspirante> findByEstado(Boolean estado, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (estado == null) {
            throw new IllegalArgumentException("Estado inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<Aspirante> q = getEntityManager().createNamedQuery(
                    "Aspirante.findByEstado", Aspirante.class);
            q.setParameter("estado", estado);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aspirantes por estado", ex);
        }
    }

    /**
     * Cuenta la cantidad total de aspirantes cuyo nombre coincide
     * parcial o totalmente con el texto enviado.
     *
     * Este método se utiliza principalmente para soportar paginación
     * en búsquedas REST o interfaces gráficas.
     *
     * Por ejemplo:
     * Si findByNombre("juan", 0, 10) devuelve solo 10 registros,
     * este método permite saber cuántos resultados existen realmente
     * en la base de datos (ej: 25), para calcular:
     *
     * - Total de registros encontrados
     * - Total de páginas disponibles
     * - Si existen más resultados
     * - Navegación de paginación en frontend
     *
     * La búsqueda es parcial y no sensible a mayúsculas/minúsculas,
     * utilizando LIKE con UPPER().
     *
     * @param nombre texto a buscar dentro del nombre del aspirante
     * @return cantidad total de aspirantes encontrados
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     * @throws IllegalStateException si ocurre un error al consultar la base de datos
     */
    public Long countByNombre(String nombre)
            throws IllegalArgumentException, IllegalStateException {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre inválido");
        }
        try {
            TypedQuery<Long> q = getEntityManager().createNamedQuery(
                    "Aspirante.countByNombre", Long.class);
            q.setParameter("nombre", "%" + nombre.trim().toUpperCase() + "%");
            return q.getSingleResult();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar aspirantes por nombre", ex);
        }
    }
}