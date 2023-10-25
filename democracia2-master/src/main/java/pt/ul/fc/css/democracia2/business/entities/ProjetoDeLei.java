package pt.ul.fc.css.democracia2.business.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.NonNull;

/** Esta classe representa um Projeto de Lei */
@Entity
public class ProjetoDeLei {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @NonNull private String titulo;

  @NonNull private String textoDescriptivo;

  @Lob private byte[] anexo;

  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime dataValidade;

  @ManyToOne
  @JoinColumn(name = "tema_id", nullable = false)
  private Tema tema;

  @ManyToOne
  @JoinColumn(name = "delegado_id", nullable = false)
  private Delegado delegadoProponente;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "projeto_de_lei_cidadao",
    joinColumns = @JoinColumn(name = "projeto_de_lei_id"),
    inverseJoinColumns = @JoinColumn(name = "cidadao_id")
  )
  private List<Cidadao> listaCidadaosApoiantes;

  @Enumerated(EnumType.STRING)
  private EstadoProjetoLei estadoProjetoLei;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "votacao_id")
  private Votacao votacao;

  private int numerodeApoiantes;

  /** Constructor needed by JPA. */
  public ProjetoDeLei() {}

  /**
   * Construtor que cria um novo objeto ProjetoDeLei com o título, descrição, anexo, data de
   * validade, tema e delegado proponente fornecidos. Define o estado inicial como ABERTO, o número
   * de apoiantes como 1 (que é o delegado proponente) e limita a data de validade a um ano após a
   * data atual.
   *
   * @param titulo título do projeto de lei
   * @param textoDescriptivo descrição do projeto de lei
   * @param anexo anexo PDF
   * @param dataValidade data de validade do projeto de lei
   * @param tema tema do projeto de lei
   * @param delegadoProponente delegado proponente do projeto de lei
   */
  public ProjetoDeLei(
      String titulo,
      String textoDescriptivo,
      byte[] anexo,
      LocalDateTime dataValidade,
      Tema tema,
      Delegado delegadoProponente) {
    this.titulo = titulo;
    this.textoDescriptivo = textoDescriptivo;
    this.anexo = anexo;
    this.tema = tema;
    this.delegadoProponente = delegadoProponente;
    this.listaCidadaosApoiantes = new ArrayList<>();
    this.listaCidadaosApoiantes.add(delegadoProponente);
    this.estadoProjetoLei = EstadoProjetoLei.ABERTO;
    this.numerodeApoiantes = 1;

    // Assume-se que quando a data máxima é ultrapassada, coloca-se o valor na data máxima permitida
    // (1 Ano)
    LocalDateTime dataMaximaValidade = LocalDateTime.now().plusYears(1);
    if (dataValidade.isAfter(dataMaximaValidade)) {
      this.dataValidade = dataMaximaValidade;
    } else {
      this.dataValidade = dataValidade;
    }
  }

  /**
   * Retorna o ID do projeto de lei.
   *
   * @return o ID do projeto de lei
   */
  public Long getId() {
    return id;
  }

  /**
   * Retorna o número de cidadãos que apoiam este projeto de lei.
   *
   * @return o número de apoiantes do projeto de lei
   */
  public int getNumerodeApoiantes() {
    return numerodeApoiantes;
  }

  /**
   * Define o número de cidadãos que apoiam este projeto de lei.
   *
   * @param numerodeApoiantes o número de apoiantes do projeto de lei
   */
  public void setNumerodeApoiantes(int numerodeApoiantes) {
    this.numerodeApoiantes = numerodeApoiantes;
  }

  /** Incrementa o número de cidadãos que apoiam este projeto de lei em 1. */
  public void incrementaNumerodeApoiantes() {
    this.numerodeApoiantes += 1;
  }

  /**
   * Retorna o título do projeto de lei.
   *
   * @return o título do projeto de lei
   */
  public String getTitulo() {
    return titulo;
  }

  /**
   * Retorna a data de validade do projeto de lei.
   *
   * @return a data de validade do projeto de lei
   */
  public LocalDateTime getDataValidade() {
    return this.dataValidade;
  }

  /**
   * Retorna o tema do projeto de lei.
   *
   * @return o tema do projeto de lei
   */
  public Tema getTema() {
    return tema;
  }

  /**
   * Retorna o delegado proponente do projeto de lei.
   *
   * @return o delegado proponente do projeto de lei
   */
  public Delegado getDelegadoProponente() {
    return delegadoProponente;
  }

  /**
   * Retorna a lista de cidadãos apoiantes do projeto de lei.
   *
   * @return a lista de cidadãos apoiantes do projeto de lei
   */
  public List<Cidadao> getApoiantes() {
    return listaCidadaosApoiantes;
  }

  /**
   * Define a votação do projeto de lei.
   *
   * @param votacao a votação do projeto de lei
   */
  public void setVotacao(Votacao votacao) {
    this.votacao = votacao;
  }

  /**
   * Retorna o estado atual do projeto de lei.
   *
   * @return o estado atual do projeto de lei
   */
  public EstadoProjetoLei getEstadoProjetoLei() {
    return this.estadoProjetoLei;
  }

  /**
   * Define o estado atual do projeto de lei.
   *
   * @param estadoProjetoLei o estado atual do projeto de lei
   */
  public void setEstadoProjetoLei(EstadoProjetoLei estadoProjetoLei) {
    this.estadoProjetoLei = estadoProjetoLei;
  }

  /**
   * Retorna a votação do projeto de lei.
   *
   * @return a votação do projeto de lei
   */
  public Votacao getVotacao() {
    return this.votacao;
  }

  @NonNull
  public String getTextoDescriptivo() {
    return textoDescriptivo;
  }

  /**
   * Retorna uma string que representa o projeto de lei.
   *
   * @return uma string que representa o projeto de lei
   */
  @Override
  public String toString() {
    return "ProjetoDeLei{"
        + "titulo='"
        + titulo
        + '\''
        + ", textoDescriptivo='"
        + textoDescriptivo
        + '\''
        + ", anexo="
        // + Arrays.toString(anexo)
        + ", dataValidade="
        + dataValidade
        + ", tema="
        + tema
        + ", delegadoProponente="
        + delegadoProponente
        + ", listaCidadaosApoiantes="
        + listaCidadaosApoiantes
        + ", estadoProjetoLei="
        + estadoProjetoLei
        + ", votacao="
        + votacao
        + ", numerodeApoiantes="
        + numerodeApoiantes
        + '}';
  }

  /**
   * Define a data de validade do projeto de lei.
   *
   * @param localDateTime a data de validade do projeto de lei
   */
  public void setDataValidade(LocalDateTime localDateTime) {
    this.dataValidade = localDateTime;
  }
}
