package pt.ul.fc.css.democracia2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pt.ul.fc.css.democracia2.application.CidadaoDTO;
import pt.ul.fc.css.democracia2.application.DelegadoDTO;
import pt.ul.fc.css.democracia2.application.Democracia2Service;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoProjetoDeLei;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoTema;
import pt.ul.fc.css.democracia2.business.entities.Tema;
import pt.ul.fc.css.democracia2.business.entities.TipoDeVoto;
import pt.ul.fc.css.democracia2.controller.RestProjetoLeiController;

@SpringBootTest(classes = Democracia2Application.class)
public class TestsServer {

  @Autowired private EntityManagerFactory emf;

  @Autowired private Democracia2Service democracia2Service;
  @Autowired private RestProjetoLeiController restProjetoLeiController;

  @BeforeEach
  public void setup() {
    democracia2Service = new Democracia2Service(emf);
  }

  @Test
  @DirtiesContext
  public void testLoginSuccess() throws Exception {
    CidadaoDTO cidadao1 =
        democracia2Service.cidadaoService.createCidadao("test", "test", 123456789L);
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(restProjetoLeiController).build();
    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("http://localhost:8080/api/login/{cc}", 123456789L))
            .andExpect(status().isOk())
            .andReturn();

    assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
  }

  @Test
  @DirtiesContext
  public void testLoginFailure() throws Exception {
    CidadaoDTO cidadao1 =
        democracia2Service.cidadaoService.createCidadao("test", "test", 123456789L);
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(restProjetoLeiController).build();
    MvcResult mvcResult =
        mockMvc
            .perform(MockMvcRequestBuilders.post("http://localhost:8080/api/login/{cc}", 12345678L))
            .andExpect(status().isBadRequest())
            .andReturn();

    assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
  }

  @Test
  @DirtiesContext
  public void testApoiarProjetoSuccess() throws Exception {
    CatalogoTema catalogoTema = new CatalogoTema(emf.createEntityManager());
    DelegadoDTO delegado =
        democracia2Service.delegadoService.createDelegado("Teste", "Teste", 123456789L);
    Tema tema1 = catalogoTema.createTema("TesteApoiar", null);
    catalogoTema.close();
    CidadaoDTO cidadao =
        democracia2Service.cidadaoService.createCidadao("Teste", "Teste", 12345678L);
    Long projetoid =
        democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
            "Titulo2",
            "Texto",
            null,
            LocalDateTime.now().plusYears(1),
            tema1.getId(),
            delegado.getId());
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(restProjetoLeiController).build();
    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(
                        "http://localhost:8080/api/projetos-de-lei-n-expirados/apoio/{id}/{cc}",
                        projetoid,
                        12345678L)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
  }

  @Test
  @DirtiesContext
  public void testApoiarProjetoFailure() throws Exception {
    CatalogoTema catalogoTema = new CatalogoTema(emf.createEntityManager());
    DelegadoDTO delegado =
        democracia2Service.delegadoService.createDelegado("Teste", "Teste", 123456789L);
    Tema tema1 = catalogoTema.createTema("TesteApoiar", null);
    catalogoTema.close();
    CidadaoDTO cidadao =
        democracia2Service.cidadaoService.createCidadao("Teste", "Teste", 12345678L);

    Long projetoid =
        democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
            "Titulo2",
            "Texto",
            null,
            LocalDateTime.now().plusYears(1),
            tema1.getId(),
            delegado.getId());
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(restProjetoLeiController).build();
    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(
                        "http://localhost:8080/api/projetos-de-lei-n-expirados/apoio/{id}/{cc}",
                        projetoid,
                        12345678L)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

    MvcResult mvcResult2 =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(
                        "http://localhost:8080/api/projetos-de-lei-n-expirados/apoio/{id}/{cc}",
                        projetoid,
                        12345678L)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();

    assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult2.getResponse().getStatus());

    MvcResult mvcResult3 =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(
                        "http://localhost:8080/api/projetos-de-lei-n-expirados/apoio/{id}/{cc}",
                        projetoid,
                        123456789L)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();

    assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult3.getResponse().getStatus());
  }

  @Test
  @DirtiesContext
  public void testConsultarProjetosNaoExpirados() throws Exception {
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(restProjetoLeiController).build();
    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("http://localhost:8080/api/projetos-de-lei-n-expirados")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
  }

  @Test
  @DirtiesContext
  public void testConsultarProjetoNaoExpirado() throws Exception {
    CatalogoTema catalogoTema = new CatalogoTema(emf.createEntityManager());
    DelegadoDTO delegado =
        democracia2Service.delegadoService.createDelegado("Teste", "Teste", 123456789L);
    Tema tema = catalogoTema.createTema("Teste", null);
    catalogoTema.close();
    Long projetoId =
        democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
            "Titulo",
            "Texto",
            null,
            LocalDateTime.now().plusYears(1),
            tema.getId(),
            delegado.getId());

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(restProjetoLeiController).build();
    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(
                        "http://localhost:8080/api/projetos-de-lei-n-expirados/{id}", projetoId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
  }

  @Test
  @DirtiesContext
  public void testVotarProjetoDeLeiSuccess() throws Exception {
    CatalogoTema catalogoTema = new CatalogoTema(emf.createEntityManager());
    CatalogoProjetoDeLei catalogoProjeto = new CatalogoProjetoDeLei(emf.createEntityManager());
    DelegadoDTO delegado =
        democracia2Service.delegadoService.createDelegado("Teste", "Teste", 123456789L);
    Tema tema = catalogoTema.createTema("Teste", null);
    CidadaoDTO cidadao =
        democracia2Service.cidadaoService.createCidadao("JoÃ£o", "Silva", 12234112156789L);
    catalogoTema.close();
    Long projetoId =
        democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
            "Titulo",
            "Texto",
            null,
            LocalDateTime.now().plusYears(1),
            tema.getId(),
            delegado.getId());

    catalogoProjeto.setNumeroApoiantes(projetoId, 9999);
    democracia2Service.projetoDeLeiService.apoiarProjeto(cidadao.getId(), projetoId);
    catalogoProjeto.close();

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(restProjetoLeiController).build();
    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(
                        "http://localhost:8080/api/votacao/{id}/votar/{cc}/{voto}",
                        projetoId,
                        12234112156789L,
                        TipoDeVoto.FAVORAVEL)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
  }

  @Test
  @DirtiesContext
  public void testVotarProjetoDeLeiFailure() throws Exception {
    CatalogoTema catalogoTema = new CatalogoTema(emf.createEntityManager());
    DelegadoDTO delegado =
        democracia2Service.delegadoService.createDelegado("Teste", "Teste", 123456789L);
    Tema tema = catalogoTema.createTema("Teste", null);
    catalogoTema.close();
    Long projetoId =
        democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
            "Titulo",
            "Texto",
            null,
            LocalDateTime.now().plusYears(1),
            tema.getId(),
            delegado.getId());
    CidadaoDTO cidadao =
        democracia2Service.cidadaoService.createCidadao("Teste", "Teste", 12345678L);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(restProjetoLeiController).build();

    MvcResult mvcResult2 =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(
                        "http://localhost:8080/api/votacao/{id}/votar/{cc}/{voto}",
                        projetoId,
                        0L,
                        TipoDeVoto.FAVORAVEL)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();

    assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult2.getResponse().getStatus());

    CatalogoProjetoDeLei catalogoProjeto = new CatalogoProjetoDeLei(emf.createEntityManager());
    catalogoProjeto.setNumeroApoiantes(projetoId, 9999);
    democracia2Service.projetoDeLeiService.apoiarProjeto(cidadao.getId(), projetoId);
    catalogoProjeto.close();

    mockMvc = MockMvcBuilders.standaloneSetup(restProjetoLeiController).build();
    MvcResult mvcResult3 =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(
                        "http://localhost:8080/api/votacao/{id}/votar/{cc}/{voto}",
                        projetoId,
                        123456789L,
                        TipoDeVoto.FAVORAVEL)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();

    assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult3.getResponse().getStatus());

    mockMvc = MockMvcBuilders.standaloneSetup(restProjetoLeiController).build();
    MvcResult mvcResult4 =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(
                        "http://localhost:8080/api/votacao/{id}/votar/{cc}/{voto}",
                        projetoId,
                        12345678L,
                        TipoDeVoto.FAVORAVEL)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    assertEquals(HttpStatus.OK.value(), mvcResult4.getResponse().getStatus());

    mockMvc = MockMvcBuilders.standaloneSetup(restProjetoLeiController).build();
    MvcResult mvcResult5 =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(
                        "http://localhost:8080/api/votacao/{id}/votar/{cc}/{voto}",
                        projetoId,
                        12345678L,
                        TipoDeVoto.FAVORAVEL)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();

    assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult5.getResponse().getStatus());
  }
}
