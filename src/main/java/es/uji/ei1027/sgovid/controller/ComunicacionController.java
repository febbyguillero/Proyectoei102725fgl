package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.APRequestDao;
import es.uji.ei1027.sgovid.dao.ComunicacionDao;
import es.uji.ei1027.sgovid.model.APRequest;
import es.uji.ei1027.sgovid.model.ComunicacionUsuarioViPAP;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/comunicaciones")
public class ComunicacionController {

    @Autowired private ComunicacionDao comunicacionDao;
    @Autowired private APRequestDao solicitudDao;

    @GetMapping("/solicitud/{idSolicitud}")
    public String listComunicaciones(@PathVariable int idSolicitud,
                                     Model model, HttpSession session) {
        if (session.getAttribute("usuariId") == null) return "redirect:/login";

        APRequest solicitud = solicitudDao.getRequest(idSolicitud);
        if (solicitud == null) return "redirect:/";

        List<ComunicacionUsuarioViPAP> comunicaciones =
                comunicacionDao.getComunicacionesBySolicitud(idSolicitud);

        String rol = (String) session.getAttribute("rol");
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("comunicaciones", comunicaciones);
        model.addAttribute("esTecnico", "TECNICO".equals(rol));
        return "comunicaciones/list";
    }

    @PostMapping("/solicitud/{idSolicitud}")
    public String enviarMissatge(@PathVariable int idSolicitud,
                                 @RequestParam String text,
                                 HttpSession session, Model model) {
        if (session.getAttribute("usuariId") == null) return "redirect:/login";

        String rol = (String) session.getAttribute("rol");

        if (text == null || text.trim().isEmpty()) {
            APRequest solicitud = solicitudDao.getRequest(idSolicitud);
            model.addAttribute("solicitud", solicitud);
            model.addAttribute("comunicaciones", comunicacionDao.getComunicacionesBySolicitud(idSolicitud));
            model.addAttribute("esTecnico", "TECNICO".equals(rol));
            model.addAttribute("errorText", "El missatge no pot estar buit.");
            return "comunicaciones/list";
        }

        // La BD té CHECK: direccion IN ('saliente','entrante')
        // El técnic envia (saliente), l'usuari rep i respon (entrante des del punt de vista del tècnic)
        String direccion = "TECNICO".equals(rol) ? "saliente" : "entrante";

        // La BD té CHECK: tipo_comunicacion IN ('email','telefono','presencial','videollamada')
        // Usem 'presencial' com a valor genèric per a missatges de la plataforma
        String tipoComunicacion = "presencial";

        ComunicacionUsuarioViPAP msg = new ComunicacionUsuarioViPAP();
        msg.setIdSolicitud(idSolicitud);
        msg.setResumen(text.trim());
        msg.setTipoComunicacion(tipoComunicacion);
        msg.setDireccion(direccion);
        msg.setFechaComunicacion(LocalDateTime.now());
        comunicacionDao.addComunicacion(msg);

        return "redirect:/comunicaciones/solicitud/" + idSolicitud;
    }

    // DELETE via formulari POST (sense JavaScript) — només tècnic
    @PostMapping("/delete/{idComunicacion}/solicitud/{idSolicitud}")
    public String deleteComunicacion(@PathVariable int idComunicacion,
                                     @PathVariable int idSolicitud,
                                     HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if (!"TECNICO".equals(rol)) return "redirect:/login";
        comunicacionDao.deleteComunicacion(idComunicacion);
        return "redirect:/comunicaciones/solicitud/" + idSolicitud;
    }
}