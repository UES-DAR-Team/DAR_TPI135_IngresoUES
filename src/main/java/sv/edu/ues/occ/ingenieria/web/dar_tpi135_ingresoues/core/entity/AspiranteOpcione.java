package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.Date;

@Entity
@Table(name = "aspirante_opciones", schema = "public")
@NamedQueries({
        @NamedQuery(
                name = "AspiranteOpcione.buscarPorAspirante",
                query = "SELECT ao FROM AspiranteOpcione ao WHERE ao.idAspirante.id = :idAspirante"
        ),
        @NamedQuery(
                name = "AspiranteOpcione.findByCodigoPrograma",
                query = "SELECT ao FROM AspiranteOpcione ao WHERE ao.codigoPrograma = :codigoPrograma"
        ),
        @NamedQuery(
                name = "AspiranteOpcione.findByNombrePrograma",
                query = "SELECT ao FROM AspiranteOpcione ao WHERE UPPER(ao.nombrePrograma) LIKE :nombrePrograma"
        ),

        @NamedQuery(
                name = "AspiranteOpcione.countByAspirante",
                query = "SELECT COUNT(ao) FROM AspiranteOpcione ao WHERE ao.idAspirante.id = :idAspirante"
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

    @NotNull
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")//para que funcione con JSON-B ya que no soporta Date, deberia usarse LocalDateTime
    @Temporal(TemporalType.TIMESTAMP) // Necesario para almacenar fecha y hora
    @Column(name = "fecha_seleccion", nullable = false)
    private Date fechaSeleccion;

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

    public Date getFechaSeleccion() {
        return fechaSeleccion;
    }

    public void setFechaSeleccion(Date fechaSeleccion) {
        this.fechaSeleccion = fechaSeleccion;
    }

}