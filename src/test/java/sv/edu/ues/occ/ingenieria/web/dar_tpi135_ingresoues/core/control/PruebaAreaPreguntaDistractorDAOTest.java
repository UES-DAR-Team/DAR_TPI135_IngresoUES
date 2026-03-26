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
    void setUp() {
        dao = new PruebaAreaPreguntaDistractorDAO() {
            @Override
            public EntityManager getEntityManager() {
                return em;
            }
        };
    }

    private PruebaAreaPreguntaDistractorDAO daoConEntityNulo() {
        return new PruebaAreaPreguntaDistractorDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

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

    @Test
    void testFindByPruebaAreaPreguntaParametroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAreaPregunta(null, 0, 10));
    }

    @Test
    void testFindByPruebaAreaPreguntaFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAreaPregunta(1, -1, 10));
    }

    @Test
    void testFindByPruebaAreaPreguntaMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAreaPregunta(1, 0, 0));
    }

    @Test
    void testFindByPruebaAreaPreguntaParametrosValidosRetornaLista() {
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

    @Test
    void testFindByPruebaAreaPreguntaRetornaListaVacia() {
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

    @Test
    void testFindByPruebaAreaPreguntaErrorInterno() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findByPruebaAreaPregunta",
                PruebaAreaPreguntaDistractor.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPruebaAreaPregunta(1, 0, 10));
    }

    @Test
    void testFindByPruebaAreaPreguntaEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findByPruebaAreaPregunta(1, 0, 10));
    }

    // Verifica el uso de getSingleResult para obtener la respuesta correcta.
    // Si no existe una respuesta, se lanza NoResultException, la cual es capturada por el DAO
    // retornando null. Se asume que solo existe una respuesta correcta por pregunta.
    @Test
    void testFindRespuestaCorrectaParametroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findRespuestaCorrecta(null));
    }

    @Test
    void testFindRespuestaCorrectaRetornaObjeto() {
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


    @Test
    void testFindRespuestaCorrectaRetornaNull() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findRespuestaCorrecta",
                PruebaAreaPreguntaDistractor.class)).thenReturn(query);
        when(query.setParameter("idPruebaAreaPregunta", 1)).thenReturn(query);
        when(query.getSingleResult()).thenThrow(NoResultException.class);

        PruebaAreaPreguntaDistractor resultado = dao.findRespuestaCorrecta(1);

        assertNull(resultado);
    }

    @Test
    void testFindRespuestaCorrectaErrorInterno() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findRespuestaCorrecta",
                PruebaAreaPreguntaDistractor.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findRespuestaCorrecta(1));
    }

    @Test
    void testFindRespuestaCorrectaEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findRespuestaCorrecta(1));
    }

    @Test
    void testFindByDistractorParametroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByDistractor(null, 0, 10));
    }

    @Test
    void testFindByDistractorFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByDistractor(1, -1, 10));
    }

    @Test
    void testFindByDistractorMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByDistractor(1, 0, 0));
    }

    @Test
    void testFindByDistractorParametrosValidosRetornaLista() {
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

    @Test
    void testFindByDistractorRetornarListaVacia() {
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

    @Test
    void testFindByDistractorErrorInterno() {
        when(em.createNamedQuery("PruebaAreaPreguntaDistractor.findByDistractor",
                PruebaAreaPreguntaDistractor.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByDistractor(1, 0, 10));
    }

    @Test
    void testFindByDistractorEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findByDistractor(1, 0, 10));
    }
}