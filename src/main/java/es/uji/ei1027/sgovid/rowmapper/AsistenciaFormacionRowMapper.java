package es.uji.ei1027.sgovid.rowmapper;

import es.uji.ei1027.sgovid.model.AsistenciaFormacion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AsistenciaFormacionRowMapper implements RowMapper<AsistenciaFormacion> {

    @Override
    public AsistenciaFormacion mapRow(ResultSet rs, int rowNum) throws SQLException {
        AsistenciaFormacion asistencia = new AsistenciaFormacion();
        asistencia.setIdAsistencia(rs.getInt("id_asistencia"));
        asistencia.setIdUsuari(rs.getInt("id_usuari"));
        asistencia.setIdActividad(rs.getInt("id_actividad"));
        asistencia.setFechaAsistencia(rs.getDate("fecha_asistencia") != null ?
                rs.getDate("fecha_asistencia").toLocalDate() : null);
        asistencia.setHoraEntrada(rs.getTime("hora_entrada") != null ?
                rs.getTime("hora_entrada").toLocalTime() : null);
        asistencia.setHoraSalida(rs.getTime("hora_salida") != null ?
                rs.getTime("hora_salida").toLocalTime() : null);
        asistencia.setCertificadoEmitido(rs.getBoolean("certificado_emitido"));
        asistencia.setObservaciones(rs.getString("observaciones"));
        return asistencia;
    }
}