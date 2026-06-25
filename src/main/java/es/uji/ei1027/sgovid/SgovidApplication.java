package es.uji.ei1027.sgovid;

import es.uji.ei1027.sgovid.dao.UsuarioOVIDao;
import es.uji.ei1027.sgovid.model.UsuarioOVI;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.time.LocalDate;
import java.util.logging.Logger;

@SpringBootApplication
public class SgovidApplication implements CommandLineRunner {

    private static final Logger log = Logger.getLogger(SgovidApplication.class.getName());

    @Autowired
    private UsuarioOVIDao usuarioDao;

    public static void main(String[] args) {
        new SpringApplicationBuilder(SgovidApplication.class).run(args);
    }

    // El mètode run() s'executa DESPRÉS que Spring haja inicialitzat tots els components,
    // inclòs el pool de connexions a la BD (HikariCP). Per açò és el lloc correcte
    // per a fer inicialitzacions que necessiten la BD, seguint el patró de la Sessió 2.
    @Override
    public void run(String... args) throws Exception {
        log.info("Aquí va mi código");
        inicializarTecnico();
    }

    private void inicializarTecnico() {
        try {
            UsuarioOVI tecnico = usuarioDao.getUsuarioByIdentificador("TECNICO");
            if (tecnico == null) {
                BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
                UsuarioOVI nuevo = new UsuarioOVI();
                nuevo.setIdentificadorSgovi("TECNICO");
                nuevo.setContrasenya(passwordEncryptor.encryptPassword("tecnico123"));
                nuevo.setEmail("tecnico@ovi.es");
                nuevo.setNom("Tècnic");
                nuevo.setCognoms("OVI");
                nuevo.setDni("00000000T");
                nuevo.setDataNaixement(LocalDate.of(1980, 1, 1));
                nuevo.setConsentimentInformat(true);
                nuevo.setEstatTecnicAcceptat(true);
                usuarioDao.addUsuario(nuevo);
                log.info("Tècnic OVI creat a la BD.");
            }
        } catch (Exception e) {
            log.warning("No s'ha pogut inicialitzar el tècnic: " + e.getMessage());
        }
    }
}