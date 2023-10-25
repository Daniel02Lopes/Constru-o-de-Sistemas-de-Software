package pt.ul.fc.css.democracia2.business.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/** Representa uma votacao */
@Entity
public class Votacao {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private long id;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Voto> votos = new LinkedList<>();

  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime dataFecho;

  private int votos_favoraveis;

  private int votos_desfavoravies;

  /** Construtor da votacao */
  public Votacao() {
    votos_desfavoravies = 0;
    votos_favoraveis = 0;
  }

  /**
   * Funcao que atribui a data e hora de fecho da votacao
   *
   * @param dataFecho data e hora de fecho da votacao
   */
  public void setExpirationDate(LocalDateTime dataFecho) {

    this.dataFecho = dataFecho;
  }

  /**
   * Funcao que retorna o id da votacao
   *
   * @return O id da votacao
   */
  public Long getId() {
    return id;
  }

  /**
   * Funcao que adiciona um voto publico
   *
   * @param delegado delegado que vai votar
   * @param tipoDeVoto voto do delegado
   */
  public void addVotoPublico(Delegado delegado, TipoDeVoto tipoDeVoto) {
    VotoPublico novoVoto = new VotoPublico(delegado, tipoDeVoto);
    if (tipoDeVoto == TipoDeVoto.FAVORAVEL) {
      votos_favoraveis++;
    } else if (tipoDeVoto == TipoDeVoto.DESFAVORAVEL) {
      votos_desfavoravies++;
    }
    votos.add(novoVoto);
  }

  /**
   * Funcao que adiciona um voto privado
   *
   * @param cidadao cidadao que vai votar de forma privada
   * @param tipoDeVoto voto do cidadao
   */
  public void addVotoPrivado(Cidadao cidadao, TipoDeVoto tipoDeVoto) {
    if (tipoDeVoto == TipoDeVoto.FAVORAVEL) {
      votos_favoraveis++;
    } else if (tipoDeVoto == TipoDeVoto.DESFAVORAVEL) {
      votos_desfavoravies++;
    }
    VotoPrivado novoVoto = new VotoPrivado(cidadao);
    votos.add(novoVoto);
  }

  /**
   * Funcao que retorna o numero de votos desfavoraveis
   *
   * @return numero de votos desfavoraveis
   */
  public int getVotos_desfavoravies() {
    return votos_desfavoravies;
  }

  /**
   * Funcao que retorna o numero de votos favoraveis
   *
   * @return numero de votos favoraveis
   */
  public int getVotos_favoraveis() {
    return votos_favoraveis;
  }

  /**
   * Funcao que retorna a data e hora do fecho da votacao
   *
   * @return data e hora do fecho da votacao
   */
  public LocalDateTime getDataFecho() {
    return dataFecho;
  }

  /**
   * funcao que retorna a lista dos votos
   *
   * @return lista dos votos
   */
  public List<Voto> getVotos() {
    return new LinkedList<>(votos);
  }
}
