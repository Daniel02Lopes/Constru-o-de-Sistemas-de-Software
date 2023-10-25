package pt.ul.fc.css.democracia2.business.entities;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
/** Representa um voto */
public class Voto {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  @Temporal(TemporalType.TIMESTAMP)
  @NonNull
  private LocalDateTime dataVoto;

  /**
   * Constutor do voto
   *
   * @param dataVoto data e hora de fecho da votacao
   */
  public Voto(@NonNull LocalDateTime dataVoto) {
    this.dataVoto = dataVoto;
  }

  /** Constutor do voto */
  public Voto() {}

  /**
   * Funcao que retorna a data e hora de fecho da votacao
   *
   * @return data e hora de fecho da votacao
   */
  @NonNull
  public LocalDateTime getDataVoto() {
    return dataVoto;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setDataVoto(@NonNull LocalDateTime dataVoto) {
    this.dataVoto = dataVoto;
  }
}
