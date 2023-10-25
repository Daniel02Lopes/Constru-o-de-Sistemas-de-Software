package pt.ul.fc.css.democracia2.application;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoCidadao;
import pt.ul.fc.css.democracia2.business.entities.Cidadao;
import pt.ul.fc.css.democracia2.business.entities.Delegado;
import pt.ul.fc.css.democracia2.business.handlers.FechaProjetosDeLeiExpiradosHandler;
import pt.ul.fc.css.democracia2.business.handlers.FecharVotacoesHandler;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/** Camada Service Layer para os metodos relativos ao Delegado */
public class DelegadoService {
  private EntityManagerFactory emf;
  private FecharVotacoesHandler fecharVotacoesHandler;
  private FechaProjetosDeLeiExpiradosHandler fechaProjetosDeLeiExpiradosHandler;

  /**
   * Construtor da Service Layer para o Delegado que inicializa todos os handlers para os casos de
   * uso.
   *
   * @param emf - o Entity Manager Factory
   */
  public DelegadoService(EntityManagerFactory emf) {
    this.emf = emf;
    this.fecharVotacoesHandler = new FecharVotacoesHandler(emf);
    this.fechaProjetosDeLeiExpiradosHandler = new FechaProjetosDeLeiExpiradosHandler(emf);
  }

  /**
   * Funcao que cria um Delegado
   *
   * @param name - nome do Delegado
   * @param surname - sobrenome do Delegado
   * @param cc - cartao de cidadao do Delegado
   * @return retorna o delegado criado
   * @throws ApplicationException
   */
  public DelegadoDTO createDelegado(String name, String surname, Long cc)
      throws ApplicationException {
    CatalogoCidadao catalogoCidadao = new CatalogoCidadao(emf.createEntityManager());
    Delegado delegado = catalogoCidadao.newDelegado(name, surname, cc);
    catalogoCidadao.close();
    return delegadoDtofy(delegado);
  }

  public DelegadoDTO delegadoDtofy(Delegado d) {
    DelegadoDTO cDTO = new DelegadoDTO();
    cDTO.setId(d.getId());
    cDTO.setName(d.getName());
    cDTO.setSurname(d.getSurname());
    cDTO.setDelegados_escolhidos(d.getDelegados_escolhidos());
    cDTO.setProjetosDeLeiPropostos(d.getProjetosDeLeiPropostos());
    return cDTO;
  }

  public boolean isDelegado(long id) {
    CatalogoCidadao catalogoCidadao = new CatalogoCidadao(emf.createEntityManager());
    Optional<Cidadao> c = catalogoCidadao.findCidadaoById(id);
    catalogoCidadao.close();
    return c.get() instanceof Delegado;
  }

  public List<Delegado> getDelegados() {
    CatalogoCidadao catalogoDel = new CatalogoCidadao(emf.createEntityManager());
    List<Delegado> delegados = catalogoDel.getDelegados();
    catalogoDel.close();
    return delegados;
  }
}
