package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AreaConocimientoDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<AreaConocimiento> query;

    @InjectMocks
    AreaConocimientoDAO dao;

    private AreaConocimiento area;

    @BeforeEach
    void setUp() {
        area = new AreaConocimiento();
        area.setId(1);
    }


    //====================pruebas al findByNameLike========================
    //entonces aqui lo que se puede evaluar que sea proprio de la clase es el manejo de los parametros de la busqueda por nombre
    //retorna una lista vacia pero ademas toma la excepcion
    // aqui solo probamos los parametros osea --> if (name != null && !name.isBlank() && first >= 0 && max > 0)
    @Test
    void findByNameLikeNombreVacio() {
        List<AreaConocimiento> resultado = dao.findByNameLike("", 0, 10);
        assertTrue(resultado.isEmpty());
    }

    //nombre nulo
    @Test
    void findByNameLikeNombreNulo() {
        List<AreaConocimiento> resultado = dao.findByNameLike(null, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    //first negativo
    @Test
    void findByNameLikeFirstNegativo() {
        List<AreaConocimiento> resultado = dao.findByNameLike("test", -1, 10);
        assertTrue(resultado.isEmpty());
    }

    //max negativo
    @Test
    void findByNameLikeMaxNegativo() {
        List<AreaConocimiento> resultado = dao.findByNameLike("test", 0, -1);
        assertTrue(resultado.isEmpty());
    }

    //cuando parametros validos
    @Test
    void findByNameLikeParametrosValidos() {
        when(em.createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class))
                .thenReturn(query);
        when(query.setParameter("name", "%TEST%")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(area));
        List<AreaConocimiento> resultado = dao.findByNameLike("test", 0, 10);
        assertTrue(resultado.contains(area));
        verify(em).createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class);
        verify(query).setParameter("name", "%TEST%");
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    //aqui se prueba la captura de excepciones del findByNameLike, para cubirir la rama de excepciones
    @Test
    void findByNameLikeException(){
        when(em.createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class))
                .thenThrow(new RuntimeException("DB error")); //simulacion de excepcion al hacer la query
        //llama al metodo con parametros validos para que llegue a la parte de la query
        List<AreaConocimiento> resultado = dao.findByNameLike("test", 0,10);
        assertTrue(resultado.isEmpty());// verifica que que devuelva lista vacia
        //verficar que se hizo la llamada a la query
        verify(em).createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class);
    }
    //====================fin de las pruebas al findByNameLike========================

    //========================pruebas de findByAreaPadre========================
    //pruebas a las funciones de las querys de las entidades
    // en el dao solo se llama el resultado de la query y maneja los parametros
    //find tipo padre
    @Test
    void findByAreaPadreOk() {
        when(em.createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class))
                .thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(area));
        List<AreaConocimiento> resultado = dao.findByAreaPadre();
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertSame(area, resultado.getFirst());

        verify(em).createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class);
        verify(query).getResultList();
    }

    //prueba de excepcion --> covertura
    //en caso de que la query falle, el dao debe manejar la excepcion y retornar una lista vacia
    @Test
    void findByAreaPadreException(){
        when(em.createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class))
                .thenThrow(new RuntimeException("DB error"));
        List<AreaConocimiento> resultado = dao.findByAreaPadre();
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class);
    }
    //========================fin de pruebas de findByAreaPadre========================

    //========================pruebas de findHijosByPadre========================

    //probar parametros validos / prueba tambien el funcionamiento de la query,
    // aunque esta parte es mas para probar la query que el dao,
    // pero se puede probar que el dao maneja bien los parametros y el resultado de la query
    @Test
    void findHijosByPadreParametrosValidos(){
        when(em.createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class))
                .thenReturn(query);
        when(query.setParameter("idPadre", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(area));
        List<AreaConocimiento> resultado = dao.findHijosByPadre(1);
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertSame(area, resultado.getFirst());
    }

    //probar parametros nulos
    @Test
    void findHijosByPadreParametrosNulos(){
        //en este caso el dao no maneja la validacion de los parametros, por lo que se espera que la query falle
        List<AreaConocimiento> resultado = dao.findHijosByPadre(null);
        assertTrue(resultado.isEmpty());
    }

    //probar parametros invalidos
    @Test
    void findHijosByPadreParametroNegativo(){
        List<AreaConocimiento> resultado = dao.findHijosByPadre(-1);
        assertTrue(resultado.isEmpty());
    }

    //probar excepciones de la query, para cubrir la rama de excepciones del dao
    @Test
    void findHijosByPadreException(){
        when(em.createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class))
                .thenThrow(new RuntimeException("DB error"));
        List<AreaConocimiento> resultado = dao.findHijosByPadre(1);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class);
    }
    //=======================Fin de las pruebas de findHijosByPadre========================


}