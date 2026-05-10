package es.uji.ei1027.sgovid.rowmapper;

import es.uji.ei1027.sgovid.model.RegistroContrato;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistroContratoRowMapper implements RowMapper<RegistroContrato> {

    @Override
    public RegistroContrato mapRow(ResultSet rs, int rowNum) throws SQLException {
        RegistroContrato contrato = new RegistroContrato();
        contrato.setIdContrato(rs.getInt("id_contrato"));
        contrato.setIdSolicitud(rs.getInt("id_solicitud"));
        contrato.setDniAsistente(rs.getString("dni_asistente"));
        contrato.setFechaInicio(rs.getDate("fecha_inicio") != null ?
                rs.getDate("fecha_inicio").toLocalDate() : null);
        contrato.setFechaFin(rs.getDate("fecha_fin") != null ?
                rs.getDate("fecha_fin").toLocalDate() : null);
        contrato.setDocumentoPdf(rs.getString("documento_pdf"));
        contrato.setFechaRegistro(rs.getTimestamp("fecha_registro") != null ?
                rs.getTimestamp("fecha_registro").toLocalDateTime() : null);
        contrato.setObservaciones(rs.getString("observaciones"));
        contrato.setEstadoContrato(rs.getString("estado_contrato"));
        return contrato;
    }
}