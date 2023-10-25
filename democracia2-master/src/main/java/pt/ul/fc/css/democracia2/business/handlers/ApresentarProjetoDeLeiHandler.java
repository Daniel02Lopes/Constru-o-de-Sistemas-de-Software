package pt.ul.fc.css.democracia2.business.handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoCidadao;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoProjetoDeLei;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoTema;
import pt.ul.fc.css.democracia2.business.entities.Delegado;
import pt.ul.fc.css.democracia2.business.entities.Tema;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

/**
 * Esta classe representa um Handler associado à implementação do caso de Uso E E -> Apresentar um
 * projecto de lei
 */
public class ApresentarProjetoDeLeiHandler {
  private EntityManagerFactory emf;

  /**
   * Cria um novo handler para apresentar projetos de lei.
   *
   * @param emf a factory de entity managers usada para a comunicação com a base de dados
   */
  public ApresentarProjetoDeLeiHandler(EntityManagerFactory emf) {
    this.emf = emf;
  }

  /**
   * Converte um arquivo num array de bytes.
   *
   * @param file o arquivo a ser convertido
   * @return um array de bytes representando o conteúdo do arquivo
   * @throws IOException se ocorrer um erro de leitura ou escrita durante a conversão
   */
  private static byte[] convertToByteArray(File file) throws IOException {
    FileInputStream inputStream = new FileInputStream(file);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
    }
    return outputStream.toByteArray();
  }

  /**
   * Apresenta um novo projeto de lei.
   *
   * @param titulo o título do projeto de lei
   * @param textoDescriptivo o texto descritivo do projeto de lei
   * @param anexo o arquivo anexo do projeto de lei, em formato PDF
   * @param dataValidade a data de validade do projeto de lei
   * @param tema_id o ID do tema do projeto de lei
   * @param delegadoProponente_id o ID do delegado proponente do projeto de lei
   * @return o ID do projeto de lei criado
   * @throws ApplicationException se ocorrer um erro ao ir buscar ou adicionar informações à base de
   *     dados
   * @throws IllegalArgumentException se algum dos argumentos não for válido
   * @throws IOException se ocorrer um erro de leitura ou escrita durante a conversão do arquivo
   */
  public Long apresentaProjetoDeLei(
      String titulo,
      String textoDescriptivo,
      File anexo,
      LocalDateTime dataValidade,
      Long tema_id,
      Long delegadoProponente_id)
      throws ApplicationException, IOException {

    // if (anexo == null && !anexo.getName().toLowerCase().endsWith(".pdf")) {
    //  throw new IllegalArgumentException("O anexo não existe ou não é um PDF válido.");
    // }

    if (titulo == null || titulo.trim().isEmpty()) {
      throw new IllegalArgumentException("O título não pode ser vazio.");
    }

    if (textoDescriptivo == null || textoDescriptivo.trim().isEmpty()) {
      throw new IllegalArgumentException("O texto descritivo não pode ser vazio.");
    }

    if (dataValidade.isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("A data de validade precisa ser posterior à data atual.");
    }

    if (tema_id == null) {
      throw new IllegalArgumentException("O tema não pode ser nulo.");
    }

    if (delegadoProponente_id == null) {
      throw new IllegalArgumentException("O delegado proponente não pode ser nulo.");
    }

    EntityManager em = emf.createEntityManager();
    CatalogoCidadao catalogoCidadao = new CatalogoCidadao(em);
    CatalogoTema catalogoTema = new CatalogoTema(em);
    CatalogoProjetoDeLei catalogoProj = new CatalogoProjetoDeLei(em);

    try {
      em.getTransaction().begin();
      // byte[] anexoBytes = convertToByteArray(anexo);
      byte[] dummy = null;
      Tema tem =
          catalogoTema
              .findTemaById(tema_id)
              .orElseThrow(() -> new ApplicationException("Tema não encontrado"));

      Delegado del =
          catalogoCidadao
              .findDelegadoById(delegadoProponente_id)
              .orElseThrow(() -> new ApplicationException("Delegado não encontrado"));

      Long idProjeto =
          catalogoProj.adicionaProjetoDeLei(
              titulo, textoDescriptivo, dummy, dataValidade, tem, del);

      em.getTransaction().commit();
      return idProjeto;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      System.out.println(e.getMessage());
      throw e;
    } finally {
      em.close();
    }
  }
}
