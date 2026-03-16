import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SgovidConfiguration {

    // Configura el acceso a la base de datos (DataSource)
    // a partir de las propiedades en src/main/resources/application.properties
    // que comienzan con el prefijo spring.datasource
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}