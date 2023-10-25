package pt.ul.fc.css.democracia2.application;

import jakarta.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoTema;
import pt.ul.fc.css.democracia2.business.entities.*;
import pt.ul.fc.css.democracia2.business.handlers.*;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/** Camada Service Layer para os metodos relativos ao ProjetoDeLei */
@Component
public class ProjetoDeLeiService {
  private ApoiarProjetosDeLeiHandler apoiarProjetosDeLeiHandler;
  private ApresentarProjetoDeLeiHandler apresentaProjetoDeLeiHandler;

  private ListarVotacoesPassadasHandler listarVotacoesPassadasHandler;
  private ListarVotacoesEmCursoHandler listarVotacoesEmCursoHandler;

  private ConsultaProjetosDeLeiNaoExpiradosHandler consultaProjetosDeLeiNaoExpiradosHandler;
  private FecharVotacoesHandler fecharVotacoesHandler;
  private FechaProjetosDeLeiExpiradosHandler fechaProjetosDeLeiExpiradosHandler;
  private EntityManagerFactory emf;

  /**
   * Construtor da Service Layer para o ProjetoDeLei que inicializa todos os handlers para os casos
   * de uso.
   *
   * @param emf - o Entity Manager Factory
   */
  public ProjetoDeLeiService(EntityManagerFactory emf) {
    this.emf = emf;
    this.apresentaProjetoDeLeiHandler = new ApresentarProjetoDeLeiHandler(emf);
    this.apoiarProjetosDeLeiHandler = new ApoiarProjetosDeLeiHandler(emf);
    this.listarVotacoesPassadasHandler = new ListarVotacoesPassadasHandler(emf);
    this.fechaProjetosDeLeiExpiradosHandler = new FechaProjetosDeLeiExpiradosHandler(emf);
    this.consultaProjetosDeLeiNaoExpiradosHandler =
        new ConsultaProjetosDeLeiNaoExpiradosHandler(emf);
    this.fecharVotacoesHandler = new FecharVotacoesHandler(emf);
    this.listarVotacoesEmCursoHandler = new ListarVotacoesEmCursoHandler(emf);
  }

  /**
   * Funcao para apoiar um ProjetoDeLei
   *
   * @param cidadaoId - id do cidadao que esta apoiar
   * @param projetoDeLeiId - id do projeto a ser apoianto
   * @throws ApplicationException
   */
  public void apoiarProjeto(Long cidadaoId, Long projetoDeLeiId) throws ApplicationException {
    apoiarProjetosDeLeiHandler.apoiaProjetoDeLei(cidadaoId, projetoDeLeiId);
  }

  public List<Tema> getTemas() {
    CatalogoTema catalogoTema = new CatalogoTema(emf.createEntityManager());
    List<Tema> temas = catalogoTema.getTemas();
    catalogoTema.close();
    return temas;
  }

  /**
   * Funcao para apresentar um ProjetoDeLei
   *
   * @param titulo - titulo do projeto
   * @param textoDescriptivo - texto descritivo de um projeto
   * @param anexo - ficheiro pdf para
   * @param dataValidade - dataValidade de um projeto
   * @param tema - tema do projeto
   * @param delegadoProponente - delegado proponente do projeto
   * @return retorna o id do projeto criado
   * @throws ApplicationException
   * @throws IOException
   */
  public Long apresentaProjetoDeLei(
      String titulo,
      String textoDescriptivo,
      File anexo,
      LocalDateTime dataValidade,
      Long tema,
      Long delegadoProponente)
      throws ApplicationException, IOException {
    return apresentaProjetoDeLeiHandler.apresentaProjetoDeLei(
        titulo, textoDescriptivo, anexo, dataValidade, tema, delegadoProponente);
  }

  /**
   * Lista os projetosDeLei nao expirados e os detades do projeto que pretendeu consultar
   *
   * @return retorna uma string com a lista de projetosDeLei nao expirados e os detalhes do projeto
   *     a consutlar
   * @throws ApplicationException
   */
  public List<ProjetoDeLeiDTO> consultaListaProjetosNaoExpirados() throws ApplicationException {
    List<ProjetoDeLeiDTO> list = new LinkedList<>();
    for (ProjetoDeLei proj :
        consultaProjetosDeLeiNaoExpiradosHandler.consultaListaProjetosNaoExpirados()) {
      ProjetoDeLeiDTO projDto = projetoDeLeiDtofy(proj);
      list.add(projDto);
    }
    return list;
  }

