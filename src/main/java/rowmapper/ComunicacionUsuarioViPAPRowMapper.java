package rowmapper;

import model.ComunicacionUsuarioViPAP;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ComunicacionUsuarioViPAPRowMapper implements RowMapper<ComunicacionUsuarioViPAP> {

    @Override
    public ComunicacionUsuarioViPAP mapRow(ResultSet rs, int rowNum) throws SQLException {
        ComunicacionUsuarioViPAP comunicacion = new ComunicacionUsuarioViPAP();
        comunicacion.setIdComunicacion(rs.getInt("id_comunicacion"));
        comunicacion.setIdSolicitud(rs.getInt("id_solicitud"));
        comunicacion.setDniAsistente(rs.getString("dni_asistente"));
        comunicacion.setFechaComunicacion(rs.getTimestamp("fecha_comunicacion") != null ?
                rs.getTimestamp("fecha_comunicacion").toLocalDateTime() : null);
        comunicacion.setTipoComunicacion(rs.getString("tipo_comunicacion"));
        comunicacion.setDireccion(rs.getString("direccion"));
        comunicacion.setResumen(rs.getString("resumen"));
        comunicacion.setObservaciones(rs.getString("observaciones"));
        return comunicacion;
    }
}