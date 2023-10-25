package pt.ul.fc.css.democracia2.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pt.ul.fc.css.democracia2.application.CidadaoDTO;
import pt.ul.fc.css.democracia2.application.Democracia2Service;
import pt.ul.fc.css.democracia2.application.ProjetoDeLeiDTO;
import pt.ul.fc.css.democracia2.application.ProjetoDeLeiService;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

@RestController
@RequestMapping("/api")
public class RestProjetoLeiController {

  @Autowired private Democracia2Service democracia2Service;
  @Autowired private ProjetoDeLeiService projetoDeLeiService;

  @PostMapping("/projetos-de-lei-n-expirados/apoio/{id}/{cc}")
  public ResponseEntity<?> apoiarProjeto_(@PathVariable Long id, @PathVariable Long cc) {
    try {
      democracia2Service.setUser(cc.toString());
      CidadaoDTO currentCid =
          democracia2Service.getUser(); // Retrieve the current cid from the service
      democracia2Service.projetoDeLeiService.apoiarProjeto(currentCid.getId(), id);
      return ResponseEntity.ok("Projeto de lei apoiado com sucesso.");
    } catch (ApplicationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping("/votacoes-em-curso")
  List<ProjetoDeLeiDTO> allVotacoesCurso() throws ApplicationException {
    return democracia2Service.projetoDeLeiService.listarVotacoesEmCurso();
  }

  @RequestMapping(value = "/index")
  public ModelAndView home() {
    return new ModelAndView("index");
  }

  @GetMapping("/projetos-de-lei-n-expirados")
  List<ProjetoDeLeiDTO> consultaListaProjetosNaoExpirados() throws ApplicationException {
    List<ProjetoDeLeiDTO> projetos =
        democracia2Service.projetoDeLeiService.consultaListaProjetosNaoExpirados();
    return projetos;
  }

  @PostMapping("/login/{cc}")
  public ResponseEntity<?> login(@PathVariable Long cc) throws ApplicationException {
    try {
      democracia2Service.setUser(cc.toString());
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (ApplicationException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/projetos-de-lei-n-expirados/{id}")
  public ProjetoDeLeiDTO consultaProjetoNaoExpirados(@PathVariable Long id)
      throws ApplicationException {
    Optional<ProjetoDeLeiDTO> proj =
        democracia2Service.projetoDeLeiService.consultaProjetoNaoExpirado(id);
    if (proj.isPresent()) {
      return proj.get();
    }
    return null;
  }

  @PostMapping("/customers")
  ResponseEntity<?> createCustomer(@RequestBody CidadaoDTO newCustomer) {
    try {
      CidadaoDTO c =
          democracia2Service.cidadaoService.createCidadao(
              newCustomer.getName(), newCustomer.getSurname(), newCustomer.getCc());
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (ApplicationException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/votacao/{id}/votar/{cc}")
  public ModelMap votar(@PathVariable Long id, @PathVariable Long cc) throws ApplicationException {
    ModelMap model = new ModelMap();
    democracia2Service.setUser(cc.toString());
    System.out.println(id);
    Optional<ProjetoDeLeiDTO> p =
        democracia2Service.projetoDeLeiService.consultaProjetoNaoExpirado(id);
    ProjetoDeLeiDTO pro = p.get();
    model.addAttribute("id", pro.getId());
    if (projetoDeLeiService.getVotoPorOmissao(
            democracia2Service.getUser().getId(), pro.getVotacao().getId(), pro.getTema())
        == null) {
      model.addAttribute("votoPorOmissao", null);
    } else {
      model.addAttribute(
          "votoPorOmissao",
          projetoDeLeiService
              .getVotoPorOmissao(
                  democracia2Service.getUser().getId(), pro.getVotacao().getId(), pro.getTema())
              .toString());
    }
    return model;
  }

  @PostMapping("/votacao/{id}/votar/{cc}/{voto}")
  ResponseEntity<?> votarProjetoDeLei(
      @PathVariable Long id, @PathVariable Long cc, @PathVariable String voto) throws Exception {

    try {
      democracia2Service.setUser(cc.toString());
      Optional<ProjetoDeLeiDTO> p =
          democracia2Service.projetoDeLeiService.consultaProjetoNaoExpirado(id);
      ProjetoDeLeiDTO pro = p.get();

      democracia2Service.projetoDeLeiService.votarNumaVotacao(
          democracia2Service.getUser().getId(), pro.getVotacao().getId(), pro.getTema(), voto);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (ApplicationException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }
}
