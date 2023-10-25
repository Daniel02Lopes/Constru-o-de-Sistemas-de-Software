package pt.ul.fc.css.democracia2;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import pt.ul.fc.css.democracia2.application.CidadaoDTO;
import pt.ul.fc.css.democracia2.application.DelegadoDTO;
import pt.ul.fc.css.democracia2.application.Democracia2Service;
import pt.ul.fc.css.democracia2.application.ProjetoDeLeiDTO;
import pt.ul.fc.css.democracia2.application.VotacaoDTO;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoProjetoDeLei;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoTema;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoVotacoes;
import pt.ul.fc.css.democracia2.business.entities.*;
import pt.ul.fc.css.democracia2.business.handlers.ApresentarProjetoDeLeiHandler;
import pt.ul.fc.css.democracia2.business.handlers.FechaProjetosDeLeiExpiradosHandler;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

@SpringBootTest(classes = Democracia2Application.class)
public class Tests {

  @Autowired private EntityManagerFactory emf;

  private Democracia2Service democracia2Service;
  private CatalogoTema catalogoTema;

  @BeforeEach
  public void setup() {
    democracia2Service = new Democracia2Service(emf);
    catalogoTema = new CatalogoTema(emf.createEntityManager());
  }

  @Test
  @DirtiesContext
  void escolheDelegadoTest1() {
    boolean expected = true;
    try {
      CidadaoDTO cidadao1 =
          democracia2Service.cidadaoService.createCidadao("João", "Silva", 123456789L);
      DelegadoDTO delegado1 =
          democracia2Service.delegadoService.createDelegado("Joana", "Lopes", 123456L);
      Tema tema1 = catalogoTema.createTema("QualidadeDeVida", null);
      democracia2Service.cidadaoService.escolheDelegado(
          cidadao1.getId(), delegado1.getId(), tema1.getId());
    } catch (ApplicationException e) {
      System.out.println(e.getMessage());
      assertEquals(expected, false);
    }
  }

  @Test
  @DirtiesContext
  void escolheDelegadoTest2() {
    boolean expected = false;
    try {
      CidadaoDTO cidadao1 =
          democracia2Service.cidadaoService.createCidadao("João", "Silva", 1223456789L);
      DelegadoDTO delegado1 =
          democracia2Service.delegadoService.createDelegado("Joana", "Lopes", 1232456L);
      DelegadoDTO delegado2 =
          democracia2Service.delegadoService.createDelegado("Rui", "Martins", 2222422222L);
      Tema tema1 = catalogoTema.createTema("QualidadeDeVidas", null);
      democracia2Service.cidadaoService.escolheDelegado(
          cidadao1.getId(), delegado2.getId(), tema1.getId());
    } catch (ApplicationException e) {
      System.out.println(e.getMessage());
      assertEquals(expected, false);
    }
  }

  @Test
  @DirtiesContext
  void testarApoiar() {
    boolean expected = false;
    try {
      ApresentarProjetoDeLeiHandler apresentaProjetoDeLeiHandler =
          new ApresentarProjetoDeLeiHandler(emf);

      String resourceName = "/fase1.pdf";
      URL resourceUrl = getClass().getResource(resourceName);
      if (resourceUrl == null) {
        throw new IllegalArgumentException("Arquivo não encontrado: " + resourceName);
      }
      File file = new File(resourceUrl.getFile());
      DelegadoDTO delegado =
          democracia2Service.delegadoService.createDelegado("Joana", "Lopesss", 1234523413216L);
      Tema tema1 = catalogoTema.createTema("QualidadeDeVidaasa", null);
      // 1
      catalogoTema.close();
      CidadaoDTO cidadao =
          democracia2Service.cidadaoService.createCidadao("João", "Silva", 12234112156789L);

      Long projetoid =
          apresentaProjetoDeLeiHandler.apresentaProjetoDeLei(
              "Titulo2",
              "Texto",
              file,
              LocalDateTime.now().plusYears(1),
              tema1.getId(),
              delegado.getId());
      CatalogoProjetoDeLei catalogoProjetoDeLei =
          new CatalogoProjetoDeLei(emf.createEntityManager());
      assertEquals(1, catalogoProjetoDeLei.findProjetoById(projetoid).get().getNumerodeApoiantes());
      democracia2Service.projetoDeLeiService.apoiarProjeto(cidadao.getId(), projetoid);
      assertEquals(2, catalogoProjetoDeLei.findProjetoById(projetoid).get().getNumerodeApoiantes());
      // PROXIMO APOIO NAO CONTA POIS JA APOIOU
      assertEquals(2, catalogoProjetoDeLei.findProjetoById(projetoid).get().getNumerodeApoiantes());

    } catch (ApplicationException | IOException e) {
      System.out.println(e.getMessage());
      assertEquals(expected, false);
    }
  }

