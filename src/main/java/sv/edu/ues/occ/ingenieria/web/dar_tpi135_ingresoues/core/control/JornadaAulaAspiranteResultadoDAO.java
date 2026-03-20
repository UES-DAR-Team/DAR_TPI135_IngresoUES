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

@Stateless
@LocalBean
public class JornadaAulaAspiranteResultadoDAO extends IngresoDefaultDataAcces<JornadaAulaAspiranteResultado,Object> implements Serializable {

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

    public List<JornadaAulaAspiranteResultado> buscarPorJornadaAulaAspirante(Integer idJornadaAulaAspirante, int first, int max) {
        try {
            if (idJornadaAulaAspirante != null && first >= 0 && max > 0) {

                TypedQuery<JornadaAulaAspiranteResultado> q = em.createNamedQuery(
                        "JornadaAulaAspiranteResultado.buscarPorJornadaAulaAspirante",
                        JornadaAulaAspiranteResultado.class
                );

                q.setParameter("idJornadaAulaAspirante", idJornadaAulaAspirante);
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar resultados por jornada aula aspirante", ex);
        }

        return List.of();
    }

    public List<JornadaAulaAspiranteResultado> buscarPorAprobado(Boolean aprobado, int first, int max) {
        try {
            if (aprobado != null && first >= 0 && max > 0) {

                TypedQuery<JornadaAulaAspiranteResultado> q = em.createNamedQuery(
                        "JornadaAulaAspiranteResultado.buscarPorAprobado",
                        JornadaAulaAspiranteResultado.class
                );

                q.setParameter("aprobado", aprobado);
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar resultados por estado de aprobación", ex);
        }

        return List.of();
    }

    public List<JornadaAulaAspiranteResultado> buscarPorRangoPuntaje(BigDecimal min, BigDecimal max) {

        TypedQuery<JornadaAulaAspiranteResultado> q = em.createNamedQuery(
                "JornadaAulaAspiranteResultado.buscarPorRangoPuntaje",
                JornadaAulaAspiranteResultado.class
        );

        q.setParameter("min", min);
        q.setParameter("max", max);

        return q.getResultList();
    }
}
