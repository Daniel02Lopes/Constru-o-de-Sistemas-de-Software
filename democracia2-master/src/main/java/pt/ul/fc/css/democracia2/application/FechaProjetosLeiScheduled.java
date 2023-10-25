package pt.ul.fc.css.democracia2.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FechaProjetosLeiScheduled {
  private final Logger logger = LoggerFactory.getLogger(FechaProjetosLeiScheduled.class);
  @Autowired private Democracia2Service democracia2Service;

  @Scheduled(cron = "*/30 * * * * *") // atualizar de 30 em 30 segundos!!!
  public void fecharProjetosDeLeiExpirados() {
    try {
      democracia2Service.projetoDeLeiService.fechaProjetosDeLeiExpirados();
      logger.info("Tarefa de fechar projetos de lei expirados concluída com sucesso!");
    } catch (Exception e) {
      logger.error("Erro ao fechar projetos de lei expirados.", e);
    }
  }
}
