package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@Table(name = "aspirante_prueba", schema = "public")
@NamedQueries({
        @NamedQuery(
                name = "AspirantePrueba.buscarPorAspirante",
                query = "SELECT ap FROM AspirantePrueba ap WHERE ap.idAspirante.id = :idAspirante"
        ),
        @NamedQuery(
                name = "AspirantePrueba.buscarPorPrueba",
                query = "SELECT ap FROM AspirantePrueba ap WHERE ap.idPrueba.id = :idPrueba"
        ),
        @NamedQuery(
                name = "AspirantePrueba.countByAspirante",
                query = "SELECT COUNT(ap) FROM AspirantePrueba ap WHERE ap.idAspirante.id = :idAspirante"
        )
})
public class AspirantePrueba {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aspirante_prueba", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_aspirante", nullable = false)
    private Aspirante idAspirante;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_prueba", nullable = false)
    private Prueba idPrueba;

    @NotNull
    @Column(name = "fecha_asignacion", nullable = false)
    private OffsetDateTime fechaAsignacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Aspirante getIdAspirante() {
        return idAspirante;
    }

    public void setIdAspirante(Aspirante idAspirante) {
        this.idAspirante = idAspirante;
    }

    public Prueba getIdPrueba() {
        return idPrueba;
    }

    public void setIdPrueba(Prueba idPrueba) {
        this.idPrueba = idPrueba;
    }

    public OffsetDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(OffsetDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

}