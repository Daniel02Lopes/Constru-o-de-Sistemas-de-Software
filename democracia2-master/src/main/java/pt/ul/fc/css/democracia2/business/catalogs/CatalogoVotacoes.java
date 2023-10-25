package pt.ul.fc.css.democracia2.business.catalogs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import pt.ul.fc.css.democracia2.business.entities.*;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/** Representa um Catalogo de Votacoes */
public class CatalogoVotacoes {

  /** EntityManager que vai permitir fazer as operacoes com a base de dados */
  private EntityManager em;

  /**
   * Construtor de um Catalogo de Votacoes
   *
   * @param em EntityManager que vai permitir fazer as operacoes com a base de dados
   */
  public CatalogoVotacoes(EntityManager em) {
    this.em = em;
  }

  /**
   * Funcao que vai criar uma noca votacao
   *
   * @param delegado O delegado proponente
   * @param dataFecho Data de Validade do projeto de lei
   * @return Votacao criada
   * @throws ApplicationException
   */
  public Votacao newVotacao(Delegado delegado, LocalDateTime dataFecho)
      throws ApplicationException {
    try {
      em.getTransaction().begin();
      Votacao v = new Votacao();
      if (LocalDateTime.now().plusDays(15).isAfter(dataFecho)) {
        v.setExpirationDate(LocalDateTime.now().plusDays(15));
      } else if (LocalDateTime.now().plusMonths(2).isBefore(dataFecho)) {
        v.setExpirationDate(LocalDateTime.now().plusMonths(2));
      } else {
        v.setExpirationDate(dataFecho);
      }
      v.addVotoPublico(delegado, TipoDeVoto.FAVORAVEL);
      em.persist(v);
      em.getTransaction().commit();
      return v;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();

      throw new ApplicationException("Erro a criar votacao", e);
    }
  }

  /**
   * Funcao que retorna a lista de Votacoes
   *
   * @return retorna a lista de votacoes
   * @throws ApplicationException
   */
  public List<Votacao> listarVotacoes() throws ApplicationException {
    try {
      em.getTransaction().begin();
      TypedQuery<Votacao> query = em.createQuery("SELECT v FROM Votacao v", Votacao.class);
      List<Votacao> votacao = query.getResultList();
      em.getTransaction().commit();
      return votacao;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new ApplicationException("Erro a consultar Votacoes passadas ", e);
    }
  }

  /**
   * Funcao que vai buscar uma determinada votacao pelo id
   *
   * @param id id da votacao
   * @return a votacao que tem o id fornecido
   * @throws ApplicationException
   */
  public Votacao findVotacaoById(Long id) throws ApplicationException {
    try {
      Votacao v = em.find(Votacao.class, id);
      em.refresh(v);
      return v;
    } catch (Exception e) {
      throw new ApplicationException("Voto nao existe com este id ");
    }
  }

  /**
   * Funcao que verifica se um determinado cidadao ja votou numa votacao
   *
   * @param cidadao cidadao que vai ser verificado se votou
   * @param votacao votacao onde vai ser verificado se o cidadao votou
   * @return true se tiver votado ou false caso contrario
   */
  public boolean hasVoted(Cidadao cidadao, Votacao votacao) {

    boolean toReturn = false;
    List<Voto> votos = votacao.getVotos();
    for (Voto v : votos) {

      if (cidadao instanceof Delegado) {

        if (v instanceof VotoPublico) {
          if (((Delegado) cidadao).getId() == (((VotoPublico) v).getDelegado().getId())) {
            return true;
          }
        } else {
          if (cidadao.getId() == (((VotoPrivado) v).getCidadao().getId())) {
            return true;
          }
        }
      } else {
        if (v instanceof VotoPrivado) {
          if (cidadao.getId() == (((VotoPrivado) v).getCidadao().getId())) {
            return true;
          }
        }
      }
    }
    return toReturn;
  }

  /**
   * Funcao que vai fechar uma votacao ,atribuindo os votos que faltam atribuir e dando o projeto
   * como aprovado, caso o numero de votos favoraveis seja maior que o numero de votos
   * desfavoraveis, e rejeitado caso contrario
   *
   * @param projetoDeLeiId O id do projeto cuja vutacao vai ser fechada
   * @throws ApplicationException
   */
  public void fecharUmaVotacao(Long projetoDeLeiId) throws ApplicationException {

    CatalogoProjetoDeLei catPdl = new CatalogoProjetoDeLei(em);
    ProjetoDeLei pDL =
        catPdl
            .findProjetoById(projetoDeLeiId)
            .orElseThrow(
                () -> new ApplicationException("Nao existe um Projeto De Lei com id fornecidoo"));
    if (!pDL.getEstadoProjetoLei().equals(EstadoProjetoLei.EM_VOTACAO)) {
      throw new ApplicationException("Projeto De Lei nao esta em votacao");
    }

    try {
      Votacao v = pDL.getVotacao();
      if (v.getDataFecho().isAfter(LocalDateTime.now())) {
        return;
      }
      CatalogoCidadao catC = new CatalogoCidadao(em);
      em.getTransaction().begin();
      List<Cidadao> cidadaos = catC.listarCidadaos();

      for (Cidadao c : cidadaos) {

        if (!hasVoted(c, v)) {

          Optional<Delegado> d = catC.getDelegadoByTemaGeneralizado(c, pDL.getTema());

          if (!d.isEmpty()) {

            List<Voto> listaVotos = v.getVotos();

            for (Voto voto : listaVotos) {

              if (voto instanceof VotoPublico) {

                VotoPublico votoPublico = (VotoPublico) voto;
                if (votoPublico.getDelegado().equals(d.get())) {

                  v.addVotoPrivado(c, votoPublico.getTipoDeVoto());
                  em.merge(v);
                }
              }
            }
          }
        }
      }

      if (v.getVotos_favoraveis() > v.getVotos_desfavoravies()) {

        pDL.setEstadoProjetoLei(EstadoProjetoLei.APROVADO);
      } else {

        pDL.setEstadoProjetoLei(EstadoProjetoLei.RECUSADO);
      }

      em.merge(pDL);
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }

      throw new ApplicationException("Erro a fechar Votacoes em curso", e);
    }
  }

  public void close() {
    em.close();
  }
}
