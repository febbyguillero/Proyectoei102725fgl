package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.APRequestDao;
import es.uji.ei1027.sgovid.dao.AsistentePersonalDao;
import es.uji.ei1027.sgovid.dao.RegistroContratoDao;
import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.APRequest;
import es.uji.ei1027.sgovid.model.AsistentePersonal;
import es.uji.ei1027.sgovid.model.RegistroContrato;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    @Autowired private APRequestDao apRequestDao;
    @Autowired private UsuarioOVIDao usuarioDao;
    @Autowired private RegistroContratoDao contratoDao;
    @Autowired private AsistentePersonalDao asistenteDao;

    private String comprobarRolUsuario(HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (rol == null) return "redirect:/login";
        if (!"USUARIO".equals(rol)) return "redirect:/tecnico/panel";
        return null;
    }

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model, HttpSession session) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;
        model.addAttribute("solicitud", new APRequest());
        return "solicitudes/nueva-peticion";
    }

    @PostMapping("/nueva")
    public String guardarPeticion(@ModelAttribute("solicitud") APRequest solicitud,
                                  HttpSession session, Model model) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;

        if (solicitud.getTipusServei() == null || solicitud.getTipusServei().isEmpty()) {
            model.addAttribute("errorTipus", "El tipus de servei és obligatori");
            return "solicitudes/nueva-peticion";
        }

        Object usuariId = session.getAttribute("usuariId");
        solicitud.setUsuariIdent(usuariId != null ? usuariId.toString() : "1");
        solicitud.setEstat("PENDIENTE");
        solicitud.setDataCreacio(LocalDate.now());
        if (solicitud.getDies() == null) solicitud.setDies("");

        apRequestDao.addRequest(solicitud);
        return "redirect:/solicitudes/mis-solicitudes";
    }

    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(Model model, HttpSession session) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;
        Object usuariId = session.getAttribute("usuariId");
        List<APRequest> solicitudes = apRequestDao.getRequestsByUsuari(usuariId.toString());
        model.addAttribute("solicitudes", solicitudes);
        return "solicitudes/mis-solicitudes";
    }

    @GetMapping("/detalle/{id}")
    public String detalleSolicitud(@PathVariable int id, Model model, HttpSession session) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;
        Object usuariId = session.getAttribute("usuariId");

        APRequest solicitud = apRequestDao.getRequest(id);
        if (solicitud == null) return "redirect:/solicitudes/mis-solicitudes";
        if (!solicitud.getUsuariIdent().equals(usuariId.toString())) {
            return "redirect:/solicitudes/mis-solicitudes";
        }
        model.addAttribute("solicitud", solicitud);

        RegistroContrato contrato = contratoDao.getContratoBySolicitud(id);
        if (contrato != null) {
            model.addAttribute("contrato", contrato);
            // Nombre del asistente para mostrar en lugar del DNI
            if (contrato.getDniAsistente() != null) {
                AsistentePersonal a = asistenteDao.getAsistente(contrato.getDniAsistente());
                if (a != null) model.addAttribute("nombreAsistente", a.getNombre() + " " + a.getApellidos());
            }
        }
        return "solicitudes/detalle_solicitud";
    }

    @GetMapping("/mi-perfil")
    public String miPerfil(Model model, HttpSession session) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;
        Object usuariId = session.getAttribute("usuariId");
        UsuarioOVI usuario = null;
        try {
            usuario = usuarioDao.getUsuario(Integer.parseInt(usuariId.toString()));
        } catch (NumberFormatException e) {
            return "redirect:/";
        }
        if (usuario == null) return "redirect:/solicitudes/mis-solicitudes";
        model.addAttribute("usuario", usuario);
        return "solicitudes/mi-perfil";
    }

    @GetMapping("/mis-contratos")
    public String misContratos(Model model, HttpSession session) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;
        Object usuariId = session.getAttribute("usuariId");

        List<APRequest> solicitudes = apRequestDao.getRequestsByUsuari(usuariId.toString());
        List<RegistroContrato> contratos = new ArrayList<>();
        for (APRequest sol : solicitudes) {
            RegistroContrato c = contratoDao.getContratoBySolicitud(sol.getIdRequest());
            if (c != null) contratos.add(c);
        }

        Map<String, String> nombresAsistentes = new HashMap<>();
        for (RegistroContrato c : contratos) {
            if (c.getDniAsistente() != null && !nombresAsistentes.containsKey(c.getDniAsistente())) {
                AsistentePersonal a = asistenteDao.getAsistente(c.getDniAsistente());
                if (a != null) nombresAsistentes.put(c.getDniAsistente(), a.getNombre() + " " + a.getApellidos());
            }
        }
        model.addAttribute("contratos", contratos);
        model.addAttribute("nombresAsistentes", nombresAsistentes);
        return "solicitudes/mis-contratos";
    }
}
