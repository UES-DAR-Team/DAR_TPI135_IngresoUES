package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspirantePrueba;

import java.io.Serializable;

@Stateless
@LocalBean
public class AspirantePruebaDAO extends IngresoDefaultDataAcces<AspirantePrueba, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public AspirantePruebaDAO() {
        super(AspirantePrueba.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<AspirantePrueba> getEntityClass() {
        return AspirantePrueba.class;
    }
}
