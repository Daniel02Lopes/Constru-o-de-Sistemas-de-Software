package pt.ul.fc.css.democracia2.business.handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoCidadao;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoProjetoDeLei;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoVotacoes;
import pt.ul.fc.css.democracia2.business.entities.Cidadao;
import pt.ul.fc.css.democracia2.business.entities.EstadoProjetoLei;
import pt.ul.fc.css.democracia2.business.entities.ProjetoDeLei;
import pt.ul.fc.css.democracia2.business.entities.Votacao;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/**
 * Esta classe representa um Handler associado à implementação do caso de Uso H H -> Apoiar
 * projectos de lei
 */
public class ApoiarProjetosDeLeiHandler {

  private final EntityManagerFactory emf;

  /**
   * Cria um novo handler para apoiar projetos de lei.
   *
   * @param emf a factory de entity managers usada para a comunicação com a base de dados
   */
  public ApoiarProjetosDeLeiHandler(EntityManagerFactory emf) {
    this.emf = emf;
  }

  /**
   * Método responsável por apoiar um projeto de lei.
   *
   * @param cidadao_id o identificador do cidadão que está a apoiar o projeto
   * @param projeto_id o identificador do projeto de lei que está a ser apoiado
   * @throws ApplicationException se ocorrer algum erro na execução do método
   */
  public void apoiaProjetoDeLei(Long cidadao_id, Long projeto_id) throws ApplicationException {
    EntityManager em = emf.createEntityManager();
    CatalogoCidadao catalogoCidadao = new CatalogoCidadao(em);
    CatalogoProjetoDeLei catalogoProjetoDeLei = new CatalogoProjetoDeLei(em);
    CatalogoVotacoes catalogoVotacoes = new CatalogoVotacoes(em);
    try {
      em.getTransaction().begin();
      Cidadao cidadao =
          catalogoCidadao
              .findCidadaoById(cidadao_id)
              .orElseThrow(() -> new ApplicationException("Nao existe Cidadao com id fornecido"));

      ProjetoDeLei projetoDeLei =
          catalogoProjetoDeLei
              .findProjetoById(projeto_id)
              .orElseThrow(() -> new ApplicationException("Projeto de Lei não encontrado"));
      if (projetoDeLei.getEstadoProjetoLei() == EstadoProjetoLei.EXPIRADO)
        throw new ApplicationException("Projeto expirou.");

      if (projetoDeLei.getApoiantes().contains(cidadao)) {
        throw new ApplicationException("Cidadão já apoiou este projeto.");
      }
      projetoDeLei.getApoiantes().add(cidadao);
      projetoDeLei.incrementaNumerodeApoiantes();
      em.merge(projetoDeLei);
      if (projetoDeLei.getNumerodeApoiantes() >= 10000
          && projetoDeLei.getEstadoProjetoLei() == EstadoProjetoLei.ABERTO) {

        em.getTransaction().commit();
        Votacao votacao =
            catalogoVotacoes.newVotacao(
                projetoDeLei.getDelegadoProponente(), projetoDeLei.getDataValidade());
        em.getTransaction().begin();

        projetoDeLei.setEstadoProjetoLei(EstadoProjetoLei.EM_VOTACAO);
        projetoDeLei.setVotacao(votacao);
        em.merge(projetoDeLei);
      }
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      System.out.println(e.getMessage());
      throw new ApplicationException("Erro ao apoiar projeto de lei", e);
    } finally {
      em.close();
    }
  }
}
