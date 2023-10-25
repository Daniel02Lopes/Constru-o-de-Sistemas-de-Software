package pt.ul.fc.css.democracia2.business.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Representa um voto publico */
@Entity
@DiscriminatorValue("Pub")
public class VotoPublico extends Voto {
  @OneToOne private Delegado delegado;

  @Enumerated(EnumType.STRING)
  private TipoDeVoto tipoDeVoto;

  /**
   * Construtor de um voto publico
   *
   * @param delegado delegado que votou publico
   * @param tipoDeVoto voto de delegado
   */
  public VotoPublico(Delegado delegado, TipoDeVoto tipoDeVoto) {
    super(LocalDateTime.now());
    this.delegado = delegado;
    this.tipoDeVoto = tipoDeVoto;
  }

  public VotoPublico() {}

  /**
   * Funcao que retorna o Delegado deste voto publico
   *
   * @return Delegado deste voto
   */
  public Delegado getDelegado() {
    return this.delegado;
  }

  /**
   * Funcao que retorna o tipo de voto deste voto publico
   *
   * @return retorna o tipo de voto deste voto
   */
  public TipoDeVoto getTipoDeVoto() {
    return tipoDeVoto;
  }
}
