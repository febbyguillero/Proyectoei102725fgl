package dao;

import model.ActividadFormacion;
import rowmapper.ActividadFormacionRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ActividadFormacionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addActividad(ActividadFormacion actividad) {
        jdbcTemplate.update(
                "INSERT INTO actividad_formacion (titulo, descripcion, tipo_actividad, fecha_inicio, fecha_fin, lugar, aforo_maximo, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                actividad.getTitulo(), actividad.getDescripcion(), actividad.getTipoActividad(),
                actividad.getFechaInicio(), actividad.getFechaFin(), actividad.getLugar(),
                actividad.getAforoMaximo(), actividad.getEstado());
    }

    public void deleteActividad(int idActividad) {
        jdbcTemplate.update("DELETE FROM actividad_formacion WHERE id_actividad = ?", idActividad);
    }

    public void updateActividad(ActividadFormacion actividad) {
        jdbcTemplate.update(
                "UPDATE actividad_formacion SET titulo=?, descripcion=?, tipo_actividad=?, fecha_inicio=?, fecha_fin=?, lugar=?, aforo_maximo=?, estado=? WHERE id_actividad=?",
                actividad.getTitulo(), actividad.getDescripcion(), actividad.getTipoActividad(),
                actividad.getFechaInicio(), actividad.getFechaFin(), actividad.getLugar(),
                actividad.getAforoMaximo(), actividad.getEstado(), actividad.getIdActividad());
    }

    public ActividadFormacion getActividad(int idActividad) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM actividad_formacion WHERE id_actividad = ?",
                    new ActividadFormacionRowMapper(), idActividad);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<ActividadFormacion> getActividades() {
        try {
            return jdbcTemplate.query("SELECT * FROM actividad_formacion ORDER BY id_actividad",
                    new ActividadFormacionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}