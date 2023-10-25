package pt.ul.fc.css.democracia2.business.handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoCidadao;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoTema;
import pt.ul.fc.css.democracia2.business.entities.Cidadao;
import pt.ul.fc.css.democracia2.business.entities.Delegado;
import pt.ul.fc.css.democracia2.business.entities.Tema;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/**
 * Handler responsavel pelo o caso de : I. (F1) Escolher delegado. Um cidadão pode escolher vários
 * delegados, mas apenas um para cada tema.
 */
public class EscolheDelegadoHandler {
  private EntityManagerFactory emf;

  /**
   * Construtor do handler
   *
   * @param emf O EntityManagerFactory
   */
  public EscolheDelegadoHandler(EntityManagerFactory emf) {
    this.emf = emf;
  }

  /**
   * Funcao que associa um delegado escolhido a um tema.
   *
   * @param cidadao_id - id do cidadao que escolheu o delegado
   * @param delegado_id - delegado escolhido
   * @param tema_id - o tema a que o delegado ira ficar associado
   * @throws ApplicationException
   */
  public void escolheDelegado(Long cidadao_id, Long delegado_id, Long tema_id)
      throws ApplicationException {
    EntityManager em = emf.createEntityManager();
    CatalogoCidadao catalogoCidadao = new CatalogoCidadao(em);
    CatalogoTema catalogoTema = new CatalogoTema(em);
    Cidadao cidadao =
        catalogoCidadao
            .findCidadaoById(cidadao_id)
            .orElseThrow(() -> new ApplicationException("Nao existe Cidadao com id fornecido"));
    Delegado delegado =
        catalogoCidadao
            .findDelegadoById(delegado_id)
            .orElseThrow(() -> new ApplicationException("Nao existe Delegado com id fornecidoo"));
    Tema tema =
        catalogoTema
            .findTemaById(tema_id)
            .orElseThrow(() -> new ApplicationException("Nao existe Tema com id fornecido"));
    catalogoCidadao.atribuiDelegadoATema(cidadao, delegado, tema);
    em.close();
  }
}
