package model;

import java.time.LocalDateTime;

public class ComunicacionUsuarioViPAP {
    private int idComunicacion;
    private Integer idSolicitud;
    private String dniAsistente;
    private LocalDateTime fechaComunicacion;
    private String tipoComunicacion;
    private String direccion;
    private String resumen;
    private String observaciones;

    public int getIdComunicacion() {
        return idComunicacion;
    }

    public void setIdComunicacion(int idComunicacion) {
        this.idComunicacion = idComunicacion;
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

    public LocalDateTime getFechaComunicacion() {
        return fechaComunicacion;
    }

    public void setFechaComunicacion(LocalDateTime fechaComunicacion) {
        this.fechaComunicacion = fechaComunicacion;
    }

    public String getTipoComunicacion() {
        return tipoComunicacion;
    }

    public void setTipoComunicacion(String tipoComunicacion) {
        this.tipoComunicacion = tipoComunicacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "ComunicacionUsuarioViPAP{" +
                "idComunicacion=" + idComunicacion +
                ", tipoComunicacion='" + tipoComunicacion + '\'' +
                ", direccion='" + direccion + '\'' +
                ", fechaComunicacion=" + fechaComunicacion +
                '}';
    }
}