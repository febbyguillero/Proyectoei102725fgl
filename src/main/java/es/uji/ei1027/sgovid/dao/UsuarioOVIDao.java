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

    public List<UsuarioOVI> getUsuarios() {
        try {
            String sql = "SELECT * FROM UsuariOVI ORDER BY id_usuari";
            return jdbcTemplate.query(sql, new UsuarioOVIRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    private String columnaOrden(String sort) {
        if (sort == null) return "id_usuari";
        switch (sort) {
            case "nom":      return "nom";
            case "cognoms":  return "cognoms";
            case "dni":      return "dni";
            case "email":    return "email";
            case "id":       return "id_usuari";
            default:         return "id_usuari";
        }
    }

    private String direccionOrden(String dir) {
        return "desc".equalsIgnoreCase(dir) ? "DESC" : "ASC";
    }

    public List<UsuarioOVI> getUsuarios(String q, String sort, String dir, int page, int pageSize) {
        String like = "%" + (q == null ? "" : q.trim()) + "%";
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM UsuariOVI " +
                "WHERE (nom ILIKE ? OR cognoms ILIKE ? OR dni ILIKE ? OR email ILIKE ?) " +
                "ORDER BY " + columnaOrden(sort) + " " + direccionOrden(dir) + " " +
                "LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new UsuarioOVIRowMapper(),
                    like, like, like, like, pageSize, offset);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public int countUsuarios(String q) {
        String like = "%" + (q == null ? "" : q.trim()) + "%";
        String sql = "SELECT COUNT(*) FROM UsuariOVI " +
                "WHERE (nom ILIKE ? OR cognoms ILIKE ? OR dni ILIKE ? OR email ILIKE ?)";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, like, like, like, like);
        return total == null ? 0 : total;
    }

    public UsuarioOVI getUsuario(int idUsuari) {
        try {
            String sql = "SELECT * FROM UsuariOVI WHERE id_usuari = ?";
            return jdbcTemplate.queryForObject(sql, new UsuarioOVIRowMapper(), idUsuari);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UsuarioOVI getUsuarioByIdentificador(String identificador) {
        try {
            String sql = "SELECT * FROM UsuariOVI WHERE identificador_sgovi = ?";
            return jdbcTemplate.queryForObject(sql, new UsuarioOVIRowMapper(), identificador);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void addUsuario(UsuarioOVI usuario) {
        String sql = "INSERT INTO UsuariOVI (identificador_sgovi, contrasenya, email, nom, cognoms, " +
                "telefon, adreca, dni, data_naixement, consentiment_informat, estat_tecnic_acceptat, " +
                "tutor_legal_nom, tutor_legal_contacte, zona_geografica) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                usuario.getZonaGeografica()
        );
    }

    public void updateUsuario(UsuarioOVI usuario) {
        String sql = "UPDATE UsuariOVI SET identificador_sgovi=?, contrasenya=?, email=?, nom=?, " +
                "cognoms=?, telefon=?, adreca=?, dni=?, data_naixement=?, consentiment_informat=?, " +
                "estat_tecnic_acceptat=?, tutor_legal_nom=?, tutor_legal_contacte=?, zona_geografica=? " +
                "WHERE id_usuari=?";
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
                usuario.getZonaGeografica(),
                usuario.getIdUsuari()
        );
    }

    public void deleteUsuario(int idUsuari) {
        String sql = "DELETE FROM UsuariOVI WHERE id_usuari = ?";
        jdbcTemplate.update(sql, idUsuari);
    }

    public void setEstatTecnicAcceptat(int idUsuari, boolean acceptat) {
        jdbcTemplate.update(
                "UPDATE UsuariOVI SET estat_tecnic_acceptat = ? WHERE id_usuari = ?",
                acceptat, idUsuari);
    }
}
