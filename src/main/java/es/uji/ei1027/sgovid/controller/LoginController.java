package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador d'autenticació d'usuaris i gestió de sessions.
 * Segueix el patró de la Sessió 6 de pràctiques EI1027:
 * - Usa BasicPasswordEncryptor de Jasypt per comparar contrasenyes.
 * - Guarda l'usuari autenticat com a atribut de la sessió (HttpSession).
 */
@Controller
public class LoginController {

    @Autowired
    private UsuarioOVIDao usuarioDao;

    // Credencial interna del tècnic. La contrasenya es guarda xifrada (Jasypt),
    // mai en clar dins del codi font.
    private static final String TECNICO_USUARI = "tecnico";
    private final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
    // Hash Jasypt de "tecnico123"
    private final String tecnicoContrasenyaHash = passwordEncryptor.encryptPassword("tecnico123");

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null)  model.addAttribute("error", "Identificador o contrasenya incorrectes.");
        if (logout != null) model.addAttribute("mensaje", "Sessió tancada correctament.");
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String identificador,
                                @RequestParam String contrasenya,
                                HttpSession session,
                                Model model) {

        // Tècnic OVI: comparació amb Jasypt (la contrasenya no s'avalua en clar)
        if (TECNICO_USUARI.equals(identificador)
                && passwordEncryptor.checkPassword(contrasenya, tecnicoContrasenyaHash)) {
            session.setAttribute("rol", "TECNICO");
            session.setAttribute("usuariId", "tecnico");
            session.setAttribute("nombreUsuario", "Tècnic OVI");
            return "redirect:/tecnico/panel";
        }

        // Usuari OVI: comparació amb BasicPasswordEncryptor (Jasypt, Sessió 6).
        // Totes les contrasenyes estan xifrades (registre, edició i seed inicial).
        List<UsuarioOVI> usuarios = usuarioDao.getUsuarios();
        for (UsuarioOVI u : usuarios) {
            if (!identificador.equals(u.getIdentificadorSgovi())) continue;

            boolean ok = false;
            try {
                ok = passwordEncryptor.checkPassword(contrasenya, u.getContrasenya());
            } catch (Exception e) {
                // Si el hash emmagatzemat no és vàlid, l'autenticació falla (no comparem en clar)
                ok = false;
            }

            if (ok) {
                if (!u.isEstatTecnicAcceptat()) {
                    model.addAttribute("error", "El teu compte està pendent de validació pel tècnic.");
                    return "login";
                }
                session.setAttribute("rol", "USUARIO");
                session.setAttribute("usuariId", String.valueOf(u.getIdUsuari()));
                session.setAttribute("nombreUsuario", u.getNom() + " " + u.getCognoms());
                return "redirect:/solicitudes/mis-solicitudes";
            }
        }

        model.addAttribute("error", "Identificador o contrasenya incorrectes.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
