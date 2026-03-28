package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@Table(name = "prueba_jornada", schema = "public")
@NamedQueries({
        @NamedQuery(name = "PruebaJornada.findByPrueba", query = "SELECT pj FROM PruebaJornada pj WHERE pj.idPrueba.id = :idPrueba"),
        @NamedQuery(name = "PruebaJornada.findByJornada", query = "SELECT pj FROM PruebaJornada pj WHERE pj.idJornada.id = :idJornada"),
        @NamedQuery(name = "PruebaJornada.findByPruebaAndJornada", query = "SELECT pj FROM PruebaJornada pj WHERE pj.idPrueba.id = :idPrueba AND pj.idJornada.id = :idJornada")
})
public class PruebaJornada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prueba_jornada", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_prueba", nullable = false)
    private Prueba idPrueba;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_jornada", nullable = false)
    private Jornada idJornada;

    @NotNull
    @Column(name = "fecha_asignacion", nullable = false)
    private OffsetDateTime fechaAsignacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Prueba getIdPrueba() {
        return idPrueba;
    }

    public void setIdPrueba(Prueba idPrueba) {
        this.idPrueba = idPrueba;
    }

    public Jornada getIdJornada() {
        return idJornada;
    }

    public void setIdJornada(Jornada idJornada) {
        this.idJornada = idJornada;
    }

    public OffsetDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(OffsetDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

}