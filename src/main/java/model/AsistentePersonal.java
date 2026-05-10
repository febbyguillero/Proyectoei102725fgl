package model;

public class AsistentePersonal {
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private Integer edad;
    private String titulacion;
    private Boolean estado;
    private String zonaGeografica;
    private String disponibilidad;
    private String formacion;
    private String experiencia;

    public String getZonaGeografica() { return zonaGeografica; }
    public void setZonaGeografica(String zonaGeografica) { this.zonaGeografica = zonaGeografica; }

    public String getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }

    public String getFormacion() { return formacion; }
    public void setFormacion(String formacion) { this.formacion = formacion; }

    public String getExperiencia() { return experiencia; }
    public void setExperiencia(String experiencia) { this.experiencia = experiencia; }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getTitulacion() {
        return titulacion;
    }

    public void setTitulacion(String titulacion) {
        this.titulacion = titulacion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "AsistentePersonal{" +
                "dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", estado=" + estado +
                '}';
    }
}