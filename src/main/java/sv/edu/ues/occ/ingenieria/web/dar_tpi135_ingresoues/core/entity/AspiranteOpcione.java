package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

@Entity
@Table(name = "aspirante_opciones", schema = "public")
@NamedQueries({
        @NamedQuery(
                name = "AspiranteOpcione.buscarPorAspirante",
                query = "SELECT ao FROM AspiranteOpcione ao WHERE ao.idAspirante.id = :idAspirante"
        )
})
public class AspiranteOpcione {
    @Id
    @Column(name = "id_aspirante_opciones", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_aspirante", nullable = false)
    private Aspirante idAspirante;

    @Size(max = 20)
    @Column(name = "codigo_programa", length = 20)
    private String codigoPrograma;

    @Size(max = 250)
    @Column(name = "nombre_programa", length = 250)
    private String nombrePrograma;

    @Column(name = "orden_preferencia")
    private Short ordenPreferencia;

    @NotNull
    @Column(name = "fecha_seleccion", nullable = false)
    private OffsetDateTime fechaSeleccion;

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

    public String getCodigoPrograma() {
        return codigoPrograma;
    }

    public void setCodigoPrograma(String codigoPrograma) {
        this.codigoPrograma = codigoPrograma;
    }

    public String getNombrePrograma() {
        return nombrePrograma;
    }

    public void setNombrePrograma(String nombrePrograma) {
        this.nombrePrograma = nombrePrograma;
    }

    public Short getOrdenPreferencia() {
        return ordenPreferencia;
    }

    public void setOrdenPreferencia(Short ordenPreferencia) {
        this.ordenPreferencia = ordenPreferencia;
    }

    public OffsetDateTime getFechaSeleccion() {
        return fechaSeleccion;
    }

    public void setFechaSeleccion(OffsetDateTime fechaSeleccion) {
        this.fechaSeleccion = fechaSeleccion;
    }

}