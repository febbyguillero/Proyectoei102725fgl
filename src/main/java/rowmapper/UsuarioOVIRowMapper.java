package rowmapper;

import model.UsuarioOVI;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioOVIRowMapper implements RowMapper<UsuarioOVI> {

    @Override
    public UsuarioOVI mapRow(ResultSet rs, int rowNum) throws SQLException {
        UsuarioOVI usuario = new UsuarioOVI();
        usuario.setIdUsuari(rs.getInt("id_usuari"));
        usuario.setIdentificadorSgovi(rs.getString("identificador_sgovi"));
        usuario.setContrasenya(rs.getString("contrasenya"));
        usuario.setEmail(rs.getString("email"));
        usuario.setNom(rs.getString("nom"));
        usuario.setCognoms(rs.getString("cognoms"));
        usuario.setTelefon(rs.getString("telefon"));
        usuario.setAdreca(rs.getString("adreca"));
        usuario.setDni(rs.getString("dni"));
        usuario.setDataNaixement(rs.getDate("data_naixement") != null ?
                rs.getDate("data_naixement").toLocalDate() : null);
        usuario.setConsentimentInformat(rs.getBoolean("consentiment_informat"));
        usuario.setDataRegistre(rs.getTimestamp("data_registre") != null ?
                rs.getTimestamp("data_registre").toLocalDateTime() : null);
        usuario.setEstatTecnicAcceptat(rs.getBoolean("estat_tecnic_acceptat"));
        usuario.setTutorLegalNom(rs.getString("tutor_legal_nom"));
        usuario.setTutorLegalContacte(rs.getString("tutor_legal_contacte"));
        return usuario;
    }
}