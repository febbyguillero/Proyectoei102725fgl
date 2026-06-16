package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.APRequestDao;
import es.uji.ei1027.sgovid.dao.AsistentePersonalDao;
import es.uji.ei1027.sgovid.dao.ComunicacionDao;
import es.uji.ei1027.sgovid.model.APRequest;
import es.uji.ei1027.sgovid.model.AsistentePersonal;
import es.uji.ei1027.sgovid.model.ComunicacionUsuarioViPAP;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador per a la comunicació entre l'usuari OVI i el PAP/PATI.
 * Implementa el registre i la consulta de comunicacions associades a una sol·licitud,
 * tal com demana el projecte per a grups de dos persones (millora pactada).
 * Segueix el patró MVC de les pràctiques EI1027 (Sessió 4: gestió d'objectes de domini).
 */
@Controller
@RequestMapping("/comunicaciones")
public class ComunicacionController {

    @Autowired
    private ComunicacionDao comunicacionDao;

    @Autowired
    private APRequestDao solicitudDao;

    @Autowired
    private AsistentePersonalDao asistenteDao;

    // Llista de comunicacions d'una sol·licitud (tècnic i usuari OVI)
    @GetMapping("/solicitud/{idSolicitud}")
    public String listComunicaciones(@PathVariable int idSolicitud,
                                     Model model, HttpSession session) {
        if (session.getAttribute("usuariId") == null) return "redirect:/login";

        APRequest solicitud = solicitudDao.getRequest(idSolicitud);
        if (solicitud == null) return "redirect:/";

        List<ComunicacionUsuarioViPAP> comunicaciones =
                comunicacionDao.getComunicacionesBySolicitud(idSolicitud);

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("comunicaciones", comunicaciones);
        model.addAttribute("nuevaComunicacion", new ComunicacionUsuarioViPAP());

        // El tècnic pot veure l'assistent assignat
        String rol = (String) session.getAttribute("rol");
        if ("TECNICO".equals(rol) && solicitud.getUsuariIdent() != null) {
            // Buscar assistent del contracte si n'hi ha
            model.addAttribute("esTecnico", true);
        }

        return "comunicaciones/list";
    }

    // Formulari per registrar una nova comunicació (GET)
    @GetMapping("/solicitud/{idSolicitud}/add")
    public String addComunicacionForm(@PathVariable int idSolicitud,
                                      Model model, HttpSession session) {
        if (session.getAttribute("usuariId") == null) return "redirect:/login";

        APRequest solicitud = solicitudDao.getRequest(idSolicitud);
        if (solicitud == null) return "redirect:/";

        ComunicacionUsuarioViPAP nueva = new ComunicacionUsuarioViPAP();
        nueva.setIdSolicitud(idSolicitud);

        // Si hi ha assistent assignat al contracte, preomplir el DNI
        List<AsistentePersonal> asistentes = asistenteDao.getCandidatosAptos(null);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("comunicacion", nueva);
        model.addAttribute("asistentes", asistentes);
        return "comunicaciones/add";
    }

    // Registrar una nova comunicació (POST)
    @PostMapping("/solicitud/{idSolicitud}/add")
    public String addComunicacion(@PathVariable int idSolicitud,
                                  @ModelAttribute("comunicacion") ComunicacionUsuarioViPAP comunicacion,
                                  HttpSession session, Model model) {
        if (session.getAttribute("usuariId") == null) return "redirect:/login";

        // Validació bàsica (seguint la pràctica 4: BindingResult + missatge d'error)
        if (comunicacion.getTipoComunicacion() == null || comunicacion.getTipoComunicacion().isEmpty()) {
            model.addAttribute("error", "El tipus de comunicació és obligatori.");
            model.addAttribute("solicitud", solicitudDao.getRequest(idSolicitud));
            model.addAttribute("comunicacion", comunicacion);
            model.addAttribute("asistentes", asistenteDao.getCandidatosAptos(null));
            return "comunicaciones/add";
        }
        if (comunicacion.getResumen() == null || comunicacion.getResumen().isEmpty()) {
            model.addAttribute("error", "El resum és obligatori.");
            model.addAttribute("solicitud", solicitudDao.getRequest(idSolicitud));
            model.addAttribute("comunicacion", comunicacion);
            model.addAttribute("asistentes", asistenteDao.getCandidatosAptos(null));
            return "comunicaciones/add";
        }

        comunicacion.setIdSolicitud(idSolicitud);
        comunicacion.setFechaComunicacion(LocalDateTime.now());
        comunicacionDao.addComunicacion(comunicacion);

        return "redirect:/comunicaciones/solicitud/" + idSolicitud;
    }

    // Eliminar una comunicació (tècnic)
    @GetMapping("/delete/{idComunicacion}/solicitud/{idSolicitud}")
    public String deleteComunicacion(@PathVariable int idComunicacion,
                                     @PathVariable int idSolicitud,
                                     HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if (!"TECNICO".equals(rol)) return "redirect:/login";
        comunicacionDao.deleteComunicacion(idComunicacion);
        return "redirect:/comunicaciones/solicitud/" + idSolicitud;
    }
}
