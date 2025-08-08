package com.ufcg.psoft.commerce.service.interesse.validadores;

import com.ufcg.psoft.commerce.enums.*;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;

public class IntessePrecoValid implements InteresseEspecifico {
  @Override
  public void validar(Usuario usuario, Ativo ativo, TipoInteresseEnum interesse) {
    Cliente cliente = (Cliente) usuario;
    if (interesse == TipoInteresseEnum.PRECO && cliente.getPlano() != PlanoEnum.PREMIUM) {
      throw new CommerceException(ErrorCode.INTERESSE_PRECO_RESTRITO);
    }
  }
}
