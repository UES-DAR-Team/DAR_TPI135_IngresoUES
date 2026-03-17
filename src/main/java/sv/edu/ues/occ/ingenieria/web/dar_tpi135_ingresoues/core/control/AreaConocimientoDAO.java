package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class AreaConocimientoDAO extends IngresoDefaultDataAcces<AreaConocimiento, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public AreaConocimientoDAO() {
        super(AreaConocimiento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<AreaConocimiento> getEntityClass() {
        return AreaConocimiento.class;
    }

    // vamos a necesitar un metodo para buscar id por texto, para ello vamos a hacer un conversor en el boundary
    // vamos a necesitar un metodo para buscarpor id area_conocimiento para implementarlo en otra clase necesaria como para posible area conocimiento para una pregunta
    // en donde se va a crear un pregunta y asignar a una area de conocimiento

    //buscar area de conocimiento por coincidencia de nombre
    public List<AreaConocimiento> findByNameLike(final String name, int first, int max) {
        try {
            if (name != null && !name.isBlank() && first >= 0 && max > 0) {
                TypedQuery<AreaConocimiento> q = em.createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class);
                q.setParameter("name", "%" + name.trim().toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }
        } catch (Exception e) {
            Logger.getLogger(AreaConocimientoDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return List.of();
    }

    //buscar por id con super find
    //este metodo ya se comprobo en el test de la clase abstracta
    // por lo que no es necesario problarla de nuevo aqui
    public AreaConocimiento findById(Object id) {
        return super.findById(id);
    }

    //buscar por padre
    public List<AreaConocimiento> findByAreaPadre() {
        try {
            return em.createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class).getResultList();
        } catch (Exception e) {
            Logger.getLogger(AreaConocimientoDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return List.of();
        }
    }


    //buscar por hijo
    public List<AreaConocimiento> findHijosByPadre(Integer idPadre) {
        try {
            if (idPadre != null && idPadre > 0) {
                return em.createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class)
                        .setParameter("idPadre", idPadre)
                        .getResultList();
            }
        } catch (Exception e) {
            Logger.getLogger(AreaConocimientoDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
      return List.of();
    }

}