  @Test
  @DirtiesContext
  void testTodosOsVotosPossiveis() {
    boolean expected = true;
    try {

      ApresentarProjetoDeLeiHandler apresentaProjetoDeLeiHandler =
          new ApresentarProjetoDeLeiHandler(emf);
      String resourceName = "/fase1.pdf";
      URL resourceUrl = getClass().getResource(resourceName);
      if (resourceUrl == null) {
        throw new IllegalArgumentException("Arquivo não encontrado: " + resourceName);
      }
      File file = new File(resourceUrl.getFile());
      DelegadoDTO delegado =
          democracia2Service.delegadoService.createDelegado("Joana", "Lopesss", 111111L);
      Tema tema1 = catalogoTema.createTema("QualidadeDeVidaasa", null);
      Tema subTema = catalogoTema.createTema("Casas", tema1);
      catalogoTema.close();
      // 1
      CidadaoDTO cidadao =
          democracia2Service.cidadaoService.createCidadao("João", "Silva", 1111112L);

      // ApresentarProjeto

      Long projetoid =
          apresentaProjetoDeLeiHandler.apresentaProjetoDeLei(
              "Titulo2",
              "Texto",
              file,
              LocalDateTime.now().plusYears(1),
              subTema.getId(),
              delegado.getId());
      CatalogoProjetoDeLei catalogoProjetoDeLei =
          new CatalogoProjetoDeLei(emf.createEntityManager());
      catalogoProjetoDeLei.setNumeroApoiantes(projetoid, 9999);
      catalogoProjetoDeLei.close();

      democracia2Service.projetoDeLeiService.apoiarProjeto(cidadao.getId(), projetoid);
      assertEquals(1, democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().size());
      assertEquals(
          1,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_favoraveis());

      // Voto por omissao onde o tema do delegado é o mesmo do projeto
      CidadaoDTO cidadao2 =
          democracia2Service.cidadaoService.createCidadao("Maria", "Alves", 1111113L);
      democracia2Service.cidadaoService.escolheDelegado(
          cidadao2.getId(), delegado.getId(), subTema.getId());
      democracia2Service.cidadaoService.votarNumaVotacao(
          cidadao2.getId(),
          democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().get(0).getId(),
          TipoDeVoto.POR_OMISSAO,
          subTema);

      assertEquals(
          2,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_favoraveis());
      assertEquals(
          0,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_desfavoravies());

      // Vota por omissao mas nao tem delegado
      CidadaoDTO cidadao3 =
          democracia2Service.cidadaoService.createCidadao("Jose", "Alves", 1111114L);
      democracia2Service.cidadaoService.votarNumaVotacao(
          cidadao3.getId(),
          democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().get(0).getId(),
          TipoDeVoto.POR_OMISSAO,
          subTema);

      assertEquals(
          2,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_favoraveis());
      assertEquals(
          0,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_desfavoravies());

      DelegadoDTO delegado2 =
          democracia2Service.delegadoService.createDelegado("Andreia", "Lopes", 1111115L);

      democracia2Service.cidadaoService.votarNumaVotacao(
          delegado2.getId(),
          democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().get(0).getId(),
          TipoDeVoto.DESFAVORAVEL,
          subTema);
      assertEquals(
          2,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_favoraveis());
      assertEquals(
          1,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_desfavoravies());

      // NAO VAI VOTAR PORQUE JA VOTOU

      assertThrows(
          ApplicationException.class,
          () -> {
            democracia2Service.cidadaoService.votarNumaVotacao(
                delegado2.getId(),
                democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().get(0).getId(),
                TipoDeVoto.DESFAVORAVEL,
                subTema);
          });

      democracia2Service.cidadaoService.escolheDelegado(
          cidadao3.getId(), delegado2.getId(), tema1.getId());
      democracia2Service.cidadaoService.votarNumaVotacao(
          cidadao3.getId(),
          democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().get(0).getId(),
          TipoDeVoto.POR_OMISSAO,
          subTema);
      // Voto por omissao onde o TEMA do delegado ESCOLHIDO é "PaiTema" do projeto

      assertEquals(
          2,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_favoraveis());
      assertEquals(
          2,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_desfavoravies());

      DelegadoDTO delegado3 =
          democracia2Service.delegadoService.createDelegado("Tomas", "Lopes", 1111116L);
      CidadaoDTO cidadao4 =
          democracia2Service.cidadaoService.createCidadao("Antonio", "Alves", 1111117L);
      democracia2Service.cidadaoService.votarNumaVotacao(
          cidadao4.getId(),
          democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().get(0).getId(),
          TipoDeVoto.POR_OMISSAO,
          subTema);

      // Votar por omissao mas nao tem delegado
      assertEquals(
          2,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_favoraveis());
      assertEquals(
          2,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_desfavoravies());
      democracia2Service.cidadaoService.escolheDelegado(
          cidadao4.getId(), delegado3.getId(), subTema.getId());

      // Votar por omissao mas o Delegado não votou DO TEMA DO PROJETO NAO VOTOU
      democracia2Service.cidadaoService.votarNumaVotacao(
          cidadao4.getId(),
          democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().get(0).getId(),
          TipoDeVoto.POR_OMISSAO,
          subTema);
      assertEquals(
          2,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_favoraveis());
      assertEquals(
          2,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_desfavoravies());

      democracia2Service.cidadaoService.escolheDelegado(
          delegado3.getId(), delegado.getId(), subTema.getId());
      democracia2Service.cidadaoService.votarNumaVotacao(
          delegado3.getId(),
          democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().get(0).getId(),
          TipoDeVoto.POR_OMISSAO,
          subTema);
      assertEquals(
          3,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_favoraveis());
      assertEquals(
          2,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_desfavoravies());

      // VOTAR POR OMISSAO MAS O SEU DELEGADO VOTOU POR OMISSAO PORTANTO NAO E PROPAGADO
      democracia2Service.cidadaoService.votarNumaVotacao(
          cidadao4.getId(),
          democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().get(0).getId(),
          TipoDeVoto.POR_OMISSAO,
          subTema);
      assertEquals(
          3,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_favoraveis());
      assertEquals(
          2,
          democracia2Service
              .projetoDeLeiService
              .listarVotacoesEmCurso()
              .get(0)
              .getVotacao()
              .getVotos_desfavoravies());

      CidadaoDTO cidadao5 =
          democracia2Service.cidadaoService.createCidadao("Toni", "Alves", 1111118L);
      democracia2Service.cidadaoService.escolheDelegado(
          cidadao5.getId(), delegado.getId(), subTema.getId());

      // FECHAR UMA VOTACAO OS VOTOS QUE NAO VOTARAM E TEM DELEGADO QUE VOTARAM, O SEU VOTO SERA POR
      // OMISSAO
      // PORTANTO SO FALTA O TONI
      EntityManager em = emf.createEntityManager();
      em.getTransaction().begin();
      ProjetoDeLeiDTO prj = democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().get(0);
      VotacaoDTO v = prj.getVotacao();
      CatalogoVotacoes ctg = new CatalogoVotacoes(emf.createEntityManager());
      Votacao vAux = ctg.findVotacaoById(v.getId());
      ctg.close();
      vAux.setExpirationDate(LocalDateTime.now().minusDays(1));
      em.merge(vAux);
      em.getTransaction().commit();
      em.close();

      CatalogoProjetoDeLei catProLei = new CatalogoProjetoDeLei(emf.createEntityManager());

      assertEquals(
          true, democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().size() == 0);

      assertEquals(
          4, catProLei.findProjetoById(projetoid).get().getVotacao().getVotos_favoraveis());
      assertEquals(
          2, catProLei.findProjetoById(projetoid).get().getVotacao().getVotos_desfavoravies());
      assertEquals(1, democracia2Service.projetoDeLeiService.listarVotacoesPassadas().size());
      assertEquals(
          EstadoProjetoLei.APROVADO,
          catProLei.findProjetoById(projetoid).get().getEstadoProjetoLei());

      // VOTAR NUMA VOTACAO FECHADA
      CidadaoDTO cidadao6 =
          democracia2Service.cidadaoService.createCidadao("Mauricio", "Alves", 11111178L);

      assertThrows(
          ApplicationException.class,
          () -> {
            democracia2Service.cidadaoService.votarNumaVotacao(
                cidadao6.getId(), v.getId(), TipoDeVoto.FAVORAVEL, prj.getTema());
          });

      catProLei.close();
    } catch (ApplicationException | IOException e) {

      System.out.println(e.getMessage());
      assertEquals(expected, false);
    }
  }

