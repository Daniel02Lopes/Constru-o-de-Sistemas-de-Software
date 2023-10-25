package pt.ul.fc.css.democracia2.business.handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoProjetoDeLei;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoVotacoes;
import pt.ul.fc.css.democracia2.business.entities.EstadoProjetoLei;
import pt.ul.fc.css.democracia2.business.entities.ProjetoDeLei;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/** Handler responsavel pelo caso de uso K: (F1) Fechar uma votação. */
public class FecharVotacoesHandler {

  private final EntityManagerFactory emf;

  /**
   * Construtor do FecharVotacoesHandler
   *
   * @param emf EntityManagerFactory
   */
  public FecharVotacoesHandler(EntityManagerFactory emf) {
    this.emf = emf;
  }

  /**
   * Funcao que vai fechar as votacoes
   *
   * @throws ApplicationException
   */
  public void fecharVotacoes() throws ApplicationException {
    EntityManager em = emf.createEntityManager();
    CatalogoProjetoDeLei catalogoProj = new CatalogoProjetoDeLei(em);
    CatalogoVotacoes catalogoVot = new CatalogoVotacoes(em);
    List<ProjetoDeLei> projetosVotacao =
        catalogoProj.listarProjetosEstado(EstadoProjetoLei.EM_VOTACAO);
    if (projetosVotacao == null || projetosVotacao.size() == 0) {
      em.close();
      return;
    }
    for (ProjetoDeLei projetoDeLei : projetosVotacao) {
      catalogoVot.fecharUmaVotacao(projetoDeLei.getId());
    }
    em.close();
  }
}
