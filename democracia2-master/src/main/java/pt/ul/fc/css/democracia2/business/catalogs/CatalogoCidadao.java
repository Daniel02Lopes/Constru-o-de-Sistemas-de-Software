package pt.ul.fc.css.democracia2.business.catalogs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.lang.NonNull;
import pt.ul.fc.css.democracia2.business.entities.Cidadao;
import pt.ul.fc.css.democracia2.business.entities.Delegado;
import pt.ul.fc.css.democracia2.business.entities.Tema;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/** Classe que responsavel por gerir um catalogo de Cidadao */
public class CatalogoCidadao {
  private EntityManager em;

  /**
   * Contrutor do Catalogo de Cidadao
   *
   * @param em EntityManager
   */
  public CatalogoCidadao(EntityManager em) {
    this.em = em;
  }

  /**
   * Funcao que cria um novo cidadao e adiciona ah base de dados
   *
   * @param nome - nome do cidadao
   * @param surname - sobrenome do cidadao
   * @param cc - cartao de cidadao do cidadao
   * @return retorna o objecto criado do tipo Cidadao
   * @throws ApplicationException
   */
  public Cidadao newCidadao(@NonNull String nome, @NonNull String surname, @NonNull Long cc)
      throws ApplicationException {
    try {
      em.getTransaction().begin();
      Cidadao c = new Cidadao(nome, surname, cc);
      em.persist(c);
      em.getTransaction().commit();
      return c;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new ApplicationException("Erro ao criar um cidadao");
    }
  }

  /**
   * Funcao que cria um novo Delegado e adiciona ah base de dados
   *
   * @param nome - nome do delegado
   * @param surname - sobrenome do delegado
   * @param cc - cartao de cidadao do delegado
   * @return retorna o obejto criado do delegado
   * @throws ApplicationException
   */
  public Delegado newDelegado(@NonNull String nome, @NonNull String surname, @NonNull Long cc)
      throws ApplicationException {
    try {
      em.getTransaction().begin();
      Delegado d = new Delegado(nome, surname, cc);
      em.persist(d);
      em.getTransaction().commit();
      return d;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new ApplicationException("Erro ao criar um Delegado");
    }
  }

  /**
   * Atribui um Cidadao atribui um dado delegado a um tema
   *
   * @param cidadao cidadao que ira escolher o delegadoi
   * @param delegado delegado a ser atribuido a um tema
   * @param tema tema escolhido
   * @throws ApplicationException
   */
  public void atribuiDelegadoATema(Cidadao cidadao, Delegado delegado, Tema tema)
      throws ApplicationException {
    try {
      em.getTransaction().begin();
      Map<Tema, Delegado> delegadosEscolhidos = cidadao.getDelegados_escolhidos();
      if (delegadosEscolhidos.containsKey(tema)) {
        throw new ApplicationException("Já existe um delegado associado a este tema.");
      }
      delegadosEscolhidos.put(tema, delegado);
      em.merge(cidadao);
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new ApplicationException("Erro ao atribuir um Delegado a um Tema");
    }
  }

  /**
   * Obtem um possivel delegado escolhido a um tema caso este exista
   *
   * @param cidadao cidadao que escolheu o delegado
   * @param tema o tema escolhido
   * @return o possivel delegado caso este exista
   */
  public Optional<Delegado> getDelegadoByTema(Cidadao cidadao, Tema tema) {
    Map<Tema, Delegado> delegadosEscolhidos = cidadao.getDelegados_escolhidos();
    if (delegadosEscolhidos.containsKey(tema)) {
      return Optional.of(delegadosEscolhidos.get(tema));
    } else {
      return Optional.empty();
    }
  }

  /**
   * Obtem um possivel Cidadao da base de dados atraves de um id caso este exista
   *
   * @param id id do cidadao
   * @return um possivel Cidadao caso este exista
   */
  public Optional<Cidadao> findCidadaoById(Long id) {
    Cidadao c = em.find(Cidadao.class, id);
    em.refresh(c);
    return Optional.ofNullable(c);
  }

  public Optional<Cidadao> findCidadaoByCC(Long cc) {
    String jpql = "SELECT c FROM Cidadao c WHERE c.cc = :cc";
    TypedQuery<Cidadao> query = em.createQuery(jpql, Cidadao.class);
    query.setParameter("cc", cc);
    try {
      Cidadao cidadao = query.getSingleResult();
      em.refresh(cidadao);
      return Optional.of(cidadao);
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  /**
   * Obtem um possivel delegado da base de dados atraves de um id caso este exista
   *
   * @param id id do delegado
   * @return um possivel Delegado caso este exista
   */
  public Optional<Delegado> findDelegadoById(Long id) {
    Delegado d = em.find(Delegado.class, id);
    em.refresh(d);
    return Optional.ofNullable(d);
  }

  /**
   * Obtem um possivel delegado escolhido a um tema caso este exista. Caso nao tenha delegado ira
   * pesquisar a temas mais gerais
   *
   * @param cidadao - cidadao que escolheu o delegado
   * @param tema - tema que atribuiu
   * @return um possivel objeto Delegado que foi esocolhido por um cidadao a um tema ou a um tema
   *     mais geral.
   * @throws ApplicationException
   */
  public Optional<Delegado> getDelegadoByTemaGeneralizado(Cidadao cidadao, Tema tema)
      throws ApplicationException {
    if (tema == null) {
      return Optional.empty();
    }
    Optional<Delegado> delegado = getDelegadoByTema(cidadao, tema);
    if (!delegado.isPresent()) {
      return getDelegadoByTemaGeneralizado(cidadao, tema.getPaiTema());
    }
    return Optional.of(delegado.get());
  }

  /**
   * Obtem da base de dados todos os cidadao criados
   *
   * @return lista de Cidadao
   * @throws ApplicationException
   */
  public List<Cidadao> listarCidadaos() throws ApplicationException {
    try {
      TypedQuery<Cidadao> query = em.createQuery("SELECT c FROM Cidadao c", Cidadao.class);
      return query.getResultList();
    } catch (Exception e) {
      throw new ApplicationException("Erro ao listar projetos de lei por estado", e);
    }
  }

  public List<Delegado> getDelegados() {
    try {
      TypedQuery<Delegado> query = em.createQuery("SELECT v FROM Delegado v", Delegado.class);
      List<Delegado> delegados = query.getResultList();
      return delegados;
    } catch (Exception e) {
    }
    return null;
  }

  /** Fecha o EntityManager do catalogo */
  public void close() {
    em.close();
  }
}
