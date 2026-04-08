package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;


import java.io.Serializable;
import java.util.List;


@Stateless
@LocalBean
public class PreguntaDAO extends IngresoDefaultDataAcces<Pregunta, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public PreguntaDAO() {
        super(Pregunta.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Pregunta> getEntityClass() {
        return Pregunta.class;
    }

    public List<Pregunta> findByCoincidenciaTexto(final String text, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if(text == null || text.isBlank()){
            throw new IllegalArgumentException("Parametro invalido: texto");
        }
        if(first < 0 || max <= 0){
            throw new IllegalArgumentException("Parametros invalidos: first, max");
        }
        try {
                TypedQuery<Pregunta> q = em.createNamedQuery("Pregunta.findByCoincidenciaTexto", Pregunta.class);
                q.setParameter("text", "%" + text.trim().toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
        }catch (RuntimeException e){
            throw new IllegalStateException("Error de sistema en la ejecucion de query", e);
        }
    }


}