  @Test
  @DirtiesContext
  void testListarVotacoes() {
    boolean expected = true;
    try {
      ApresentarProjetoDeLeiHandler apresentaProjetoDeLeiHandler =
          new ApresentarProjetoDeLeiHandler(emf);
      String resourceName = "/fase1.pdf";
      URL resourceUrl = getClass().getResource(resourceName);
      if (resourceUrl == null) {
        throw new IllegalArgumentException("Arquivo não encontrado: " + resourceName);
      }
      File file = new File(resourceUrl.getFile());
      DelegadoDTO delegado =
          democracia2Service.delegadoService.createDelegado("Joana", "Lopesss", 1234523413216L);
      Tema tema1 = catalogoTema.createTema("QualidadeDeVidaasa", null);
      // 1
      catalogoTema.close();
      CidadaoDTO cidadao =
          democracia2Service.cidadaoService.createCidadao("João", "Silva", 12234112156789L);

      Long projetoid =
          apresentaProjetoDeLeiHandler.apresentaProjetoDeLei(
              "Titulo2",
              "Texto",
              file,
              LocalDateTime.now().plusYears(1),
              tema1.getId(),
              delegado.getId());
      CatalogoProjetoDeLei catalogoProjetoDeLei =
          new CatalogoProjetoDeLei(emf.createEntityManager());
      catalogoProjetoDeLei.setNumeroApoiantes(projetoid, 9999);
      democracia2Service.projetoDeLeiService.apoiarProjeto(cidadao.getId(), projetoid);
      EntityManager em = emf.createEntityManager();
      em.getTransaction().begin();
      ProjetoDeLeiDTO prj = democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().get(0);
      VotacaoDTO v = prj.getVotacao();
      CatalogoVotacoes ctg = new CatalogoVotacoes(emf.createEntityManager());
      Votacao vAux = ctg.findVotacaoById(v.getId());
      ctg.close();
      vAux.setExpirationDate(LocalDateTime.now().minusDays(1));
      em.merge(vAux);
      em.getTransaction().commit();
      em.close();

      catalogoProjetoDeLei.close();
      assertTrue(
          democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().size() == 0
              && democracia2Service.projetoDeLeiService.listarVotacoesPassadas().size() == 1);
    } catch (ApplicationException | IOException e) {
      System.out.println(e.getMessage());
      assertEquals(expected, false);
    }
  }

