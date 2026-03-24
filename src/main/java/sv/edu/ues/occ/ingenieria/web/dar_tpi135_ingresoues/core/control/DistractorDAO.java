package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class DistractorDAO extends IngresoDefaultDataAcces<Distractor, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public DistractorDAO() {
        super(Distractor.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Distractor> getEntityClass() {
        return Distractor.class;
    }

    public List<Distractor> findByCoincidenciaTexto(final String text, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        try {
            if (text != null && !text.isBlank() && first >= 0 && max > 0) {
                TypedQuery<Distractor> q = em.createNamedQuery("Distractor.findByCoincidenciaTexto", Distractor.class);
                q.setParameter("text", "%" + text.trim().toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }
        } catch (Exception ex) {
            Logger.getLogger(DistractorDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

}
