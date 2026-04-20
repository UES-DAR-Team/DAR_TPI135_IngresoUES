package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class AreaConocimientoDAO extends IngresoDefaultDataAcces<AreaConocimiento, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    EntityManager em;

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
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("parametro invalido: name");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parametros invalidos: first, max");
        }
        try {
            TypedQuery<AreaConocimiento> q = em.createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class);
            q.setParameter("name", "%" + name.trim().toUpperCase() + "%");
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();

        } catch (RuntimeException e) {
            throw new IllegalStateException("Error de sistema en la ejecucion de query", e);
        }
    }

    public List<AreaConocimiento> findByAreaPadre()
            throws IllegalStateException {
        try {
            return em.createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class).getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Error de sistema en la ejecucion de query", e);
        }
    }

    //falto paginar
    public List<AreaConocimiento> findHijosByPadre(final UUID idPadre)
            throws IllegalArgumentException, IllegalStateException {
        if(idPadre == null ){
            throw new IllegalArgumentException("Parametro invalido: idPadre");
        }
        try {
                return em.createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class)
                        .setParameter("idPadre", idPadre)
                        .getResultList();
        } catch (RuntimeException e) {
         throw new IllegalStateException("Error de sistema en la ejecucion de query", e);
        }
    }

}
