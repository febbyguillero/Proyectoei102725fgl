package es.uji.ei1027.sgovid.dao;

import es.uji.ei1027.sgovid.model.ActividadFormacion;
import es.uji.ei1027.sgovid.rowmapper.ActividadFormacionRowMapper;
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
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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

    // ---- Listado con busqueda, ordenacion y paginacion (todo en el servidor) ----

    private String columnaOrden(String sort) {
        if (sort == null) return "id_actividad";
        switch (sort) {
            case "titulo": return "titulo";
            case "tipo":   return "tipo_actividad";
            case "inicio": return "fecha_inicio";
            case "estado": return "estado";
            case "id":     return "id_actividad";
            default:       return "id_actividad";
        }
    }

    private String direccionOrden(String dir) {
        return "desc".equalsIgnoreCase(dir) ? "DESC" : "ASC";
    }

    public List<ActividadFormacion> getActividades(String q, String sort, String dir, int page, int pageSize) {
        String like = "%" + (q == null ? "" : q.trim()) + "%";
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM actividad_formacion " +
                "WHERE (titulo ILIKE ? OR tipo_actividad ILIKE ? OR lugar ILIKE ?) " +
                "ORDER BY " + columnaOrden(sort) + " " + direccionOrden(dir) + " " +
                "LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new ActividadFormacionRowMapper(),
                    like, like, like, pageSize, offset);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public int countActividades(String q) {
        String like = "%" + (q == null ? "" : q.trim()) + "%";
        String sql = "SELECT COUNT(*) FROM actividad_formacion " +
                "WHERE (titulo ILIKE ? OR tipo_actividad ILIKE ? OR lugar ILIKE ?)";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, like, like, like);
        return total == null ? 0 : total;
    }
}
