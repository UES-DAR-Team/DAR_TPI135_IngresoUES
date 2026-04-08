package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aula;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class AulaDAO extends IngresoDefaultDataAcces<Aula, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public AulaDAO() {
        super(Aula.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Aula> getEntityClass() {
        return Aula.class;
    }

    public List<Aula> findByCapacidadMin(final Integer capacidad, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (capacidad == null) {
            throw new IllegalArgumentException("Capacidad inválida");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<Aula> q = getEntityManager().createNamedQuery("Aula.findByCapacidadMin", Aula.class);
            q.setParameter("capacidad", capacidad);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aulas por capacidad mínima", ex);
        }
    }

    public List<Aula> findByNombre(final String nombre, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<Aula> q = getEntityManager().createNamedQuery("Aula.findByNombre", Aula.class);
            q.setParameter("nombre", nombre.trim());
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        }  catch (Exception ex) {
        throw new IllegalStateException("Error al buscar aulas por nombre", ex);
    }
    }

    public List<Aula> findActivos(int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<Aula> q = getEntityManager().createNamedQuery("Aula.findActivos", Aula.class);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aulas activas", ex);

        }
    }
}