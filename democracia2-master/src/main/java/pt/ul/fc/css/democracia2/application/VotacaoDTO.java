package pt.ul.fc.css.democracia2.application;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class VotacaoDTO {

  private long id;

  private LocalDateTime dataFecho;

  private int votos_favoraveis;

  private int votos_desfavoravies;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public LocalDateTime getDataFecho() {
    return dataFecho;
  }

  public void setDataFecho(LocalDateTime dataFecho) {
    this.dataFecho = dataFecho;
  }

  public int getVotos_favoraveis() {
    return votos_favoraveis;
  }

  public void setVotos_favoraveis(int votos_favoraveis) {
    this.votos_favoraveis = votos_favoraveis;
  }

  public int getVotos_desfavoravies() {
    return votos_desfavoravies;
  }

  public void setVotos_desfavoravies(int votos_desfavoravies) {
    this.votos_desfavoravies = votos_desfavoravies;
  }

  public void setExpirationDate(LocalDateTime dataFecho) {
    this.dataFecho = dataFecho;
  }
}
