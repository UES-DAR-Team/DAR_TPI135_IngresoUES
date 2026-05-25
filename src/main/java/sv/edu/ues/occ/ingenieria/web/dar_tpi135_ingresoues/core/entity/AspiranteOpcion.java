package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@Table(name = "aspirante_opcion", schema = "public")
@NamedQueries({
        @NamedQuery(name = "AspiranteOpcion.findByIdAspirante",
                query = "SELECT ao FROM AspiranteOpcion ao WHERE ao.idAspirante.id = :idAspirante"),
        @NamedQuery(name = "AspiranteOpcion.findByIdOpcion",
                query = "SELECT ao FROM AspiranteOpcion ao WHERE ao.idOpcion.id = :idOpcion")
})
public class AspiranteOpcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aspirante_opcion", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_opcion", nullable = false)
    private Opcion idOpcion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_aspirante", nullable = false)
    private Aspirante idAspirante;

    @NotNull
    @Column(name = "fecha_seleccion", nullable = false)
    private OffsetDateTime fechaSeleccion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Opcion getIdOpcion() {
        return idOpcion;
    }

    public void setIdOpcion(Opcion idOpcion) {
        this.idOpcion = idOpcion;
    }

    public Aspirante getIdAspirante() {
        return idAspirante;
    }

    public void setIdAspirante(Aspirante idAspirante) {
        this.idAspirante = idAspirante;
    }

    public OffsetDateTime getFechaSeleccion() {
        return fechaSeleccion;
    }

    public void setFechaSeleccion(OffsetDateTime fechaSeleccion) {
        this.fechaSeleccion = fechaSeleccion;
    }

}