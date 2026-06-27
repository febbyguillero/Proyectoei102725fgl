package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import jakarta.servlet.http.HttpServletResponse;
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
 *
 * La inicialització del tècnic OVI a la BD es fa a SgovidApplication.run(),
 * seguint el patró CommandLineRunner de la Sessió 2.
 */
@Controller
public class LoginController {

    public static final String TECNICO_IDENT = "TECNICO";

    private final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    @Autowired
    private UsuarioOVIDao usuarioDao;

    // Cabeceres anti-caché reutilitzables. Eviten que el navegador guarde en memòria
    // pàgines protegides; així, al polsar Enrere després de tancar sessió, el navegador
    // torna a demanar la pàgina al servidor i es comprova la sessió.
    private void noCachear(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            HttpServletResponse response,
                            Model model) {
        noCachear(response);
        if (error != null)  model.addAttribute("error", "Identificador o contrasenya incorrectes.");
        if (logout != null) model.addAttribute("mensaje", "Sessió tancada correctament.");
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String identificador,
                                @RequestParam String contrasenya,
                                HttpSession session,
                                Model model) {

        // Tècnic OVI: buscar a la BD per identificador_sgovi = 'TECNICO'
        if (TECNICO_IDENT.equals(identificador)) {
            try {
                UsuarioOVI tecnico = usuarioDao.getUsuarioByIdentificador(TECNICO_IDENT);
                if (tecnico != null && passwordEncryptor.checkPassword(contrasenya, tecnico.getContrasenya())) {
                    session.setAttribute("rol", "TECNICO");
                    session.setAttribute("usuariId", "tecnico");
                    session.setAttribute("nombreUsuario", tecnico.getNom() + " " + tecnico.getCognoms());
                    return "redirect:/tecnico/panel";
                }
            } catch (Exception e) {
                // Si falla la consulta, caer al mensaje de error genérico
            }
            model.addAttribute("error", "Identificador o contrasenya incorrectes.");
            return "login";
        }

        // Usuari OVI: comparació amb BasicPasswordEncryptor (Jasypt, Sessió 6).
        List<UsuarioOVI> usuarios = usuarioDao.getUsuarios();
        for (UsuarioOVI u : usuarios) {
            if (!identificador.equals(u.getIdentificadorSgovi())
                    && !identificador.equals(u.getEmail())) continue;
            if (TECNICO_IDENT.equals(u.getIdentificadorSgovi())) continue;

            boolean ok = false;
            try {
                ok = passwordEncryptor.checkPassword(contrasenya, u.getContrasenya());
            } catch (Exception e) {
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

    @GetMapping("/logout-confirm")
    public String logoutConfirm(HttpSession session, HttpServletResponse response) {
        if (session.getAttribute("rol") == null) return "redirect:/login";
        noCachear(response);
        return "logout-confirm";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        noCachear(response);
        return "redirect:/login?logout";
    }
}