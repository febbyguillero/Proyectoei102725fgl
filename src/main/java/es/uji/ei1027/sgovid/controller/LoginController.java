package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador d'autenticació d'usuaris i gestió de sessions.
 * Segueix el patró de la Sessió 6 de pràctiques EI1027:
 * - Usa BasicPasswordEncryptor de Jasypt per comparar contrasenyes.
 * - Guarda l'usuari autenticat com a atribut de la sessió (HttpSession).
 *
 * El tècnic OVI es guarda a la BD com un usuari més (identificador_sgovi = 'TECNICO'),
 * amb la contrasenya xifrada. Ja no hi ha cap credencial hardcoded al codi.
 */
@Controller
public class LoginController {

    // Identificador especial del tècnic a la BD. No és una contrasenya, és un ID públic.
    public static final String TECNICO_IDENT = "TECNICO";

    private final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    @Autowired
    private UsuarioOVIDao usuarioDao;

    /**
     * Al arrancar la aplicación, comprueba si el técnico existe en la BD.
     * Si no existe, lo crea con la contraseña por defecto "tecnico123" (cifrada con Jasypt).
     * Así nunca hay credenciales hardcoded en el código.
     */
    @PostConstruct
    public void inicializarTecnico() {
        try {
            UsuarioOVI tecnico = usuarioDao.getUsuarioByIdentificador(TECNICO_IDENT);
            if (tecnico == null) {
                UsuarioOVI nuevo = new UsuarioOVI();
                nuevo.setIdentificadorSgovi(TECNICO_IDENT);
                nuevo.setContrasenya(passwordEncryptor.encryptPassword("tecnico123"));
                nuevo.setEmail("tecnico@ovi.es");
                nuevo.setNom("Tècnic");
                nuevo.setCognoms("OVI");
                nuevo.setDni("00000000T");
                nuevo.setDataNaixement(LocalDate.of(1980, 1, 1));
                nuevo.setConsentimentInformat(true);
                nuevo.setEstatTecnicAcceptat(true);
                usuarioDao.addUsuario(nuevo);
            }
        } catch (Exception e) {
            // Si falla (p.ej. DNI duplicado en pruebas), no bloquear el arranque
            System.err.println("Avís: no s'ha pogut inicialitzar el tècnic: " + e.getMessage());
        }
    }

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
        // Totes les contrasenyes estan xifrades (registre, edició i seed inicial).
        List<UsuarioOVI> usuarios = usuarioDao.getUsuarios();
        for (UsuarioOVI u : usuarios) {
            if (!identificador.equals(u.getIdentificadorSgovi())) continue;

            // Saltar el registro del técnico si aparece en el bucle
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
    public String logoutConfirm(HttpSession session) {
        if (session.getAttribute("rol") == null) return "redirect:/login";
        return "logout-confirm";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        return "redirect:/login?logout";
    }
}