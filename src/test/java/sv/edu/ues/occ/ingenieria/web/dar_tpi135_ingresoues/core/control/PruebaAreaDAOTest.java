package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaArea;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaAreaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<PruebaArea> query;

    PruebaAreaDAO dao;

    @BeforeEach
    void setUp() {
        dao = new PruebaAreaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return em;
            }
        };
    }

    // Retorna un DAO donde el EntityManager es nulo, simulando un fallo de inyeccion
    private PruebaAreaDAO daoConEmNulo() {
        return new PruebaAreaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

   //Construye una PruebaArea valida para los tests con resultados esperados
    // Necesita instanciar Prueba y AreaConocimiento primero porque son relaciones obligatorias
    private PruebaArea pruebaAreaValida() {
        Prueba prueba = new Prueba();
        prueba.setId(1);

        AreaConocimiento area = new AreaConocimiento();
        area.setId(1);

        PruebaArea pa = new PruebaArea();
        pa.setId(1);
        pa.setIdPrueba(prueba);
        pa.setIdAreaConocimiento(area);
        pa.setNumPreguntas((short) 5);
        pa.setFechaAsignacion(OffsetDateTime.now());
        return pa;
    }

    // findByPrueba ----------------------------------

    // El id de la prueba es nulo, el DAO debe rechazarlo antes de consultar la BD
    @Test
    void findByPrueba_cuandoIdNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(null, 0, 10));
    }

    // El parametro first es negativo, cada condicion se prueba por separado
    @Test
    void findByPrueba_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(1, -1, 10));
    }

    // El parametro max es cero, separado del anterior para identificar cual falla
    @Test
    void findByPrueba_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(1, 0, 0));
    }

    // Todos los parametros son validos, se verifica que la query se ejecute correctamente
    @Test
    void findByPrueba_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("PruebaArea.findByPrueba", PruebaArea.class)).thenReturn(query);
        when(query.setParameter("idPrueba", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaAreaValida()));

        List<PruebaArea> resultado = dao.findByPrueba(1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaArea.findByPrueba", PruebaArea.class);
        verify(query).setParameter("idPrueba", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // No hay areas asignadas a esa prueba, se devuelve lista vacia sin excepcion
    @Test
    void findByPrueba_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("PruebaArea.findByPrueba", PruebaArea.class)).thenReturn(query);
        when(query.setParameter("idPrueba", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaArea> resultado = dao.findByPrueba(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La BD falla, el DAO debe envolver el error en ISE
    @Test
    void findByPrueba_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("PruebaArea.findByPrueba", PruebaArea.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPrueba(1, 0, 10));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findByPrueba_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByPrueba(1, 0, 10));
    }

    // findByAreaConocimiento ----------------------------------

    // El id del area es nulo, el DAO debe rechazarlo antes de consultar la BD
    @Test
    void findByAreaConocimiento_cuandoIdNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAreaConocimiento(null, 0, 10));
    }

    // El parametro first es negativo
    @Test
    void findByAreaConocimiento_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAreaConocimiento(1, -1, 10));
    }

    // El parametro max es cero
    @Test
    void findByAreaConocimiento_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAreaConocimiento(1, 0, 0));
    }

    // Todos los parametros son validos, se verifica que la query se ejecute correctamente
    @Test
    void findByAreaConocimiento_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("PruebaArea.findByAreaConocimiento", PruebaArea.class)).thenReturn(query);
        when(query.setParameter("idAreaConocimiento", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaAreaValida()));

        List<PruebaArea> resultado = dao.findByAreaConocimiento(1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaArea.findByAreaConocimiento", PruebaArea.class);
        verify(query).setParameter("idAreaConocimiento", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // No hay pruebas asignadas a ese area, se devuelve lista vacia sin excepcion
    @Test
    void findByAreaConocimiento_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("PruebaArea.findByAreaConocimiento", PruebaArea.class)).thenReturn(query);
        when(query.setParameter("idAreaConocimiento", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaArea> resultado = dao.findByAreaConocimiento(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La BD falla, el DAO debe envolver el error en ISE
    @Test
    void findByAreaConocimiento_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("PruebaArea.findByAreaConocimiento", PruebaArea.class))
                .thenThrow(new RuntimeException("fallo en la Base de datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByAreaConocimiento(1, 0, 10));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findByAreaConocimiento_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByAreaConocimiento(1, 0, 10));
    }

    // findByNumPreguntasMin ----------------------------------

    // El valor de numPreguntas es nulo, el DAO debe rechazarlo antes de consultar la BD
    @Test
    void findByNumPreguntasMin_cuandoNumPreguntasNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNumPreguntasMin(null, 0, 10));
    }

    // El parametro first es negativo
    @Test
    void findByNumPreguntasMin_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNumPreguntasMin((short) 5, -1, 10));
    }

    // El parametro max es cero
    @Test
    void findByNumPreguntasMin_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNumPreguntasMin((short) 5, 0, 0));
    }

    // Todos los parametros son validos, el cast (short) es necesario para que Mockito coincida
    @Test
    void findByNumPreguntasMin_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("PruebaArea.findByNumPreguntasMin", PruebaArea.class)).thenReturn(query);
        when(query.setParameter("numPreguntas", (short) 5)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaAreaValida()));

        List<PruebaArea> resultado = dao.findByNumPreguntasMin((short) 5, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaArea.findByNumPreguntasMin", PruebaArea.class);
        verify(query).setParameter("numPreguntas", (short) 5);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // No hay asignaciones con esa cantidad minima, se devuelve lista vacia sin excepcion
    @Test
    void findByNumPreguntasMin_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("PruebaArea.findByNumPreguntasMin", PruebaArea.class)).thenReturn(query);
        when(query.setParameter("numPreguntas", (short) 99)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaArea> resultado = dao.findByNumPreguntasMin((short) 99, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La BD falla, el DAO debe envolver el error en ISE
    @Test
    void findByNumPreguntasMin_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("PruebaArea.findByNumPreguntasMin", PruebaArea.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByNumPreguntasMin((short) 5, 0, 10));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findByNumPreguntasMin_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByNumPreguntasMin((short) 5, 0, 10));
    }
}