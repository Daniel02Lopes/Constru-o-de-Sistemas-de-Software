package pt.ul.fc.css.democracia2.business.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;
import org.springframework.lang.NonNull;

@Entity
public class Delegado extends Cidadao {
  @JsonIgnore
  @OneToMany(mappedBy = "delegadoProponente")
  private List<ProjetoDeLei> projetosDeLeiPropostos;

  /**
   * Construtor da classe Delegado.
   *
   * @param nome o nome do delegado
   * @param surname o sobrenome do delegado
   * @param cc o número do cartão de cidadão do delegado
   */
  public Delegado(@NonNull String nome, @NonNull String surname, @NonNull Long cc) {
    super(nome, surname, cc);
  }

  /** Construtor vazio da classe Delegado. */
  public Delegado() {
    super();
  }

  public List<ProjetoDeLei> getProjetosDeLeiPropostos() {
    return projetosDeLeiPropostos;
  }
}
