package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "aspirante", schema = "public")
public class Aspirante {
    @Id
    @Column(name = "id_aspirante", nullable = false)
    private UUID id;

    @Size(max = 150)
    @NotNull
    @Column(name = "nombre_aspirante", nullable = false, length = 150)
    private String nombreAspirante;

    @Size(max = 150)
    @Column(name = "apellido_aspirante", length = 150)
    private String apellidoAspirante;

    @Size(max = 20)
    @Column(name = "identificacion", length = 20)
    private String identificacion;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @NotNull
    @Column(name = "fecha_registro", nullable = false)
    private OffsetDateTime fechaRegistro;

    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo = false;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombreAspirante() {
        return nombreAspirante;
    }

    public void setNombreAspirante(String nombreAspirante) {
        this.nombreAspirante = nombreAspirante;
    }

    public String getApellidoAspirante() {
        return apellidoAspirante;
    }

    public void setApellidoAspirante(String apellidoAspirante) {
        this.apellidoAspirante = apellidoAspirante;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public OffsetDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(OffsetDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

}