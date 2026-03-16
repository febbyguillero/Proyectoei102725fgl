package es.uji.ei1027.sgovid.dao;

import es.uji.ei1027.sgovid.model.UsuarioOVI;
import es.uji.ei1027.sgovid.rowmapper.UsuarioOVIRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioOVIDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addUsuario(UsuarioOVI usuario) {
        jdbcTemplate.update(
                "INSERT INTO UsuariOVI (identificador_sgovi, contrasenya, email, nom, cognoms, telefon, adreca, dni, data_naixement, consentiment_informat, estat_tecnic_acceptat, tutor_legal_nom, tutor_legal_contacte) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                usuario.getIdentificadorSgovi(), usuario.getContrasenya(), usuario.getEmail(),
                usuario.getNom(), usuario.getCognoms(), usuario.getTelefon(), usuario.getAdreca(),
                usuario.getDni(), usuario.getDataNaixement(), usuario.isConsentimentInformat(),
                usuario.isEstatTecnicAcceptat(), usuario.getTutorLegalNom(), usuario.getTutorLegalContacte());
    }

    public void deleteUsuario(int idUsuari) {
        jdbcTemplate.update("DELETE FROM UsuariOVI WHERE id_usuari = ?", idUsuari);
    }

    public void updateUsuario(UsuarioOVI usuario) {
        jdbcTemplate.update(
                "UPDATE UsuariOVI SET identificador_sgovi=?, contrasenya=?, email=?, nom=?, cognoms=?, telefon=?, adreca=?, dni=?, data_naixement=?, consentiment_informat=?, estat_tecnic_acceptat=?, tutor_legal_nom=?, tutor_legal_contacte=? WHERE id_usuari=?",
                usuario.getIdentificadorSgovi(), usuario.getContrasenya(), usuario.getEmail(),
                usuario.getNom(), usuario.getCognoms(), usuario.getTelefon(), usuario.getAdreca(),
                usuario.getDni(), usuario.getDataNaixement(), usuario.isConsentimentInformat(),
                usuario.isEstatTecnicAcceptat(), usuario.getTutorLegalNom(), usuario.getTutorLegalContacte(),
                usuario.getIdUsuari());
    }

    public UsuarioOVI getUsuario(int idUsuari) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM UsuariOVI WHERE id_usuari = ?",
                    new UsuarioOVIRowMapper(), idUsuari);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<UsuarioOVI> getUsuarios() {
        try {
            return jdbcTemplate.query("SELECT * FROM UsuariOVI ORDER BY id_usuari",
                    new UsuarioOVIRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}