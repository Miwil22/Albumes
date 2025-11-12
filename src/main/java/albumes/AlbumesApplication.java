package albumes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class AlbumesApplication {

    static void main(String[] args) {
        SpringApplication.run(AlbumesApplication.class, args);
    }

}