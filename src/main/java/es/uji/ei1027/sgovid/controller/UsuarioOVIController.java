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
@RequestMapping("/usuario")
public class UsuarioOVIController {

    private UsuarioOVIDao usuarioDao;

    @Autowired
    public void setUsuarioDao(UsuarioOVIDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    // LISTAR - OK
    @RequestMapping("/list")
    public String listUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioDao.getUsuarios());
        return "usuario/list";
    }

    // AÑADIR (formulario)
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addUsuario(Model model) {
        model.addAttribute("usuario", new UsuarioOVI());
        return "usuario/add";
    }

    // AÑADIR (procesar)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("usuario") UsuarioOVI usuario) {
        usuario.setDataRegistre(LocalDateTime.now());
        usuario.setConsentimentInformat(true);
        usuarioDao.addUsuario(usuario);
        return "redirect:list";
    }

    // EDITAR (formulario)
    @RequestMapping(value = "/update/{idUsuari}", method = RequestMethod.GET)
    public String editUsuario(Model model, @PathVariable int idUsuari) {
        model.addAttribute("usuario", usuarioDao.getUsuario(idUsuari));
        return "usuario/update";
    }

    // EDITAR (procesar)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("usuario") UsuarioOVI usuario) {
        usuarioDao.updateUsuario(usuario);
        return "redirect:list";
    }

    // ELIMINAR
    @RequestMapping(value = "/delete/{idUsuari}")
    public String processDelete(@PathVariable int idUsuari) {
        usuarioDao.deleteUsuario(idUsuari);
        return "redirect:../list";
    }
}