package es.uji.ei1027.sgovid.rowmapper;

import es.uji.ei1027.sgovid.model.UsuarioOVI;
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

        if (rs.getDate("data_naixement") != null) {
            usuario.setDataNaixement(rs.getDate("data_naixement").toLocalDate());
        }

        usuario.setConsentimentInformat(rs.getBoolean("consentiment_informat"));

        if (rs.getTimestamp("data_registre") != null) {
            usuario.setDataRegistre(rs.getTimestamp("data_registre").toLocalDateTime());
        }

        usuario.setEstatTecnicAcceptat(rs.getBoolean("estat_tecnic_acceptat"));
        usuario.setTutorLegalNom(rs.getString("tutor_legal_nom"));
        usuario.setTutorLegalContacte(rs.getString("tutor_legal_contacte"));

        return usuario;
    }
}