package com.ufcg.psoft.commerce.service.interesse.validadores;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Usuario;

public interface InteresseEspecifico {
  void validar(Usuario usuario, Ativo ativo, TipoInteresseEnum interesse);
}
