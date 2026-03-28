package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaAreaConocimiento;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class PreguntaAreaConocimientoDAO extends IngresoDefaultDataAcces<PreguntaAreaConocimiento, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public PreguntaAreaConocimientoDAO() {
        super(PreguntaAreaConocimiento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<PreguntaAreaConocimiento> getEntityClass() {
        return PreguntaAreaConocimiento.class;
    }

    public List<PreguntaAreaConocimiento> findPreguntaByIdAreaConocimiento(final UUID idAreaConocimiento, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        try {
            if (idAreaConocimiento != null && first >= 0 && max >= 0) {
                TypedQuery<PreguntaAreaConocimiento> q = em.createNamedQuery("PreguntaAreaConocimiento.findByIdAreaConocimiento", PreguntaAreaConocimiento.class)
                        .setParameter("idAreaConocimiento", idAreaConocimiento)
                        .setFirstResult(first)
                        .setMaxResults(max);
                return q.getResultList();
            }
        } catch (Exception ex) {
            Logger.getLogger(PreguntaAreaConocimientoDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

    public List<PreguntaAreaConocimiento> findByIdPregunta(final UUID idPregunta, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        try {
            if (idPregunta != null && first >= 0 && max >= 0) {
                TypedQuery<PreguntaAreaConocimiento> q = em.createNamedQuery("PreguntaAreaConocimiento.findByIdPregunta", PreguntaAreaConocimiento.class)
                        .setParameter("idPregunta", idPregunta)
                        .setFirstResult(first)
                        .setMaxResults(max);
                return q.getResultList();
            }
        } catch (Exception ex) {
            Logger.getLogger(PreguntaAreaConocimientoDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }


}
