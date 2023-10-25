package pt.ul.fc.css.democracia2.application;

import jakarta.persistence.*;
import java.util.Map;
import pt.ul.fc.css.democracia2.business.entities.Delegado;
import pt.ul.fc.css.democracia2.business.entities.Tema;

public class CidadaoDTO {
  private Long id;

  /** O nome do cidadao. */
  protected String name;

  /** O apelido do cidadao. */
  protected String surname;

  protected Long cc;

  protected Map<Tema, Delegado> delegados_escolhidos;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public Long getCc() {
    return cc;
  }

  public void setCc(Long cc) {
    this.cc = cc;
  }

  public Map<Tema, Delegado> getDelegados_escolhidos() {
    return delegados_escolhidos;
  }

  public void setDelegados_escolhidos(Map<Tema, Delegado> delegados_escolhidos) {
    this.delegados_escolhidos = delegados_escolhidos;
  }
}
