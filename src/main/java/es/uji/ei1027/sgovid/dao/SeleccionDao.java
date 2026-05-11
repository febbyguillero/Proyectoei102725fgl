package es.uji.ei1027.sgovid.dao;


import es.uji.ei1027.sgovid.model.Seleccion;
import es.uji.ei1027.sgovid.rowmapper.SeleccionRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SeleccionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Insertar una nueva selección
    public void addSeleccion(Seleccion seleccion) {
        jdbcTemplate.update(
                "INSERT INTO Seleccion (id_solicitud, id_candidato, fecha_propuesta, estado) VALUES (?, ?, NOW(), ?)",
                seleccion.getIdSolicitud(), seleccion.getIdCandidato(), seleccion.getEstado()
        );
    }

    // Obtener selecciones por solicitud
    public List<Seleccion> getSeleccionesPorSolicitud(int idSolicitud) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM Seleccion WHERE id_solicitud = ?",
                    new SeleccionRowMapper(), idSolicitud
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}