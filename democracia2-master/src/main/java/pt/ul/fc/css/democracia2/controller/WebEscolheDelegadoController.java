package pt.ul.fc.css.democracia2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pt.ul.fc.css.democracia2.application.CidadaoDTO;
import pt.ul.fc.css.democracia2.application.Democracia2Service;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

@Controller
public class WebEscolheDelegadoController {

  Logger logger = LoggerFactory.getLogger(WebEscolheDelegadoController.class);

  @Autowired private Democracia2Service democracia2Service;

  public WebEscolheDelegadoController() {
    super();
  }

  @PostMapping("/escolher-delegado")
  public String escolherDelegado(
      @RequestParam("tema") Long temaId, @RequestParam("delegado") Long delegadoId, Model model) {
    try {
      CidadaoDTO currentCid = democracia2Service.getUser();
      democracia2Service.cidadaoService.escolheDelegado(currentCid.getId(), delegadoId, temaId);
      model.addAttribute("message", "Delegado escolhido com sucesso.");
      return "redirect:/index";
    } catch (ApplicationException e) {
      model.addAttribute("error", "Já associou um delegado a este tema!");
      model.addAttribute("delegados", democracia2Service.delegadoService.getDelegados());
      model.addAttribute("temas", democracia2Service.projetoDeLeiService.getTemas());
      model.addAttribute("democracia2Service", democracia2Service);

      return "escolherDelegado";
    }
  }

  @GetMapping("/escolher-delegado")
  public String escolheDelegado(final Model model) {
    model.addAttribute("delegados", democracia2Service.delegadoService.getDelegados());
    model.addAttribute("temas", democracia2Service.projetoDeLeiService.getTemas());
    model.addAttribute("democracia2Service", democracia2Service);
    return "escolherDelegado";
  }
}
