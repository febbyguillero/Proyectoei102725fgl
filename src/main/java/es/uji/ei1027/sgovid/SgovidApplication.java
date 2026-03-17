package es.uji.ei1027.sgovid;

import java.util.logging.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SgovidApplication implements CommandLineRunner {

    private static final Logger log = Logger.getLogger(SgovidApplication.class.getName());

    public static void main(String[] args) {
        new SpringApplicationBuilder(SgovidApplication.class).run(args);
    }

    public void run(String... strings) throws Exception {
        log.info("Aquí va mi código");
    }
}