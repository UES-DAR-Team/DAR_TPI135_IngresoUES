package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaJornada;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class PruebaJornadaDAO extends IngresoDefaultDataAcces<PruebaJornada,Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    EntityManager em;

    public PruebaJornadaDAO() {
        super(PruebaJornada.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<PruebaJornada> getEntityClass() {
        return PruebaJornada.class;
    }

    public List<PruebaJornada> findByJornada(final UUID idJornada, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (idJornada == null) {
            throw new IllegalArgumentException("idJornada inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<PruebaJornada> q = getEntityManager().createNamedQuery(
                    "PruebaJornada.findByJornada", PruebaJornada.class);
            q.setParameter("idJornada", idJornada);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
        throw new IllegalStateException("Error al buscar pruebas por jornada", ex);
    }
    }

    public List<PruebaJornada> findByPrueba(final UUID idPrueba, int first, int max)
            throws IllegalArgumentException, IllegalStateException{
        if (idPrueba == null) {
            throw new IllegalArgumentException("idPrueba inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<PruebaJornada> q = getEntityManager().createNamedQuery(
                    "PruebaJornada.findByPrueba", PruebaJornada.class);
            q.setParameter("idPrueba", idPrueba);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar jornadas por prueba", ex);
        }
    }

    /**
     * Verifica si una prueba ya esta registrada en una jornada especifica,
     * evitando que se repita la misma prueba en la misma jornada.
     * Si la lista retornada no esta vacia, significa que la asignación ya existe y no debe registrarse nuevamente.
     *
     * @param idPrueba ID de la prueba a verificar. No debe ser null.
     * @param idJornada ID de la jornada a verificar. No debe ser null.
     * @param first posicion inicial de la paginacion. Debe ser mayor o igual a 0.
     * @param max cantidad maxima de resultados a retornar. Debe ser mayor a 0.
     * @return lista de registros que coinciden con la prueba y la jornada indicadas.
     *         Si la lista no está vacía, la asignación ya existe.
     * @throws IllegalArgumentException si idPrueba o idJornada son null, o si los valores de first o max son invalidos.
     * @throws IllegalStateException si ocurre un error al consultar la base de datos.
     */

    public List<PruebaJornada> findByPruebaAndJornada(final UUID idPrueba, UUID idJornada, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (idPrueba == null) {
            throw new IllegalArgumentException("idPrueba inválido");
        }
        if (idJornada == null) {
            throw new IllegalArgumentException("idJornada inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<PruebaJornada> q = getEntityManager().createNamedQuery(
                    "PruebaJornada.findByPruebaAndJornada", PruebaJornada.class);
            q.setParameter("idPrueba", idPrueba);
            q.setParameter("idJornada", idJornada);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar asignación por prueba y jornada", ex);
        }
    }
}