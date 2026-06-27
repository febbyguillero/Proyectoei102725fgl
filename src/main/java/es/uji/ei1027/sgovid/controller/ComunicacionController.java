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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/comunicaciones")
public class ComunicacionController {

    @Autowired private ComunicacionDao comunicacionDao;
    @Autowired private APRequestDao solicitudDao;
    @Autowired private AsistentePersonalDao asistenteDao;

    @GetMapping("/solicitud/{idSolicitud}")
    public String listComunicaciones(@PathVariable int idSolicitud,
                                     Model model, HttpSession session) {
        if (session.getAttribute("usuariId") == null) return "redirect:/login";

        APRequest solicitud = solicitudDao.getRequest(idSolicitud);
        if (solicitud == null) return "redirect:/";

        List<ComunicacionUsuarioViPAP> comunicaciones =
                comunicacionDao.getComunicacionesBySolicitud(idSolicitud);

        Map<String, String> nombresAsistentes = new HashMap<>();
        for (ComunicacionUsuarioViPAP com : comunicaciones) {
            if (com.getDniAsistente() != null && !nombresAsistentes.containsKey(com.getDniAsistente())) {
                AsistentePersonal a = asistenteDao.getAsistente(com.getDniAsistente());
                if (a != null) nombresAsistentes.put(com.getDniAsistente(), a.getNombre() + " " + a.getApellidos());
            }
        }

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("comunicaciones", comunicaciones);
        model.addAttribute("nombresAsistentes", nombresAsistentes);
        model.addAttribute("nuevaComunicacion", new ComunicacionUsuarioViPAP());

        String rol = (String) session.getAttribute("rol");
        if ("TECNICO".equals(rol)) {
            model.addAttribute("esTecnico", true);
        }

        return "comunicaciones/list";
    }

    @GetMapping("/solicitud/{idSolicitud}/add")
    public String addComunicacionForm(@PathVariable int idSolicitud,
                                      Model model, HttpSession session) {
        if (session.getAttribute("usuariId") == null) return "redirect:/login";

        APRequest solicitud = solicitudDao.getRequest(idSolicitud);
        if (solicitud == null) return "redirect:/";

        ComunicacionUsuarioViPAP nueva = new ComunicacionUsuarioViPAP();
        nueva.setIdSolicitud(idSolicitud);

        List<AsistentePersonal> asistentes = asistenteDao.getCandidatosAptos(null);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("comunicacion", nueva);
        model.addAttribute("asistentes", asistentes);
        return "comunicaciones/add";
    }

    @PostMapping("/solicitud/{idSolicitud}/add")
    public String addComunicacion(@PathVariable int idSolicitud,
                                  @ModelAttribute("comunicacion") ComunicacionUsuarioViPAP comunicacion,
                                  HttpSession session, Model model) {
        if (session.getAttribute("usuariId") == null) return "redirect:/login";

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
