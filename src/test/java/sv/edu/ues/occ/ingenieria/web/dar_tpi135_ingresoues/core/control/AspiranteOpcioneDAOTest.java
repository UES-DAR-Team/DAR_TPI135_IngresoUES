package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AspiranteOpcioneDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<AspiranteOpcione> query;

    @InjectMocks
    private AspiranteOpcioneDAO dao;

    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
    }

    @Test
    void getEntityManager_debeRetornarEntityManager() {
        assertNotNull(dao.getEntityManager());
    }

    @Test
    void getEntityClass_debeRetornarClaseCorrecta() {
        assertEquals(AspiranteOpcione.class, dao.getEntityClass());
    }

    @Test
    void debeRetornarLista_siParametrosValidos() {

        List<AspiranteOpcione> listaEsperada = List.of(new AspiranteOpcione());

        when(em.createNamedQuery(anyString(), eq(AspiranteOpcione.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(listaEsperada);

        List<AspiranteOpcione> resultado = dao.buscarPorAspirante(id, 0, 10);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void retornaVacio_siIdNull() {
        List<AspiranteOpcione> resultado = dao.buscarPorAspirante(null, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo() {
        List<AspiranteOpcione> resultado = dao.buscarPorAspirante(id, -1, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void retornaVacio_siMaxInvalido() {
        List<AspiranteOpcione> resultado = dao.buscarPorAspirante(id, 0, 0);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery() {

        when(em.createNamedQuery(anyString(), eq(AspiranteOpcione.class)))
                .thenThrow(new RuntimeException("error"));

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorAspirante(id, 0, 10));
    }


    @Test
    void lanzaIllegalState_siFallaGetResultList() {

        when(em.createNamedQuery(anyString(), eq(AspiranteOpcione.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("error"));

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorAspirante(id, 0, 10));
    }
}