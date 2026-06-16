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

    //NUEVA SOLICITUD
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

    //SOLICITUDES

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

    //DETALLE SOLICITUD USUARIO OVI

    @GetMapping("/detalle/{id}")
    public String detalleSolicitud(@PathVariable int id, Model model,
                                   jakarta.servlet.http.HttpSession session) {
        Object usuariId = session.getAttribute("usuariId");
        if (usuariId == null) return "redirect:/login";

        APRequest solicitud = apRequestDao.getRequest(id);
        if (solicitud == null) return "redirect:/solicitudes/mis-solicitudes";

        if (!solicitud.getUsuariIdent().equals(usuariId.toString())) {
            return "redirect:/solicitudes/mis-solicitudes";
        }

        model.addAttribute("solicitud", solicitud);

        RegistroContrato contrato = contratoDao.getContratoBySolicitud(id);
        if (contrato != null) model.addAttribute("contrato", contrato);

        // El fichero de la plantilla se llama detalle_solicitud.html (guion bajo)
        return "solicitudes/detalle_solicitud";
    }

    //PERFIL

    @GetMapping("/mi-perfil")
    public String miPerfil(Model model, jakarta.servlet.http.HttpSession session) {
        Object usuariId = session.getAttribute("usuariId");
        if (usuariId == null) return "redirect:/login";

        // Blindaje: el técnico tiene usuariId = "tecnico" (no numérico).
        UsuarioOVI usuario = null;
        try {
            usuario = usuarioDao.getUsuario(Integer.parseInt(usuariId.toString()));
        } catch (NumberFormatException e) {
            // No es un usuario OVI (p.ej. el técnico): no hay perfil que mostrar.
            return "redirect:/";
        }
        if (usuario == null) return "redirect:/solicitudes/mis-solicitudes";

        model.addAttribute("usuario", usuario);
        return "solicitudes/mi-perfil";
    }

    //CONTRATOS
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
