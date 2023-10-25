package pt.ul.fc.css.democracia2.business.entities;

import jakarta.persistence.*;
import java.util.Objects;
import org.springframework.lang.NonNull;

/** Classe que representa o Tema do Democracia2 */
@Entity
public class Tema {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(unique = true)
  @NonNull
  private String titulo;

  @ManyToOne private Tema paiTema;

  /**
   * Construtor de um Tema
   *
   * @param titulo - titulo do tema
   */
  public Tema(@NonNull String titulo) {
    this.titulo = titulo;
  }

  /**
   * Construtor de um Tema que sera mais especifico de um Tema existente
   *
   * @param titulo - titulo de um tema
   * @param paiTema - tema geral
   */
  public Tema(@NonNull String titulo, Tema paiTema) {
    this.titulo = titulo;
    this.paiTema = paiTema;
  }

  /** Construtor vazio */
  public Tema() {}

  /**
   * obtem o nome do tema
   *
   * @return o nome do tema
   */
  public String getTitulo() {
    return titulo;
  }

  /**
   * Obtem o id do Tema criado pela base de dados
   *
   * @return o id do Tema
   */
  public Long getId() {
    return id;
  }

  /**
   * Obtem o Tema mais geral deste Tema
   *
   * @return o Tema mais geral deste Tema
   */
  public Tema getPaiTema() {
    return paiTema;
  }

  /**
   * Atribui um tema Geral
   *
   * @param paiTema
   */
  public void setPaiTema(Tema paiTema) {
    this.paiTema = paiTema;
  }

  /**
   * Atribui um titulo
   *
   * @param titulo titulo do Tema
   */
  public void setTitulo(@NonNull String titulo) {
    this.titulo = titulo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tema tema = (Tema) o;
    return Objects.equals(id, tema.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Tema{" + "titulo='" + titulo + '\'' + ", paiTema=" + paiTema + '}';
  }
}
