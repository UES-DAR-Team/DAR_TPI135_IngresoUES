package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import java.util.List;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public abstract class IngresoDefaultDataAcces<T, ID> implements IngresoDAOInterface<T, ID> {

    public abstract EntityManager getEntityManager();

    protected abstract Class<T> getEntityClass();

    private final Class<T> TipoDato;

    protected IngresoDefaultDataAcces(Class<T> tipoDato) {
        TipoDato = tipoDato;
    }

    public void create(final T obj) throws IllegalStateException, IllegalArgumentException  {
        EntityManager em = null;

        if (obj == null) {
            throw new IllegalArgumentException("Parametro no valido:objeto nulo");
        }
        try {
           em = getEntityManager();
            if (em == null) {
                throw new IllegalArgumentException("Parametro no valido:entity manager nulo");
            }
            em.persist(obj);
            em.flush();
        } catch (Exception e) {
            if(e instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e;
            }
            throw new IllegalStateException("Error al acceder al repositorio", e);
        }
    }

    public void delete(final T obj) throws  IllegalStateException, IllegalArgumentException {
        EntityManager em = null;

        if (obj == null) {
            throw new IllegalArgumentException("Parametro no valido:entity nulo");
        }
        try {
            //asignacion en limpio
            em = getEntityManager();
            if (em == null) {
                throw new IllegalArgumentException("Parametro no valido:entity manager nulo");
            }
            //merge para obtener la entidad gestionada,
            // esto es necesario para evitar problemas de transaccionalidad y manejo de entidades no gestionadas
            T managedEntity = em.merge(obj);
            //remover la entidad gestionada directamente,
            // evitando problemas de transaccionalidad y manejo de entidades no gestionadas
            em.remove(managedEntity);
        } catch (Exception ex) {
            if (ex instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) ex;
            }
            throw  new IllegalStateException("Error al acceder al repositorio", ex);
        }
    }

    @Override
    public T update(final T registro)
            throws IllegalArgumentException, IllegalStateException {

        if (registro == null) {
            throw new IllegalArgumentException("Parámetro no válido: registro es null");
        }

        try {
            EntityManager em = getEntityManager();

            if (em == null) {
                throw new IllegalStateException("No se pudo obtener el EntityManager");
            }

            return em.merge(registro);

        } catch (Exception ex) {
            throw new IllegalStateException("Error al actualizar el registro", ex);
        }
    }

    @Override
    public List<T> findRange(int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (first < 0 || max < 1) {
            throw new IllegalArgumentException("Parámetros no válidos");
        }

        try {
            EntityManager em = getEntityManager();

            if (em == null) {
                throw new IllegalStateException("No se pudo obtener el EntityManager");
            }

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(getEntityClass());
            Root<T> root = cq.from(getEntityClass());

            cq.select(root);

            TypedQuery<T> query = em.createQuery(cq);
            query.setFirstResult(first);
            query.setMaxResults(max);

            return query.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo acceder al repositorio", ex);
        }
    }


    public int count() throws IllegalStateException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            if (em == null) {
                throw new IllegalStateException("Error al acceder al repositorio");
            }

        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo acceder al repositorio", ex);
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(getEntityClass());
        cq.select(cb.count(root));
        return em.createQuery(cq).getSingleResult().intValue();
    }

    public T findById(final Object id) throws IllegalArgumentException, IllegalStateException{
        EntityManager em = null;

        if(id==null){
            throw new IllegalArgumentException("Parametro no valido: ID");
        }
        try {
            em = getEntityManager();
            if(em == null){
                throw new IllegalStateException("Error al acceder al repositorio");
            }

        }catch (Exception ex){
            throw new  IllegalStateException("Error al acceder al repositorio",ex);
        }
        return em.find(TipoDato, id);
    }


}
