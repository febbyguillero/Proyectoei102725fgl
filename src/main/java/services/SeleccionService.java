package services;


import dao.AsistentePersonalDao;
import dao.APRequestDao;
import dao.SeleccionDao;
import dao.UsuarioOVIDao;
import model.APRequest;
import model.AsistentePersonal;
import model.Seleccion;
import model.UsuarioOVI;
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