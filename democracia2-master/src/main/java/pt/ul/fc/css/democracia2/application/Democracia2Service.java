package pt.ul.fc.css.democracia2.application;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Component;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

@Component
public class Democracia2Service {

  private CidadaoDTO user;
  public ProjetoDeLeiService projetoDeLeiService;

  public CidadaoService cidadaoService;

  public DelegadoService delegadoService;

  public Democracia2Service(EntityManagerFactory emf) {
    cidadaoService = new CidadaoService(emf);
    delegadoService = new DelegadoService(emf);
    projetoDeLeiService = new ProjetoDeLeiService(emf);
  }

  public CidadaoDTO getUser() {
    return user;
  }

  public void setUser(String cc) throws ApplicationException {
    this.user = cidadaoService.findCidadao(cc);
  }
}
