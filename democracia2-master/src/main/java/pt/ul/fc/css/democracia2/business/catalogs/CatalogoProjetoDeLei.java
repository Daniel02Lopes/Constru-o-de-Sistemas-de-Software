package pt.ul.fc.css.democracia2.business.catalogs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import pt.ul.fc.css.democracia2.business.entities.Delegado;
import pt.ul.fc.css.democracia2.business.entities.EstadoProjetoLei;
import pt.ul.fc.css.democracia2.business.entities.ProjetoDeLei;
import pt.ul.fc.css.democracia2.business.entities.Tema;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/** Classe responsável por gerir um catálogo de projetos de lei. */
public class CatalogoProjetoDeLei {
  private EntityManager em;

  /**
   * Construtor da classe.
   *
   * @param em EntityManager Utilizado para realizar as operações na base de dados.
   */
  public CatalogoProjetoDeLei(EntityManager em) {
    this.em = em;
  }

  /**
   * Lista todos os projetos de lei que estão num determinado estado.
   *
   * @param estadoProjetoLei Estado dos projetos de lei a serem listados.
   * @return Lista de projetos de lei que estão no estado indicado.
   * @throws ApplicationException Caso ocorra algum erro ao listar os projetos de lei.
   */
  public List<ProjetoDeLei> listarProjetosEstado(EstadoProjetoLei estadoProjetoLei)
      throws ApplicationException {
    try {
      TypedQuery<ProjetoDeLei> query =
          em.createQuery(
              "SELECT p FROM ProjetoDeLei p WHERE p.estadoProjetoLei = :estado",
              ProjetoDeLei.class);
      query.setParameter("estado", estadoProjetoLei);
      return query.getResultList();
    } catch (Exception e) {
      throw new ApplicationException(
          "Erro ao listar projetos de lei por estado:" + e.getMessage(), e);
    }
  }

  /**
   * Adiciona um novo projeto de lei.
   *
   * @param titulo Título do projeto de lei.
   * @param textoDescriptivo Descrição do projeto de lei.
   * @param anexo Arquivo pdf do projeto de lei.
   * @param dataValidade Data de validade do projeto de lei.
   * @param tema Tema do projeto de lei.
   * @param delegadoProponente Delegado que propôs o projeto de lei.
   * @return ID do projeto de lei adicionado.
   * @throws ApplicationException Caso ocorra algum erro ao adicionar o projeto de lei.
   */
  public Long adicionaProjetoDeLei(
      String titulo,
      String textoDescriptivo,
      byte[] anexo,
      LocalDateTime dataValidade,
      Tema tema,
      Delegado delegadoProponente)
      throws ApplicationException {

    ProjetoDeLei projeto =
        new ProjetoDeLei(titulo, textoDescriptivo, anexo, dataValidade, tema, delegadoProponente);
    em.persist(projeto);
    return projeto.getId();
  }

  /**
   * Lista todos os projetos de lei.
   *
   * @return Lista de todos os projetos de lei.
   * @throws ApplicationException Caso ocorra algum erro ao listar os projetos de lei.
   */
  public List<ProjetoDeLei> listaProjetosTodos() throws ApplicationException {
    try {
      TypedQuery<ProjetoDeLei> q =
          em.createQuery("SELECT p FROM ProjetoDeLei p", ProjetoDeLei.class);
      return q.getResultList();
    } catch (Exception e) {
      throw new ApplicationException("Erro ao listar todos os projetos", e);
    }
  }

  /**
   * Vai buscar um projeto de lei pelo seu ID.
   *
   * @param id o identificador único do projeto de lei
   * @return Optional contendo o projeto de lei encontrado
   */
  public Optional<ProjetoDeLei> findProjetoById(Long id) {
    ProjetoDeLei c = em.find(ProjetoDeLei.class, id);
    if (c != null) {
      em.refresh(c);
    }
    return Optional.ofNullable(c);
  }

  /** Encerra a conexão com a base de dados. */
  public void close() {
    em.close();
  }

  /**
   * Define o número de apoiantes de um projeto de lei e atualiza na base de dados.
   *
   * @param projeto ID do projeto de lei
   * @param numero novo número de apoiantes do projeto de lei
   * @throws ApplicationException caso não exista um projeto de lei com o id fornecido
   */
  public void setNumeroApoiantes(long projeto, int numero) throws ApplicationException {
    em.getTransaction().begin();
    ProjetoDeLei projetoLei =
        findProjetoById(projeto)
            .orElseThrow(
                () -> new ApplicationException("Nao existe ProjetoDeLei com id fornecido"));
    ;
    projetoLei.setNumerodeApoiantes(numero);
    em.merge(projetoLei);
    em.getTransaction().commit();
  }
}
