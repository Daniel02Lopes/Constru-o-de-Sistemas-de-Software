package pt.ul.fc.css.democracia2.application;

import java.util.List;
import pt.ul.fc.css.democracia2.business.entities.ProjetoDeLei;

public class DelegadoDTO extends CidadaoDTO {
  private List<ProjetoDeLei> projetosDeLeiPropostos;

  public List<ProjetoDeLei> getProjetosDeLeiPropostos() {
    return projetosDeLeiPropostos;
  }

  public void setProjetosDeLeiPropostos(List<ProjetoDeLei> projetosDeLeiPropostos) {
    this.projetosDeLeiPropostos = projetosDeLeiPropostos;
  }
}
