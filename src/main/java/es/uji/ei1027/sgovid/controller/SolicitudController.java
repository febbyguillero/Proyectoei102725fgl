package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.APRequestDao;
import es.uji.ei1027.sgovid.model.APRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    @Autowired
    private APRequestDao apRequestDao;

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("solicitud", new APRequest());
        return "solicitudes/nueva-peticion";
    }

    @PostMapping("/nueva")
    public String guardarPeticion(
            @RequestParam("tipusServei") String tipusServei,
            @RequestParam(value = "dies", required = false) List<String> dies,
            @RequestParam(value = "franjaHoraria", required = false) String franjaHoraria,
            @RequestParam(value = "observations", required = false) String observations,
            jakarta.servlet.http.HttpSession session) {

        //Validación
        if (tipusServei == null || tipusServei.isEmpty()) {
            return "redirect:/solicitudes/nueva?error=tipusServei";
        }

        APRequest solicitud = new APRequest();
        //En un sistema real vendría de la sesión — por ahora usamos el primero disponible
        Object usuariId = session.getAttribute("usuariId");
        solicitud.setUsuariIdent(usuariId != null ? usuariId.toString() : "1");
        solicitud.setTipusServei(tipusServei);
        solicitud.setEstat("PENDIENTE");
        solicitud.setDataCreacio(LocalDate.now());
        solicitud.setObservations(observations);
        solicitud.setDies(dies != null ? String.join(",", dies) : "");
        solicitud.setFranjaHoraria(franjaHoraria);

        apRequestDao.addRequest(solicitud);
        return "redirect:/solicitudes/mis-solicitudes";
    }

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
}