package es.uji.ei1027.sgovid.controller;

import es.uji.ei1027.sgovid.model.APRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class SolicitudValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return APRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        APRequest solicitud = (APRequest) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tipusServei", "required", "El tipus de servei és obligatori");

        if (solicitud.getTipusServei() != null &&
                !solicitud.getTipusServei().equals("PAP") &&
                !solicitud.getTipusServei().equals("PATI")) {
            errors.rejectValue("tipusServei", "invalid", "El tipus de servei ha de ser PAP o PATI");
        }

        if (solicitud.getObservations() != null && solicitud.getObservations().length() > 300) {
            errors.rejectValue("observations", "maxlength", "Les observacions no poden superar els 300 caràcters");
        }
    }
}