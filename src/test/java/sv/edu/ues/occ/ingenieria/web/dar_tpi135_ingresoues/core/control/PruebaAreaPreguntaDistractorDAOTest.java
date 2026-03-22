package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPreguntaDistractor;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaAreaPreguntaDistractorDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<PruebaAreaPreguntaDistractor> query;

    PruebaAreaPreguntaDistractorDAO dao;

    @BeforeEach
    void setup() {
        dao = new PruebaAreaPreguntaDistractorDAO() {
            @Override
            public EntityManager getEntityManager() {
                return em;
            }
        };
    }

    // Retorna un DAO donde el EntityManager es nulo, haciendo simular un fallo de inyeccion
    private PruebaAreaPreguntaDistractorDAO daoConEmNulo() {
        return new PruebaAreaPreguntaDistractorDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

    // construye un PruebaAreaPreguntaDistractor valido para los tests con resultados esperados
    // necesita instanciar PruebaAreaPregunta y Distractor primero porq son relaciones obligatorias
    private PruebaAreaPreguntaDistractor distractorValido() {
        PruebaAreaPregunta pap = new PruebaAreaPregunta();
        pap.setId(1);

        Distractor distractor = new Distractor();
        distractor.setId(1);

        PruebaAreaPreguntaDistractor d = new PruebaAreaPreguntaDistractor();
        d.setId(1);
        d.setIdPruebaAreaPregunta(pap);
        d.setIdDistractor(distractor);
        d.setEsRespuestaCorrecta(false);
        d.setFechaRegistro(OffsetDateTime.now());
        return d;
    }

    // findByPruebaAreaPregunta ----------------------------------

    // El id de la pregunta es nulo, el DAO debe rechazarlo antes de consultar la BD
    @Test
    void findByPruebaAreaPregunta_cuandoIdNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAreaPregunta(null, 0, 10));
    }

    // El parametro first es negativo, cada condicion se prueba por separado
    @Test
    void findByPruebaAreaPregunta_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAreaPregunta(1, -1, 10));
    }

    // El parametro max es cero, separado del anterior para identificar cual falla
    @Test
    void findByPruebaAreaPregunta_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAreaPregunta(1, 0, 0));
    }

    // Todos los parametros son validos, se verifica que la query se ejecute correctamente
    @Test
    void findByPruebaAreaPregunta_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findByPruebaAreaPregunta",
                PruebaAreaPreguntaDistractor.class)).thenReturn(query);
        when(query.setParameter("idPruebaAreaPregunta", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(distractorValido()));

        List<PruebaAreaPreguntaDistractor> resultado =
                dao.findByPruebaAreaPregunta(1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaAreaPreguntaDistractor.findByPruebaAreaPregunta", PruebaAreaPreguntaDistractor.class);
        verify(query).setParameter("idPruebaAreaPregunta", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // No hay distractores para esa pregunta, se devuelve lista vacia sin excepcion
    @Test
    void findByPruebaAreaPregunta_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findByPruebaAreaPregunta",
                PruebaAreaPreguntaDistractor.class)).thenReturn(query);
        when(query.setParameter("idPruebaAreaPregunta", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaAreaPreguntaDistractor> resultado =
                dao.findByPruebaAreaPregunta(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La BD falla, el DAO debe envolver el error en ISE
    @Test
    void findByPruebaAreaPregunta_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findByPruebaAreaPregunta",
                PruebaAreaPreguntaDistractor.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPruebaAreaPregunta(1, 0, 10));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findByPruebaAreaPregunta_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByPruebaAreaPregunta(1, 0, 10));
    }

    // findRespuestaCorrecta ----------------------------------
    // Devuelve un solo objeto con getSingleResult en lugar de una lista si no hay respuesta correcta lanza NoResultException que el DAO captura y retorna null
    // Solo hay una respuesta correcta por pregunta

    // El id de la pregunta es nulo, el DAO debe rechazarlo antes de consultar la BD
    @Test
    void findRespuestaCorrecta_cuandoIdNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findRespuestaCorrecta(null));
    }

    // Hay una respuesta correcta, se devuelve el objeto encontrado
    @Test
    void findRespuestaCorrecta_cuandoExisteRespuesta_deberiaRetornarObjeto() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findRespuestaCorrecta",
                PruebaAreaPreguntaDistractor.class)).thenReturn(query);
        when(query.setParameter("idPruebaAreaPregunta", 1)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(distractorValido());

        PruebaAreaPreguntaDistractor resultado = dao.findRespuestaCorrecta(1);

        assertNotNull(resultado);
        verify(em).createNamedQuery("PruebaAreaPreguntaDistractor.findRespuestaCorrecta",
                PruebaAreaPreguntaDistractor.class);
        verify(query).setParameter("idPruebaAreaPregunta", 1);
        verify(query).getSingleResult();
    }

    // La pregunta no tiene respuesta correcta asignada, el DAO captura NoResultException y retorna null
    @Test
    void findRespuestaCorrecta_cuandoNoHayRespuesta_deberiaRetornarNull() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findRespuestaCorrecta",
                PruebaAreaPreguntaDistractor.class)).thenReturn(query);
        when(query.setParameter("idPruebaAreaPregunta", 1)).thenReturn(query);
        when(query.getSingleResult()).thenThrow(NoResultException.class);

        PruebaAreaPreguntaDistractor resultado = dao.findRespuestaCorrecta(1);

        assertNull(resultado);
    }

    // La BD falla con un error distinto a NoResultException, el DAO lanza ISE
    @Test
    void findRespuestaCorrecta_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findRespuestaCorrecta",
                PruebaAreaPreguntaDistractor.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findRespuestaCorrecta(1));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findRespuestaCorrecta_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findRespuestaCorrecta(1));
    }

    // findByDistractor ----------------------------------

    // El id del distractor es nulo, el DAO debe rechazarlo antes de consultar la BD
    @Test
    void findByDistractor_cuandoIdNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByDistractor(null, 0, 10));
    }

    // El parametro first es negativo, cada condicion se prueba por separado
    @Test
    void findByDistractor_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByDistractor(1, -1, 10));
    }

    // El parametro max es cero, separado del anterior para identificar cual falla
    @Test
    void findByDistractor_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByDistractor(1, 0, 0));
    }

    // Todos los parametros son validos, se verifica que la query se ejecute correctamente
    @Test
    void findByDistractor_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findByDistractor",
                PruebaAreaPreguntaDistractor.class)).thenReturn(query);
        when(query.setParameter("idDistractor", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(distractorValido()));

        List<PruebaAreaPreguntaDistractor> resultado =
                dao.findByDistractor(1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaAreaPreguntaDistractor.findByDistractor",
                PruebaAreaPreguntaDistractor.class);
        verify(query).setParameter("idDistractor", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // No hay preguntas que usen ese distractor, se devuelve lista vacia sin excepcion
    @Test
    void findByDistractor_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findByDistractor",
                PruebaAreaPreguntaDistractor.class)).thenReturn(query);
        when(query.setParameter("idDistractor", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaAreaPreguntaDistractor> resultado =
                dao.findByDistractor(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La base falla, el DAO debe envolver el error en ISE
    @Test
    void findByDistractor_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findByDistractor",
                PruebaAreaPreguntaDistractor.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByDistractor(1, 0, 10));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findByDistractor_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByDistractor(1, 0, 10));
    }
}