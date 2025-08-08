package com.ufcg.psoft.commerce.service.interesse.validadores;

import com.ufcg.psoft.commerce.enums.*;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.Ativo;
import org.springframework.stereotype.Component;

@Component
public class InteresseDisponibilidadeTesouroValid implements InteresseEspecifico {

  @Override
  public void validar(Usuario usuario, Ativo ativo, TipoInteresseEnum interesse) {
    Cliente cliente = (Cliente) usuario;
    if (cliente.getPlano() != PlanoEnum.PREMIUM && ativo.getTipo() != AtivoTipo.TESOURO) {
      throw new CommerceException(ErrorCode.INTERESSE_DISPONIBILIDADE_DIF_TESOURO);
    }
  }
}
