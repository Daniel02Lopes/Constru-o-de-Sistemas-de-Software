package pt.ul.fc.css.democracia2.business.entities;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;

/** Representa um voto privado */
@Entity
@DiscriminatorValue("Priv")
public class VotoPrivado extends Voto {
  @OneToOne private Cidadao cidadao;

  /**
   * Construtor de um voto privado
   *
   * @param cidadao cidadao que votou privado
   */
  public VotoPrivado(@NonNull Cidadao cidadao) {

    super(LocalDateTime.now());
    this.cidadao = cidadao;
  }

  /** Construtor de um voto privado */
  public VotoPrivado() {}

  /**
   * Funcao que retorna o cidadao que votou neste voto
   *
   * @return cidadao que votou neste voto
   */
  public Cidadao getCidadao() {
    return cidadao;
  }
}
