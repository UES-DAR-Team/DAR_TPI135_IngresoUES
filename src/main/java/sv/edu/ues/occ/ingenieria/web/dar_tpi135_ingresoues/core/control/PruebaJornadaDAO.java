package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaJornada;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class PruebaJornadaDAO extends IngresoDefaultDataAcces<PruebaJornada,Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

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

    //buscar que pruebas tiene una jornada
    public List<PruebaJornada> findByJornada(Integer idJornada, int first, int max) {
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

    //buscar en que jorandas aparece una prueba especifica
    public List<PruebaJornada> findByPrueba(Integer idPrueba, int first, int max) {
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

    //para no repetir prueba en la misma jornada
    public List<PruebaJornada> findByPruebaAndJornada(Integer idPrueba, Integer idJornada, int first, int max) {
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