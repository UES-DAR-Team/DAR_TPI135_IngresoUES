package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaArea;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPregunta;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaAreaPreguntaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<PruebaAreaPregunta> query;

    PruebaAreaPreguntaDAO dao;

    @BeforeEach
    void setup() {
        dao = new PruebaAreaPreguntaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return em;
            }
        };
    }

    // Retorna un DAO cuyo EntityManager es nulo, que simule un fallo de inyeccion
    private PruebaAreaPreguntaDAO daoConEmNulo() {
        return new PruebaAreaPreguntaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

    // Construye una PruebaAreaPregunta valida para los tests con resultados esperados
    // Necesita instanciar PruebaArea y Pregunta primero porque son relaciones obligatorias
    private PruebaAreaPregunta pruebaAreaPreguntaValida() {
        PruebaArea pruebaArea = new PruebaArea();
        pruebaArea.setId(1);

        Pregunta pregunta = new Pregunta();
        pregunta.setId(1);

        PruebaAreaPregunta pap = new PruebaAreaPregunta();
        pap.setId(1);
        pap.setIdPruebaArea(pruebaArea);
        pap.setIdPregunta(pregunta);
        pap.setOrden((short) 1);
        pap.setFechaAsignacion(OffsetDateTime.now());
        return pap;
    }

    // findByPruebaArea ----------------------------------

    // El id del area de prueba es nulo, el DAO debe rechazarlo antes de consultar la BD
    @Test
    void findByPruebaArea_cuandoIdNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaArea(null, 0, 10));
    }

    // El parametro first es negativo, cada condicion se prueba por separado
    @Test
    void findByPruebaArea_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaArea(1, -1, 10));
    }

    // El parametro max es cero, separado del anterior para identificar cual falla
    @Test
    void findByPruebaArea_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaArea(1, 0, 0));
    }

    // Todos los parametros son validos, se verifica que la query se ejecute correctamente
    @Test
    void findByPruebaArea_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("PruebaAreaPregunta.findByPruebaArea", PruebaAreaPregunta.class)).thenReturn(query);
        when(query.setParameter("idPruebaArea", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaAreaPreguntaValida()));

        List<PruebaAreaPregunta> resultado = dao.findByPruebaArea(1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaAreaPregunta.findByPruebaArea", PruebaAreaPregunta.class);
        verify(query).setParameter("idPruebaArea", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // No hay preguntas asignadas a ese area, se devuelve lista vacia sin excepcion
    @Test
    void findByPruebaArea_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("PruebaAreaPregunta.findByPruebaArea", PruebaAreaPregunta.class)).thenReturn(query);
        when(query.setParameter("idPruebaArea", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaAreaPregunta> resultado = dao.findByPruebaArea(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La BD falla, el DAO debe envolver el error en ISE
    @Test
    void findByPruebaArea_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("PruebaAreaPregunta.findByPruebaArea", PruebaAreaPregunta.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPruebaArea(1, 0, 10));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findByPruebaArea_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByPruebaArea(1, 0, 10));
    }

    // findByPregunta ----------------------------------

    // El id de la pregunta es nulo, el DAO debe rechazarlo antes de consultar la BD
    @Test
    void findByPregunta_cuandoIdNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPregunta(null, 0, 10));
    }

    // El parametro first es negativo, cada condicion se prueba por separado
    @Test
    void findByPregunta_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPregunta(1, -1, 10));
    }

    // El parametro max es cero, separado del anterior para identificar cual falla
    @Test
    void findByPregunta_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPregunta(1, 0, 0));
    }

    // Todos los parametros son validos, se verifica que la query se ejecute correctamente
    @Test
    void findByPregunta_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("PruebaAreaPregunta.findByPregunta", PruebaAreaPregunta.class)).thenReturn(query);
        when(query.setParameter("idPregunta", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaAreaPreguntaValida()));

        List<PruebaAreaPregunta> resultado = dao.findByPregunta(1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaAreaPregunta.findByPregunta", PruebaAreaPregunta.class);
        verify(query).setParameter("idPregunta", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // No hay areas de prueba que contengan esa pregunta, se devuelve lista vacia sin excepcion
    @Test
    void findByPregunta_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("PruebaAreaPregunta.findByPregunta", PruebaAreaPregunta.class)).thenReturn(query);
        when(query.setParameter("idPregunta", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaAreaPregunta> resultado = dao.findByPregunta(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La BD falla, el DAO debe envolver el error en ISE
    @Test
    void findByPregunta_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("PruebaAreaPregunta.findByPregunta", PruebaAreaPregunta.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPregunta(1, 0, 10));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findByPregunta_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByPregunta(1, 0, 10));
    }
}