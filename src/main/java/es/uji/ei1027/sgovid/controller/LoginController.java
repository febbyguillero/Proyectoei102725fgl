package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private UsuarioOVIDao usuarioDao;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String identificador,
            @RequestParam String contrasenya,
            HttpSession session,
            Model model) {

        //Técnico hardcoded (en un sistema real estaría en BD)
        if ("tecnico".equals(identificador) && "tecnico123".equals(contrasenya)) {
            session.setAttribute("rol", "TECNICO");
            session.setAttribute("nombreUsuario", "Técnico OVI");
            return "redirect:/tecnico/panel";
        }

        //Buscar usuario OVI en BD
        List<UsuarioOVI> usuarios = usuarioDao.getUsuarios();
        for (UsuarioOVI u : usuarios) {
            if (identificador.equals(u.getIdentificadorSgovi())
                    && contrasenya.equals(u.getContrasenya())
                    && u.isEstatTecnicAcceptat()) {
                session.setAttribute("rol", "USUARIO");
                session.setAttribute("usuariId", String.valueOf(u.getIdUsuari()));
                session.setAttribute("nombreUsuario", u.getNom() + " " + u.getCognoms());
                return "redirect:/solicitudes/mis-solicitudes";
            }
        }

        model.addAttribute("error", "Identificador o contrasenya incorrectes");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}