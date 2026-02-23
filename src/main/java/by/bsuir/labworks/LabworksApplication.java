package by.bsuir.labworks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс Spring Boot приложения.
 * Запускает приложение и инициализирует контекст Spring.
 */

@SpringBootApplication
public class LabworksApplication {

  public static void main(String[] args) {
    SpringApplication.run(LabworksApplication.class, args);
  }

}

