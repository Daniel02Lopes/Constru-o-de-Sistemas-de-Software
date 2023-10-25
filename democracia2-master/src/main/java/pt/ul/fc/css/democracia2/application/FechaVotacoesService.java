package pt.ul.fc.css.democracia2.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FechaVotacoesService {

  private final Logger logger = LoggerFactory.getLogger(FechaProjetosLeiScheduled.class);
  @Autowired private ProjetoDeLeiService projetoDeLeiService;

  @Scheduled(fixedDelay = 30000)
  public void fecharVotacoesExpiradas() {
    try {
      projetoDeLeiService.fecharVotacoes();
    } catch (Exception e) {
      logger.error("Erro ao fechar votacões expirados.", e);
    }
  }
}
