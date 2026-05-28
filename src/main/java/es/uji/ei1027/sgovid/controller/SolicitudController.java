package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.APRequestDao;
import es.uji.ei1027.sgovid.dao.RegistroContratoDao;
import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.APRequest;
import es.uji.ei1027.sgovid.model.RegistroContrato;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    @Autowired
    private APRequestDao apRequestDao;

    @Autowired
    private UsuarioOVIDao usuarioDao;

    @Autowired
    private RegistroContratoDao contratoDao;

    // Nueva solicitud
    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("solicitud", new APRequest());
        return "solicitudes/nueva-peticion";
    }

    @PostMapping("/nueva")
    public String guardarPeticion(
            @ModelAttribute("solicitud") APRequest solicitud,
            jakarta.servlet.http.HttpSession session,
            Model model) {

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

    // Mis solicitudes
    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(Model model, jakarta.servlet.http.HttpSession session) {
        Object usuariId = session.getAttribute("usuariId");
        List<APRequest> solicitudes;
        if (usuariId != null) {
            solicitudes = apRequestDao.getRequestsByUsuari(usuariId.toString());
        } else {
            solicitudes = apRequestDao.getRequests();
        }
        model.addAttribute("solicitudes", solicitudes);
        return "solicitudes/mis-solicitudes";
    }

    // Mi perfil
    @GetMapping("/mi-perfil")
    public String miPerfil(Model model, jakarta.servlet.http.HttpSession session) {
        Object usuariId = session.getAttribute("usuariId");
        if (usuariId == null) return "redirect:/login";
        UsuarioOVI usuario = usuarioDao.getUsuario(Integer.parseInt(usuariId.toString()));
        model.addAttribute("usuario", usuario);
        return "solicitudes/mi-perfil";
    }

    // Mis contratos
    @GetMapping("/mis-contratos")
    public String misContratos(Model model, jakarta.servlet.http.HttpSession session) {
        Object usuariId = session.getAttribute("usuariId");
        if (usuariId == null) return "redirect:/login";
        List<APRequest> solicitudes = apRequestDao.getRequestsByUsuari(usuariId.toString());
        List<RegistroContrato> contratos = new ArrayList<>();
        for (APRequest sol : solicitudes) {
            RegistroContrato c = contratoDao.getContratoBySolicitud(sol.getIdRequest());
            if (c != null) contratos.add(c);
        }
        model.addAttribute("contratos", contratos);
        return "solicitudes/mis-contratos";
    }
}