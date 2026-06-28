package es.uji.ei1027.sgovid.dao;

import es.uji.ei1027.sgovid.model.APRequest;
import es.uji.ei1027.sgovid.rowmapper.APRequestRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class APRequestDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addRequest(APRequest request) {
        jdbcTemplate.update(
                "INSERT INTO APRequest (usuari_ident, tipus_servei, estat, data_creacio, observations, dies, franja_horaria) VALUES (?, ?, ?, ?, ?, ?, ?)",
                request.getUsuariIdent(), request.getTipusServei(), request.getEstat(),
                request.getDataCreacio(), request.getObservations(),
                request.getDies(), request.getFranjaHoraria());
    }

    public void deleteRequest(int idRequest) {
        jdbcTemplate.update("DELETE FROM APRequest WHERE id_request = ?", idRequest);
    }

    public void updateRequest(APRequest request) {
        jdbcTemplate.update(
                "UPDATE APRequest SET usuari_ident=?, tipus_servei=?, estat=?, data_creacio=?, observations=?, dies=?, franja_horaria=? WHERE id_request=?",
                request.getUsuariIdent(), request.getTipusServei(), request.getEstat(),
                request.getDataCreacio(), request.getObservations(),
                request.getDies(), request.getFranjaHoraria(), request.getIdRequest());
    }

    public APRequest getRequest(int idRequest) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM APRequest WHERE id_request = ?",
                    new APRequestRowMapper(), idRequest);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<APRequest> getRequests() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM APRequest ORDER BY data_creacio DESC",
                    new APRequestRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<APRequest> getRequestsByUsuari(String usuariIdent) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM APRequest WHERE usuari_ident = ? ORDER BY data_creacio DESC",
                    new APRequestRowMapper(), usuariIdent);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void cambiarEstado(int idRequest, String nuevoEstado) {
        jdbcTemplate.update(
                "UPDATE APRequest SET estat = ? WHERE id_request = ?",
                nuevoEstado, idRequest);
    }


    private String columnaOrden(String sort) {
        if (sort == null) return "APRequest.id_request";
        switch (sort) {
            case "id":      return "APRequest.id_request";
            case "usuario": return "APRequest.usuari_ident";
            case "tipo":    return "APRequest.tipus_servei";
            case "fecha":   return "APRequest.data_creacio";
            case "estado":  return "APRequest.estat";
            default:        return "APRequest.id_request";
        }
    }

    private String direccionOrden(String dir) {
        return "desc".equalsIgnoreCase(dir) ? "DESC" : "ASC";
    }

    private static final String FROM_JOIN =
            "FROM APRequest " +
                    "LEFT JOIN UsuariOVI ON APRequest.usuari_ident = CAST(UsuariOVI.id_usuari AS VARCHAR) ";

    private StringBuilder construirFiltros(String q, String estado, String tipo, List<Object> params) {
        StringBuilder sql = new StringBuilder(" WHERE 1=1 ");
        if (q != null && !q.trim().isEmpty()) {
            sql.append("AND (UsuariOVI.nom ILIKE ? OR UsuariOVI.cognoms ILIKE ? " +
                    "OR APRequest.tipus_servei ILIKE ? OR APRequest.observations ILIKE ?) ");
            String like = "%" + q.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (estado != null && !estado.trim().isEmpty()) {
            sql.append("AND APRequest.estat = ? ");
            params.add(estado.trim());
        }
        if (tipo != null && !tipo.trim().isEmpty()) {
            sql.append("AND APRequest.tipus_servei = ? ");
            params.add(tipo.trim());
        }
        return sql;
    }

    public List<APRequest> getRequests(String q, String estado, String tipo,
                                       String sort, String dir, int page, int pageSize) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT APRequest.* ");
        sql.append(FROM_JOIN);
        sql.append(construirFiltros(q, estado, tipo, params));
        sql.append("ORDER BY ").append(columnaOrden(sort)).append(" ").append(direccionOrden(dir)).append(" ");
        sql.append("LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        try {
            return jdbcTemplate.query(sql.toString(), new APRequestRowMapper(), params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public int countRequests(String q, String estado, String tipo) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) ");
        sql.append(FROM_JOIN);
        sql.append(construirFiltros(q, estado, tipo, params));
        Integer total = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return total == null ? 0 : total;
    }

    public List<APRequest> getRequestsByUsuari(String usuariIdent, String q,
                                               String sort, String dir,
                                               int page, int pageSize) {
        String like = "%" + (q == null ? "" : q.trim()) + "%";
        int offset = (page - 1) * pageSize;
        String col = "asc".equalsIgnoreCase(dir) ? "ASC" : "DESC";
        String sql = "SELECT * FROM APRequest " +
                "WHERE usuari_ident = ? " +
                "AND (tipus_servei ILIKE ? OR estat ILIKE ? OR dies ILIKE ?) " +
                "ORDER BY data_creacio " + col + " LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, new APRequestRowMapper(),
                    usuariIdent, like, like, like, pageSize, offset);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public int countRequestsByUsuari(String usuariIdent, String q) {
        String like = "%" + (q == null ? "" : q.trim()) + "%";
        String sql = "SELECT COUNT(*) FROM APRequest " +
                "WHERE usuari_ident = ? " +
                "AND (tipus_servei ILIKE ? OR estat ILIKE ? OR dies ILIKE ?)";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class,
                usuariIdent, like, like, like);
        return total == null ? 0 : total;
    }
}
