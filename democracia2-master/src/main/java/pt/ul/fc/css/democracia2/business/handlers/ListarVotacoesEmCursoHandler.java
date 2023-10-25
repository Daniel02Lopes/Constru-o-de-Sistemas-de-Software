package pt.ul.fc.css.democracia2.business.handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoProjetoDeLei;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoVotacoes;
import pt.ul.fc.css.democracia2.business.entities.EstadoProjetoLei;
import pt.ul.fc.css.democracia2.business.entities.ProjetoDeLei;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/**
 * Esta classe representa um Handler associado à implementação do caso de Uso D D -> Listar as
 * votações em curso.
 */
public class ListarVotacoesEmCursoHandler {
  private final EntityManagerFactory emf;

  /**
   * Cria um novo handler para listar todas as votacoes de projeto de lei em curso
   *
   * @param emf a factory de entity managers usada para a comunicação com a base de dados
   */
  public ListarVotacoesEmCursoHandler(EntityManagerFactory emf) {
    this.emf = emf;
  }

  /**
   * Retorna a lista de projetos de lei em votação.
   *
   * @return a lista de projetos de lei em votação
   * @throws ApplicationException se ocorrer um erro ao listar as votações em curso
   */
  public List<ProjetoDeLei> listaProjetosVotacao() throws ApplicationException {
    EntityManager em = emf.createEntityManager();
    CatalogoProjetoDeLei catalogoProj = new CatalogoProjetoDeLei(em);
    CatalogoVotacoes catalogoVot = new CatalogoVotacoes(em);
    try {
      List<ProjetoDeLei> projetosVotacao =
          catalogoProj.listarProjetosEstado(EstadoProjetoLei.EM_VOTACAO);
      if (projetosVotacao == null) {
        em.close();
        return null;
      }
      projetosVotacao = catalogoProj.listarProjetosEstado(EstadoProjetoLei.EM_VOTACAO);
      return projetosVotacao;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      System.out.println(e.getMessage());
      throw new ApplicationException("Não existem Votações em Curso", e);
    } finally {
      em.close();
    }
  }
}
