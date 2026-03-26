package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspirante;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class JornadaAulaAspiranteDAO extends IngresoDefaultDataAcces<JornadaAulaAspirante, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public JornadaAulaAspiranteDAO() {
        super(JornadaAulaAspirante.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<JornadaAulaAspirante> getEntityClass() {
        return JornadaAulaAspirante.class;
    }

    public List<JornadaAulaAspirante> findByJornadaAula(Integer idJornadaAula, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (idJornadaAula == null) {
            throw new IllegalArgumentException("Id de jornada aula inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<JornadaAulaAspirante> q = getEntityManager().createNamedQuery(
                    "JornadaAulaAspirante.buscarPorJornadaAula",
                    JornadaAulaAspirante.class
            );

            q.setParameter("idJornadaAula", idJornadaAula);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar por jornada aula", ex);
        }
    }

    public List<JornadaAulaAspirante> findByAspirantePrueba(Integer idAspirantePrueba, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (idAspirantePrueba == null) {
            throw new IllegalArgumentException("Id de aspirante prueba inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<JornadaAulaAspirante> q = getEntityManager().createNamedQuery(
                    "JornadaAulaAspirante.buscarPorAspirantePrueba",
                    JornadaAulaAspirante.class
            );

            q.setParameter("idAspirantePrueba", idAspirantePrueba);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar por aspirante prueba", ex);
        }
    }

    public List<JornadaAulaAspirante> findByAsistencia(Boolean asistio, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (asistio == null) {
            throw new IllegalArgumentException("Estado de asistencia inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<JornadaAulaAspirante> q = getEntityManager().createNamedQuery(
                    "JornadaAulaAspirante.buscarPorAsistencia",
                    JornadaAulaAspirante.class
            );

            q.setParameter("asistio", asistio);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar por asistencia", ex);
        }
    }

    public Long countByJornadaAula(Integer idJornadaAula)
            throws IllegalArgumentException, IllegalStateException {

        if (idJornadaAula == null) {
            throw new IllegalArgumentException("Id de jornada aula inválido");
        }

        try {
            TypedQuery<Long> q = getEntityManager().createQuery(
                    "SELECT COUNT(jaa) FROM JornadaAulaAspirante jaa WHERE jaa.idJornadaAula.id = :idJornadaAula",
                    Long.class
            );

            q.setParameter("idJornadaAula", idJornadaAula);

            return q.getSingleResult();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar por jornada aula", ex);
        }
    }

    public Long countByAsistencia(Boolean asistio)
            throws IllegalArgumentException, IllegalStateException {

        if (asistio == null) {
            throw new IllegalArgumentException("Estado de asistencia inválido");
        }

        try {
            TypedQuery<Long> q = getEntityManager().createQuery(
                    "SELECT COUNT(jaa) FROM JornadaAulaAspirante jaa WHERE jaa.asistio = :asistio",
                    Long.class
            );

            q.setParameter("asistio", asistio);

            return q.getSingleResult();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar por asistencia", ex);
        }
    }
}