  @Test
  @DirtiesContext
  void testFecharVotacoes() {
    boolean expected = true;
    try {
      ApresentarProjetoDeLeiHandler apresentaProjetoDeLeiHandler =
          new ApresentarProjetoDeLeiHandler(emf);
      String resourceName = "/fase1.pdf";
      URL resourceUrl = getClass().getResource(resourceName);
      if (resourceUrl == null) {
        throw new IllegalArgumentException("Arquivo não encontrado: " + resourceName);
      }
      File file = new File(resourceUrl.getFile());
      DelegadoDTO delegado =
          democracia2Service.delegadoService.createDelegado("Joana", "Lopesss", 1234523413216L);
      Tema tema1 = catalogoTema.createTema("QualidadeDeVidaasa", null);
      // 1
      catalogoTema.close();
      CidadaoDTO cidadao =
          democracia2Service.cidadaoService.createCidadao("João", "Silva", 12234112156789L);

      Long projetoid =
          apresentaProjetoDeLeiHandler.apresentaProjetoDeLei(
              "Titulo2",
              "Texto",
              file,
              LocalDateTime.now().plusYears(1),
              tema1.getId(),
              delegado.getId());
      CatalogoProjetoDeLei catalogoProjetoDeLei =
          new CatalogoProjetoDeLei(emf.createEntityManager());
      catalogoProjetoDeLei.setNumeroApoiantes(projetoid, 9999);
      democracia2Service.projetoDeLeiService.apoiarProjeto(cidadao.getId(), projetoid);

      // 2
      Long projetoid2 =
          apresentaProjetoDeLeiHandler.apresentaProjetoDeLei(
              "Titulo3",
              "Texto",
              file,
              LocalDateTime.now().plusYears(1),
              tema1.getId(),
              delegado.getId());

      catalogoProjetoDeLei.setNumeroApoiantes(projetoid2, 9999);
      democracia2Service.projetoDeLeiService.apoiarProjeto(cidadao.getId(), projetoid2);

      ProjetoDeLeiDTO prj = democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().get(1);

      EntityManager em = emf.createEntityManager();
      em.getTransaction().begin();
      VotacaoDTO v = prj.getVotacao();
      CatalogoVotacoes ctg = new CatalogoVotacoes(emf.createEntityManager());
      Votacao vAux = ctg.findVotacaoById(v.getId());
      ctg.close();
      vAux.setExpirationDate(LocalDateTime.now().minusDays(1));
      em.merge(vAux);
      em.getTransaction().commit();
      em.close();

      // 3

      Long projetoid3 =
          apresentaProjetoDeLeiHandler.apresentaProjetoDeLei(
              "Titulo4",
              "Texto",
              file,
              LocalDateTime.now().plusYears(1),
              tema1.getId(),
              delegado.getId());
      catalogoProjetoDeLei.setNumeroApoiantes(projetoid3, 9999);
      democracia2Service.projetoDeLeiService.apoiarProjeto(cidadao.getId(), projetoid3);

      assertEquals(2, democracia2Service.projetoDeLeiService.listarVotacoesEmCurso().size());

      catalogoProjetoDeLei.close();
    } catch (ApplicationException | IOException e) {
      System.out.println(e.getMessage());
      assertEquals(expected, false);
    }
  }

