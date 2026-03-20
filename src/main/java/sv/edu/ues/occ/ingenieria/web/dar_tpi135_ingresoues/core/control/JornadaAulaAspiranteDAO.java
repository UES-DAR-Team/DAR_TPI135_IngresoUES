package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspirante;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class JornadaAulaAspiranteDAO extends IngresoDefaultDataAcces<JornadaAulaAspirante,Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public JornadaAulaAspiranteDAO() {
        super(JornadaAulaAspirante.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<JornadaAulaAspirante> getEntityClass() {
        return JornadaAulaAspirante.class;
    }

    public List<JornadaAulaAspirante> buscarPorJornadaAula(Integer idJornadaAula, int first, int max) {
        try {
            if (idJornadaAula != null && first >= 0 && max > 0) {

                TypedQuery<JornadaAulaAspirante> q = em.createNamedQuery(
                        "JornadaAulaAspirante.buscarPorJornadaAula",
                        JornadaAulaAspirante.class
                );

                q.setParameter("idJornadaAula", idJornadaAula);
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar por jornada aula", ex);
        }

        return List.of();
    }

    public List<JornadaAulaAspirante> buscarPorAspirantePrueba(Integer idAspirantePrueba, int first, int max) {
        try {
            if (idAspirantePrueba != null && first >= 0 && max > 0) {

                TypedQuery<JornadaAulaAspirante> q = em.createNamedQuery(
                        "JornadaAulaAspirante.buscarPorAspirantePrueba",
                        JornadaAulaAspirante.class
                );

                q.setParameter("idAspirantePrueba", idAspirantePrueba);
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar por aspirante prueba", ex);
        }

        return List.of();
    }

    public List<JornadaAulaAspirante> buscarPorAsistencia(Boolean asistio, int first, int max) {
        try {
            if (asistio != null && first >= 0 && max > 0) {

                TypedQuery<JornadaAulaAspirante> q = em.createNamedQuery(
                        "JornadaAulaAspirante.buscarPorAsistencia",
                        JornadaAulaAspirante.class
                );

                q.setParameter("asistio", asistio);
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar por asistencia", ex);
        }

        return List.of();
    }
}
