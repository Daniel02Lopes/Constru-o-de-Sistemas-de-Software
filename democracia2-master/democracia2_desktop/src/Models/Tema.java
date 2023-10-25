package Models;

import java.util.Objects;

public class Tema {

  private Long id;

  private String titulo;

  private Tema paiTema;

  public Tema(String titulo) {
    this.titulo = titulo;
  }

  public Tema(String titulo, Tema paiTema) {
    this.titulo = titulo;
    this.paiTema = paiTema;
  }

  public Tema() {}

  public String getTitulo() {
    return titulo;
  }

  public Long getId() {
    return id;
  }

  public Tema getPaiTema() {
    return paiTema;
  }

  public void setPaiTema(Tema paiTema) {
    this.paiTema = paiTema;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tema tema = (Tema) o;
    return Objects.equals(id, tema.id);
  }

  public int hashCode() {
    return Objects.hash(id);
  }

  public String toString() {
    return "Tema{" + "titulo='" + titulo + '\'' + ", paiTema=" + paiTema + '}';
  }
}
