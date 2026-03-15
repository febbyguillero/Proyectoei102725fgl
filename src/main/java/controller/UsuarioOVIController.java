package controller;

import dao.UsuarioOVIDao;
import model.UsuarioOVI;
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

    @RequestMapping("/list")
    public String listUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioDao.getUsuarios());
        return "usuario/list";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addUsuario(Model model) {
        UsuarioOVI usuario = new UsuarioOVI();
        usuario.setDataRegistre(LocalDateTime.now());
        usuario.setConsentimentInformat(true);
        model.addAttribute("usuario", usuario);
        return "usuario/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("usuario") UsuarioOVI usuario,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "usuario/add";
        }
        usuarioDao.addUsuario(usuario);
        return "redirect:list";
    }

    @RequestMapping(value = "/update/{idUsuari}", method = RequestMethod.GET)
    public String editUsuario(Model model, @PathVariable int idUsuari) {
        model.addAttribute("usuario", usuarioDao.getUsuario(idUsuari));
        return "usuario/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("usuario") UsuarioOVI usuario,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "usuario/update";
        }
        usuarioDao.updateUsuario(usuario);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/{idUsuari}")
    public String processDelete(@PathVariable int idUsuari) {
        usuarioDao.deleteUsuario(idUsuari);
        return "redirect:../list";
    }
}