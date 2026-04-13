package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaJornada;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaJornadaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<PruebaJornada> query;

    @InjectMocks
    PruebaJornadaDAO dao;

    private PruebaJornadaDAO daoConEntityNulo() {
        return new PruebaJornadaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

    private PruebaJornada pruebaJornadaValida() {
        Prueba prueba = new Prueba();
        prueba.setId(UUID.randomUUID());

        Jornada jornada = new Jornada();
        jornada.setId(UUID.randomUUID());

        PruebaJornada pj = new PruebaJornada();
        pj.setId(1);
        pj.setIdPrueba(prueba);
        pj.setIdJornada(jornada);
        pj.setFechaAsignacion(OffsetDateTime.now());
        return pj;
    }

    @Test
    void testFindByJornadaIdNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(null, 0, 10));
    }

    @Test
    void testFindByJornadaFirstNegativo() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(id, -1, 10));
    }

    @Test
    void testFindByJornadaMaxCeroNegativo() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(id, 0, 0));
    }

    @Test
    void testFindByJornadaParametrosValidosRetornaLista() {
        UUID id = UUID.randomUUID();
        when(em.createNamedQuery("PruebaJornada.findByJornada", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idJornada", id)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaJornadaValida()));

        List<PruebaJornada> resultado = dao.findByJornada(id, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaJornada.findByJornada", PruebaJornada.class);
        verify(query).setParameter("idJornada", id);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByJornadaRetornaListaVacia() {
        UUID id = UUID.randomUUID();
        when(em.createNamedQuery("PruebaJornada.findByJornada", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idJornada", id)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaJornada> resultado = dao.findByJornada(id, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByJornadaErrorInterno() {
        UUID id = UUID.randomUUID();
        when(em.createNamedQuery("PruebaJornada.findByJornada", PruebaJornada.class))
                .thenThrow(new RuntimeException("Fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByJornada(id, 0, 10));
    }

    @Test
    void testFindByJornadaEntityManagerNulo() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findByJornada(id, 0, 10));
    }

    @Test
    void testFindByPruebaParametroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(null, 0, 10));
    }

    @Test
    void testFindByPruebaFirstNegativo() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(id, -1, 10));
    }

    @Test
    void testFindByPruebaMaxCeroNegativo() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(id, 0, 0));
    }

    @Test
    void testFindByPruebaParametrosValidosRetornaLista() {
        UUID id = UUID.randomUUID();
        when(em.createNamedQuery("PruebaJornada.findByPrueba", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idPrueba", id)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaJornadaValida()));

        List<PruebaJornada> resultado = dao.findByPrueba(id, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaJornada.findByPrueba", PruebaJornada.class);
        verify(query).setParameter("idPrueba", id);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByPruebaRetornaListaVacia() {
        UUID id = UUID.randomUUID();
        when(em.createNamedQuery("PruebaJornada.findByPrueba", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idPrueba", id)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaJornada> resultado = dao.findByPrueba(id, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByPruebaErrorInterno() {
        UUID id = UUID.randomUUID();
        when(em.createNamedQuery("PruebaJornada.findByPrueba", PruebaJornada.class))
                .thenThrow(new RuntimeException("Fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPrueba(id, 0, 10));
    }

    @Test
    void testFindByPruebaEntityManagerNulo() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findByPrueba(id, 0, 10));
    }

    /**
     * Verifica el comportamiento del metodo al filtrar por prueba y jornada
     * asegurando que ambos parametros se envían correctamente a la consulta
     * y que la paginación se aplica adecuadamente.
     */

    @Test
    void testFindByPruebaAndJornadaIdPruebaNulo() {
        UUID idJornada = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAndJornada(null, idJornada, 0, 10));
    }

    @Test
    void testFindByPruebaAndJornadaIdJornadaNulo() {
        UUID idPrueba = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAndJornada(idPrueba, null, 0, 10));
    }

    @Test
    void testFindByPruebaAndJornadaFirstNegativo() {
        UUID idPrueba = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAndJornada(idPrueba, idJornada, -1, 10));
    }

    @Test
    void testfFindByPruebaAndJornadaMaxCeroNegativo() {
        UUID idPrueba = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAndJornada(idPrueba, idJornada, 0, 0));
    }

    @Test
    void testFindByPruebaAndJornadaParametrosValidosRetornaLista() {
        UUID idPrueba = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();

        when(em.createNamedQuery("PruebaJornada.findByPruebaAndJornada", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idPrueba", idPrueba)).thenReturn(query);
        when(query.setParameter("idJornada", idJornada)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaJornadaValida()));

        List<PruebaJornada> resultado = dao.findByPruebaAndJornada(idPrueba, idJornada, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaJornada.findByPruebaAndJornada", PruebaJornada.class);
        verify(query).setParameter("idPrueba", idPrueba);
        verify(query).setParameter("idJornada", idJornada);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByPruebaAndJornadaRetornaListaVacia() {
        UUID idPrueba = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();

        when(em.createNamedQuery("PruebaJornada.findByPruebaAndJornada", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idPrueba", idPrueba)).thenReturn(query);
        when(query.setParameter("idJornada", idJornada)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaJornada> resultado = dao.findByPruebaAndJornada(idPrueba, idJornada, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByPruebaAndJornadaErrorInterno() {
        UUID idPrueba = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();

        when(em.createNamedQuery("PruebaJornada.findByPruebaAndJornada", PruebaJornada.class))
                .thenThrow(new RuntimeException("Fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPruebaAndJornada(idPrueba, idJornada, 0, 10));
    }

    @Test
    void testFindByPruebaAndJornadaEntityManagerNulo() {
        UUID idPrueba = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();

        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findByPruebaAndJornada(idPrueba, idJornada, 0, 10));
    }
}