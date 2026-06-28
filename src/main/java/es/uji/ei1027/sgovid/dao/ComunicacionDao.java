package es.uji.ei1027.sgovid.dao;

import es.uji.ei1027.sgovid.model.ComunicacionUsuarioViPAP;
import es.uji.ei1027.sgovid.rowmapper.ComunicacionUsuarioViPAPRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ComunicacionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Obtenir totes les comunicacions d'una sol·licitud (ordre cronològic)
    public List<ComunicacionUsuarioViPAP> getComunicacionesBySolicitud(int idSolicitud) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM comunicacionusuariovipap WHERE id_solicitud = ? ORDER BY fecha_comunicacion ASC",
                    new ComunicacionUsuarioViPAPRowMapper(), idSolicitud);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // Comptar missatges d'una sol·licitud enviats per una direcció concreta.
    // direccion = 'saliente' (tècnic) o 'entrante' (usuari).
    // S'usa per mostrar avís de missatges pendents a l'altra part.
    public int countMensajesByDireccion(int idSolicitud, String direccion) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM comunicacionusuariovipap WHERE id_solicitud = ? AND direccion = ?",
                    Integer.class, idSolicitud, direccion);
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // Registrar un nou missatge
    public void addComunicacion(ComunicacionUsuarioViPAP comunicacion) {
        if (comunicacion.getFechaComunicacion() == null) {
            comunicacion.setFechaComunicacion(LocalDateTime.now());
        }
        jdbcTemplate.update(
                "INSERT INTO comunicacionusuariovipap " +
                        "(id_solicitud, fecha_comunicacion, tipo_comunicacion, direccion, resumen) " +
                        "VALUES (?, ?, ?, ?, ?)",
                comunicacion.getIdSolicitud(),
                comunicacion.getFechaComunicacion(),
                comunicacion.getTipoComunicacion(),
                comunicacion.getDireccion(),
                comunicacion.getResumen());
    }

    // Eliminar un missatge (operació reservada al tècnic)
    public void deleteComunicacion(int idComunicacion) {
        jdbcTemplate.update(
                "DELETE FROM comunicacionusuariovipap WHERE id_comunicacion = ?",
                idComunicacion);
    }
}