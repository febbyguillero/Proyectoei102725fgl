package es.uji.ei1027.sgovid.rowmapper;

import es.uji.ei1027.sgovid.model.ActividadFormacion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActividadFormacionRowMapper implements RowMapper<ActividadFormacion> {

    @Override
    public ActividadFormacion mapRow(ResultSet rs, int rowNum) throws SQLException {
        ActividadFormacion actividad = new ActividadFormacion();
        actividad.setIdActividad(rs.getInt("id_actividad"));
        actividad.setTitulo(rs.getString("titulo"));
        actividad.setDescripcion(rs.getString("descripcion"));
        actividad.setTipoActividad(rs.getString("tipo_actividad"));
        actividad.setFechaInicio(rs.getTimestamp("fecha_inicio") != null ?
                rs.getTimestamp("fecha_inicio").toLocalDateTime() : null);
        actividad.setFechaFin(rs.getTimestamp("fecha_fin") != null ?
                rs.getTimestamp("fecha_fin").toLocalDateTime() : null);
        actividad.setLugar(rs.getString("lugar"));
        actividad.setAforoMaximo(rs.getInt("aforo_maximo"));
        actividad.setEstado(rs.getString("estado"));
        return actividad;
    }
}