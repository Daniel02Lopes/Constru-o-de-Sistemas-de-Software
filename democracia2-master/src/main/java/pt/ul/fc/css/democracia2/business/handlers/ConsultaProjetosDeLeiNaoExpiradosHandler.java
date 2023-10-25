package pt.ul.fc.css.democracia2.business.handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoProjetoDeLei;
import pt.ul.fc.css.democracia2.business.entities.EstadoProjetoLei;
import pt.ul.fc.css.democracia2.business.entities.ProjetoDeLei;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/**
 * Esta classe representa um Handler associado à implementação do caso de Uso G G -> Consultar
 * projectos de lei.
 */
public class ConsultaProjetosDeLeiNaoExpiradosHandler {
  private final EntityManagerFactory emf;

  /**
   * Cria um novo handler para consultar projetos de lei nao expirados.
   *
   * @param emf a factory de entity managers usada para a comunicação com a base de dados
   */
  public ConsultaProjetosDeLeiNaoExpiradosHandler(EntityManagerFactory emf) {
    this.emf = emf;
  }

  /**
   * Método responsável por consultar projetos de lei não expirados a partir de um ID de projeto.
   *
   * @return uma string contendo informações sobre o projeto de lei consultado e uma lista de
   *     projetos de lei não expirados.
   * @throws ApplicationException se houver erro durante a execução da consulta.
   */
  public List<ProjetoDeLei> consultaListaProjetosNaoExpirados() throws ApplicationException {
    EntityManager em = emf.createEntityManager();
    CatalogoProjetoDeLei catalogoProj = new CatalogoProjetoDeLei(em);
    try {
      em.getTransaction().begin();
      List<ProjetoDeLei> projetosNaoExpirados =
          catalogoProj.listarProjetosEstado(EstadoProjetoLei.ABERTO);
      em.getTransaction().commit();
      return projetosNaoExpirados;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw new ApplicationException("Erro a consultar projetos nao expirados", e);
    } finally {
      em.close();
    }
  }

  public ProjetoDeLei consultaProjetoNaoExpirado(Long id_projeto) throws ApplicationException {
    EntityManager em = emf.createEntityManager();
    CatalogoProjetoDeLei catalogoProj = new CatalogoProjetoDeLei(em);
    try {
      em.getTransaction().begin();
      ProjetoDeLei projetoDeLei =
          catalogoProj
              .findProjetoById(id_projeto)
              .orElseThrow(
                  () -> new ApplicationException("Nao existe um Projeto De Lei com id fornecidoo"));
      List<ProjetoDeLei> projetosNaoExpirados =
          catalogoProj.listarProjetosEstado(EstadoProjetoLei.ABERTO);
      projetosNaoExpirados.addAll(catalogoProj.listarProjetosEstado(EstadoProjetoLei.EM_VOTACAO));
      if (!projetosNaoExpirados.contains(projetoDeLei)) {
        throw new ApplicationException("O projeto nao esta na lista de expirados");
      }
      em.getTransaction().commit();
      return projetoDeLei;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw new ApplicationException("Erro a consultar projeto nao expirado", e);
    } finally {
      em.close();
    }
  }
}
