package es.uji.ei1027.sgovid.dao;

import es.uji.ei1027.sgovid.model.AsistentePersonal;
import es.uji.ei1027.sgovid.rowmapper.AsistentePersonalRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AsistentePersonalDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addAsistente(AsistentePersonal asistente) {
        String estado = (asistente.getEstado() == null || asistente.getEstado().isEmpty())
                ? "PENDIENTE" : asistente.getEstado();
        jdbcTemplate.update(
                "INSERT INTO AsistentePersonal " +
                        "(dni, nombre, apellidos, email, telefono, edad, titulacion, estado, zona_geografica) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                asistente.getDni(), asistente.getNombre(), asistente.getApellidos(),
                asistente.getEmail(), asistente.getTelefono(), asistente.getEdad(),
                asistente.getTitulacion(), estado, asistente.getZonaGeografica());
    }

    public void deleteAsistente(String dni) {
        jdbcTemplate.update("DELETE FROM AsistentePersonal WHERE dni = ?", dni);
    }

    public void updateAsistente(AsistentePersonal asistente) {
        jdbcTemplate.update(
                "UPDATE AsistentePersonal SET nombre=?, apellidos=?, email=?, telefono=?, edad=?, " +
                        "titulacion=?, estado=?, zona_geografica=? WHERE dni=?",
                asistente.getNombre(), asistente.getApellidos(), asistente.getEmail(),
                asistente.getTelefono(), asistente.getEdad(), asistente.getTitulacion(),
                asistente.getEstado(), asistente.getZonaGeografica(),
                asistente.getDni());
    }

    public AsistentePersonal getAsistente(String dni) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM AsistentePersonal WHERE dni = ?",
                    new AsistentePersonalRowMapper(), dni);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<AsistentePersonal> getAsistentes() {
        try {
            return jdbcTemplate.query("SELECT * FROM AsistentePersonal ORDER BY dni",
                    new AsistentePersonalRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // ---- Listado con busqueda, ordenacion y paginacion (todo en el servidor) ----

    private String columnaOrden(String sort) {
        if (sort == null) return "dni";
        switch (sort) {
            case "dni":     return "dni";
            case "nombre":  return "nombre";
            case "email":   return "email";
            case "zona":    return "zona_geografica";
            case "estado":  return "estado";
            default:        return "dni";
        }
    }

    private String direccionOrden(String dir) {
        return "desc".equalsIgnoreCase(dir) ? "DESC" : "ASC";
    }

    public List<AsistentePersonal> getAsistentes(String q, String sort, String dir, int page, int pageSize) {
        String like = "%" + (q == null ? "" : q.trim()) + "%";
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM AsistentePersonal " +
                "WHERE (dni ILIKE ? OR nombre ILIKE ? OR apellidos ILIKE ? OR email ILIKE ? OR zona_geografica ILIKE ?) " +
                "ORDER BY " + columnaOrden(sort) + " " + direccionOrden(dir) + " " +
                "LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new AsistentePersonalRowMapper(),
                    like, like, like, like, like, pageSize, offset);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public int countAsistentes(String q) {
        String like = "%" + (q == null ? "" : q.trim()) + "%";
        String sql = "SELECT COUNT(*) FROM AsistentePersonal " +
                "WHERE (dni ILIKE ? OR nombre ILIKE ? OR apellidos ILIKE ? OR email ILIKE ? OR zona_geografica ILIKE ?)";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, like, like, like, like, like);
        return total == null ? 0 : total;
    }

    // Candidatos ACEPTADOS, opcionalmente filtrados por zona geografica.
    public List<AsistentePersonal> getCandidatosAptos(String zona) {
        try {
            if (zona == null || zona.trim().isEmpty()) {
                return jdbcTemplate.query(
                        "SELECT * FROM AsistentePersonal WHERE estado = 'ACEPTADO' ORDER BY dni",
                        new AsistentePersonalRowMapper());
            }
            return jdbcTemplate.query(
                    "SELECT * FROM AsistentePersonal " +
                            "WHERE estado = 'ACEPTADO' AND zona_geografica ILIKE ? ORDER BY dni",
                    new AsistentePersonalRowMapper(),
                    "%" + zona.trim() + "%");
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
