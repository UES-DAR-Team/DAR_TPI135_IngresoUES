package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaDistractor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class PreguntaDistractorDAO extends IngresoDefaultDataAcces<PreguntaDistractor, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public PreguntaDistractorDAO() {
        super(PreguntaDistractor.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<PreguntaDistractor> getEntityClass() {
        return PreguntaDistractor.class;
    }

    public List<PreguntaDistractor> findByIdPregunta(final UUID idPregunta, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        try {
            if (idPregunta != null && first >= 0 && max >= 0) {
                TypedQuery<PreguntaDistractor> q = em.createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class)
                        .setParameter("idPregunta", idPregunta)
                        .setFirstResult(first)
                        .setMaxResults(max);
                return q.getResultList();
            }
        } catch (Exception ex) {
            Logger.getLogger(PreguntaDistractorDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

    public List<PreguntaDistractor> findByIdDistractor(final UUID idDistractor, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        try {
            if (idDistractor != null && first >= 0 && max >= 0) {
                TypedQuery<PreguntaDistractor> q = em.createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class)
                        .setParameter("idDistractor", idDistractor)
                        .setFirstResult(first)
                        .setMaxResults(max);
                return q.getResultList();
            }
        } catch (Exception ex) {
            Logger.getLogger(PreguntaDistractorDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }
}
