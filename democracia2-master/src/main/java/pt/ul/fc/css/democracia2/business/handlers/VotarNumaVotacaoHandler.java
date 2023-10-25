package pt.ul.fc.css.democracia2.business.handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoCidadao;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoVotacoes;
import pt.ul.fc.css.democracia2.business.entities.*;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/** Handler responsavel pelo caso de uso J: (F1) Votar numa proposta. */
public class VotarNumaVotacaoHandler {

  private final EntityManagerFactory emf;

  /**
   * Construtor do VotarNumaVotacaoHandler
   *
   * @param emf EntityManagerFactory
   */
  public VotarNumaVotacaoHandler(EntityManagerFactory emf) {
    this.emf = emf;
  }

  /**
   * Funcao que premite votar numa votacao
   *
   * @param cidadaoId id do cidadao que vai votar
   * @param votacaoId id da votacao onde o cidadao quer votar
   * @param tipoDevoto tipo de voto do cidadao
   * @param temaId id do tema da proposta
   * @throws ApplicationException
   */
  public void votarNumaVotacao(Long cidadaoId, Long votacaoId, TipoDeVoto tipoDevoto, Tema temaId)
      throws ApplicationException {
    EntityManager em = emf.createEntityManager();
    try {
      em.getTransaction().begin();
      CatalogoVotacoes catalogoVotacoes = new CatalogoVotacoes(em);
      CatalogoCidadao catalogoCidadao = new CatalogoCidadao(em);
      Optional<Cidadao> cidadao = catalogoCidadao.findCidadaoById(cidadaoId);
      Votacao v = catalogoVotacoes.findVotacaoById(votacaoId);
      if (v == null) {
        throw new ApplicationException("A votação que pretende votar  não existe!");
      }
      if (v.getDataFecho().isBefore(LocalDateTime.now())) {
        throw new ApplicationException("A votação que pretende votar já esxpirou!");
      }
      if (!cidadao.isEmpty()) {
        if (catalogoVotacoes.hasVoted(cidadao.get(), v)) {
          throw new ApplicationException("O cidadao ja votou");
        }

        if (tipoDevoto == TipoDeVoto.POR_OMISSAO) {
          Optional<Delegado> d =
              catalogoCidadao.getDelegadoByTemaGeneralizado(cidadao.get(), temaId);
          if (!d.isEmpty()) {
            List<Voto> votos = new LinkedList<>(v.getVotos());
            for (Voto voto : votos) {
              if (voto instanceof VotoPublico) {
                VotoPublico votoPublico = (VotoPublico) voto;
                if (votoPublico.getDelegado().equals(d.get())) {
                  v.addVotoPrivado(cidadao.get(), votoPublico.getTipoDeVoto());
                }
              }
            }
          }
        } else {
          if (cidadao.get() instanceof Delegado) {
            v.addVotoPublico((Delegado) cidadao.get(), tipoDevoto);
          } else {
            v.addVotoPrivado(cidadao.get(), tipoDevoto);
          }
        }
      }
      em.persist(v);
      em.getTransaction().commit();
    } catch (Exception e) {

      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new ApplicationException("Erro a votar numa proposta", e);
    } finally {
      em.close();
    }
  }

  public TipoDeVoto getVotoOmissao(Long cidadaoId, Long votacaoId, Tema temaId)
      throws ApplicationException {
    CatalogoCidadao catalogoCidadao = new CatalogoCidadao(emf.createEntityManager());
    CatalogoVotacoes catalogoVotacoes = new CatalogoVotacoes(emf.createEntityManager());
    Optional<Cidadao> c = catalogoCidadao.findCidadaoById(cidadaoId);
    Votacao v = catalogoVotacoes.findVotacaoById(votacaoId);

    Optional<Delegado> d = catalogoCidadao.getDelegadoByTemaGeneralizado(c.get(), temaId);
    if (!d.isEmpty()) {
      List<Voto> votos = new LinkedList<>(v.getVotos());
      for (Voto voto : votos) {
        if (voto instanceof VotoPublico) {
          VotoPublico votoPublico = (VotoPublico) voto;
          if (votoPublico.getDelegado().equals(d.get())) {
            catalogoCidadao.close();
            catalogoVotacoes.close();
            return votoPublico.getTipoDeVoto();
          }
        }
      }
    }
    catalogoCidadao.close();
    catalogoVotacoes.close();
    return null;
  }
}