  @Test
  @DirtiesContext
  void TesteApoiaProjetoEPassagemParaVotacao() {
    boolean expected = true;
    try {
      ApresentarProjetoDeLeiHandler apresentaProjetoDeLeiHandler =
          new ApresentarProjetoDeLeiHandler(emf);
      String resourceName = "/fase1.pdf";
      URL resourceUrl = getClass().getResource(resourceName);
      if (resourceUrl == null) {
        throw new IllegalArgumentException("Arquivo não encontrado: " + resourceName);
      }
      File file = new File(resourceUrl.getFile());
      DelegadoDTO delegado =
          democracia2Service.delegadoService.createDelegado("Joana", "Lopesss", 123452313216L);
      Tema tema1 = catalogoTema.createTema("QualidadeDeVidaa", null);
      catalogoTema.close();
      CidadaoDTO cidadao =
          democracia2Service.cidadaoService.createCidadao("João", "Silva", 1223411156789L);
      Long projetoid =
          apresentaProjetoDeLeiHandler.apresentaProjetoDeLei(
              "Titulo",
              "Texto",
              file,
              LocalDateTime.now().plusYears(1),
              tema1.getId(),
              delegado.getId());
      CatalogoProjetoDeLei catalogoProjetoDeLei =
          new CatalogoProjetoDeLei(emf.createEntityManager());
      catalogoProjetoDeLei.setNumeroApoiantes(projetoid, 9999);
      democracia2Service.projetoDeLeiService.apoiarProjeto(cidadao.getId(), projetoid);

      EntityManager em = emf.createEntityManager();
      ProjetoDeLei projetoDeLei = em.find(ProjetoDeLei.class, projetoid);
      em.refresh(projetoDeLei);
      em.close();
      boolean test = projetoDeLei.getEstadoProjetoLei().equals(EstadoProjetoLei.EM_VOTACAO);
      catalogoProjetoDeLei.close();
      catalogoProjetoDeLei.close();
      assertEquals(expected, test);
    } catch (ApplicationException | IOException e) {
      System.out.println(e.getMessage());
      assertEquals(expected, false);
    }
  }

