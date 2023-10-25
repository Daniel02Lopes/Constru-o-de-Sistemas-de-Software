package pt.ul.fc.css.democracia2.business.handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoProjetoDeLei;
import pt.ul.fc.css.democracia2.business.entities.EstadoProjetoLei;
import pt.ul.fc.css.democracia2.business.entities.ProjetoDeLei;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/**
 * Esta classe representa um Handler associado à implementação do caso de Uso F F -> Fechar
 * projectos de lei expirados.
 */
public class FechaProjetosDeLeiExpiradosHandler {
  private final EntityManagerFactory emf;

  /**
   * Cria um novo handler para fechar projetos de lei nao expirados.
   *
   * @param emf a factory de entity managers usada para a comunicação com a base de dados
   */
  public FechaProjetosDeLeiExpiradosHandler(EntityManagerFactory emf) {
    this.emf = emf;
  }

  /**
   * Fecha projetos de lei expirados, alterando o seu estado para EXPIRADO.
   *
   * @throws ApplicationException se ocorrer algum erro durante o processo de fechar projetos
   *     expirados
   */
  public void fechaProjetosExpirados() throws ApplicationException {
    EntityManager em = emf.createEntityManager();
    CatalogoProjetoDeLei catalogoProj = new CatalogoProjetoDeLei(em);
    try {
      em.getTransaction().begin();
      List<ProjetoDeLei> projetos = catalogoProj.listaProjetosTodos();
      for (ProjetoDeLei projeto : projetos) {
        if (projeto.getEstadoProjetoLei().equals(EstadoProjetoLei.ABERTO)) {
          if (projeto.getDataValidade().isBefore(LocalDateTime.now())) {
            projeto.setEstadoProjetoLei(EstadoProjetoLei.EXPIRADO);
            em.merge(projeto);
          }
        }
      }
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw new ApplicationException("Erro a fechar projetos expirados", e);
    } finally {
      em.close();
    }
  }
}
