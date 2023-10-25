package pt.ul.fc.css.democracia2;

import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoTema;
import pt.ul.fc.css.democracia2.business.entities.Tema;

@Configuration
@EnableScheduling
@SpringBootApplication
public class Democracia2Application extends SpringBootServletInitializer {

  private static final Logger log = LoggerFactory.getLogger(Democracia2Application.class);

  @Autowired EntityManagerFactory emf;

  public static void main(String[] args) {
    SpringApplication.run(Democracia2Application.class, args);
  }

  @Bean
  public CommandLineRunner demo() {
    return (args) -> {
      CatalogoTema catalogoTema = new CatalogoTema(emf.createEntityManager());
      Tema tema1 = catalogoTema.createTema("Diversão", null);
      Tema tema2 = catalogoTema.createTema("Festas na FCUL", tema1);
      catalogoTema.close();
    };
  }
}
