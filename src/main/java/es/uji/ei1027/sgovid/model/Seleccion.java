package es.uji.ei1027.sgovid.model;

import java.time.LocalDateTime;

public class Seleccion {
    private int idSeleccion;
    private int idSolicitud;
    private String idCandidato;
    private LocalDateTime fechaPropuesta;
    private String estado;
    private String observaciones;
    private LocalDateTime fechaRespuesta;


    public int getIdSeleccion() {
        return idSeleccion;
    }

    public void setIdSeleccion(int idSeleccion) {
        this.idSeleccion = idSeleccion;
    }

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getIdCandidato() {
        return idCandidato;
    }

    public void setIdCandidato(String idCandidato) {
        this.idCandidato = idCandidato;
    }

    public LocalDateTime getFechaPropuesta() {
        return fechaPropuesta;
    }

    public void setFechaPropuesta(LocalDateTime fechaPropuesta) {
        this.fechaPropuesta = fechaPropuesta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    @Override
    public String toString() {
        return "Seleccion{" +
                "idSeleccion=" + idSeleccion +
                ", idSolicitud=" + idSolicitud +
                ", idCandidato='" + idCandidato + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}