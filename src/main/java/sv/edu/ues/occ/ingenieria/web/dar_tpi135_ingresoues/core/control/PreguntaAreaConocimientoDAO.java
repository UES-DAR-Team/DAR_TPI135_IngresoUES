package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaAreaConocimiento;

import java.io.Serializable;

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
}
