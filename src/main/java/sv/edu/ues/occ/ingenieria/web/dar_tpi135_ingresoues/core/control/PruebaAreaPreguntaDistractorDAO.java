package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPreguntaDistractor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

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

    public List<PruebaAreaPreguntaDistractor> findByPruebaAreaPregunta(final Integer idPruebaAreaPregunta, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (idPruebaAreaPregunta == null) {
            throw new IllegalArgumentException("idPruebaAreaPregunta inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<PruebaAreaPreguntaDistractor> q = getEntityManager().createNamedQuery(
                    "PruebaAreaPreguntaDistractor.findByPruebaAreaPregunta", PruebaAreaPreguntaDistractor.class);
            q.setParameter("idPruebaAreaPregunta", idPruebaAreaPregunta);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
        throw new IllegalStateException("Error al buscar distractores por pregunta", ex);
    }
    }

    /**
     * Obtiene la respuesta correcta de una pregunta específica. Si la pregunta no tiene respuesta correcta asignada, retorna null.
     *
     * @param idPruebaAreaPregunta ID de la pregunta a consultar. No debe ser null.
     * @return La respuesta correcta de la pregunta, o null si no tiene ninguna asignada.
     * @throws IllegalArgumentException si idPruebaAreaPregunta es null.
     * @throws IllegalStateException si ocurre un error al consultar la base de datos.
     */
    public PruebaAreaPreguntaDistractor findRespuestaCorrecta(final Integer idPruebaAreaPregunta)
            throws IllegalArgumentException, IllegalStateException {
        if (idPruebaAreaPregunta == null) {
            throw new IllegalArgumentException("idPruebaAreaPregunta inválido");
        }
        try {
            TypedQuery<PruebaAreaPreguntaDistractor> q = getEntityManager().createNamedQuery(
                    "PruebaAreaPreguntaDistractor.findRespuestaCorrecta", PruebaAreaPreguntaDistractor.class);
            q.setParameter("idPruebaAreaPregunta", idPruebaAreaPregunta);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar respuesta correcta", ex);
        }
    }

    public List<PruebaAreaPreguntaDistractor> findByDistractor(final UUID idDistractor, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (idDistractor == null) {
            throw new IllegalArgumentException("idDistractor inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<PruebaAreaPreguntaDistractor> q = getEntityManager().createNamedQuery(
                    "PruebaAreaPreguntaDistractor.findByDistractor", PruebaAreaPreguntaDistractor.class);
            q.setParameter("idDistractor", idDistractor);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
        throw new IllegalStateException("Error al buscar preguntas por distractor", ex);
    }
    }
}
