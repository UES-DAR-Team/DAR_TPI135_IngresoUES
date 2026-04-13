package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aula;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AulaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Aula> query;

    @InjectMocks
    AulaDAO dao;

    private AulaDAO daoConEntityNulo() {
        return new AulaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

    private Aula aulaValida() {
        Aula a = new Aula();
        a.setId(UUID.randomUUID());
        a.setNombreAula("Aula 1");
        a.setCapacidad(30);
        a.setFechaCreacion(OffsetDateTime.now());
        a.setActivo(true);
        return a;
    }

    @Test
    void testFindByCapacidadMinParametroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByCapacidadMin(null, 0, 10));
    }

    @Test
    void testFindByCapacidadMinFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByCapacidadMin(10, -1, 10));
    }

    @Test
    void testFindByCapacidadMinMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByCapacidadMin(10, 0, 0));
    }

    @Test
    void testFindByCapacidadMinParametrosValidos() {
        when(em.createNamedQuery("Aula.findByCapacidadMin", Aula.class)).thenReturn(query);
        when(query.setParameter("capacidad", 10)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(aulaValida()));

        List<Aula> resultado = dao.findByCapacidadMin(10, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("Aula.findByCapacidadMin", Aula.class);
        verify(query).setParameter("capacidad", 10);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByCapacidadMinRetornaListaVacia() {
        when(em.createNamedQuery("Aula.findByCapacidadMin", Aula.class)).thenReturn(query);
        when(query.setParameter("capacidad", 99)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Aula> resultado = dao.findByCapacidadMin(99, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByCapacidadMinErrorInterno() {
        when(em.createNamedQuery("Aula.findByCapacidadMin", Aula.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByCapacidadMin(10, 0, 10));
    }

    @Test
    void testFindByCapacidadMinEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findByCapacidadMin(10, 0, 10));
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
                () -> dao.findByNombre("Aula1", -1, 10));
    }

    @Test
    void testFindByNombreMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre("Aula1", 0, 0));
    }

    @Test
    void testFindByNombreParametrosValidos() {
        when(em.createNamedQuery("Aula.findByNombre", Aula.class)).thenReturn(query);
        when(query.setParameter("nombre", "Aula1")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(aulaValida()));

        List<Aula> resultado = dao.findByNombre("Aula1", 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("Aula.findByNombre", Aula.class);
        verify(query).setParameter("nombre", "Aula1");
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByNombreAplicaTrimAlParametro() {
        when(em.createNamedQuery("Aula.findByNombre", Aula.class)).thenReturn(query);
        when(query.setParameter("nombre", "Aula1")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        dao.findByNombre("  Aula1  ", 0, 10);

        verify(query).setParameter("nombre", "Aula1");
    }

    @Test
    void testFindByNombreSinResultados() {
        when(em.createNamedQuery("Aula.findByNombre", Aula.class)).thenReturn(query);
        when(query.setParameter("nombre", "XYZ")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Aula> resultado = dao.findByNombre("XYZ", 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByNombreErrorInterno() {
        when(em.createNamedQuery("Aula.findByNombre", Aula.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByNombre("Aula1", 0, 10));
    }

    @Test
    void testFindByNombreEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findByNombre("Aula1", 0, 10));
    }

    @Test
    void testFindActivosFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findActivos(-1, 10));
    }

    @Test
    void testFindActivosMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findActivos(0, 0));
    }

    @Test
    void testFindActivosParametrosValidos() {
        when(em.createNamedQuery("Aula.findActivos", Aula.class)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(aulaValida()));

        List<Aula> resultado = dao.findActivos(0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("Aula.findActivos", Aula.class);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindActivosRetornaListaVacia() {
        when(em.createNamedQuery("Aula.findActivos", Aula.class)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Aula> resultado = dao.findActivos(0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindActivosErrorInterno() {
        when(em.createNamedQuery("Aula.findActivos", Aula.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findActivos(0, 10));
    }


    @Test
    void testFindActivosEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findActivos(0, 10));
    }
}