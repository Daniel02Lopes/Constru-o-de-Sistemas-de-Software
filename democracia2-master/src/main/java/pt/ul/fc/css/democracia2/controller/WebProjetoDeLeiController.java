package pt.ul.fc.css.democracia2.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pt.ul.fc.css.democracia2.application.CidadaoDTO;
import pt.ul.fc.css.democracia2.application.Democracia2Service;
import pt.ul.fc.css.democracia2.application.ProjetoDeLeiDTO;
import pt.ul.fc.css.democracia2.application.ProjetoDeLeiService;
import pt.ul.fc.css.democracia2.facade.exceptions.ApplicationException;

@Controller
public class WebProjetoDeLeiController {

  Logger logger = LoggerFactory.getLogger(WebProjetoDeLeiController.class);

  @Autowired private Democracia2Service democracia2Service;

  @Autowired private ProjetoDeLeiService projetoDeLeiService;

  public WebProjetoDeLeiController() {
    super();
  }

  @PostMapping("/projetos-de-lei-n-expirados/apoio")
  public String apoiarProjeto(@RequestParam("projetoId") Long projetoId, Model model)
      throws ApplicationException {
    try {
      CidadaoDTO currentCid =
          democracia2Service.getUser(); // Retrieve the current cid from the service
      democracia2Service.projetoDeLeiService.apoiarProjeto(currentCid.getId(), projetoId);
      model.addAttribute("message", "Projeto de lei apoiado com sucesso.");
      return "redirect:/projetos-de-lei-n-expirados";
    } catch (ApplicationException e) {
      Optional<ProjetoDeLeiDTO> p =
          democracia2Service.projetoDeLeiService.consultaProjetoNaoExpirado(projetoId);
      model.addAttribute("error", "ERRO! Você já apoiou este projeto!");
      model.addAttribute("projeto", p.get());
      return "projetoDeLei_detail";
    }
  }

  @GetMapping("/votacoes-em-curso")
  public String votacoesEmCurso(final Model model) {
    try {
      model.addAttribute(
          "votacoes_curso", democracia2Service.projetoDeLeiService.listarVotacoesEmCurso());
    } catch (ApplicationException e) {
      model.addAttribute("error", "Erro ao obter as votações em curso");
      return "index";
    }
    return "votacoes_curso_list";
  }

  @GetMapping("/projetos-de-lei-n-expirados")
  public String consultaListaProjetosNaoExpirados(final Model model) {
    try {
      model.addAttribute(
          "projetos", democracia2Service.projetoDeLeiService.consultaListaProjetosNaoExpirados());
    } catch (ApplicationException e) {
      model.addAttribute("error", "Erro ao obter os projetos de Lei não expirados");
      return "index";
    }
    return "ProjetosDeLeiNaoExpirados";
  }

  @GetMapping("/projetos-de-lei-n-expirados/{id}")
  public String consultaProjetoNaoExpirados(final Model model, @PathVariable Long id) {
    try {
      Optional<ProjetoDeLeiDTO> p =
          democracia2Service.projetoDeLeiService.consultaProjetoNaoExpirado(id);
      if (p.isPresent()) {
        model.addAttribute("projeto", p.get());
        return "projetoDeLei_detail";
      } else {
        model.addAttribute("error", "Projeto de Lei não existe");
        return "ProjetosDeLeiNaoExpirados";
      }
    } catch (ApplicationException e) {
      model.addAttribute("error", "Erro ao consultar o projeto de Lei");
      return "ProjetosDeLeiNaoExpirados";
    }
  }

  @GetMapping("/votacao/{id}")
  public String consultaVotacao(final Model model, @PathVariable Long id) {
    try {
      Optional<ProjetoDeLeiDTO> p =
          democracia2Service.projetoDeLeiService.consultaProjetoNaoExpirado(id);
      if (p.isPresent()) {
        model.addAttribute("projeto", p.get());
        return "votacao_detail";
      } else {
        throw new ApplicationException("Erro ao consultar");
      }
    } catch (ApplicationException e) {
      model.addAttribute("error", "Erro ao consultar a Votação");
      return "votacoes-em-curso";
    }
  }

  @PostMapping("/projetos-de-lei/new")
  public String apresentarProjetoDeLei(
      Model model,
      @RequestParam("titulo") String titulo,
      @RequestParam("textoDescriptivo") String textoDescriptivo,
      @RequestParam("anexo") MultipartFile anexo,
      @RequestParam("dataValidade") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime dataValidade,
      @RequestParam("tema") Long tema)
      throws Exception {

    try {
      File tempFile = File.createTempFile("anexo_", "");
      anexo.transferTo(tempFile);
      democracia2Service.projetoDeLeiService.apresentaProjetoDeLei(
          titulo,
          textoDescriptivo,
          tempFile,
          dataValidade,
          tema,
          democracia2Service.getUser().getId());
      return "redirect:/projetos-de-lei-n-expirados";
    } catch (ApplicationException e) {
      model.addAttribute("temas", projetoDeLeiService.getTemas());
      model.addAttribute("error", "Erro ao apresentar o Projeto de Lei");
      return "apresentarProjetoLei";
    }
  }

  @GetMapping("/projetos-de-lei/new")
  public String novoProjeto(final Model model) {
    model.addAttribute("temas", projetoDeLeiService.getTemas());
    return "apresentarProjetoLei";
  }

  @GetMapping("/votacao/{id}/votar")
  public String votar(final Model model, @PathVariable Long id) throws ApplicationException {
    try {
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
      return "votar";
    } catch (ApplicationException e) {
      Optional<ProjetoDeLeiDTO> p =
          democracia2Service.projetoDeLeiService.consultaProjetoNaoExpirado(id);
      ProjetoDeLeiDTO pro = p.get();
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
      model.addAttribute("error", "ERRO! Você já votou neste Projeto");
      return "votar";
    }
  }

  @PostMapping("/votacao/{id}/votar")
  public String votarProjetoDeLei(
      Model model, @PathVariable Long id, @RequestParam("voto") String voto) throws Exception {
    try {
      Optional<ProjetoDeLeiDTO> p =
          democracia2Service.projetoDeLeiService.consultaProjetoNaoExpirado(id);
      ProjetoDeLeiDTO pro = p.get();
      democracia2Service.projetoDeLeiService.votarNumaVotacao(
          democracia2Service.getUser().getId(), pro.getVotacao().getId(), pro.getTema(), voto);
      return "redirect:/votacoes-em-curso";
    } catch (ApplicationException e) {
      model.addAttribute("error", "ERRO! Você já votou neste Projeto de Lei");
      return "votar";
    }
  }
}
