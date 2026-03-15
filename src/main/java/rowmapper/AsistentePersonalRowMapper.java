package rowmapper;

import model.AsistentePersonal;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AsistentePersonalRowMapper implements RowMapper<AsistentePersonal> {

    @Override
    public AsistentePersonal mapRow(ResultSet rs, int rowNum) throws SQLException {
        AsistentePersonal asistente = new AsistentePersonal();
        asistente.setDni(rs.getString("dni"));
        asistente.setNombre(rs.getString("nombre"));
        asistente.setApellidos(rs.getString("apellidos"));
        asistente.setEmail(rs.getString("email"));
        asistente.setTelefono(rs.getString("telefono"));
        asistente.setEdad(rs.getInt("edad"));
        asistente.setTitulacion(rs.getString("titulacion"));
        asistente.setEstado(rs.getBoolean("estado"));
        return asistente;
    }
}