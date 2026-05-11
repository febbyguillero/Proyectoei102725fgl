package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.APRequestDao;
import es.uji.ei1027.sgovid.model.APRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String guardarPeticion(@ModelAttribute("solicitud") APRequest solicitud) {
        return "redirect:/solicitudes/mis-solicitudes";
    }

    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(Model model) {
        List<APRequest> solicitudes = apRequestDao.getRequests();
        model.addAttribute("solicitudes", solicitudes);
        return "solicitudes/mis-solicitudes";
    }
}