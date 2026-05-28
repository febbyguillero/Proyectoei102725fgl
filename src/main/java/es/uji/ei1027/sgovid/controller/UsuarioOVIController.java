package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class UsuarioOVIController {

    private UsuarioOVIDao usuarioDao;

    @Autowired
    public void setUsuarioDao(UsuarioOVIDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    // ===================== BACK-OFFICE (técnico) =====================

    @RequestMapping("/usuario/list")
    public String listUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioDao.getUsuarios());
        return "usuario/list";
    }

    @RequestMapping(value = "/usuario/add", method = RequestMethod.GET)
    public String addUsuario(Model model) {
        model.addAttribute("usuario", new UsuarioOVI());
        return "usuario/add";
    }

    @RequestMapping(value = "/usuario/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("usuario") UsuarioOVI usuario,
                                   BindingResult bindingResult) {
        UsuarioOVIValidator validator = new UsuarioOVIValidator();
        validator.validate(usuario, bindingResult);
        if (bindingResult.hasErrors()) {
            return "usuario/add";
        }
        usuario.setDataRegistre(LocalDateTime.now());
        usuario.setConsentimentInformat(true);
        usuario.setEstatTecnicAcceptat(false);
        usuarioDao.addUsuario(usuario);
        return "redirect:/usuario/list";
    }

    @RequestMapping(value = "/usuario/update/{idUsuari}", method = RequestMethod.GET)
    public String editUsuario(Model model, @PathVariable int idUsuari) {
        model.addAttribute("usuario", usuarioDao.getUsuario(idUsuari));
        return "usuario/update";
    }

    @RequestMapping(value = "/usuario/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("usuario") UsuarioOVI usuario) {
        usuarioDao.updateUsuario(usuario);
        return "redirect:/usuario/list";
    }

    @RequestMapping(value = "/usuario/delete/{idUsuari}")
    public String processDelete(@PathVariable int idUsuari) {
        usuarioDao.deleteUsuario(idUsuari);
        return "redirect:/usuario/list";
    }

    // ===================== REGISTRO PÚBLICO (sin login) =====================

    @GetMapping("/registro/usuario")
    public String mostrarRegistroPublico(Model model) {
        model.addAttribute("usuario", new UsuarioOVI());
        return "registro-usuario";
    }

    @PostMapping("/registro/usuario")
    public String procesarRegistroPublico(@ModelAttribute("usuario") UsuarioOVI usuario,
                                          BindingResult bindingResult,
                                          Model model) {
        // Validación básica
        if (usuario.getNom() == null || usuario.getNom().trim().isEmpty()) {
            model.addAttribute("error", "El nom és obligatori.");
            return "registro-usuario";
        }
        if (usuario.getDni() == null || usuario.getDni().trim().isEmpty()) {
            model.addAttribute("error", "El DNI és obligatori.");
            return "registro-usuario";
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            model.addAttribute("error", "L'email és obligatori.");
            return "registro-usuario";
        }
        if (!usuario.isConsentimentInformat()) {
            model.addAttribute("error", "Has d'acceptar el consentiment informat.");
            return "registro-usuario";
        }

        // Valores que pone el sistema, no el usuario
        usuario.setDataRegistre(LocalDateTime.now());
        usuario.setEstatTecnicAcceptat(false); // pendiente de validación
        usuario.setIdentificadorSgovi("PENDENT"); // el técnico lo asignará al aceptar

        usuarioDao.addUsuario(usuario);

        model.addAttribute("ok", "Sol·licitud enviada correctament. El tècnic OVI la revisarà prompte.");
        model.addAttribute("usuario", new UsuarioOVI());
        return "registro-usuario";
    }
}
