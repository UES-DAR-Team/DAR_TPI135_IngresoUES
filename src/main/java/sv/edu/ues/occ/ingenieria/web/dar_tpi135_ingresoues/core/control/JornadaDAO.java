package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class JornadaDAO extends IngresoDefaultDataAcces<Jornada, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public JornadaDAO() {
        super(Jornada.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Jornada> getEntityClass() {
        return Jornada.class;
    }

    public List<Jornada> findByNombre(String nombre, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<Jornada> q = getEntityManager().createNamedQuery(
                    "Jornada.buscarPorNombre",
                    Jornada.class
            );

            q.setParameter("nombre", "%" + nombre.trim().toUpperCase() + "%");
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar jornadas por nombre", ex);
        }
    }

    public List<Jornada> findByActivo(Boolean activo, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (activo == null) {
            throw new IllegalArgumentException("Estado inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<Jornada> q = getEntityManager().createNamedQuery(
                    "Jornada.buscarPorActivo",
                    Jornada.class
            );

            q.setParameter("activo", activo);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar jornadas por estado", ex);
        }
    }

    public Long countByNombre(String nombre)
            throws IllegalArgumentException, IllegalStateException {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre inválido");
        }

        try {
            TypedQuery<Long> q = getEntityManager().createNamedQuery(
                    "Jornada.countByNombre",
                    Long.class
            );

            q.setParameter("nombre", "%" + nombre.trim().toUpperCase() + "%");

            return q.getSingleResult();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar jornadas por nombre", ex);
        }
    }

    public Long countByActivo(Boolean activo)
            throws IllegalArgumentException, IllegalStateException {

        if (activo == null) {
            throw new IllegalArgumentException("Estado inválido");
        }

        try {
            TypedQuery<Long> q = getEntityManager().createNamedQuery(
                    "Jornada.countByActivo",
                    Long.class
            );

            q.setParameter("activo", activo);

            return q.getSingleResult();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar jornadas por estado", ex);
        }
    }
}