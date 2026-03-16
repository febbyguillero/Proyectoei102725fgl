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
                "INSERT INTO APRequest (id_request, usuari_ident, tipus_servei, estat, data_creacio, observations) VALUES (?, ?, ?, ?, ?, ?)",
                request.getIdRequest(), request.getUsuariIdent(), request.getTipusServei(),
                request.getEstat(), request.getDataCreacio(), request.getObservations());
    }

    public void deleteRequest(int idRequest) {
        jdbcTemplate.update("DELETE FROM APRequest WHERE id_request = ?", idRequest);
    }

    public void updateRequest(APRequest request) {
        jdbcTemplate.update(
                "UPDATE APRequest SET usuari_ident=?, tipus_servei=?, estat=?, data_creacio=?, observations=? WHERE id_request=?",
                request.getUsuariIdent(), request.getTipusServei(), request.getEstat(),
                request.getDataCreacio(), request.getObservations(), request.getIdRequest());
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
            return jdbcTemplate.query("SELECT * FROM APRequest ORDER BY id_request",
                    new APRequestRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}