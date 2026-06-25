package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.APRequestDao;
import es.uji.ei1027.sgovid.dao.AsistentePersonalDao;
import es.uji.ei1027.sgovid.dao.RegistroContratoDao;
import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.APRequest;
import es.uji.ei1027.sgovid.model.AsistentePersonal;
import es.uji.ei1027.sgovid.model.RegistroContrato;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import es.uji.ei1027.sgovid.services.SeleccionService;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tecnico")
public class TecnicoController {

    private static final int TAM_PAGINA = 10;

    // Xifrat de contrasenyes amb Jasypt (Sessió 6 EI1027)
    private final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    @Autowired private APRequestDao solicitudDao;
    @Autowired private AsistentePersonalDao asistenteDao;
    @Autowired private UsuarioOVIDao usuarioDao;
    @Autowired private SeleccionService seleccionService;
    @Autowired private RegistroContratoDao contratoDao;

    @RequestMapping("/panel")
    public String panel() {
        return "tecnico/panel";
    }

    @RequestMapping("/solicitudes")
    public String listarSolicitudes(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String estado,
            @RequestParam(defaultValue = "") String tipo,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        if (page < 1) page = 1;
        int total = solicitudDao.countRequests(q, estado, tipo);
        int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAM_PAGINA));
        if (page > totalPaginas) page = totalPaginas;

        List<APRequest> solicitudes = solicitudDao.getRequests(q, estado, tipo, sort, dir, page, TAM_PAGINA);
        model.addAttribute("solicitudes", solicitudes);

        // Nombre del usuario por cada solicitud de la página (sin id en la tabla)
        Map<String, String> nombresUsuarios = new HashMap<>();
        for (APRequest sol : solicitudes) {
            String ident = sol.getUsuariIdent();
            if (ident != null && !nombresUsuarios.containsKey(ident)) {
                try {
                    UsuarioOVI u = usuarioDao.getUsuario(Integer.parseInt(ident));
                    if (u != null) nombresUsuarios.put(ident, u.getNom() + " " + u.getCognoms());
                } catch (Exception ignored) {}
            }
        }
        model.addAttribute("nombresUsuarios", nombresUsuarios);

        model.addAttribute("q", q);
        model.addAttribute("estado", estado);
        model.addAttribute("tipo", tipo);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("page", page);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("total", total);
        return "tecnico/solicitudes";
    }

    @RequestMapping("/solicitud/{id}")
    public String verSolicitud(@PathVariable int id, Model model) {
        APRequest solicitud = solicitudDao.getRequest(id);
        if (solicitud == null) return "redirect:/tecnico/solicitudes";
        model.addAttribute("solicitud", solicitud);

        try {
            UsuarioOVI usuario = usuarioDao.getUsuario(Integer.parseInt(solicitud.getUsuariIdent()));
            if (usuario != null) model.addAttribute("nombreUsuario", usuario.getNom() + " " + usuario.getCognoms());
        } catch (Exception ignored) {}

        if ("PENDIENTE".equals(solicitud.getEstat())) {
            List<AsistentePersonal> candidatos = seleccionService.proponerCandidatos(id);
            model.addAttribute("candidatosPropuestos", candidatos);
        } else if (!"RECHAZADA".equals(solicitud.getEstat())) {
            List<AsistentePersonal> candidatos = asistenteDao.getAsistentes();
            model.addAttribute("candidatosPropuestos", candidatos);
        }

        RegistroContrato contrato = contratoDao.getContratoBySolicitud(id);
        if (contrato != null) model.addAttribute("contrato", contrato);

        return "tecnico/solicitud-detalle";
    }

    @RequestMapping("/solicitud/{id}/asignar/{dni}")
    public String asignarCandidato(@PathVariable int id, @PathVariable String dni) {
        RegistroContrato contrato = new RegistroContrato();
        contrato.setIdSolicitud(id);
        contrato.setDniAsistente(dni);
        contrato.setFechaInicio(LocalDate.now());
        contrato.setEstadoContrato("activo");
        contrato.setObservaciones("Contrato generado automáticamente al asignar candidato");
        contratoDao.addContrato(contrato);
        solicitudDao.cambiarEstado(id, "APROBADA");
        return "redirect:/tecnico/solicitud/" + id;
    }

    @RequestMapping("/solicitud/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable int id) {
        solicitudDao.cambiarEstado(id, "RECHAZADA");
        return "redirect:/tecnico/solicitudes";
    }

    @RequestMapping("/asistentes-pendientes")
    public String listarAsistentesPendientes(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        if (page < 1) page = 1;
        int total = asistenteDao.countAsistentes(q);
        int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAM_PAGINA));
        if (page > totalPaginas) page = totalPaginas;

        model.addAttribute("asistentes", asistenteDao.getAsistentes(q, sort, dir, page, TAM_PAGINA));
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("page", page);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("total", total);
        return "tecnico/asistentes-pendientes";
    }

    @RequestMapping("/aceptar-asistente/{dni}")
    public String aceptarAsistente(@PathVariable String dni) {
        AsistentePersonal a = asistenteDao.getAsistente(dni);
        if (a != null) {
            a.setEstado("ACEPTADO");
            asistenteDao.updateAsistente(a);
        }
        return "redirect:/tecnico/asistentes-pendientes";
    }

    @RequestMapping("/rechazar-asistente/{dni}")
    public String rechazarAsistente(@PathVariable String dni) {
        AsistentePersonal a = asistenteDao.getAsistente(dni);
        if (a != null) {
            a.setEstado("RECHAZADO");
            asistenteDao.updateAsistente(a);
        }
        return "redirect:/tecnico/asistentes-pendientes";
    }

    @RequestMapping("/editar-asistente/{dni}")
    public String editarAsistente(@PathVariable String dni, Model model) {
        AsistentePersonal a = asistenteDao.getAsistente(dni);
        if (a == null) return "redirect:/tecnico/asistentes-pendientes";
        model.addAttribute("asistente", a);
        return "asistente/registro";
    }

    @RequestMapping("/usuarios-pendientes")
    public String listarUsuariosPendientes(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

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
        return "tecnico/usuarios-pendientes";
    }

    @RequestMapping("/aceptar-usuario/{id}")
    public String aceptarUsuario(@PathVariable int id) {
        UsuarioOVI u = usuarioDao.getUsuario(id);
        if (u != null) {
            u.setEstatTecnicAcceptat(true);
            if (u.getIdentificadorSgovi() == null || u.getIdentificadorSgovi().isEmpty()) {
                u.setIdentificadorSgovi("USR" + String.format("%03d", id));
            }
            if (u.getContrasenya() == null || u.getContrasenya().isEmpty()) {
                // Contrasenya per defecte xifrada amb Jasypt (mai en clar a la BD)
                u.setContrasenya(passwordEncryptor.encryptPassword("ovi" + id + "2026"));
            }
            usuarioDao.updateUsuario(u);
        }
        return "redirect:/tecnico/usuarios-pendientes";
    }

    @RequestMapping("/rechazar-usuario/{id}")
    public String rechazarUsuario(@PathVariable int id) {
        UsuarioOVI u = usuarioDao.getUsuario(id);
        if (u != null) {
            u.setEstatTecnicAcceptat(false);
            usuarioDao.updateUsuario(u);
        }
        return "redirect:/tecnico/usuarios-pendientes";
    }

    @RequestMapping("/editar-usuario/{id}")
    public String editarUsuario(@PathVariable int id, Model model) {
        UsuarioOVI u = usuarioDao.getUsuario(id);
        if (u == null) return "redirect:/tecnico/usuarios-pendientes";
        // No mostramos el hash de la contrasena en el formulario de edicion
        u.setContrasenya("");
        model.addAttribute("usuario", u);
        return "usuario/update";
    }

    @RequestMapping("/contratos")
    public String listarContratos(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        if (page < 1) page = 1;
        int total = contratoDao.countContratos(q);
        int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAM_PAGINA));
        if (page > totalPaginas) page = totalPaginas;

        model.addAttribute("contratos", contratoDao.getContratos(q, sort, dir, page, TAM_PAGINA));
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("page", page);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("total", total);
        return "tecnico/contratos";
    }

    @RequestMapping(value = "/contrato/{id}/editar", method = RequestMethod.GET)
    public String editarContrato(@PathVariable int id, Model model) {
        RegistroContrato contrato = contratoDao.getContrato(id);
        if (contrato == null) return "redirect:/tecnico/contratos";
        model.addAttribute("contrato", contrato);
        return "tecnico/contrato-editar";
    }

    @RequestMapping(value = "/contrato/actualizar", method = RequestMethod.POST)
    public String actualizarContrato(@ModelAttribute("contrato") RegistroContrato contrato) {
        contratoDao.updateContrato(contrato);
        return "redirect:/tecnico/contratos";
    }

    @RequestMapping("/contrato/{id}/cerrar")
    public String cerrarContrato(@PathVariable int id) {
        RegistroContrato contrato = contratoDao.getContrato(id);
        if (contrato != null) {
            contrato.setEstadoContrato("finalizado");
            contrato.setFechaFin(LocalDate.now());
            contratoDao.updateContrato(contrato);
        }
        return "redirect:/tecnico/contratos";
    }
}
