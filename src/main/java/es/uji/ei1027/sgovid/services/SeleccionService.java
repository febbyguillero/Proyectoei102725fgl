package es.uji.ei1027.sgovid.services;


import es.uji.ei1027.sgovid.dao.AsistentePersonalDao;
import es.uji.ei1027.sgovid.dao.APRequestDao;
import es.uji.ei1027.sgovid.dao.SeleccionDao;
import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.APRequest;
import es.uji.ei1027.sgovid.model.AsistentePersonal;
import es.uji.ei1027.sgovid.model.Seleccion;
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

    public List<AsistentePersonal> proponerCandidatos(int idSolicitud) {
        // 1. Obtener la solicitud
        APRequest solicitud = solicitudDao.getRequest(idSolicitud);
        if (solicitud == null) return new ArrayList<>();

        // 2. Obtener el usuario que hizo la solicitud
        UsuarioOVI usuario = usuarioDao.getUsuario(
                Integer.parseInt(solicitud.getUsuariIdent())
        );
        if (usuario == null) return new ArrayList<>();

        // 3. Buscar candidatos aceptados en la misma zona
        List<AsistentePersonal> candidatos = asistenteDao
                .getCandidatosAptos(usuario.getZonaGeografica());

        // 4. Guardar propuesta para cada candidato apto
        for (AsistentePersonal c : candidatos) {
            Seleccion s = new Seleccion();
            s.setIdSolicitud(idSolicitud);
            s.setIdCandidato(c.getDni());
            s.setEstado("PROPUESTO");
            seleccionDao.addSeleccion(s);
        }

        // 5. Cambiar estado de la solicitud a APROBADA
        solicitudDao.cambiarEstado(idSolicitud, "APROBADA");

        return candidatos;
    }
}