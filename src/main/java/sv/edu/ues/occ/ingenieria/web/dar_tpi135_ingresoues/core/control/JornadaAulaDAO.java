package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class JornadaAulaDAO extends IngresoDefaultDataAcces<JornadaAula, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public JornadaAulaDAO() {
        super(JornadaAula.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<JornadaAula> getEntityClass() {
        return JornadaAula.class;
    }

    public List<JornadaAula> buscarPorJornada(Integer idJornada, int first, int max) {
        try {
            if (idJornada != null && first >= 0 && max > 0) {

                TypedQuery<JornadaAula> q = em.createNamedQuery(
                        "JornadaAula.buscarPorJornada",
                        JornadaAula.class
                );

                q.setParameter("idJornada", idJornada);
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aulas por jornada", ex);
        }

        return List.of();
    }

    public List<JornadaAula> buscarPorAula(Integer idAula, int first, int max) {
        try {
            if (idAula != null && first >= 0 && max > 0) {

                TypedQuery<JornadaAula> q = em.createNamedQuery(
                        "JornadaAula.buscarPorAula",
                        JornadaAula.class
                );

                q.setParameter("idAula", idAula);
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar jornadas por aula", ex);
        }

        return List.of();
    }
}
