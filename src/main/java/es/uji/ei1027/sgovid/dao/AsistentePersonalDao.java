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
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addAsistente(AsistentePersonal asistente) {
        jdbcTemplate.update(
                "INSERT INTO AsistentePersonal (dni, nombre, apellidos, email, telefono, edad, titulacion, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                asistente.getDni(), asistente.getNombre(), asistente.getApellidos(),
                asistente.getEmail(), asistente.getTelefono(), asistente.getEdad(),
                asistente.getTitulacion(), asistente.getEstado());
    }

    public void deleteAsistente(String dni) {
        jdbcTemplate.update("DELETE FROM AsistentePersonal WHERE dni = ?", dni);
    }

    public void updateAsistente(AsistentePersonal asistente) {
        jdbcTemplate.update(
                "UPDATE AsistentePersonal SET nombre=?, apellidos=?, email=?, telefono=?, edad=?, titulacion=?, estado=? WHERE dni=?",
                asistente.getNombre(), asistente.getApellidos(), asistente.getEmail(),
                asistente.getTelefono(), asistente.getEdad(), asistente.getTitulacion(),
                asistente.getEstado(), asistente.getDni());
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
}