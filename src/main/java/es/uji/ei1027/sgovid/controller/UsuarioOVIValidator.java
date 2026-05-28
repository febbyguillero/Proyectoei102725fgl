package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.model.UsuarioOVI;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class UsuarioOVIValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UsuarioOVI.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UsuarioOVI usuario = (UsuarioOVI) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nom", "required", "El nom és obligatori");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cognoms", "required", "Els cognoms són obligatoris");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dni", "required", "El DNI és obligatori");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "required", "L'email és obligatori");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "identificadorSgovi", "required", "L'identificador és obligatori");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contrasenya", "required", "La contrasenya és obligatòria");

        if (usuario.getDni() != null && !usuario.getDni().isEmpty()) {
            if (!usuario.getDni().matches("[0-9]{8}[A-Za-z]")) {
                errors.rejectValue("dni", "invalid", "El DNI ha de tenir 8 dígits i una lletra");
            }
        }

        if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
            if (!usuario.getEmail().contains("@")) {
                errors.rejectValue("email", "invalid", "L'email no és vàlid");
            }
        }

        if (usuario.getContrasenya() != null && usuario.getContrasenya().length() < 6) {
            errors.rejectValue("contrasenya", "minlength", "La contrasenya ha de tenir almenys 6 caràcters");
        }
    }
}