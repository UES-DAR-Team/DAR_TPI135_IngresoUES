package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;


import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class PreguntaDAO extends IngresoDefaultDataAcces<Pregunta, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public PreguntaDAO() {
        super(Pregunta.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Pregunta> getEntityClass() {
        return Pregunta.class;
    }

    //luego necesitamos un metodo para buscar pregunta por coincidencia de contenido de texto
    public List<Pregunta> findByCoincidenciaTexto(final String texto, int first, int max){

        //capturar excepciones
        try {
            //validacion de parametros
            if (texto != null && !texto.isBlank() && first >= 0 && max > 0) {
                TypedQuery<Pregunta> q = em.createNamedQuery("Pregunta.findByCoincidenciaTexto", Pregunta.class);
                q.setParameter("texto", "%" + texto.trim().toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }
        }catch (Exception e){
            Logger.getLogger(PreguntaDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return List.of();
    }

    //buscar por id con super find
    //este metodo ya se comprobo en el test de la clase abstracta
    public Pregunta findById(Object id) {
        return super.findById(id);
    }





}

