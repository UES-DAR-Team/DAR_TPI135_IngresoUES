package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;

import java.io.Serializable;

@Stateless
@LocalBean
public class AreaConocimientoDAO extends IngresoDefaultDataAcces<AreaConocimiento, Object> implements Serializable {
    @PersistenceContext(unitName="IngresoPU")
    private EntityManager em;

    public AreaConocimientoDAO() {
        super(AreaConocimiento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return null;
    }

    @Override
    protected Class<AreaConocimiento> getEntityClass() {
        return null;
    }


}
