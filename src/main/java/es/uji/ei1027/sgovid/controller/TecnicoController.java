package es.uji.ei1027.sgovid.controller;


import es.uji.ei1027.sgovid.dao.APRequestDao;
import es.uji.ei1027.sgovid.dao.AsistentePersonalDao;
import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.APRequest;
import es.uji.ei1027.sgovid.model.AsistentePersonal;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import es.uji.ei1027.sgovid.services.SeleccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tecnico")
public class TecnicoController {

    @Autowired
    private APRequestDao solicitudDao;

    @Autowired
    private AsistentePersonalDao asistenteDao;

    @Autowired
    private UsuarioOVIDao usuarioDao;

    @Autowired
    private SeleccionService seleccionService;

    // Panel principal del técnico
    @RequestMapping("/panel")
    public String panel() {
        return "tecnico/panel";
    }

    // Listar solicitudes
    @RequestMapping("/solicitudes")
    public String listarSolicitudes(Model model) {
        model.addAttribute("solicitudes", solicitudDao.getRequests());
        return "tecnico/solicitudes";
    }

    // Ver detalle de solicitud y generar propuesta
    @RequestMapping("/solicitud/{id}")
    public String verSolicitud(@PathVariable int id, Model model) {
        APRequest solicitud = solicitudDao.getRequest(id);
        model.addAttribute("solicitud", solicitud);

        if ("PENDIENTE".equals(solicitud.getEstat())) {
            List<AsistentePersonal> candidatos = seleccionService.proponerCandidatos(id);
            model.addAttribute("candidatosPropuestos", candidatos);
            model.addAttribute("mensaje", "Se han propuesto " + candidatos.size() + " candidatos.");
        }

        return "tecnico/solicitud-detalle";
    }

    // Listar asistentes pendientes de aceptación
    @RequestMapping("/asistentes-pendientes")
    public String listarAsistentesPendientes(Model model) {
        model.addAttribute("asistentes", asistenteDao.getAsistentes());
        return "tecnico/asistentes-pendientes";
    }

    // Aceptar un asistente
    @RequestMapping("/aceptar-asistente/{dni}")
    public String aceptarAsistente(@PathVariable String dni) {
        AsistentePersonal a = asistenteDao.getAsistente(dni);
        if (a != null) {
            a.setEstado(true);
            asistenteDao.updateAsistente(a);
        }
        return "redirect:/tecnico/asistentes-pendientes";
    }

    // Listar usuarios pendientes de aceptación
    @RequestMapping("/usuarios-pendientes")
    public String listarUsuariosPendientes(Model model) {
        model.addAttribute("usuarios", usuarioDao.getUsuarios());
        return "tecnico/usuarios-pendientes";
    }

    // Aceptar un usuario
    @RequestMapping("/aceptar-usuario/{id}")
    public String aceptarUsuario(@PathVariable int id) {
        UsuarioOVI u = usuarioDao.getUsuario(id);
        if (u != null) {
            u.setEstatTecnicAcceptat(true);
            usuarioDao.updateUsuario(u);
        }
        return "redirect:/tecnico/usuarios-pendientes";
    }
}