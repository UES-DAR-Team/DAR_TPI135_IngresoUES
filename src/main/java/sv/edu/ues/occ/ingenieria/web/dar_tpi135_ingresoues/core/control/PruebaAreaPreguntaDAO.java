package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class PruebaAreaPreguntaDAO extends IngresoDefaultDataAcces<PruebaAreaPregunta, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public PruebaAreaPreguntaDAO() {
        super(PruebaAreaPregunta.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<PruebaAreaPregunta> getEntityClass() {
        return PruebaAreaPregunta.class;
    }

    public List<PruebaAreaPregunta> findByPruebaArea(Integer idPruebaArea, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
    if (idPruebaArea == null) {
        throw new IllegalArgumentException("idPruebaArea inválido");
    }
    if (first < 0 || max <= 0) {
        throw new IllegalArgumentException("Parámetros de paginación inválidos");
    }
    try {
        TypedQuery<PruebaAreaPregunta> q = getEntityManager().createNamedQuery(
                "PruebaAreaPregunta.findByPruebaArea", PruebaAreaPregunta.class);
        q.setParameter("idPruebaArea", idPruebaArea);
        q.setFirstResult(first);
        q.setMaxResults(max);
        return q.getResultList();
    } catch (Exception ex) {
        throw new IllegalStateException("Error al buscar preguntas por área de prueba", ex);
    }
}

    public List<PruebaAreaPregunta> findByPregunta(Integer idPregunta, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (idPregunta == null) {
            throw new IllegalArgumentException("idPregunta inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<PruebaAreaPregunta> q = getEntityManager().createNamedQuery(
                    "PruebaAreaPregunta.findByPregunta", PruebaAreaPregunta.class);
            q.setParameter("idPregunta", idPregunta);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar áreas de prueba por pregunta", ex);
        }
    }
}
