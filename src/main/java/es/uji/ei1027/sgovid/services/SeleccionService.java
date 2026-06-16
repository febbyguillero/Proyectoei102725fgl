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

import java.util.ArrayList;
import java.util.List;

@Service
public class SeleccionService {

    @Autowired
    private AsistentePersonalDao asistenteDao;

    @Autowired
    private APRequestDao solicitudDao;

    @Autowired
    private SeleccionDao seleccionDao;

    @Autowired
    private UsuarioOVIDao usuarioDao;

    /**
     * Propone candidatos para una solicitud según la zona geográfica del usuario.
     * IMPORTANTE: este método es de SOLO LECTURA. No inserta selecciones ni cambia
     * el estado de la solicitud (eso se hace al asignar un candidato de forma explícita).
     */
    public List<AsistentePersonal> proponerCandidatos(int idSolicitud) {
        APRequest solicitud = solicitudDao.getRequest(idSolicitud);
        if (solicitud == null) return new ArrayList<>();

        UsuarioOVI usuario = resolverUsuario(solicitud.getUsuariIdent());
        String zona = (usuario != null) ? usuario.getZonaGeografica() : null;

        // getCandidatosAptos es null-safe: si no hay zona, devuelve todos los aceptados.
        return asistenteDao.getCandidatosAptos(zona);
    }

    /**
     * Resuelve el usuario a partir del identificador guardado en la solicitud.
     * Acepta tanto el id numérico (p.ej. "5") como el identificador SgOVI (p.ej. "USR005"),
     * sin lanzar NumberFormatException.
     */
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
