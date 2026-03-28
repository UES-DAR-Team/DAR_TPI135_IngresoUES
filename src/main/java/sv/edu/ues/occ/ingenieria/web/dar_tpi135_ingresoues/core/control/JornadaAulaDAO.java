package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class JornadaAulaDAO extends IngresoDefaultDataAcces<JornadaAula, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public JornadaAulaDAO() {
        super(JornadaAula.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<JornadaAula> getEntityClass() {
        return JornadaAula.class;
    }

    public List<JornadaAula> findByJornada(UUID idJornada, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (idJornada == null) {
            throw new IllegalArgumentException("Id de jornada inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<JornadaAula> q = getEntityManager().createNamedQuery(
                    "JornadaAula.buscarPorJornada",
                    JornadaAula.class
            );

            q.setParameter("idJornada", idJornada);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aulas por jornada", ex);
        }
    }

    public List<JornadaAula> findByAula(UUID idAula, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (idAula == null) {
            throw new IllegalArgumentException("Id de aula inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<JornadaAula> q = getEntityManager().createNamedQuery(
                    "JornadaAula.buscarPorAula",
                    JornadaAula.class
            );

            q.setParameter("idAula", idAula);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar jornadas por aula", ex);
        }
    }

    public Long countByJornada(UUID idJornada)
            throws IllegalArgumentException, IllegalStateException {

        if (idJornada == null) {
            throw new IllegalArgumentException("Id de jornada inválido");
        }

        try {
            TypedQuery<Long> q = getEntityManager().createQuery(
                    "SELECT COUNT(ja) FROM JornadaAula ja WHERE ja.idJornada.id = :idJornada",
                    Long.class
            );

            q.setParameter("idJornada", idJornada);

            return q.getSingleResult();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar aulas por jornada", ex);
        }
    }

    public Long countByAula(UUID idAula)
            throws IllegalArgumentException, IllegalStateException {

        if (idAula == null) {
            throw new IllegalArgumentException("Id de aula inválido");
        }

        try {
            // la query debe ir en la capa de entidades por eso se le asigna al em para constultar la query en las entidades,
            // entidades con createNamedQuery
            TypedQuery<Long> q = getEntityManager().createQuery(
                    "SELECT COUNT(ja) FROM JornadaAula ja WHERE ja.idAula.id = :idAula",
                    Long.class
            );

            q.setParameter("idAula", idAula);

            return q.getSingleResult();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar jornadas por aula", ex);
        }
    }
}