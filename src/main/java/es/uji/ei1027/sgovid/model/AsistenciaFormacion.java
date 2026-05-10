package es.uji.ei1027.sgovid.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AsistenciaFormacion {
    private int idAsistencia;
    private Integer idUsuari;
    private Integer idActividad;
    private LocalDate fechaAsistencia;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private boolean certificadoEmitido;
    private String observaciones;

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public Integer getIdUsuari() {
        return idUsuari;
    }

    public void setIdUsuari(Integer idUsuari) {
        this.idUsuari = idUsuari;
    }

    public Integer getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Integer idActividad) {
        this.idActividad = idActividad;
    }

    public LocalDate getFechaAsistencia() {
        return fechaAsistencia;
    }

    public void setFechaAsistencia(LocalDate fechaAsistencia) {
        this.fechaAsistencia = fechaAsistencia;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public boolean isCertificadoEmitido() {
        return certificadoEmitido;
    }

    public void setCertificadoEmitido(boolean certificadoEmitido) {
        this.certificadoEmitido = certificadoEmitido;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "AsistenciaFormacion{" +
                "idAsistencia=" + idAsistencia +
                ", idUsuari=" + idUsuari +
                ", idActividad=" + idActividad +
                ", fechaAsistencia=" + fechaAsistencia +
                ", certificadoEmitido=" + certificadoEmitido +
                '}';
    }
}