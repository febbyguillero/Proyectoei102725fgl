package es.uji.ei1027.sgovid.dao;

import es.uji.ei1027.sgovid.model.RegistroContrato;
import es.uji.ei1027.sgovid.rowmapper.RegistroContratoRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RegistroContratoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addContrato(RegistroContrato contrato) {
        jdbcTemplate.update(
                "INSERT INTO registrocontrato (id_solicitud, dni_asistente, fecha_inicio, fecha_fin, observaciones, estado_contrato) VALUES (?, ?, ?, ?, ?, ?)",
                contrato.getIdSolicitud(), contrato.getDniAsistente(),
                contrato.getFechaInicio(), contrato.getFechaFin(),
                contrato.getObservaciones(), contrato.getEstadoContrato());
    }

    public void updateContrato(RegistroContrato contrato) {
        jdbcTemplate.update(
                "UPDATE registrocontrato SET fecha_fin=?, observaciones=?, estado_contrato=? WHERE id_contrato=?",
                contrato.getFechaFin(), contrato.getObservaciones(),
                contrato.getEstadoContrato(), contrato.getIdContrato());
    }

    public RegistroContrato getContrato(int idContrato) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM registrocontrato WHERE id_contrato = ?",
                    new RegistroContratoRowMapper(), idContrato);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public RegistroContrato getContratoBySolicitud(int idSolicitud) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM registrocontrato WHERE id_solicitud = ? ORDER BY id_contrato DESC LIMIT 1",
                    new RegistroContratoRowMapper(), idSolicitud);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<RegistroContrato> getContratos() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM registrocontrato ORDER BY fecha_registro DESC",
                    new RegistroContratoRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}