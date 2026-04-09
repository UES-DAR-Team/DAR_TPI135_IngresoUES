package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspiranteResultado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class JornadaAulaAspiranteResultadoDAO extends IngresoDefaultDataAcces<JornadaAulaAspiranteResultado, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public JornadaAulaAspiranteResultadoDAO() {
        super(JornadaAulaAspiranteResultado.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<JornadaAulaAspiranteResultado> getEntityClass() {
        return JornadaAulaAspiranteResultado.class;
    }

    public List<JornadaAulaAspiranteResultado> findByJornadaAulaAspirante(Integer idJornadaAulaAspirante, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (idJornadaAulaAspirante == null) {
            throw new IllegalArgumentException("Id inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<JornadaAulaAspiranteResultado> q = getEntityManager().createNamedQuery(
                    "JornadaAulaAspiranteResultado.buscarPorJornadaAulaAspirante",
                    JornadaAulaAspiranteResultado.class
            );

            q.setParameter("idJornadaAulaAspirante", idJornadaAulaAspirante);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar resultados por jornada aula aspirante", ex);
        }
    }

    public List<JornadaAulaAspiranteResultado> findByAprobado(Boolean aprobado, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (aprobado == null) {
            throw new IllegalArgumentException("Estado inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<JornadaAulaAspiranteResultado> q = getEntityManager().createNamedQuery(
                    "JornadaAulaAspiranteResultado.buscarPorAprobado",
                    JornadaAulaAspiranteResultado.class
            );

            q.setParameter("aprobado", aprobado);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar resultados por estado de aprobación", ex);
        }
    }

    public List<JornadaAulaAspiranteResultado> findByRangoPuntaje(BigDecimal min, BigDecimal max, int first, int maxResults)
            throws IllegalArgumentException, IllegalStateException {

        if (min == null || max == null) {
            throw new IllegalArgumentException("Rango inválido");
        }

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("El valor mínimo no puede ser mayor al máximo");
        }

        if (first < 0 || maxResults <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<JornadaAulaAspiranteResultado> q = getEntityManager().createNamedQuery(
                    "JornadaAulaAspiranteResultado.buscarPorRangoPuntaje",
                    JornadaAulaAspiranteResultado.class
            );

            q.setParameter("min", min);
            q.setParameter("max", max);
            q.setFirstResult(first);
            q.setMaxResults(maxResults);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar resultados por rango de puntaje", ex);
        }
    }

    public Long countByAprobado(Boolean aprobado)
            throws IllegalArgumentException, IllegalStateException {

        if (aprobado == null) {
            throw new IllegalArgumentException("Estado inválido");
        }

        try {
            TypedQuery<Long> q = getEntityManager().createNamedQuery(
                    "JornadaAulaAspiranteResultado.countByAprobado",
                    Long.class
            );

            q.setParameter("aprobado", aprobado);

            return q.getSingleResult();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar resultados por estado", ex);
        }
    }
}