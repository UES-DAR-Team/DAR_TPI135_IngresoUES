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
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class JornadaAulaDAO extends IngresoDefaultDataAcces<JornadaAula, UUID> implements Serializable {

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
            TypedQuery<JornadaAula> q = em.createNamedQuery(
                    "JornadaAula.buscarPorJornada",
                    JornadaAula.class
            );

            q.setParameter("idJornada", idJornada);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            Logger.getLogger(JornadaAulaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return List.of();
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
            TypedQuery<JornadaAula> q = em.createNamedQuery(
                    "JornadaAula.buscarPorAula",
                    JornadaAula.class
            );

            q.setParameter("idAula", idAula);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            Logger.getLogger(JornadaAulaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return List.of();
    }

    public Long countByJornada(UUID idJornada)
            throws IllegalArgumentException, IllegalStateException {

        if (idJornada == null) {
            throw new IllegalArgumentException("Id de jornada inválido");
        }

        try {
            TypedQuery<Long> q = em.createNamedQuery(
                    "JornadaAula.countByJornada",
                    Long.class
            );

            q.setParameter("idJornada", idJornada);

            return q.getSingleResult();

        } catch (Exception ex) {
            Logger.getLogger(JornadaAulaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return 0L;
    }

    public Long countByAula(UUID idAula)
            throws IllegalArgumentException, IllegalStateException {

        if (idAula == null) {
            throw new IllegalArgumentException("Id de aula inválido");
        }

        try {
            TypedQuery<Long> q = em.createNamedQuery(
                    "JornadaAula.countByAula",
                    Long.class
            );

            q.setParameter("idAula", idAula);

            return q.getSingleResult();

        } catch (Exception ex) {
            Logger.getLogger(JornadaAulaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return 0L;
    }
}