package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.service;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;

//@ApplicationScoped
public class AreaConocimientoService {
    @Inject
    private AreaConocimientoDAO areaConocimientoDAO;

    //detallar la logica del negocio para crear un area de conocimiento, validando que el area padre exista
    //crear area con un area padre valido o sin area padre(que vendria siendo un area raiz)
    // para luego consumir este servicio desde rest

    public void crearAreaConocimiento(AreaConocimiento areaConocimiento) {
        //validando padre
        if (areaConocimiento.getIdAutoReferenciaArea() != null) {
            AreaConocimiento padre = areaConocimientoDAO.findById(areaConocimiento.getIdAutoReferenciaArea().getId());
            if(padre == null){
                throw new IllegalArgumentException("El area de conocimiento padre no existe");
            }
        }

        //crear si padre no existe o es valido
        areaConocimientoDAO.create(areaConocimiento);

    }

}
