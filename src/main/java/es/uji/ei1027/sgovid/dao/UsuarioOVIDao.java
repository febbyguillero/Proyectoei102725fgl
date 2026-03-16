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
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // LISTAR TODOS LOS USUARIOS
    public List<UsuarioOVI> getUsuarios() {
        try {
            String sql = "SELECT * FROM UsuariOVI ORDER BY id_usuari";
            return jdbcTemplate.query(sql, new UsuarioOVIRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // OBTENER USUARIO POR ID
    public UsuarioOVI getUsuario(int idUsuari) {
        try {
            String sql = "SELECT * FROM UsuariOVI WHERE id_usuari = ?";
            return jdbcTemplate.queryForObject(sql, new UsuarioOVIRowMapper(), idUsuari);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // AÑADIR USUARIO
    public void addUsuario(UsuarioOVI usuario) {
        String sql = "INSERT INTO UsuariOVI (identificador_sgovi, contrasenya, email, nom, cognoms, telefon, adreca, dni, data_naixement, consentiment_informat, estat_tecnic_acceptat, tutor_legal_nom, tutor_legal_contacte) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                usuario.getIdentificadorSgovi(),
                usuario.getContrasenya(),
                usuario.getEmail(),
                usuario.getNom(),
                usuario.getCognoms(),
                usuario.getTelefon(),
                usuario.getAdreca(),
                usuario.getDni(),
                usuario.getDataNaixement(),
                usuario.isConsentimentInformat(),
                usuario.isEstatTecnicAcceptat(),
                usuario.getTutorLegalNom(),
                usuario.getTutorLegalContacte()
        );
    }

    // ACTUALIZAR USUARIO
    public void updateUsuario(UsuarioOVI usuario) {
        String sql = "UPDATE UsuariOVI SET identificador_sgovi=?, contrasenya=?, email=?, nom=?, cognoms=?, telefon=?, adreca=?, dni=?, data_naixement=?, consentiment_informat=?, estat_tecnic_acceptat=?, tutor_legal_nom=?, tutor_legal_contacte=? WHERE id_usuari=?";

        jdbcTemplate.update(sql,
                usuario.getIdentificadorSgovi(),
                usuario.getContrasenya(),
                usuario.getEmail(),
                usuario.getNom(),
                usuario.getCognoms(),
                usuario.getTelefon(),
                usuario.getAdreca(),
                usuario.getDni(),
                usuario.getDataNaixement(),
                usuario.isConsentimentInformat(),
                usuario.isEstatTecnicAcceptat(),
                usuario.getTutorLegalNom(),
                usuario.getTutorLegalContacte(),
                usuario.getIdUsuari()
        );
    }

    // ELIMINAR USUARIO
    public void deleteUsuario(int idUsuari) {
        String sql = "DELETE FROM UsuariOVI WHERE id_usuari = ?";
        jdbcTemplate.update(sql, idUsuari);
    }
}