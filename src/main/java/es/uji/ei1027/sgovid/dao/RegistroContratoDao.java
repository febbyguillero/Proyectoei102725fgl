package es.uji.ei1027.sgovid.dao;

import es.uji.ei1027.sgovid.model.RegistroContrato;
import es.uji.ei1027.sgovid.rowmapper.RegistroContratoRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Repository
public class RegistroContratoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void addContrato(RegistroContrato contrato) {
        if (contrato.getFechaRegistro() == null) {
            contrato.setFechaRegistro(LocalDateTime.now());
        }
        jdbcTemplate.update(
                "INSERT INTO registrocontrato " +
                        "(id_solicitud, dni_asistente, fecha_inicio, fecha_fin, documento_pdf, fecha_registro, observaciones, estado_contrato) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                contrato.getIdSolicitud(),
                contrato.getDniAsistente(),
                contrato.getFechaInicio(),
                contrato.getFechaFin(),
                contrato.getDocumentoPdf(),
                contrato.getFechaRegistro(),
                contrato.getObservaciones(),
                contrato.getEstadoContrato());
    }

    public void updateContrato(RegistroContrato contrato) {
        jdbcTemplate.update(
                "UPDATE registrocontrato SET id_solicitud=?, dni_asistente=?, fecha_inicio=?, fecha_fin=?, " +
                        "documento_pdf=?, fecha_registro=?, observaciones=?, estado_contrato=? WHERE id_contrato=?",
                contrato.getIdSolicitud(),
                contrato.getDniAsistente(),
                contrato.getFechaInicio(),
                contrato.getFechaFin(),
                contrato.getDocumentoPdf(),
                contrato.getFechaRegistro(),
                contrato.getObservaciones(),
                contrato.getEstadoContrato(),
                contrato.getIdContrato());
    }

    public void deleteContrato(int idContrato) {
        jdbcTemplate.update("DELETE FROM registrocontrato WHERE id_contrato = ?", idContrato);
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
            List<RegistroContrato> contratos = jdbcTemplate.query(
                    "SELECT * FROM registrocontrato WHERE id_solicitud = ? ORDER BY id_contrato ASC",
                    new RegistroContratoRowMapper(), idSolicitud);
            return contratos.isEmpty() ? null : contratos.get(0);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<RegistroContrato> getContratos() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM registrocontrato ORDER BY id_contrato DESC",
                    new RegistroContratoRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    private String columnaOrden(String sort) {
        if (sort == null) return "id_contrato";
        switch (sort) {
            case "id":        return "id_contrato";
            case "solicitud": return "id_solicitud";
            case "dni":       return "dni_asistente";
            case "inicio":    return "fecha_inicio";
            case "fin":       return "fecha_fin";
            case "estado":    return "estado_contrato";
            default:          return "id_contrato";
        }
    }

    private String direccionOrden(String dir) {
        return "desc".equalsIgnoreCase(dir) ? "DESC" : "ASC";
    }

    private StringBuilder construirFiltros(String q, List<Object> params) {
        StringBuilder sql = new StringBuilder(" WHERE 1=1 ");
        if (q != null && !q.trim().isEmpty()) {
            sql.append("AND (dni_asistente ILIKE ? OR observaciones ILIKE ? OR estado_contrato ILIKE ? OR CAST(id_solicitud AS TEXT) ILIKE ?) ");
            String like = "%" + q.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        return sql;
    }

    public List<RegistroContrato> getContratos(String q, String sort, String dir, int page, int pageSize) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM registrocontrato");
        sql.append(construirFiltros(q, params));
        sql.append("ORDER BY ").append(columnaOrden(sort)).append(" ").append(direccionOrden(dir)).append(" ");
        sql.append("LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        try {
            return jdbcTemplate.query(sql.toString(), new RegistroContratoRowMapper(), params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public int countContratos(String q) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM registrocontrato");
        sql.append(construirFiltros(q, params));
        Integer total = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return total == null ? 0 : total;
    }
}
