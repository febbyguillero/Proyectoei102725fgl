package rowmapper;

import model.Seleccion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SeleccionRowMapper implements RowMapper<Seleccion> {

    @Override
    public Seleccion mapRow(ResultSet rs, int rowNum) throws SQLException {
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(rs.getInt("id_seleccion"));
        seleccion.setIdSolicitud(rs.getInt("id_solicitud"));
        seleccion.setIdCandidato(rs.getString("id_candidato"));
        seleccion.setFechaPropuesta(rs.getTimestamp("fecha_propuesta") != null ?
                rs.getTimestamp("fecha_propuesta").toLocalDateTime() : null);
        seleccion.setEstado(rs.getString("estado"));
        seleccion.setObservaciones(rs.getString("observaciones"));
        seleccion.setFechaRespuesta(rs.getTimestamp("fecha_respuesta") != null ?
                rs.getTimestamp("fecha_respuesta").toLocalDateTime() : null);
        return seleccion;
    }
}