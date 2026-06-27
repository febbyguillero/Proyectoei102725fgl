package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Controller
public class UsuarioOVIController {

    private static final int TAM_PAGINA = 10;
    private final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
    private UsuarioOVIDao usuarioDao;

    @Autowired
    public void setUsuarioDao(UsuarioOVIDao usuarioDao) { this.usuarioDao = usuarioDao; }

    private String comprobarRolTecnico(HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (rol == null) return "redirect:/login";
        if (!"TECNICO".equals(rol)) return "redirect:/solicitudes/mis-solicitudes";
        return null;
    }


    @RequestMapping("/usuario/list")
    public String listUsuarios(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "1") int page,
            HttpSession session, Model model) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        if (page < 1) page = 1;
        int total = usuarioDao.countUsuarios(q);
        int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAM_PAGINA));
        if (page > totalPaginas) page = totalPaginas;
        model.addAttribute("usuarios", usuarioDao.getUsuarios(q, sort, dir, page, TAM_PAGINA));
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("page", page);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("total", total);
        return "usuario/list";
    }

    @RequestMapping(value = "/usuario/add", method = RequestMethod.GET)
    public String addUsuario(Model model, HttpSession session) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        model.addAttribute("usuario", new UsuarioOVI());
        return "usuario/add";
    }

    @RequestMapping(value = "/usuario/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("usuario") UsuarioOVI usuario,
                                   BindingResult bindingResult, HttpSession session) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        UsuarioOVIValidator validator = new UsuarioOVIValidator();
        validator.validate(usuario, bindingResult);
        if (bindingResult.hasErrors()) return "usuario/add";
        if (usuario.getContrasenya() != null && !usuario.getContrasenya().isEmpty())
            usuario.setContrasenya(passwordEncryptor.encryptPassword(usuario.getContrasenya()));
        usuario.setDataRegistre(LocalDateTime.now());
        usuario.setConsentimentInformat(true);
        usuario.setEstatTecnicAcceptat(false);
        usuarioDao.addUsuario(usuario);
        return "redirect:/tecnico/usuarios-pendientes";
    }

    @RequestMapping(value = "/usuario/update/{idUsuari}", method = RequestMethod.GET)
    public String editUsuario(Model model, @PathVariable int idUsuari, HttpSession session) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        UsuarioOVI usuario = usuarioDao.getUsuario(idUsuari);
        if (usuario != null) usuario.setContrasenya("");
        model.addAttribute("usuario", usuario);
        return "usuario/update";
    }

    @RequestMapping(value = "/usuario/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("usuario") UsuarioOVI usuario,
                                      HttpSession session) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        if (usuario.getContrasenya() == null || usuario.getContrasenya().trim().isEmpty()) {
            UsuarioOVI original = usuarioDao.getUsuario(usuario.getIdUsuari());
            if (original != null) usuario.setContrasenya(original.getContrasenya());
        } else {
            usuario.setContrasenya(passwordEncryptor.encryptPassword(usuario.getContrasenya()));
        }
        usuarioDao.updateUsuario(usuario);
        return "redirect:/tecnico/usuarios-pendientes";
    }

    @RequestMapping(value = "/usuario/delete/{idUsuari}")
    public String processDelete(@PathVariable int idUsuari, HttpSession session) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        usuarioDao.deleteUsuario(idUsuari);
        return "redirect:/tecnico/usuarios-pendientes";
    }


    @GetMapping("/registro/usuario")
    public String mostrarRegistroPublico(Model model) {
        model.addAttribute("usuario", new UsuarioOVI());
        return "registro-usuario";
    }

    @PostMapping("/registro/usuario")
    public String procesarRegistroPublico(@ModelAttribute("usuario") UsuarioOVI usuario, Model model) {
        if (usuario.getNom() == null || usuario.getNom().trim().isEmpty()) {
            model.addAttribute("error", "El nom és obligatori."); return "registro-usuario";
        }
        if (usuario.getDni() == null || usuario.getDni().trim().isEmpty()) {
            model.addAttribute("error", "El DNI és obligatori."); return "registro-usuario";
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            model.addAttribute("error", "L'email és obligatori."); return "registro-usuario";
        }
        if (!usuario.isConsentimentInformat()) {
            model.addAttribute("error", "Has d'acceptar el consentiment informat."); return "registro-usuario";
        }
        if (usuario.getContrasenya() != null && !usuario.getContrasenya().isEmpty())
            usuario.setContrasenya(passwordEncryptor.encryptPassword(usuario.getContrasenya()));
        usuario.setDataRegistre(LocalDateTime.now());
        usuario.setEstatTecnicAcceptat(false);
        usuario.setIdentificadorSgovi("PENDENT-" + System.currentTimeMillis());
        usuarioDao.addUsuario(usuario);
        model.addAttribute("ok", "Sol·licitud enviada correctament. El tècnic OVI la revisarà prompte.");
        model.addAttribute("usuario", new UsuarioOVI());
        return "registro-usuario";
    }
}
