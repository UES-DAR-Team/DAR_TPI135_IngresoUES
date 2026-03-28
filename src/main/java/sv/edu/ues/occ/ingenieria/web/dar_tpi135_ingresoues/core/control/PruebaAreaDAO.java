package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaArea;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class PruebaAreaDAO extends IngresoDefaultDataAcces<PruebaArea,Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public PruebaAreaDAO() {
        super(PruebaArea.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<PruebaArea> getEntityClass() {
        return PruebaArea.class;
    }

    public List<PruebaArea> findByPrueba(UUID idPrueba, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (idPrueba == null) {
            throw new IllegalArgumentException("idPrueba inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<PruebaArea> q = getEntityManager().createNamedQuery("PruebaArea.findByPrueba", PruebaArea.class);
            q.setParameter("idPrueba", idPrueba);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar áreas por prueba", ex);
        }
    }

    public List<PruebaArea> findByAreaConocimiento(UUID idAreaConocimiento, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (idAreaConocimiento == null) {
            throw new IllegalArgumentException("idAreaConocimiento inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<PruebaArea> q = getEntityManager().createNamedQuery("PruebaArea.findByAreaConocimiento", PruebaArea.class);
            q.setParameter("idAreaConocimiento", idAreaConocimiento);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar pruebas por área de conocimiento", ex);
        }
    }

    public List<PruebaArea> findByNumPreguntasMin(Short numPreguntas, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (numPreguntas == null) {
            throw new IllegalArgumentException("numPreguntas inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<PruebaArea> q = getEntityManager().createNamedQuery("PruebaArea.findByNumPreguntasMin", PruebaArea.class);
            q.setParameter("numPreguntas", numPreguntas);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar por número mínimo de preguntas", ex);
        }
    }
}
