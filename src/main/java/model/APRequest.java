package model;

import java.time.LocalDate;

public class APRequest {
    private int idRequest;
    private String usuariIdent;
    private String tipusServei;
    private String estat;
    private LocalDate dataCreacio;
    private String observations;

    public int getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(int idRequest) {
        this.idRequest = idRequest;
    }

    public String getUsuariIdent() {
        return usuariIdent;
    }

    public void setUsuariIdent(String usuariIdent) {
        this.usuariIdent = usuariIdent;
    }

    public String getTipusServei() {
        return tipusServei;
    }

    public void setTipusServei(String tipusServei) {
        this.tipusServei = tipusServei;
    }

    public String getEstat() {
        return estat;
    }

    public void setEstat(String estat) {
        this.estat = estat;
    }

    public LocalDate getDataCreacio() {
        return dataCreacio;
    }

    public void setDataCreacio(LocalDate dataCreacio) {
        this.dataCreacio = dataCreacio;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    @Override
    public String toString() {
        return "APRequest{" +
                "idRequest=" + idRequest +
                ", usuariIdent='" + usuariIdent + '\'' +
                ", tipusServei='" + tipusServei + '\'' +
                ", estat='" + estat + '\'' +
                ", dataCreacio=" + dataCreacio +
                '}';
    }
}