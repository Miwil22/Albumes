package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan; // Importar para escanear entidades
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; // Importar para escanear repositorios

// @EnableCaching: Activa la "memoria rápida" (caché).
// Permite que si pedimos el mismo dato dos veces, la segunda vez no tenga que ir a la base de datos, sino que lo saque de la RAM.
@EnableCaching

// @SpringBootApplication: ¡LA ANOTACIÓN MAESTRA!
// Le dice a Java: "Esto es una aplicación Spring Boot".
// scanBasePackages: Como tenemos el código partido en dos carpetas ("albumes" y "artistas"),
// tenemos que decirle explícitamente: "Oye, busca componentes (controladores, servicios) en ESTAS dos carpetas".
@SpringBootApplication(scanBasePackages = {"org.example.albumes", "org.example.artistas"})

// @EntityScan: Configuración de la Base de Datos (Modelos).
// Le dice a Spring: "Busca las clases que tengan @Entity (tus tablas) en estos paquetes".
// Si no pones esto, no encontrará la tabla 'Artista' ni 'Album' y dará error al arrancar.
@EntityScan(basePackages = {"org.example.albumes", "org.example.artistas"})

// @EnableJpaRepositories: Configuración de los Almacenes (Repositorios).
// Le dice a Spring: "Busca las interfaces que hereden de JpaRepository en estos paquetes".
// Es necesario para que funcionen los métodos mágicos como .save() o .findAll().
@EnableJpaRepositories(basePackages = {"org.example.albumes", "org.example.artistas"})
public class Application {

    // El método MAIN estándar de Java. Es el punto de entrada.
    public static void main(String[] args) {
        // Esta línea arranca todo el framework:
        // 1. Levanta el servidor Tomcat (para recibir peticiones web).
        // 2. Conecta la base de datos H2.
        // 3. Crea todos los objetos (Servicios, Controladores) y los conecta entre sí.
        SpringApplication.run(Application.class, args);
    }

}