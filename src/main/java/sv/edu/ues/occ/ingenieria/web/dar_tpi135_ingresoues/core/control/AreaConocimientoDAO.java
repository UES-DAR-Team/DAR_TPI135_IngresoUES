package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class AreaConocimientoDAO extends IngresoDefaultDataAcces<AreaConocimiento, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public AreaConocimientoDAO() {
        super(AreaConocimiento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<AreaConocimiento> getEntityClass() {
        return AreaConocimiento.class;
    }

    public List<AreaConocimiento> findByNameLike(final String name, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        try {
            if (name != null && !name.isBlank() && first >= 0 && max >= 0) {
                TypedQuery<AreaConocimiento> q = em.createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class);
                q.setParameter("name", "%" + name.trim().toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }
        } catch (Exception e) {
            Logger.getLogger(AreaConocimientoDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return List.of();
    }

    public List<AreaConocimiento> findByAreaPadre()
            throws IllegalArgumentException, IllegalStateException {
        try {
            return em.createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class).getResultList();
        } catch (Exception e) {
            Logger.getLogger(AreaConocimientoDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return List.of();
        }
    }

    //verificar que esta funcion no
    public List<AreaConocimiento> findHijosByPadre(Integer idPadre)
            throws IllegalArgumentException, IllegalStateException {
        try {
            if (idPadre != null && idPadre > 0) {
                return em.createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class)
                        .setParameter("idPadre", idPadre)
                        .getResultList();
            }
        } catch (Exception e) {
            Logger.getLogger(AreaConocimientoDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
      return List.of();
    }

}
