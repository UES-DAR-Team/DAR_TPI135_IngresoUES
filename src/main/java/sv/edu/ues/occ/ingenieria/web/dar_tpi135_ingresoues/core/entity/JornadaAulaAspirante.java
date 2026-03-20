package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "jornada_aula_aspirante", schema = "public")
@NamedQueries({
        @NamedQuery(
                name = "JornadaAulaAspirante.buscarPorJornadaAula",
                query = "SELECT jaa FROM JornadaAulaAspirante jaa WHERE jaa.idJornadaAula.id = :idJornadaAula"
        ),
        @NamedQuery(
                name = "JornadaAulaAspirante.buscarPorAspirantePrueba",
                query = "SELECT jaa FROM JornadaAulaAspirante jaa WHERE jaa.idAspirantePrueba.id = :idAspirantePrueba"
        ),
        @NamedQuery(
                name = "JornadaAulaAspirante.buscarPorAsistencia",
                query = "SELECT jaa FROM JornadaAulaAspirante jaa WHERE jaa.asistio = :asistio"
        )
})
public class JornadaAulaAspirante {
    @Id
    @Column(name = "id_jornada_aula_aspirante", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_jornada_aula", nullable = false)
    private JornadaAula idJornadaAula;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_aspirante_prueba", nullable = false)
    private AspirantePrueba idAspirantePrueba;

    @Column(name = "hora_llegada")
    private LocalTime horaLlegada;

    @NotNull
    @Column(name = "asistio", nullable = false)
    private Boolean asistio = false;

    @NotNull
    @Column(name = "fecha_asignacion", nullable = false)
    private OffsetDateTime fechaAsignacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JornadaAula getIdJornadaAula() {
        return idJornadaAula;
    }

    public void setIdJornadaAula(JornadaAula idJornadaAula) {
        this.idJornadaAula = idJornadaAula;
    }

    public AspirantePrueba getIdAspirantePrueba() {
        return idAspirantePrueba;
    }

    public void setIdAspirantePrueba(AspirantePrueba idAspirantePrueba) {
        this.idAspirantePrueba = idAspirantePrueba;
    }

    public LocalTime getHoraLlegada() {
        return horaLlegada;
    }

    public void setHoraLlegada(LocalTime horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    public Boolean getAsistio() {
        return asistio;
    }

    public void setAsistio(Boolean asistio) {
        this.asistio = asistio;
    }

    public OffsetDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(OffsetDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

}