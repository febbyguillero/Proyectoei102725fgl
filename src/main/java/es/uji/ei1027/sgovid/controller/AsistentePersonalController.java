package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.AsistentePersonalDao;
import es.uji.ei1027.sgovid.model.AsistentePersonal;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/asistente")
public class AsistentePersonalController {

    @Autowired
    private AsistentePersonalDao asistenteDao;

    private String comprobarRolTecnico(HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (rol == null) return "redirect:/login";
        if (!"TECNICO".equals(rol)) return "redirect:/solicitudes/mis-solicitudes";
        return null;
    }


    @GetMapping("/registro")
    public String registroAsistente(Model model) {
        model.addAttribute("asistente", new AsistentePersonal());
        return "asistente/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("asistente") AsistentePersonal asistente,
                                   BindingResult bindingResult, Model model) {
        AsistentePersonalValidator validator = new AsistentePersonalValidator();
        validator.validate(asistente, bindingResult);
        if (bindingResult.hasErrors()) return "asistente/registro";

        asistente.setEstado("PENDIENTE");
        try {
            asistenteDao.addAsistente(asistente);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            model.addAttribute("asistente", asistente);
            model.addAttribute("errorDni", "Ja existeix un candidat registrat amb aquest DNI. Si ja estàs registrat/da, contacta amb l'OVI.");
            return "asistente/registro";
        }
        return "redirect:/login?registroOK";
    }

    @PostMapping("/actualizar")
    public String actualizarAsistente(@ModelAttribute("asistente") AsistentePersonal asistente,
                                      BindingResult bindingResult,
                                      HttpSession session, Model model) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;

        AsistentePersonalValidator validator = new AsistentePersonalValidator();
        validator.validate(asistente, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("asistente", asistente);
            return "asistente/registro";
        }

        if (asistente.getEstado() == null || asistente.getEstado().trim().isEmpty()) {
            AsistentePersonal original = asistenteDao.getAsistente(asistente.getDni());
            if (original != null) asistente.setEstado(original.getEstado());
        }

        try {
            asistenteDao.updateAsistente(asistente);
        } catch (Exception e) {
            model.addAttribute("asistente", asistente);
            model.addAttribute("errorDni", "No s'han pogut guardar els canvis. Comprova les dades i torna-ho a intentar.");
            return "asistente/registro";
        }
        return "redirect:/tecnico/asistentes-pendientes";
    }


    @RequestMapping("/list")
    public String listAsistentes(HttpSession session) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        return "redirect:/tecnico/asistentes-pendientes";
    }
}
