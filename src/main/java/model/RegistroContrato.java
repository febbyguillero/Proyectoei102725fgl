package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RegistroContrato {
    private int idContrato;
    private Integer idSolicitud;
    private String dniAsistente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String documentoPdf;
    private LocalDateTime fechaRegistro;
    private String observaciones;
    private String estadoContrato;

    public int getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(int idContrato) {
        this.idContrato = idContrato;
    }

    public Integer getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Integer idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getDniAsistente() {
        return dniAsistente;
    }

    public void setDniAsistente(String dniAsistente) {
        this.dniAsistente = dniAsistente;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDocumentoPdf() {
        return documentoPdf;
    }

    public void setDocumentoPdf(String documentoPdf) {
        this.documentoPdf = documentoPdf;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstadoContrato() {
        return estadoContrato;
    }

    public void setEstadoContrato(String estadoContrato) {
        this.estadoContrato = estadoContrato;
    }

    @Override
    public String toString() {
        return "RegistroContrato{" +
                "idContrato=" + idContrato +
                ", dniAsistente='" + dniAsistente + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", estadoContrato='" + estadoContrato + '\'' +
                '}';
    }
}