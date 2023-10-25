package pt.ul.fc.css.democracia2.controller;

import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import pt.ul.fc.css.democracia2.application.CidadaoDTO;
import pt.ul.fc.css.democracia2.application.DelegadoDTO;
import pt.ul.fc.css.democracia2.application.Democracia2Service;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoProjetoDeLei;
import pt.ul.fc.css.democracia2.business.catalogs.CatalogoTema;
import pt.ul.fc.css.democracia2.business.entities.Cidadao;
import pt.ul.fc.css.democracia2.business.entities.Tema;
import pt.ul.fc.css.democracia2.business.handlers.ApresentarProjetoDeLeiHandler;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

@Controller
public class WebController {

  @Autowired EntityManagerFactory emf;
  Logger logger = LoggerFactory.getLogger(WebController.class);

  @Autowired private Democracia2Service democracia2Service;

  @GetMapping("/init")
  public String init(final Model model) throws ApplicationException, IOException {
    CatalogoTema catalogoTema = new CatalogoTema(emf.createEntityManager());
    Tema tema1 = catalogoTema.createTema("Sáude", null);
    Tema tema2 = catalogoTema.createTema("QualidadeDeVida", tema1);
    Tema tema3 = catalogoTema.createTema("Universidade", null);
    Tema tema4 = catalogoTema.createTema("Escolas", null);
    catalogoTema.close();
    ApresentarProjetoDeLeiHandler apresentaProjetoDeLeiHandler =
        new ApresentarProjetoDeLeiHandler(emf);
    DelegadoDTO delegado =
        democracia2Service.delegadoService.createDelegado("Joana", "Alcides", 1234523413216L);
    DelegadoDTO delegado2 =
        democracia2Service.delegadoService.createDelegado("Mário", "Soares", 111111111L);

    CidadaoDTO cidadao =
        democracia2Service.cidadaoService.createCidadao("João", "Silva", 123456789L);
    CidadaoDTO cidadao2 =
        democracia2Service.cidadaoService.createCidadao("Tomas", "Silva", 12234112156780L);

    CidadaoDTO cidadao3 =
        democracia2Service.cidadaoService.createCidadao("Antonio", "Silva", 12234112156781L);

    Long projetoid =
        apresentaProjetoDeLeiHandler.apresentaProjetoDeLei(
            "Drogas",
            "Legalizar Drogas em Portugal",
            null,
            LocalDateTime.now().plusYears(1),
            tema2.getId(),
            delegado.getId());

    Long projetoid2 =
        apresentaProjetoDeLeiHandler.apresentaProjetoDeLei(
            "Hospitais",
            "Aumentar numero de hospitais em Portugal",
            null,
            LocalDateTime.now().plusYears(1),
            tema2.getId(),
            delegado.getId());
    Long projetoid3 =
        apresentaProjetoDeLeiHandler.apresentaProjetoDeLei(
            "Eutanásia",
            "Legalizar a Euntanásia em Portugal",
            null,
            LocalDateTime.now().plusYears(1),
            tema2.getId(),
            delegado.getId());

    CatalogoProjetoDeLei catalogoProjetoDeLei = new CatalogoProjetoDeLei(emf.createEntityManager());
    catalogoProjetoDeLei.setNumeroApoiantes(projetoid, 9999);
    catalogoProjetoDeLei.setNumeroApoiantes(projetoid2, 9999);
    democracia2Service.cidadaoService.escolheDelegado(
        cidadao.getId(), delegado2.getId(), tema2.getId());
    democracia2Service.projetoDeLeiService.apoiarProjeto(cidadao3.getId(), projetoid);
    return "login";
  }

  @RequestMapping("/")
  public String getLogin(Model model) {
    return "login";
  }

  @RequestMapping("/index")
  public String getIndex(Model model) {
    model.addAttribute("democracia2Service", democracia2Service);
    return "index";
  }

  @GetMapping("/user/new")
  public String newCustomer(final Model model) {
    model.addAttribute("user", new Cidadao());
    return "registar";
  }

  @PostMapping("/user/new")
  public String newCustomerAction(
      final Model model, @ModelAttribute CidadaoDTO c, @RequestParam("role") String role) {
    CidadaoDTO c2;
    DelegadoDTO d2;
    try {
      if (role.equals("CIDADAO")) {
        c2 =
            democracia2Service.cidadaoService.createCidadao(c.getName(), c.getSurname(), c.getCc());
        democracia2Service.setUser(c.getCc().toString());
      } else if (role.equals("DELEGADO")) {
        d2 =
            democracia2Service.delegadoService.createDelegado(
                c.getName(), c.getSurname(), c.getCc());
        democracia2Service.setUser(c.getCc().toString());
      } else {
        throw new ApplicationException("Role inválida.");
      }
      logger.debug("Cidadao added to the database.");
      return "redirect:/index";
    } catch (ApplicationException e) {
      c2 = new CidadaoDTO();
      model.addAttribute("user", c2);
      model.addAttribute("error", "Já existe um utilizador com este CC");
      return "registar";
    }
  }

  @GetMapping("/login")
  public String login(final Model model, @RequestParam String cc) {
    try {
      democracia2Service.setUser(cc);
      logger.debug("User logged in: " + democracia2Service.getUser().getName());
      return "redirect:/index";
    } catch (ApplicationException e) {
      model.addAttribute("error", "Erro no login: " + e.getMessage());
      return "login";
    }
  }
}
