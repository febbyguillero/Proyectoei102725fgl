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

    //Creo que solucionado: sin id_request, así creo que la secuencia lo genera automáticamente
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

    //Solicitudes de un usuario concreto
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
}