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

    // Obtenir totes les comunicacions d'una sol·licitud concreta
    public List<ComunicacionUsuarioViPAP> getComunicacionesBySolicitud(int idSolicitud) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM comunicacionusuariovipap WHERE id_solicitud = ? ORDER BY fecha_comunicacion DESC",
                    new ComunicacionUsuarioViPAPRowMapper(), idSolicitud);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // Registrar una nova comunicació
    public void addComunicacion(ComunicacionUsuarioViPAP comunicacion) {
        if (comunicacion.getFechaComunicacion() == null) {
            comunicacion.setFechaComunicacion(LocalDateTime.now());
        }
        jdbcTemplate.update(
                "INSERT INTO comunicacionusuariovipap " +
                        "(id_solicitud, dni_asistente, fecha_comunicacion, tipo_comunicacion, direccion, resumen, observaciones) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                comunicacion.getIdSolicitud(),
                comunicacion.getDniAsistente(),
                comunicacion.getFechaComunicacion(),
                comunicacion.getTipoComunicacion(),
                comunicacion.getDireccion(),
                comunicacion.getResumen(),
                comunicacion.getObservaciones());
    }

    // Eliminar una comunicació
    public void deleteComunicacion(int idComunicacion) {
        jdbcTemplate.update(
                "DELETE FROM comunicacionusuariovipap WHERE id_comunicacion = ?",
                idComunicacion);
    }
}
