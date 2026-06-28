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
        String franja = solicitud.getFranjaHoraria(); // "Mañana", "Tarde", "Noche"

        List<AsistentePersonal> todos = asistenteDao.getCandidatosAptos(null);

        // 1r filtre: zona geogràfica
        List<AsistentePersonal> perZona = new ArrayList<>();
        if (zona != null && !zona.trim().isEmpty()) {
            String zonaNorm = normalizar(zona);
            for (AsistentePersonal a : todos) {
                String zonaAsistNorm = normalizar(a.getZonaGeografica());
                if (zonaAsistNorm.contains(zonaNorm) || zonaNorm.contains(zonaAsistNorm)) {
                    perZona.add(a);
                }
            }
        }
        // Si no hi ha zona o cap coincidència, agafem tots
        List<AsistentePersonal> candidatsByZona = perZona.isEmpty() ? todos : perZona;

        // 2n filtre: disponibilitat horària (franja de la sol·licitud vs disponibilitat del candidat)
        // Si el candidat no té disponibilitat registrada, l'incloem igualment.
        if (franja == null || franja.trim().isEmpty()) {
            return candidatsByZona;
        }

        String franjaNorm = normalizar(franja);
        List<AsistentePersonal> perDisponibilitat = new ArrayList<>();
        List<AsistentePersonal> senseDispo = new ArrayList<>();

        for (AsistentePersonal a : candidatsByZona) {
            if (a.getDisponibilidad() == null || a.getDisponibilidad().trim().isEmpty()) {
                senseDispo.add(a); // sense disponibilitat registrada → l'incloem al final
            } else {
                String dispNorm = normalizar(a.getDisponibilidad());
                if (dispNorm.contains(franjaNorm) || franjaNorm.contains(dispNorm)) {
                    perDisponibilitat.add(a);
                }
            }
        }

        // Primer els que coincideixen, després els que no tenen dispo registrada
        List<AsistentePersonal> resultat = new ArrayList<>();
        resultat.addAll(perDisponibilitat);
        resultat.addAll(senseDispo);

        // Si cap coincideix per disponibilitat, tornem tots els de la zona
        return resultat.isEmpty() ? candidatsByZona : resultat;
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