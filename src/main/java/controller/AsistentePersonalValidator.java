package controller;

import model.AsistentePersonal;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AsistentePersonalValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return AsistentePersonal.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        AsistentePersonal asistente = (AsistentePersonal) obj;

        // 1. DNI obligatorio y formato: 8 números + 1 letra
        if (asistente.getDni() == null || asistente.getDni().trim().isEmpty()) {
            errors.rejectValue("dni", "obligatorio", "El DNI es obligatorio");
        } else if (!asistente.getDni().matches("\\d{8}[A-Za-z]")) {
            errors.rejectValue("dni", "formato", "El DNI debe tener 8 dígitos y una letra");
        }

        // 2. Nombre obligatorio
        if (asistente.getNombre() == null || asistente.getNombre().trim().isEmpty()) {
            errors.rejectValue("nombre", "obligatorio", "El nombre es obligatorio");
        }

        // 3. Email obligatorio y formato
        String email = asistente.getEmail();
        if (email == null || email.trim().isEmpty()) {
            errors.rejectValue("email", "obligatorio", "El email es obligatorio");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.rejectValue("email", "formato", "El email debe tener un formato válido");
        }

        // 4. Teléfono (opcional pero si se pone, 9 dígitos)
        String telf = asistente.getTelefono();
        if (telf != null && !telf.trim().isEmpty() && !telf.matches("\\d{9}")) {
            errors.rejectValue("telefono", "longitud", "El teléfono debe tener 9 dígitos");
        }

        // 5. Edad (mayor de 18 años)
        if (asistente.getEdad() == null) {
            errors.rejectValue("edad", "obligatorio", "La edad es obligatoria");
        } else if (asistente.getEdad() < 18) {
            errors.rejectValue("edad", "valor", "Debe ser mayor de 18 años");
        }
    }
}