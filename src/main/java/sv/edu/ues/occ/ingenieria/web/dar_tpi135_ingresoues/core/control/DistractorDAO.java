package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class DistractorDAO extends IngresoDefaultDataAcces<Distractor, Object> implements Serializable {
    @PersistenceContext(unitName="IngresoPU")
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

//No va a llevar mucho mas


}
