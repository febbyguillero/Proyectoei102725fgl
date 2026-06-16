package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.dao.ActividadFormacionDao;
import es.uji.ei1027.sgovid.model.ActividadFormacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/actividad")
public class ActividadFormacionController {

    private static final int TAM_PAGINA = 10;

    @Autowired
    private ActividadFormacionDao actividadDao;

    @GetMapping("/agenda")
    public String agendaPublica(Model model) {
        model.addAttribute("actividades", actividadDao.getActividades());
        return "actividad/agenda";
    }

    @GetMapping("/list")
    public String listActividades(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        if (page < 1) page = 1;
        int total = actividadDao.countActividades(q);
        int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAM_PAGINA));
        if (page > totalPaginas) page = totalPaginas;

        model.addAttribute("actividades", actividadDao.getActividades(q, sort, dir, page, TAM_PAGINA));
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("page", page);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("total", total);
        return "actividad/list";
    }

    @GetMapping("/add")
    public String addActividad(Model model) {
        model.addAttribute("actividad", new ActividadFormacion());
        return "actividad/add";
    }

    @PostMapping("/add")
    public String processAdd(@ModelAttribute("actividad") ActividadFormacion actividad,
                             BindingResult bindingResult) {
        if (actividad.getTitulo() == null || actividad.getTitulo().isEmpty()) {
            bindingResult.rejectValue("titulo", "required", "El títol és obligatori");
        }
        if (actividad.getTipoActividad() == null || actividad.getTipoActividad().isEmpty()) {
            bindingResult.rejectValue("tipoActividad", "required", "El tipus és obligatori");
        }
        if (bindingResult.hasErrors()) {
            return "actividad/add";
        }
        if (actividad.getEstado() == null || actividad.getEstado().isEmpty()) {
            actividad.setEstado("programada");
        }
        actividadDao.addActividad(actividad);
        return "redirect:/actividad/list";
    }

    @GetMapping("/update/{id}")
    public String editActividad(@PathVariable int id, Model model) {
        ActividadFormacion a = actividadDao.getActividad(id);
        if (a == null) return "redirect:/actividad/list";
        model.addAttribute("actividad", a);
        return "actividad/update";
    }

    @PostMapping("/update")
    public String processUpdate(@ModelAttribute("actividad") ActividadFormacion actividad) {
        if (actividad.getFechaInicio() == null || actividad.getFechaFin() == null) {
            ActividadFormacion original = actividadDao.getActividad(actividad.getIdActividad());
            if (original != null) {
                if (actividad.getFechaInicio() == null) actividad.setFechaInicio(original.getFechaInicio());
                if (actividad.getFechaFin() == null) actividad.setFechaFin(original.getFechaFin());
            }
        }
        actividadDao.updateActividad(actividad);
        return "redirect:/actividad/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteActividad(@PathVariable int id) {
        actividadDao.deleteActividad(id);
        return "redirect:/actividad/list";
    }
}
