package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import jakarta.json.bind.annotation.JsonbTransient;

@Entity
@Table(name = "turno", schema = "public")
@NamedQueries({
        @NamedQuery( name = "Turno.findByNameLike", query = "SELECT t FROM Turno t WHERE upper(t.nombreTurno) like :name")
})
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_turno", nullable = false)
    private UUID id;

    @Size(max = 150)
    @Column(name = "nombre_turno", length = 150)
    private String nombreTurno;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @OneToMany(mappedBy = "idTurno")
    private Set<TurnoJornada> turnoJornadas = new LinkedHashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombreTurno() {
        return nombreTurno;
    }

    public void setNombreTurno(String nombreTurno) {
        this.nombreTurno = nombreTurno;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @JsonbTransient
    public Set<TurnoJornada> getTurnoJornadas() {
        return turnoJornadas;
    }

    public void setTurnoJornadas(Set<TurnoJornada> turnoJornadas) {
        this.turnoJornadas = turnoJornadas;
    }
}