  @Test
  @DirtiesContext
  void testApresentaProjetoDeLei() throws ApplicationException, IOException {

    LocalDateTime dataValidade = LocalDateTime.now().plusDays(7);
    String resourceName = "/fase1.pdf";
    URL resourceUrl = getClass().getResource(resourceName);
    if (resourceUrl == null) {
      throw new IllegalArgumentException("Arquivo não encontrado: " + resourceName);
    }
    File file = new File(resourceUrl.getFile());
    DelegadoDTO novoDelegado =
        democracia2Service.delegadoService.createDelegado("Andre", "Silva", 133332313216L);
    Tema novoTema = catalogoTema.createTema("Alteracoes Area Saude Publica", null);
    catalogoTema.close();

    Long idProjeto =
        democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
            "Projeto", "Novo", file, dataValidade, novoTema.getId(), novoDelegado.getId());

    // verifica que é criado, nao e null
    assertNotNull(idProjeto);

    // verifica que é maior que 0L
    assertTrue(idProjeto > 0L);
  }

  @Test
  @DirtiesContext
  void testApresentaProjetoDeLeiTituloVazio()
      throws IllegalArgumentException, ApplicationException {

    LocalDateTime dataValidade = LocalDateTime.now().plusDays(7);
    String resourceName = "/fase1.pdf";
    URL resourceUrl = getClass().getResource(resourceName);
    if (resourceUrl == null) {
      throw new IllegalArgumentException("Arquivo não encontrado: " + resourceName);
    }
    File file = new File(resourceUrl.getFile());
    DelegadoDTO novoDelegado =
        democracia2Service.delegadoService.createDelegado("Andre", "Pereira", 133332303216L);
    Tema novoTema = catalogoTema.createTema("Alteracoes Area Economia", null);
    catalogoTema.close();

    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
                  "", "Projeto de Lei", file, dataValidade, novoTema.getId(), novoDelegado.getId());
            });

    assertEquals("O título não pode ser vazio.", exception.getMessage());
  }

  @Test
  @DirtiesContext
  void testApresentaProjetoDeLeiDataMaximaSuperior() throws ApplicationException, IOException {

    CatalogoProjetoDeLei catalogoProjetoDeLei = new CatalogoProjetoDeLei(emf.createEntityManager());

    LocalDateTime dataValidade = LocalDateTime.now().plusYears(3);
    String resourceName = "/fase1.pdf";
    URL resourceUrl = getClass().getResource(resourceName);
    if (resourceUrl == null) {
      throw new IllegalArgumentException("Arquivo não encontrado: " + resourceName);
    }
    File file = new File(resourceUrl.getFile());
    DelegadoDTO novoDelegado =
        democracia2Service.delegadoService.createDelegado("Andre", "Silva", 133332313216L);
    Tema novoTema = catalogoTema.createTema("Alteracoes Area Saude Publica", null);
    catalogoTema.close();

    Long idProjeto =
        democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
            "Projeto", "Novo", file, dataValidade, novoTema.getId(), novoDelegado.getId());
    ProjetoDeLei projetoDeLei = catalogoProjetoDeLei.findProjetoById(idProjeto).get();
    catalogoProjetoDeLei.close();
    LocalDateTime dataProjeto = projetoDeLei.getDataValidade();
    LocalDate date = dataProjeto.toLocalDate();

    // verifica que a data de validade embora superior, fica igual à data maxima permitida
    assertEquals(LocalDateTime.now().plusYears(1).toLocalDate(), date);
  }

  @Test
  @DirtiesContext
  void testConsultaProjetoDeLeiNaoExpirado() throws ApplicationException, IOException {

    CatalogoProjetoDeLei catalogoProjetoDeLei = new CatalogoProjetoDeLei(emf.createEntityManager());

    LocalDateTime dataValidade = LocalDateTime.now().plusDays(10);
    String resourceName = "/fase1.pdf";
    URL resourceUrl = getClass().getResource(resourceName);
    if (resourceUrl == null) {
      throw new IllegalArgumentException("Arquivo não encontrado: " + resourceName);
    }
    File file = new File(resourceUrl.getFile());

    DelegadoDTO novoDelegado =
        democracia2Service.delegadoService.createDelegado("Joao", "Antunes", 134432313216L);
    Tema novoTema = catalogoTema.createTema("AreaFinanças", null);

    DelegadoDTO novoDelegado1 =
        democracia2Service.delegadoService.createDelegado("Fred", "Parra", 132132313216L);
    Tema novoTema1 = catalogoTema.createTema("AreaHabitação", null);
    catalogoTema.close();

    Long idProjeto =
        democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
            "Projeto", "Novo", file, dataValidade, novoTema.getId(), novoDelegado.getId());
    Long idProjeto1 =
        democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
            "Projeto", "Novo", file, dataValidade, novoTema1.getId(), novoDelegado1.getId());

    ProjetoDeLei projeto = catalogoProjetoDeLei.findProjetoById(idProjeto).get();
    catalogoProjetoDeLei.close();
  }

  @Test
  @DirtiesContext
  void testFechaProjetosExpiradosEApoiarExpirados() throws ApplicationException, IOException {

    CatalogoProjetoDeLei catalogoProjetoDeLei = new CatalogoProjetoDeLei(emf.createEntityManager());
    FechaProjetosDeLeiExpiradosHandler fechaProjetosDeLeiExpiradosHandler =
        new FechaProjetosDeLeiExpiradosHandler(emf);

    // Projeto Expirado
    DelegadoDTO novoDelegado =
        democracia2Service.delegadoService.createDelegado("Luisa", "Pires", 134442313216L);
    Tema novoTema = catalogoTema.createTema("AreaUniversidade", null);

    String resourceName = "/fase1.pdf";
    URL resourceUrl = getClass().getResource(resourceName);
    if (resourceUrl == null) {
      throw new IllegalArgumentException("Arquivo não encontrado: " + resourceName);
    }
    File file = new File(resourceUrl.getFile());

    Long idProjeto =
        democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
            "Projeto",
            "Novo",
            file,
            LocalDateTime.now().plusDays(1),
            novoTema.getId(),
            novoDelegado.getId());
    ProjetoDeLei projeto = catalogoProjetoDeLei.findProjetoById(idProjeto).get();

    EntityManager em = emf.createEntityManager();
    em.getTransaction().begin();
    projeto.setDataValidade(LocalDateTime.now().minusDays(3));
    em.merge(projeto);
    em.getTransaction().commit();
    em.close();

    // Projeto Valido
    DelegadoDTO novoDelegado1 =
        democracia2Service.delegadoService.createDelegado("Sofia", "Alexandra", 112344313216L);
    Tema novoTema1 = catalogoTema.createTema("AreaMar", null);
    catalogoTema.close();
    Long id_Projeto =
        democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
            "Projeto",
            "Novo",
            file,
            LocalDateTime.now().plusDays(3),
            novoTema1.getId(),
            novoDelegado1.getId());

    fechaProjetosDeLeiExpiradosHandler.fechaProjetosExpirados();

    // Atualiza a entidade
    em = emf.createEntityManager();
    ProjetoDeLei projetoExpirado = em.find(ProjetoDeLei.class, idProjeto);
    em.refresh(projetoExpirado);
    em.close();

    // verifica que projetos expirados foram fechados
    assertEquals(EstadoProjetoLei.EXPIRADO, projetoExpirado.getEstadoProjetoLei());

    // testar se da para votar num projeto expirado
    CidadaoDTO cidadao5 =
        democracia2Service.cidadaoService.createCidadao("Toni", "Alves", 1111118L);
    assertThrows(
        ApplicationException.class,
        () -> {
          democracia2Service.projetoDeLeiService.apoiarProjeto(
              cidadao5.getId(), projetoExpirado.getId());
        });

    // verificar que projetos válidos não foram fechados
    ProjetoDeLei projetoValido = catalogoProjetoDeLei.findProjetoById(id_Projeto).get();
    assertEquals(EstadoProjetoLei.ABERTO, projetoValido.getEstadoProjetoLei());
  }
}
