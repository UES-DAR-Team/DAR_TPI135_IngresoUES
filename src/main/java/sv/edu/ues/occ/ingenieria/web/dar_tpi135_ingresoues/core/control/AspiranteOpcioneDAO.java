package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspiranteOpcione;

import java.io.Serializable;

@Stateless
@LocalBean
public class AspiranteOpcioneDAO extends IngresoDefaultDataAcces<AspiranteOpcione, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public AspiranteOpcioneDAO() {
        super(AspiranteOpcione.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<AspiranteOpcione> getEntityClass() {
        return AspiranteOpcione.class;
    }
}
