package pt.ul.fc.css.democracia2.application;

import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;
import org.springframework.stereotype.Component;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoCidadao;
import pt.ul.fc.css.democracia2.business.entities.*;
import pt.ul.fc.css.democracia2.business.handlers.*;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/** Camada Service Layer para os metodos relativos ao Cidadao */
@Component
public class CidadaoService {

  private EntityManagerFactory emf;

  private EscolheDelegadoHandler escolheDelegadoHandler;

  private FechaProjetosDeLeiExpiradosHandler fechaProjetosDeLeiExpiradosHandler;

  private FecharVotacoesHandler fecharVotacoesHandler;

  private VotarNumaVotacaoHandler votarNumaVotacaoHandler;

  /**
   * Construtor da Service Layer do Cidadao
   *
   * @param emf o Entity Manager Factory
   */
  public CidadaoService(EntityManagerFactory emf) {
    this.emf = emf;
    this.fechaProjetosDeLeiExpiradosHandler = new FechaProjetosDeLeiExpiradosHandler(emf);
    this.fecharVotacoesHandler = new FecharVotacoesHandler(emf);
    this.votarNumaVotacaoHandler = new VotarNumaVotacaoHandler(emf);
    this.escolheDelegadoHandler = new EscolheDelegadoHandler(emf);
  }

  /**
   * Funcao que cria um Cidadao
   *
   * @param name - nome do cidadao
   * @param surname - sobrenome do cidadao
   * @param cc - cartao de cidadao do cidadao
   * @return retorna o cidadao criado
   * @throws ApplicationException
   */
  public CidadaoDTO createCidadao(String name, String surname, Long cc)
      throws ApplicationException {
    CatalogoCidadao catalogoCidadao = new CatalogoCidadao(emf.createEntityManager());
    Cidadao cidadao = catalogoCidadao.newCidadao(name, surname, cc);
    catalogoCidadao.close();
    return cidadaoDtoFy(cidadao);
  }

  public CidadaoDTO findCidadao(String cc) throws ApplicationException {
    CatalogoCidadao catalogoCidadao = new CatalogoCidadao(emf.createEntityManager());
    Optional<Cidadao> aux = catalogoCidadao.findCidadaoByCC(Long.parseLong(cc));
    if (aux.isEmpty()) throw new ApplicationException("Utilizador com o CC introduzido não existe");
    catalogoCidadao.close();
    return cidadaoDtoFy(aux.get());
  }

  private CidadaoDTO cidadaoDtoFy(Cidadao c) {
    CidadaoDTO cDTO = new CidadaoDTO();
    cDTO.setId(c.getId());
    cDTO.setName(c.getName());
    cDTO.setSurname(c.getSurname());
    cDTO.setDelegados_escolhidos(c.getDelegados_escolhidos());
    return cDTO;
  }

  /**
   * Funcao que escolhe um Delegado para um determinado tema
   *
   * @param cidadaoId - id do cidadao que ira realizar a e escolha
   * @param delegadoId - id do delegado a ser escolhido
   * @param temaId - id do tema que ira associar o delegado
   * @throws ApplicationException
   */
  public void escolheDelegado(Long cidadaoId, Long delegadoId, Long temaId)
      throws ApplicationException {
    escolheDelegadoHandler.escolheDelegado(cidadaoId, delegadoId, temaId);
  }

  /**
   * Vota numa votacao
   *
   * @param cidadaoId - cidadao que ira votar
   * @param votacaoId - id da votacao
   * @param tipoDeVoto - tipo de voto
   * @param tema - tema do projeto
   * @throws ApplicationException
   */
  public void votarNumaVotacao(Long cidadaoId, Long votacaoId, TipoDeVoto tipoDeVoto, Tema tema)
      throws ApplicationException {
    votarNumaVotacaoHandler.votarNumaVotacao(cidadaoId, votacaoId, tipoDeVoto, tema);
  }
}
