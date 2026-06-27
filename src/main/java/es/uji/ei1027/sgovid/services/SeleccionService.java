package es.uji.ei1027.sgovid.services;

import es.uji.ei1027.sgovid.dao.AsistentePersonalDao;
import es.uji.ei1027.sgovid.dao.APRequestDao;
import es.uji.ei1027.sgovid.dao.SeleccionDao;
import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.APRequest;
import es.uji.ei1027.sgovid.model.AsistentePersonal;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeleccionService {

    @Autowired private AsistentePersonalDao asistenteDao;
    @Autowired private APRequestDao solicitudDao;
    @Autowired private SeleccionDao seleccionDao;
    @Autowired private UsuarioOVIDao usuarioDao;

    public List<AsistentePersonal> proponerCandidatos(int idSolicitud) {
        APRequest solicitud = solicitudDao.getRequest(idSolicitud);
        if (solicitud == null) return new ArrayList<>();

        UsuarioOVI usuario = resolverUsuario(solicitud.getUsuariIdent());
        String zona = (usuario != null) ? usuario.getZonaGeografica() : null;

        List<AsistentePersonal> todos = asistenteDao.getCandidatosAptos(null);

        if (zona == null || zona.trim().isEmpty()) return todos;

        String zonaNorm = normalizar(zona);
        List<AsistentePersonal> filtrats = new ArrayList<>();
        for (AsistentePersonal a : todos) {
            String zonaAsistNorm = normalizar(a.getZonaGeografica());
            if (zonaAsistNorm.contains(zonaNorm) || zonaNorm.contains(zonaAsistNorm)) {
                filtrats.add(a);
            }
        }

        return filtrats.isEmpty() ? todos : filtrats;
    }

    private String normalizar(String s) {
        if (s == null) return "";
        String descomposta = Normalizer.normalize(s.toLowerCase().trim(), Normalizer.Form.NFD);
        return descomposta.replaceAll("\\p{M}", "");
    }

    private UsuarioOVI resolverUsuario(String ident) {
        if (ident == null || ident.trim().isEmpty()) return null;
        String s = ident.trim();
        try {
            return usuarioDao.getUsuario(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            for (UsuarioOVI u : usuarioDao.getUsuarios()) {
                if (s.equals(u.getIdentificadorSgovi())) return u;
            }
            return null;
        }
    }
}
