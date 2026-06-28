package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.APRequestDao;
import es.uji.ei1027.sgovid.dao.AsistentePersonalDao;
import es.uji.ei1027.sgovid.dao.RegistroContratoDao;
import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.APRequest;
import es.uji.ei1027.sgovid.model.AsistentePersonal;
import es.uji.ei1027.sgovid.model.RegistroContrato;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    private static final int TAM_PAGINA = 20;
    private final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    @Autowired private APRequestDao apRequestDao;
    @Autowired private UsuarioOVIDao usuarioDao;
    @Autowired private RegistroContratoDao contratoDao;
    @Autowired private AsistentePersonalDao asistenteDao;

    private String comprobarRolUsuario(HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (rol == null) return "redirect:/login";
        if (!"USUARIO".equals(rol)) return "redirect:/tecnico/panel";
        return null;
    }


    @GetMapping("/nueva")
    public String mostrarFormulario(Model model, HttpSession session) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;
        model.addAttribute("solicitud", new APRequest());
        Object usuariId = session.getAttribute("usuariId");
        try {
            UsuarioOVI u = usuarioDao.getUsuario(Integer.parseInt(usuariId.toString()));
            if (u != null && u.getZonaGeografica() != null && !u.getZonaGeografica().isEmpty()) {
                model.addAttribute("zonaActual", u.getZonaGeografica());
            }
        } catch (Exception ignored) {}
        return "solicitudes/nueva-peticion";
    }

    @PostMapping("/nueva")
    public String guardarPeticion(@ModelAttribute("solicitud") APRequest solicitud,
                                  @RequestParam(required = false) String zonaGeografica,
                                  @RequestParam(required = false) List<String> dies,
                                  HttpSession session, Model model) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;

        boolean hiHaError = false;

        if (solicitud.getTipusServei() == null || solicitud.getTipusServei().isEmpty()) {
            model.addAttribute("errorTipus", "El tipus de servei és obligatori.");
            hiHaError = true;
        }
        if (dies == null || dies.isEmpty()) {
            model.addAttribute("errorDies", "Has de seleccionar almenys un dia de la setmana.");
            hiHaError = true;
        }
        if (solicitud.getFranjaHoraria() == null || solicitud.getFranjaHoraria().isEmpty()) {
            model.addAttribute("errorFranja", "La franja horària és obligatòria.");
            hiHaError = true;
        }

        if (hiHaError) {
            if (zonaGeografica != null) model.addAttribute("zonaActual", zonaGeografica);
            if (dies != null) solicitud.setDies(String.join(",", dies));
            model.addAttribute("solicitud", solicitud);
            return "solicitudes/nueva-peticion";
        }

        Object usuariId = session.getAttribute("usuariId");
        solicitud.setUsuariIdent(usuariId != null ? usuariId.toString() : "1");
        solicitud.setEstat("PENDIENTE");
        solicitud.setDataCreacio(LocalDate.now());
        solicitud.setDies(String.join(",", dies));

        if (zonaGeografica != null && !zonaGeografica.trim().isEmpty()) {
            try {
                UsuarioOVI u = usuarioDao.getUsuario(Integer.parseInt(usuariId.toString()));
                if (u != null) {
                    u.setZonaGeografica(zonaGeografica.trim());
                    usuarioDao.updateUsuario(u);
                }
            } catch (Exception ignored) {}
        }

        apRequestDao.addRequest(solicitud);
        return "redirect:/solicitudes/mis-solicitudes?enviada=true";
    }


    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(Model model, HttpSession session,
                                 @RequestParam(required = false) String enviada,
                                 @RequestParam(defaultValue = "") String q,
                                 @RequestParam(defaultValue = "desc") String dir,
                                 @RequestParam(defaultValue = "1") int page) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;
        Object usuariId = session.getAttribute("usuariId");

        if (page < 1) page = 1;
        int total = apRequestDao.countRequestsByUsuari(usuariId.toString(), q);
        int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAM_PAGINA));
        if (page > totalPaginas) page = totalPaginas;

        List<APRequest> solicitudes = apRequestDao.getRequestsByUsuari(
                usuariId.toString(), q, "data_creacio", dir, page, TAM_PAGINA);

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("q", q);
        model.addAttribute("dir", dir);
        model.addAttribute("page", page);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("total", total);
        if ("true".equals(enviada)) {
            model.addAttribute("missatgeOk", "Sol·licitud enviada correctament. El tècnic OVI la revisarà prompte.");
        }
        return "solicitudes/mis-solicitudes";
    }


    @GetMapping("/detalle/{id}")
    public String detalleSolicitud(@PathVariable int id, Model model, HttpSession session) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;
        Object usuariId = session.getAttribute("usuariId");
        APRequest solicitud = apRequestDao.getRequest(id);
        if (solicitud == null) return "redirect:/solicitudes/mis-solicitudes";
        if (!solicitud.getUsuariIdent().equals(usuariId.toString()))
            return "redirect:/solicitudes/mis-solicitudes";
        model.addAttribute("solicitud", solicitud);
        RegistroContrato contrato = contratoDao.getContratoBySolicitud(id);
        if (contrato != null) {
            model.addAttribute("contrato", contrato);
            if (contrato.getDniAsistente() != null) {
                AsistentePersonal a = asistenteDao.getAsistente(contrato.getDniAsistente());
                if (a != null) model.addAttribute("nombreAsistente", a.getNombre() + " " + a.getApellidos());
            }
        }
        return "solicitudes/detalle_solicitud";
    }


    @GetMapping("/mi-perfil")
    public String miPerfil(Model model, HttpSession session) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;
        Object usuariId = session.getAttribute("usuariId");
        UsuarioOVI usuario = null;
        try { usuario = usuarioDao.getUsuario(Integer.parseInt(usuariId.toString())); }
        catch (NumberFormatException e) { return "redirect:/"; }
        if (usuario == null) return "redirect:/solicitudes/mis-solicitudes";
        model.addAttribute("usuario", usuario);
        return "solicitudes/mi-perfil";
    }


    @GetMapping("/editar-perfil")
    public String editarPerfilGet(Model model, HttpSession session) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;
        Object usuariId = session.getAttribute("usuariId");
        try {
            UsuarioOVI u = usuarioDao.getUsuario(Integer.parseInt(usuariId.toString()));
            if (u == null) return "redirect:/solicitudes/mi-perfil";
            u.setContrasenya(""); // no mostrem el hash
            model.addAttribute("usuario", u);
        } catch (Exception e) { return "redirect:/solicitudes/mi-perfil"; }
        return "solicitudes/editar-perfil";
    }

    @PostMapping("/editar-perfil")
    public String editarPerfilPost(@ModelAttribute("usuario") UsuarioOVI usuarioForm,
                                   HttpSession session, Model model) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;
        Object usuariId = session.getAttribute("usuariId");

        UsuarioOVI original;
        try { original = usuarioDao.getUsuario(Integer.parseInt(usuariId.toString())); }
        catch (Exception e) { return "redirect:/solicitudes/mi-perfil"; }
        if (original == null) return "redirect:/solicitudes/mi-perfil";

        if (usuarioForm.getNom() == null || usuarioForm.getNom().trim().isEmpty()) {
            model.addAttribute("error", "El nom és obligatori.");
            model.addAttribute("usuario", usuarioForm);
            return "solicitudes/editar-perfil";
        }

        original.setNom(usuarioForm.getNom());
        original.setCognoms(usuarioForm.getCognoms());
        original.setEmail(usuarioForm.getEmail());
        original.setTelefon(usuarioForm.getTelefon());
        original.setAdreca(usuarioForm.getAdreca());
        original.setZonaGeografica(usuarioForm.getZonaGeografica());

        // Canvi de contrasenya: només si s'ha escrit alguna cosa
        if (usuarioForm.getContrasenya() != null && !usuarioForm.getContrasenya().trim().isEmpty()) {
            original.setContrasenya(passwordEncryptor.encryptPassword(usuarioForm.getContrasenya()));
        }

        usuarioDao.updateUsuario(original);
        session.setAttribute("nombreUsuario", original.getNom() + " " + original.getCognoms());
        return "redirect:/solicitudes/mi-perfil";
    }

    // ---- ELS MEUS CONTRACTES ----

    @GetMapping("/mis-contratos")
    public String misContratos(Model model, HttpSession session) {
        String redir = comprobarRolUsuario(session);
        if (redir != null) return redir;
        Object usuariId = session.getAttribute("usuariId");
        List<APRequest> solicitudes = apRequestDao.getRequestsByUsuari(usuariId.toString());
        List<RegistroContrato> contratos = new ArrayList<>();
        for (APRequest sol : solicitudes) {
            RegistroContrato c = contratoDao.getContratoBySolicitud(sol.getIdRequest());
            if (c != null) contratos.add(c);
        }
        Map<String, String> nombresAsistentes = new HashMap<>();
        Map<Integer, String> tipusServei = new HashMap<>();
        for (RegistroContrato c : contratos) {
            if (c.getDniAsistente() != null && !nombresAsistentes.containsKey(c.getDniAsistente())) {
                AsistentePersonal a = asistenteDao.getAsistente(c.getDniAsistente());
                if (a != null) nombresAsistentes.put(c.getDniAsistente(), a.getNombre() + " " + a.getApellidos());
            }
        }
        for (APRequest sol : solicitudes) {
            tipusServei.put(sol.getIdRequest(), sol.getTipusServei());
        }
        model.addAttribute("contratos", contratos);
        model.addAttribute("nombresAsistentes", nombresAsistentes);
        model.addAttribute("tipusServei", tipusServei);
        return "solicitudes/mis-contratos";
    }
}
