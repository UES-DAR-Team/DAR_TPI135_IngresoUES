package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Prueba> query;

    PruebaDAO dao;

    @BeforeEach
    void setUp() {
        dao = new PruebaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return em;
            }
        };
    }

    private PruebaDAO daoConEntityNulo() {
        return new PruebaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

    private Prueba pruebaValida() {
        Prueba p = new Prueba();
        p.setId(1);
        p.setNombrePrueba("Prueba de Matematica");
        p.setDuracionMin(90);
        p.setFechaCreacion(OffsetDateTime.now());
        p.setActivo(true);
        return p;
    }

    @Test
    void testFindActivasFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findActivas(-1, 10));
    }

    @Test
    void testFindActivasMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findActivas(0, 0));
    }

    @Test
    void testFindActivasParametroValidosRetornaLista() {
        when(em.createNamedQuery("Prueba.findActivas", Prueba.class)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaValida()));

        List<Prueba> resultado = dao.findActivas(0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("Prueba.findActivas", Prueba.class);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindActivasRetornaListaVacia() {
        when(em.createNamedQuery("Prueba.findActivas", Prueba.class)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Prueba> resultado = dao.findActivas(0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindActivasErrorInterno() {
        when(em.createNamedQuery("Prueba.findActivas", Prueba.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findActivas(0, 10));
    }

    @Test
    void testFindActivasEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findActivas(0, 10));
    }

    @Test
    void testFindByNombreParametroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre(null, 0, 10));
    }

    @Test
    void testFindByNombreParametroVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre("   ", 0, 10));
    }

    @Test
    void testFindByNombreFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre("Matematica", -1, 10));
    }


    @Test
    void testFindByNombreMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre("Matematica", 0, 0));
    }

    @Test
    void testFindByNombreParametrosValidos() {
        when(em.createNamedQuery("Prueba.findByNombre", Prueba.class)).thenReturn(query);
        when(query.setParameter("nombre", "Matematica")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaValida()));

        List<Prueba> resultado = dao.findByNombre("Matematica", 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("Prueba.findByNombre", Prueba.class);
        verify(query).setParameter("nombre", "Matematica");
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    /**
     * Verifica que el DAO aplica trim al parámetro nombre,
     * asegurando que la consulta reciba el valor sin espacios externos.
     */

    @Test
    void testFindByNombreAplicaTrimAlParametro() {
        when(em.createNamedQuery("Prueba.findByNombre", Prueba.class)).thenReturn(query);
        when(query.setParameter("nombre", "Matematica")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        dao.findByNombre("  Matematica  ", 0, 10);

        verify(query).setParameter("nombre", "Matematica");
    }

    @Test
    void testFindByNombreRetornaListaVacia() {
        when(em.createNamedQuery("Prueba.findByNombre", Prueba.class)).thenReturn(query);
        when(query.setParameter("nombre", "XYZ")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Prueba> resultado = dao.findByNombre("XYZ", 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByNombreErrorInterno() {
        when(em.createNamedQuery("Prueba.findByNombre", Prueba.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByNombre("Matematica", 0, 10));
    }

    @Test
    void testFindByNombreEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findByNombre("Matematica", 0, 10));
    }
}