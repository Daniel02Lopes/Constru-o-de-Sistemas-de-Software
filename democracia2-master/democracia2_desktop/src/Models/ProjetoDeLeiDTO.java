package Models;

public class ProjetoDeLeiDTO {
  private Long id;
  private String titulo;
  private String textoDescriptivo;
  private Tema tema;

  public void setTema(Tema tema) {
    this.tema = tema;
  }

  public Tema getTema() {
    return this.tema = tema;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getTextoDescriptivo() {
    return textoDescriptivo;
  }

  public void setTextoDescriptivo(String textoDescriptivo) {
    this.textoDescriptivo = textoDescriptivo;
  }
}
