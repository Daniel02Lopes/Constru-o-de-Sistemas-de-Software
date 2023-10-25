package pt.ul.fc.css.democracia2.business.handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoVotacoes;
import pt.ul.fc.css.democracia2.business.entities.Votacao;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/** Handler responsavel pelo caso de uso C: Listar as votações passadas */
public class ListarVotacoesPassadasHandler {

  private final EntityManagerFactory emf;

  /**
   * Construtor do ListarVotacoesPassadasHandler
   *
   * @param emf EntityManagerFactory
   */
  public ListarVotacoesPassadasHandler(EntityManagerFactory emf) {
    this.emf = emf;
  }

  /**
   * Funcao que vai listar as votacoes passadas
   *
   * @return votacoes passadas
   * @throws ApplicationException
   */
  public List<Votacao> listarVotacoesPassadas() throws ApplicationException {
    EntityManager em = emf.createEntityManager();
    try {
      CatalogoVotacoes cat = new CatalogoVotacoes(em);
      List<Votacao> votacao = cat.listarVotacoes();
      for (int i = 0; i < votacao.size(); i++) {
        if (!(votacao.get(i).getDataFecho().isBefore(LocalDateTime.now()))) {
          votacao.remove(i);
        }
      }
      return votacao;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new ApplicationException("Erro a consultar Votacoes passadas ", e);
    } finally {
      em.close();
    }
  }
}
