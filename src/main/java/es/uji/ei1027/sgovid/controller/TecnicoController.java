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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tecnico")
public class TecnicoController {

    private static final int TAM_PAGINA = 10;

    private final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    @Autowired private APRequestDao solicitudDao;
    @Autowired private AsistentePersonalDao asistenteDao;
    @Autowired private UsuarioOVIDao usuarioDao;
    @Autowired private SeleccionService seleccionService;
    @Autowired private RegistroContratoDao contratoDao;

    private String comprobarRolTecnico(HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (rol == null) return "redirect:/login";
        if (!"TECNICO".equals(rol)) return "redirect:/solicitudes/mis-solicitudes";
        return null;
    }

    private void noCachear(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    @RequestMapping("/panel")
    public String panel(HttpSession session, HttpServletResponse response) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        noCachear(response);
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
            HttpSession session, HttpServletResponse response,
            Model model) {

        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        noCachear(response);

        if (page < 1) page = 1;
        int total = solicitudDao.countRequests(q, estado, tipo);
        int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAM_PAGINA));
        if (page > totalPaginas) page = totalPaginas;

        List<APRequest> solicitudes = solicitudDao.getRequests(q, estado, tipo, sort, dir, page, TAM_PAGINA);
        model.addAttribute("solicitudes", solicitudes);

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
    public String verSolicitud(@PathVariable int id,
                               HttpSession session, HttpServletResponse response,
                               Model model) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        noCachear(response);

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
        } else if (!"APROBADA".equals(solicitud.getEstat()) && !"RECHAZADA".equals(solicitud.getEstat())) {
            List<AsistentePersonal> candidatos = asistenteDao.getAsistentes();
            model.addAttribute("candidatosPropuestos", candidatos);
        }

        RegistroContrato contrato = contratoDao.getContratoBySolicitud(id);
        if (contrato != null) model.addAttribute("contrato", contrato);

        return "tecnico/solicitud-detalle";
    }

    @RequestMapping("/solicitud/{id}/asignar/{dni}")
    public String asignarCandidato(@PathVariable int id, @PathVariable String dni,
                                   HttpSession session, Model model) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;

        APRequest solicitud = solicitudDao.getRequest(id);
        if (solicitud == null) return "redirect:/tecnico/solicitudes";

        AsistentePersonal asistente = asistenteDao.getAsistente(dni);
        if (asistente == null) {
            model.addAttribute("solicitud", solicitud);
            model.addAttribute("errorAsignacion", "No s'ha trobat l'assistent amb DNI: " + dni);
            List<AsistentePersonal> candidatos = seleccionService.proponerCandidatos(id);
            model.addAttribute("candidatosPropuestos", candidatos);
            return "tecnico/solicitud-detalle";
        }

        RegistroContrato contratoExistente = contratoDao.getContratoBySolicitud(id);
        if (contratoExistente != null && "activo".equals(contratoExistente.getEstadoContrato())) {
            return "redirect:/tecnico/solicitud/" + id;
        }

        RegistroContrato contrato = new RegistroContrato();
        contrato.setIdSolicitud(id);
        contrato.setDniAsistente(dni);
        contrato.setFechaInicio(LocalDate.now());
        contrato.setEstadoContrato("activo");
        contrato.setObservaciones("Contrato generado automàticament al assignar candidat");
        contratoDao.addContrato(contrato);
        solicitudDao.cambiarEstado(id, "APROBADA");
        return "redirect:/tecnico/solicitud/" + id;
    }

    @RequestMapping("/solicitud/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable int id, HttpSession session) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        solicitudDao.cambiarEstado(id, "RECHAZADA");
        return "redirect:/tecnico/solicitudes";
    }

    @RequestMapping("/asistentes-pendientes")
    public String listarAsistentesPendientes(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "1") int page,
            HttpSession session, HttpServletResponse response,
            Model model) {

        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        noCachear(response);

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
    public String aceptarAsistente(@PathVariable String dni, HttpSession session) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        AsistentePersonal a = asistenteDao.getAsistente(dni);
        if (a != null) {
            a.setEstado("ACEPTADO");
            asistenteDao.updateAsistente(a);
        }
        return "redirect:/tecnico/asistentes-pendientes";
    }

    @RequestMapping("/rechazar-asistente/{dni}")
    public String rechazarAsistente(@PathVariable String dni, HttpSession session) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        AsistentePersonal a = asistenteDao.getAsistente(dni);
        if (a != null) {
            a.setEstado("RECHAZADO");
            asistenteDao.updateAsistente(a);
        }
        return "redirect:/tecnico/asistentes-pendientes";
    }

    @RequestMapping("/editar-asistente/{dni}")
    public String editarAsistente(@PathVariable String dni,
                                  HttpSession session, Model model) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
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
            HttpSession session, HttpServletResponse response,
            Model model) {

        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        noCachear(response);

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
    public String aceptarUsuario(@PathVariable int id, HttpSession session) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        UsuarioOVI u = usuarioDao.getUsuario(id);
        if (u != null) {
            u.setEstatTecnicAcceptat(true);
            String ident = u.getIdentificadorSgovi();
            if (ident == null || ident.isEmpty() || ident.startsWith("PENDENT")) {
                u.setIdentificadorSgovi("USR" + String.format("%03d", id));
            }
            if (u.getContrasenya() == null || u.getContrasenya().isEmpty()) {
                u.setContrasenya(passwordEncryptor.encryptPassword("ovi" + id + "2026"));
            }
            usuarioDao.updateUsuario(u);
        }
        return "redirect:/tecnico/usuarios-pendientes";
    }

    @RequestMapping("/rechazar-usuario/{id}")
    public String rechazarUsuario(@PathVariable int id, HttpSession session) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        UsuarioOVI u = usuarioDao.getUsuario(id);
        if (u != null) {
            u.setEstatTecnicAcceptat(false);
            usuarioDao.updateUsuario(u);
        }
        return "redirect:/tecnico/usuarios-pendientes";
    }

    @RequestMapping("/editar-usuario/{id}")
    public String editarUsuario(@PathVariable int id,
                                HttpSession session, Model model) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        UsuarioOVI u = usuarioDao.getUsuario(id);
        if (u == null) return "redirect:/tecnico/usuarios-pendientes";
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
            HttpSession session, HttpServletResponse response,
            Model model) {

        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        noCachear(response);

        // Obtener todos los contratos ordenados (sin paginar aún) para poder filtrar por nombre
        List<RegistroContrato> todosContratos = contratoDao.getContratos("", sort, dir, 1, Integer.MAX_VALUE);

        // Construir mapas de nombres para todos los contratos
        Map<Integer, String> nombresUsuarios = new HashMap<>();
        Map<String, String> nombresAsistentes = new HashMap<>();
        for (RegistroContrato c : todosContratos) {
            if (c.getIdSolicitud() != null && !nombresUsuarios.containsKey(c.getIdSolicitud())) {
                try {
                    APRequest sol = solicitudDao.getRequest(c.getIdSolicitud());
                    if (sol != null) {
                        UsuarioOVI u = usuarioDao.getUsuario(Integer.parseInt(sol.getUsuariIdent()));
                        if (u != null) nombresUsuarios.put(c.getIdSolicitud(), u.getNom() + " " + u.getCognoms());
                    }
                } catch (Exception ignored) {}
            }
            if (c.getDniAsistente() != null && !nombresAsistentes.containsKey(c.getDniAsistente())) {
                AsistentePersonal a = asistenteDao.getAsistente(c.getDniAsistente());
                if (a != null) nombresAsistentes.put(c.getDniAsistente(), a.getNombre() + " " + a.getApellidos());
            }
        }

        // Filtrar por q: busca en estado, nombre usuario, nombre asistente (case-insensitive)
        String qLower = (q != null) ? q.trim().toLowerCase() : "";
        List<RegistroContrato> contratosFiltrados = new ArrayList<>();
        for (RegistroContrato c : todosContratos) {
            if (qLower.isEmpty()) {
                contratosFiltrados.add(c);
            } else {
                String nomUsuari = nombresUsuarios.getOrDefault(c.getIdSolicitud(), "").toLowerCase();
                String nomAssistent = nombresAsistentes.getOrDefault(c.getDniAsistente(), "").toLowerCase();
                String estat = c.getEstadoContrato() != null ? c.getEstadoContrato().toLowerCase() : "";
                String obs = c.getObservaciones() != null ? c.getObservaciones().toLowerCase() : "";
                if (nomUsuari.contains(qLower) || nomAssistent.contains(qLower)
                        || estat.contains(qLower) || obs.contains(qLower)) {
                    contratosFiltrados.add(c);
                }
            }
        }

        // Paginar manualmente
        int total = contratosFiltrados.size();
        if (page < 1) page = 1;
        int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAM_PAGINA));
        if (page > totalPaginas) page = totalPaginas;
        int desde = (page - 1) * TAM_PAGINA;
        int hasta = Math.min(desde + TAM_PAGINA, total);
        List<RegistroContrato> contratos = contratosFiltrados.subList(desde, hasta);

        model.addAttribute("contratos", contratos);
        model.addAttribute("nombresUsuarios", nombresUsuarios);
        model.addAttribute("nombresAsistentes", nombresAsistentes);
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("page", page);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("total", total);
        return "tecnico/contratos";
    }

    @RequestMapping(value = "/contrato/{id}/editar", method = RequestMethod.GET)
    public String editarContrato(@PathVariable int id,
                                 HttpSession session, Model model) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        RegistroContrato contrato = contratoDao.getContrato(id);
        if (contrato == null) return "redirect:/tecnico/contratos";

        if (contrato.getDniAsistente() != null) {
            AsistentePersonal a = asistenteDao.getAsistente(contrato.getDniAsistente());
            if (a != null) model.addAttribute("nombreAsistente", a.getNombre() + " " + a.getApellidos());
        }
        try {
            APRequest sol = solicitudDao.getRequest(contrato.getIdSolicitud());
            if (sol != null) {
                UsuarioOVI u = usuarioDao.getUsuario(Integer.parseInt(sol.getUsuariIdent()));
                if (u != null) model.addAttribute("nombreUsuario", u.getNom() + " " + u.getCognoms());
            }
        } catch (Exception ignored) {}

        model.addAttribute("contrato", contrato);
        return "tecnico/contrato-editar";
    }

    @RequestMapping(value = "/contrato/actualizar", method = RequestMethod.POST)
    public String actualizarContrato(@ModelAttribute("contrato") RegistroContrato contrato,
                                     HttpSession session, Model model) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;

        RegistroContrato original = contratoDao.getContrato(contrato.getIdContrato());
        if (original == null) return "redirect:/tecnico/contratos";

        contrato.setFechaInicio(original.getFechaInicio());
        contrato.setFechaRegistro(original.getFechaRegistro());
        contrato.setDocumentoPdf(original.getDocumentoPdf());
        // Conservar el dni_asistente original (el formulario lo envía como hidden pero puede llegar vacío)
        if (contrato.getDniAsistente() == null || contrato.getDniAsistente().trim().isEmpty()) {
            contrato.setDniAsistente(original.getDniAsistente());
        }

        // Si el estado es activo, limpiar la fecha de fin
        if ("activo".equals(contrato.getEstadoContrato())) {
            contrato.setFechaFin(null);
        }

        // Validar que fecha_fin >= fecha_inicio
        if (contrato.getFechaFin() != null && contrato.getFechaFin().isBefore(contrato.getFechaInicio())) {
            model.addAttribute("contrato", contrato);
            model.addAttribute("errorActualizar", "La data de fi no pot ser anterior a la data d'inici (" + contrato.getFechaInicio() + ").");
            return "tecnico/contrato-editar";
        }

        try {
            contratoDao.updateContrato(contrato);
        } catch (Exception e) {
            model.addAttribute("contrato", contrato);
            model.addAttribute("errorActualizar", "No s'han pogut guardar els canvis. Comprova les dades i torna-ho a intentar.");
            return "tecnico/contrato-editar";
        }
        return "redirect:/tecnico/contratos";
    }

    @RequestMapping("/contrato/{id}/cerrar")
    public String cerrarContrato(@PathVariable int id, HttpSession session) {
        String redir = comprobarRolTecnico(session);
        if (redir != null) return redir;
        RegistroContrato contrato = contratoDao.getContrato(id);
        if (contrato != null) {
            contrato.setEstadoContrato("finalizado");
            contrato.setFechaFin(LocalDate.now());
            contratoDao.updateContrato(contrato);
        }
        return "redirect:/tecnico/contratos";
    }
}