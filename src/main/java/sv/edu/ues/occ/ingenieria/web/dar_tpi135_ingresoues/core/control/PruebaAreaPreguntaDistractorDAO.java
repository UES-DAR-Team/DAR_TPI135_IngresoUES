package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPreguntaDistractor;

import java.io.Serializable;

@Stateless
@LocalBean
public class PruebaAreaPreguntaDistractorDAO extends IngresoDefaultDataAcces<PruebaAreaPreguntaDistractor,Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public PruebaAreaPreguntaDistractorDAO() {
        super(PruebaAreaPreguntaDistractor.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<PruebaAreaPreguntaDistractor> getEntityClass() {
        return PruebaAreaPreguntaDistractor.class;
    }
}