  public Optional<ProjetoDeLeiDTO> consultaProjetoNaoExpirado(Long id) throws ApplicationException {
    return Optional.of(
        projetoDeLeiDtofy(consultaProjetosDeLeiNaoExpiradosHandler.consultaProjetoNaoExpirado(id)));
  }

  public void fecharVotacoes() throws ApplicationException {
    fecharVotacoesHandler.fecharVotacoes();
  }

  public void votarNumaVotacao(Long cidadaoId, Long votacaoId, Tema tema, String voto)
      throws ApplicationException {
    VotarNumaVotacaoHandler votacaoHandler = new VotarNumaVotacaoHandler(emf);
    votacaoHandler.votarNumaVotacao(cidadaoId, votacaoId, TipoDeVoto.valueOf(voto), tema);
  }

  /**
   * Devolve uma lista com as votacoes que passadas/ que ja expiraram
   *
   * @return list das votacoes expiradas
   * @throws ApplicationException
   */
  public List<Votacao> listarVotacoesPassadas() throws ApplicationException {
    fechaProjetosDeLeiExpirados();
    fechaVotacoes();
    return listarVotacoesPassadasHandler.listarVotacoesPassadas();
  }

  /**
   * Devolve uma lista com as votacoes em curso
   *
   * @return lista de votacoes em curso
   * @throws ApplicationException
   */
  public List<ProjetoDeLeiDTO> listarVotacoesEmCurso() throws ApplicationException {
    List<ProjetoDeLeiDTO> list = new LinkedList<>();
    fechaProjetosDeLeiExpirados();
    fechaVotacoes();
    for (ProjetoDeLei proj : listarVotacoesEmCursoHandler.listaProjetosVotacao()) {
      ProjetoDeLeiDTO projDto = projetoDeLeiDtofy(proj);
      list.add(projDto);
    }
    return list;
  }

  /**
   * Fecha todos os projetos de Lei e Votacoes expiradas (Caso de Uso interno)
   *
   * @throws ApplicationException
   */
  public void fechaProjetosDeLeiExpirados() throws ApplicationException {
    fechaProjetosDeLeiExpiradosHandler.fechaProjetosExpirados();
  }

  public TipoDeVoto getVotoPorOmissao(Long cidadaoId, Long votacaoId, Tema tema)
      throws ApplicationException {
    VotarNumaVotacaoHandler votacaoHandler = new VotarNumaVotacaoHandler(emf);
    return votacaoHandler.getVotoOmissao(cidadaoId, votacaoId, tema);
  }

  public void fechaVotacoes() throws ApplicationException {
    fecharVotacoesHandler.fecharVotacoes();
  }

  private ProjetoDeLeiDTO projetoDeLeiDtofy(ProjetoDeLei proj) {
    ProjetoDeLeiDTO projDto = new ProjetoDeLeiDTO();
    projDto.setId(proj.getId());
    projDto.setEstadoProjetoLei(proj.getEstadoProjetoLei());
    projDto.setDataValidade(proj.getDataValidade());
    projDto.setTema(proj.getTema());
    if (proj.getVotacao() != null) projDto.setVotacao(votacaoDtofy(proj.getVotacao()));
    else {
      projDto.setVotacao(null);
    }
    projDto.setDelegadoProponente(proj.getDelegadoProponente());
    projDto.setListaCidadaosApoiantes(proj.getApoiantes());
    projDto.setNumerodeApoiantes(proj.getNumerodeApoiantes());
    projDto.setTitulo(proj.getTitulo());
    projDto.setTextoDescriptivo(proj.getTextoDescriptivo());
    return projDto;
  }

  private VotacaoDTO votacaoDtofy(Votacao vot) {
    VotacaoDTO votDto = new VotacaoDTO();
    votDto.setId(vot.getId());
    votDto.setDataFecho(vot.getDataFecho());
    votDto.setVotos_desfavoravies(vot.getVotos_desfavoravies());
    votDto.setVotos_favoraveis(vot.getVotos_favoraveis());
    return votDto;
  }
}
