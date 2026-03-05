package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.DistractorAreaConocimiento;

import java.io.Serializable;

@Stateless
@LocalBean
public class DistractorAreaConocimientoDAO extends IngresoDefaultDataAcces<DistractorAreaConocimiento, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

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
}
