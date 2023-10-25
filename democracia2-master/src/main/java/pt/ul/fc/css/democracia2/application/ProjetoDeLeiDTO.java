package pt.ul.fc.css.democracia2.application;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;
import pt.ul.fc.css.democracia2.business.entities.*;

@Component
public class ProjetoDeLeiDTO {

  private Long id;

  private String titulo;
  private String textoDescriptivo;

  private LocalDateTime dataValidade;

  private Tema tema;

  private Delegado delegadoProponente;

  private List<Cidadao> listaCidadaosApoiantes;

  private EstadoProjetoLei estadoProjetoLei;

  private VotacaoDTO votacao;
  private int numerodeApoiantes;

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

  public LocalDateTime getDataValidade() {
    return dataValidade;
  }

  public void setDataValidade(LocalDateTime dataValidade) {
    this.dataValidade = dataValidade;
  }

  public Tema getTema() {
    return tema;
  }

  public void setTema(Tema tema) {
    this.tema = tema;
  }

  public Delegado getDelegadoProponente() {
    return delegadoProponente;
  }

  public void setDelegadoProponente(Delegado delegadoProponente) {
    this.delegadoProponente = delegadoProponente;
  }

  public List<Cidadao> getListaCidadaosApoiantes() {
    return listaCidadaosApoiantes;
  }

  public void setListaCidadaosApoiantes(List<Cidadao> listaCidadaosApoiantes) {
    this.listaCidadaosApoiantes = listaCidadaosApoiantes;
  }

  public EstadoProjetoLei getEstadoProjetoLei() {
    return estadoProjetoLei;
  }

  public void setEstadoProjetoLei(EstadoProjetoLei estadoProjetoLei) {
    this.estadoProjetoLei = estadoProjetoLei;
  }

  public VotacaoDTO getVotacao() {
    return votacao;
  }

  public void setVotacao(VotacaoDTO votacao) {
    this.votacao = votacao;
  }

  public int getNumerodeApoiantes() {
    return numerodeApoiantes;
  }

  public void setNumerodeApoiantes(int numerodeApoiantes) {
    this.numerodeApoiantes = numerodeApoiantes;
  }
}
