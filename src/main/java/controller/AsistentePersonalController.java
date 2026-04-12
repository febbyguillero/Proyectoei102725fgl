package controller;

import dao.AsistentePersonalDao;
import model.AsistentePersonal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/asistente")
public class AsistentePersonalController {

    @Autowired
    private AsistentePersonalDao asistenteDao;

    @RequestMapping("/registro")
    public String registroAsistente(Model model) {
        model.addAttribute("asistente", new AsistentePersonal());
        return "asistente/registro";
    }

    @RequestMapping(value = "/registro", method = RequestMethod.POST)
    public String procesarRegistro(@ModelAttribute("asistente") AsistentePersonal asistente,
                                   BindingResult bindingResult) {
        AsistentePersonalValidator validator = new AsistentePersonalValidator();
        validator.validate(asistente, bindingResult);

        if (bindingResult.hasErrors()) {
            return "asistente/registro";
        }

        asistente.setEstado(false);
        asistenteDao.addAsistente(asistente);
        return "redirect:/";
    }
}