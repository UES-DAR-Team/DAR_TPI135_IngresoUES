package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;


import java.io.Serializable;

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

    //luego necesitamos un metodo para buscar pregunta por coincidencia de nombre o contenido de texto

}
