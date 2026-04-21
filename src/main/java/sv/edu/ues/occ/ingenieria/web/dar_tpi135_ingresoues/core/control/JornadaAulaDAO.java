package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class JornadaAulaDAO extends IngresoDefaultDataAcces<JornadaAula, Integer> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    EntityManager em;

    public JornadaAulaDAO() {
        super(JornadaAula.class);
    }

    @Override
    public EntityManager getEntityManager() {
        if (em == null) {
            throw new IllegalStateException("EntityManager no inicializado");
        }
        return em;
    }

    @Override
    protected Class<JornadaAula> getEntityClass() {
        return JornadaAula.class;
    }

    @Override
    public void create(JornadaAula entity) {
        em.persist(entity);
        em.flush();
    }

    public List<JornadaAula> findByJornada(UUID idJornada, int first, int max) {

        if (idJornada == null) {
            throw new IllegalArgumentException("Id de jornada inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<JornadaAula> q = getEntityManager()
                    .createNamedQuery("JornadaAula.buscarPorJornada", JornadaAula.class);

            q.setParameter("idJornada", idJornada);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar por jornada", ex);
        }
    }

    public List<JornadaAula> findByAula(UUID idAula, int first, int max) {

        if (idAula == null) {
            throw new IllegalArgumentException("Id de aula inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<JornadaAula> q = getEntityManager()
                    .createNamedQuery("JornadaAula.buscarPorAula", JornadaAula.class);

            q.setParameter("idAula", idAula);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar por aula", ex);
        }
    }

    public Long countByJornada(UUID idJornada) {

        if (idJornada == null) {
            throw new IllegalArgumentException("Id de jornada inválido");
        }

        try {
            TypedQuery<Long> q = getEntityManager()
                    .createNamedQuery("JornadaAula.countByJornada", Long.class);

            q.setParameter("idJornada", idJornada);

            return q.getSingleResult();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar por jornada", ex);
        }
    }

    public Long countByAula(UUID idAula) {

        if (idAula == null) {
            throw new IllegalArgumentException("Id de aula inválido");
        }

        try {
            TypedQuery<Long> q = getEntityManager()
                    .createNamedQuery("JornadaAula.countByAula", Long.class);

            q.setParameter("idAula", idAula);

            return q.getSingleResult();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar por aula", ex);
        }
    }
}