package albumes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan; // Importar
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; // Importar

@EnableCaching
@SpringBootApplication(scanBasePackages = {"albumes", "artistas"})
// AÑADE ESTAS DOS LÍNEAS:
@EntityScan(basePackages = {"albumes", "artistas"})
@EnableJpaRepositories(basePackages = {"albumes", "artistas"})
public class AlbumesApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlbumesApplication.class, args);
    }

}