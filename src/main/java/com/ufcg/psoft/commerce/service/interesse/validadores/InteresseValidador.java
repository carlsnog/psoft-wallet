package com.ufcg.psoft.commerce.service.interesse.validadores;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Usuario;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InteresseValidador {
  private final List<InteresseEspecifico> validacoes;

  public InteresseValidador(List<InteresseEspecifico> validacoes) {
    this.validacoes = validacoes;
  }

  public void validar(Usuario usuario, Ativo ativo, TipoInteresseEnum interesse) {
    if (!usuario.isAdmin()) {
      for (InteresseEspecifico valid : validacoes) {
        valid.validar(usuario, ativo, interesse);
      }
    }
  }
}
