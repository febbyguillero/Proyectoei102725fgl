package rowmapper;

import model.APRequest;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class APRequestRowMapper implements RowMapper<APRequest> {

    @Override
    public APRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        APRequest request = new APRequest();
        request.setIdRequest(rs.getInt("id_request"));
        request.setUsuariIdent(rs.getString("usuari_ident"));
        request.setTipusServei(rs.getString("tipus_servei"));
        request.setEstat(rs.getString("estat"));
        request.setDataCreacio(rs.getDate("data_creacio") != null ?
                rs.getDate("data_creacio").toLocalDate() : null);
        request.setObservations(rs.getString("observations"));
        return request;
    }
}