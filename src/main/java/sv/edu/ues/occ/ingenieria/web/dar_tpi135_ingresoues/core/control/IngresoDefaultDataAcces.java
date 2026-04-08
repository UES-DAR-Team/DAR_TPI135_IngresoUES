package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public abstract class IngresoDefaultDataAcces<T, ID> implements IngresoDAOInterface<T, ID> {

    private final Class<T> tipoDato;

    protected IngresoDefaultDataAcces(Class<T> tipoDato) {
        this.tipoDato = tipoDato;
    }

    public abstract EntityManager getEntityManager();

    protected Class<T> getEntityClass() {
        return tipoDato;
    }

    @Override
    public void create(final T obj) throws IllegalArgumentException, IllegalStateException {
        if (obj == null) {
            throw new IllegalArgumentException("Parámetro no válido: objeto nulo");
        }

        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("EntityManager no disponible");
        }

        try {
            em.persist(obj);
            em.flush();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder al repositorio", e);
        }
    }

    @Override
    public void delete(final T obj) throws IllegalArgumentException, IllegalStateException {
        if (obj == null) {
            throw new IllegalArgumentException("Parámetro no válido: entidad nula");
        }

        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("EntityManager no disponible");
        }

        try {
            T managedEntity = em.merge(obj);
            em.remove(managedEntity);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder al repositorio", e);
        }
    }

    @Override
    public T update(final T registro) throws IllegalArgumentException, IllegalStateException {
        if (registro == null) {
            throw new IllegalArgumentException("Parámetro no válido: registro es null");
        }

        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("No se pudo obtener el EntityManager");
        }

        try {
            return em.merge(registro);
        } catch (Exception e) {
            throw new IllegalStateException("Error al actualizar el registro", e);
        }
    }

    @Override
    public List<T> findRange(int first, int max) throws IllegalArgumentException, IllegalStateException {
        if (first < 0 || max < 1) {
            throw new IllegalArgumentException("Parámetros no válidos: first=" + first + ", max=" + max);
        }

        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("No se pudo obtener el EntityManager");
        }

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(getEntityClass());
            Root<T> root = cq.from(getEntityClass());
            cq.select(root);

            TypedQuery<T> query = em.createQuery(cq);
            query.setFirstResult(first);
            query.setMaxResults(max);

            return query.getResultList();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo acceder al repositorio", e);
        }
    }

    @Override
    public int count() throws IllegalStateException {
        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("No se pudo obtener el EntityManager");
        }

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> root = cq.from(getEntityClass());
            cq.select(cb.count(root));
            return em.createQuery(cq).getSingleResult().intValue();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo acceder al repositorio", e);
        }
    }

    @Override
    public T findById(final Object id) throws IllegalArgumentException, IllegalStateException {
        if (id == null) {
            throw new IllegalArgumentException("Parámetro no válido: ID nulo");
        }

        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("Error al acceder al repositorio");
        }

        try {
            return em.find(tipoDato, id);
        } catch (Exception e) {
            throw new IllegalStateException("Error al acceder al repositorio", e);
        }
    }
}