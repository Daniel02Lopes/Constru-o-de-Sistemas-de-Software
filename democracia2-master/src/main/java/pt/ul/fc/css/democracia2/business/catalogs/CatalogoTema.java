package pt.ul.fc.css.democracia2.business.catalogs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;
import pt.ul.fc.css.democracia2.business.entities.Tema;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/** Classe responsavel por gerir a classe Tema */
public class CatalogoTema {
  private EntityManager em;

  /**
   * Construtor do Catalogo de Tema
   *
   * @param em - EnttityManager criado previamente
   */
  public CatalogoTema(EntityManager em) {
    this.em = em;
  }

  /**
   * Funcao que cria um tema
   *
   * @param titulo - titulo do Tema
   * @param paiTema - Tema geral do tema a ser criado, caso seja null nao associa a nenhum
   * @return tema criado
   * @throws ApplicationException
   */
  public Tema createTema(@NonNull String titulo, Tema paiTema) throws ApplicationException {
    try {
      em.getTransaction().begin();
      Tema tema = null;
      if (paiTema == null) tema = new Tema(titulo);
      else tema = new Tema(titulo, paiTema);
      em.persist(tema);
      em.getTransaction().commit();
      return tema;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw new ApplicationException("Erro ao criar um Tema");
    }
  }

  /**
   * Encontra um possivel Tema atraves do seu id
   *
   * @param id id do tema a encontrar
   * @return o tema caso este exista, caso contrario null
   */
  public Optional<Tema> findTemaById(Long id) {
    Tema tema = em.find(Tema.class, id);
    em.refresh(tema);
    return Optional.ofNullable(tema);
  }

  /** Fecha o EntityManager do catalogo */
  public void close() {
    em.close();
  }

  public List<Tema> getTemas() {
    try {
      TypedQuery<Tema> query = em.createQuery("SELECT v FROM Tema v", Tema.class);
      List<Tema> temas = query.getResultList();
      return temas;
    } catch (Exception e) {
    }
    return null;
  }
}
