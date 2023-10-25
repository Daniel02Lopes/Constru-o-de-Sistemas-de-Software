package pt.ul.fc.css.democracia2.business.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.lang.NonNull;

/** Representa um cidadao no Democracia2. */
@Entity
@Table(name = "cidadao")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cidadao {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  /** O nome do cidadao. */
  @NonNull protected String name;

  /** O apelido do cidadao. */
  @NonNull protected String surname;

  /** O número do cartao de cidadao do cidadao. */
  @Column(unique = true)
  @NonNull
  protected Long cc;

  /** Os delegados escolhidos pelo cidadao, mapeados por tema. */
  @JsonIgnore
  @ManyToMany
  @JoinTable(
    name = "delegados_escolhidos",
    joinColumns = @JoinColumn(name = "cidadao_id"),
    inverseJoinColumns = @JoinColumn(name = "delegado_id")
  )
  @MapKeyJoinColumn(name = "tema_id")
  protected Map<Tema, Delegado> delegados_escolhidos;

  /** A lista de projetos de lei apoiados pelo cidadao. */
  @ManyToMany(mappedBy = "listaCidadaosApoiantes")
  protected List<ProjetoDeLei> projetosApoiados;

  /**
   * Cria um novo cidadao com o nome, apelido e número do cartao de cidadao especificados.
   *
   * @param name O nome do cidadao.
   * @param surname O apelido do cidadao.
   * @param cc O número do cartao de cidadao do cidadao.
   */
  public Cidadao(@NonNull String name, @NonNull String surname, @NonNull Long cc) {
    setName(name);
    setSurname(surname);
    setCc(cc);
    this.delegados_escolhidos = new HashMap<Tema, Delegado>();
  }

  /** Cria um novo cidadao sem informacoes iniciais. */
  public Cidadao() {}

  /**
   * Obtém os delegados escolhidos pelo cidadao, mapeados por tema.
   *
   * @return Os delegados escolhidos pelo cidadao, mapeados por tema.
   */
  public Map<Tema, Delegado> getDelegados_escolhidos() {
    return this.delegados_escolhidos;
  }

  /**
   * Define os delegados escolhidos pelo cidadao, mapeados por tema.
   *
   * @param delegados_escolhidos Os delegados escolhidos pelo cidadao, mapeados por tema.
   */
  public void setDelegados_escolhidos(HashMap<Tema, Delegado> delegados_escolhidos) {
    this.delegados_escolhidos = delegados_escolhidos;
  }

  /**
   * Obtem o numero do cartao de cidadao do cidadao.
   *
   * @return O número do cartao de cidadao do cidadao.
   */
  public Long getCc() {
    return cc;
  }

  /**
   * Obtem o nome do cidadao
   *
   * @return nome do cidadao
   */
  public String getName() {
    return name;
  }

  /**
   * Obtem o sobrenome do cidadao
   *
   * @return sobrenome do cidadao
   */
  public String getSurname() {
    return surname;
  }

  /**
   * Altera o sobrenome do cidadao
   *
   * @param surname sobrenome do cidadao
   */
  public void setSurname(@NonNull String surname) {
    this.surname = surname;
  }

  /**
   * Altera o nome do ciddao
   *
   * @param name nome do cidadao
   */
  public void setName(@NonNull String name) {
    this.name = name;
  }

  /**
   * Atribui um numero de cartao de cidadao ao cidadao
   *
   * @param cc cartao de cidadao
   */
  public void setCc(@NonNull Long cc) {
    this.cc = cc;
  }

  /**
   * Obtem o id gerado pela a base de dados do cidadao
   *
   * @return id do cidadao
   */
  public Long getId() {
    return id;
  }

  /**
   * Funcao equals
   *
   * @param o obejcto a comparar
   * @return caso o objecto o for igual ao cidadao entao retorna true caso contrario retorna false
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Cidadao cidadao = (Cidadao) o;
    return Objects.equals(id, cidadao.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Cidadao{"
        + "name='"
        + name
        + '\''
        + ", cc="
        + cc
        + ", delegados_escolhidos="
        + delegados_escolhidos
        + '}';
  }
}
