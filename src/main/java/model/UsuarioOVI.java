package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UsuarioOVI {
    private int idUsuari;
    private String identificadorSgovi;
    private String contrasenya;
    private String email;
    private String nom;
    private String cognoms;
    private String telefon;
    private String adreca;
    private String dni;
    private LocalDate dataNaixement;
    private boolean consentimentInformat;
    private LocalDateTime dataRegistre;
    private boolean estatTecnicAcceptat;
    private String tutorLegalNom;
    private String tutorLegalContacte;

    // Getters y Setters
    public int getIdUsuari() {
        return idUsuari;
    }

    public void setIdUsuari(int idUsuari) {
        this.idUsuari = idUsuari;
    }

    public String getIdentificadorSgovi() {
        return identificadorSgovi;
    }

    public void setIdentificadorSgovi(String identificadorSgovi) {
        this.identificadorSgovi = identificadorSgovi;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCognoms() {
        return cognoms;
    }

    public void setCognoms(String cognoms) {
        this.cognoms = cognoms;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getAdreca() {
        return adreca;
    }

    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getDataNaixement() {
        return dataNaixement;
    }

    public void setDataNaixement(LocalDate dataNaixement) {
        this.dataNaixement = dataNaixement;
    }

    public boolean isConsentimentInformat() {
        return consentimentInformat;
    }

    public void setConsentimentInformat(boolean consentimentInformat) {
        this.consentimentInformat = consentimentInformat;
    }

    public LocalDateTime getDataRegistre() {
        return dataRegistre;
    }

    public void setDataRegistre(LocalDateTime dataRegistre) {
        this.dataRegistre = dataRegistre;
    }

    public boolean isEstatTecnicAcceptat() {
        return estatTecnicAcceptat;
    }

    public void setEstatTecnicAcceptat(boolean estatTecnicAcceptat) {
        this.estatTecnicAcceptat = estatTecnicAcceptat;
    }

    public String getTutorLegalNom() {
        return tutorLegalNom;
    }

    public void setTutorLegalNom(String tutorLegalNom) {
        this.tutorLegalNom = tutorLegalNom;
    }

    public String getTutorLegalContacte() {
        return tutorLegalContacte;
    }

    public void setTutorLegalContacte(String tutorLegalContacte) {
        this.tutorLegalContacte = tutorLegalContacte;
    }

    @Override
    public String toString() {
        return "UsuarioOVI{" +
                "idUsuari=" + idUsuari +
                ", identificadorSgovi='" + identificadorSgovi + '\'' +
                ", email='" + email + '\'' +
                ", nom='" + nom + '\'' +
                ", cognoms='" + cognoms + '\'' +
                ", dni='" + dni + '\'' +
                '}';
    }
}