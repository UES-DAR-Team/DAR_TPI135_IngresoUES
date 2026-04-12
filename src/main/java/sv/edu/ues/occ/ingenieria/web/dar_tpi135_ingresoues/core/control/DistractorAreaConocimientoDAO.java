package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.DistractorAreaConocimiento;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class DistractorAreaConocimientoDAO extends IngresoDefaultDataAcces<DistractorAreaConocimiento, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    EntityManager em;

    public DistractorAreaConocimientoDAO() {
        super(DistractorAreaConocimiento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<DistractorAreaConocimiento> getEntityClass() {
        return DistractorAreaConocimiento.class;
    }

    public List<DistractorAreaConocimiento> findByIdDistractor(final UUID idDistractor, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if(idDistractor == null){
            throw new IllegalArgumentException("Parametro invalido: idDistractor");
        }
        if (first < 0 || max<=0){
            throw new IllegalArgumentException("Parametros invalidos: first, max");
        }
            try {
                return em.createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class)
                        .setParameter("idDistractor", idDistractor)
                        .setFirstResult(first)
                        .setMaxResults(max)
                        .getResultList();
            } catch (RuntimeException ex) {
                throw new IllegalStateException("Error de sistema en la ejecucion de query",ex);
            }
    }

   public List<DistractorAreaConocimiento> findByIdAreaConocimiento(final UUID idAreaConocimiento, int first, int max)
           throws IllegalArgumentException, IllegalStateException {
       if(idAreaConocimiento == null){
           throw new IllegalArgumentException("Parametro invalido: idAreaConocimiento");
       }
       if (first < 0 || max<=0){
           throw new IllegalArgumentException("Parametros invalidos: first, max");
       }
            try {
                return em.createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class)
                        .setParameter("idAreaConocimiento", idAreaConocimiento)
                        .setFirstResult(first)
                        .setMaxResults(max)
                        .getResultList();
            } catch (RuntimeException ex) {
                throw new IllegalStateException("Error de sistema en la ejecucion de query",ex);
            }
    }

}
