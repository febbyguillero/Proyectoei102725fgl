package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.APRequestDao;
import es.uji.ei1027.sgovid.dao.AsistentePersonalDao;
import es.uji.ei1027.sgovid.dao.RegistroContratoDao;
import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.APRequest;
import es.uji.ei1027.sgovid.model.AsistentePersonal;
import es.uji.ei1027.sgovid.model.RegistroContrato;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import es.uji.ei1027.sgovid.services.SeleccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tecnico")
public class TecnicoController {

    @Autowired private APRequestDao solicitudDao;
    @Autowired private AsistentePersonalDao asistenteDao;
    @Autowired private UsuarioOVIDao usuarioDao;
    @Autowired private SeleccionService seleccionService;
    @Autowired private RegistroContratoDao contratoDao;

    @RequestMapping("/panel")
    public String panel() {
        return "tecnico/panel";
    }

    //solicitudes

    @RequestMapping("/solicitudes")
    public String listarSolicitudes(Model model) {
        model.addAttribute("solicitudes", solicitudDao.getRequests());
        return "tecnico/solicitudes";
    }

    @RequestMapping("/solicitud/{id}")
    public String verSolicitud(@PathVariable int id, Model model) {
        APRequest solicitud = solicitudDao.getRequest(id);
        if (solicitud == null) return "redirect:/tecnico/solicitudes";
        model.addAttribute("solicitud", solicitud);

        //Buscar nombre del usuario
        try {
            UsuarioOVI usuario = usuarioDao.getUsuario(Integer.parseInt(solicitud.getUsuariIdent()));
            if (usuario != null) model.addAttribute("nombreUsuario", usuario.getNom() + " " + usuario.getCognoms());
        } catch (Exception ignored) {}

        if ("PENDIENTE".equals(solicitud.getEstat())) {
            List<AsistentePersonal> candidatos = seleccionService.proponerCandidatos(id);
            model.addAttribute("candidatosPropuestos", candidatos);
        } else if (!"RECHAZADA".equals(solicitud.getEstat())) {
            // Ya tiene candidatos propuestos, cargarlos
            List<AsistentePersonal> candidatos = asistenteDao.getAsistentes();
            model.addAttribute("candidatosPropuestos", candidatos);
        }

        // Si ya hay contrato, cargarlo
        RegistroContrato contrato = contratoDao.getContratoBySolicitud(id);
        if (contrato != null) model.addAttribute("contrato", contrato);

        return "tecnico/solicitud-detalle";
    }

    @RequestMapping("/solicitud/{id}/asignar/{dni}")
    public String asignarCandidato(@PathVariable int id, @PathVariable String dni) {
        // Crear contrato
        RegistroContrato contrato = new RegistroContrato();
        contrato.setIdSolicitud(id);
        contrato.setDniAsistente(dni);
        contrato.setFechaInicio(LocalDate.now());
        contrato.setEstadoContrato("activo");
        contrato.setObservaciones("Contrato generado automáticamente al asignar candidato");
        contratoDao.addContrato(contrato);

        // Cambiar estado solicitud
        solicitudDao.cambiarEstado(id, "APROBADA");
        return "redirect:/tecnico/solicitud/" + id;
    }

    @RequestMapping("/solicitud/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable int id) {
        solicitudDao.cambiarEstado(id, "RECHAZADA");
        return "redirect:/tecnico/solicitudes";
    }

    //asistentes

    @RequestMapping("/asistentes-pendientes")
    public String listarAsistentesPendientes(Model model) {
        model.addAttribute("asistentes", asistenteDao.getAsistentes());
        return "tecnico/asistentes-pendientes";
    }

    @RequestMapping("/aceptar-asistente/{dni}")
    public String aceptarAsistente(@PathVariable String dni) {
        AsistentePersonal a = asistenteDao.getAsistente(dni);
        if (a != null) {
            a.setEstado(true);
            asistenteDao.updateAsistente(a);
        }
        return "redirect:/tecnico/asistentes-pendientes";
    }

    @RequestMapping("/rechazar-asistente/{dni}")
    public String rechazarAsistente(@PathVariable String dni) {
        AsistentePersonal a = asistenteDao.getAsistente(dni);
        if (a != null) {
            a.setEstado(false);
            asistenteDao.deleteAsistente(dni);
        }
        return "redirect:/tecnico/asistentes-pendientes";
    }

    //usuarios

    @RequestMapping("/usuarios-pendientes")
    public String listarUsuariosPendientes(Model model) {
        model.addAttribute("usuarios", usuarioDao.getUsuarios());
        return "tecnico/usuarios-pendientes";
    }

    @RequestMapping("/aceptar-usuario/{id}")
    public String aceptarUsuario(@PathVariable int id) {
        UsuarioOVI u = usuarioDao.getUsuario(id);
        if (u != null) {
            u.setEstatTecnicAcceptat(true);
            usuarioDao.updateUsuario(u);
        }
        return "redirect:/tecnico/usuarios-pendientes";
    }

    @RequestMapping("/rechazar-usuario/{id}")
    public String rechazarUsuario(@PathVariable int id) {
        usuarioDao.deleteUsuario(id);
        return "redirect:/tecnico/usuarios-pendientes";
    }

    //cobtratos

    @RequestMapping("/contratos")
    public String listarContratos(Model model) {
        model.addAttribute("contratos", contratoDao.getContratos());
        return "tecnico/contratos";
    }

    @RequestMapping(value = "/contrato/{id}/editar", method = RequestMethod.GET)
    public String editarContrato(@PathVariable int id, Model model) {
        RegistroContrato contrato = contratoDao.getContrato(id);
        if (contrato == null) return "redirect:/tecnico/contratos";
        model.addAttribute("contrato", contrato);
        return "tecnico/contrato-editar";
    }

    @RequestMapping(value = "/contrato/actualizar", method = RequestMethod.POST)
    public String actualizarContrato(@ModelAttribute("contrato") RegistroContrato contrato) {
        contratoDao.updateContrato(contrato);
        return "redirect:/tecnico/contratos";
    }

    @RequestMapping("/contrato/{id}/cerrar")
    public String cerrarContrato(@PathVariable int id) {
        RegistroContrato contrato = contratoDao.getContrato(id);
        if (contrato != null) {
            contrato.setEstadoContrato("finalizado");
            contrato.setFechaFin(LocalDate.now());
            contratoDao.updateContrato(contrato);
        }
        return "redirect:/tecnico/contratos";
    }
}