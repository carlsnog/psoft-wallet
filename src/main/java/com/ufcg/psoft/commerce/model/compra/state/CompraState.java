package com.ufcg.psoft.commerce.model.compra.state;

import com.ufcg.psoft.commerce.enums.CompraStatusEnum;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.compra.Compra;

public abstract class CompraState {

  private Compra compra;

  public CompraState(Compra compra) {
    this.compra = compra;
  }

  public abstract void confirmar(Usuario usuario);

  public boolean isFinal() {
    return false;
  }

  protected void preValidarAtivo() {
    if (getAtivo().getStatus() != StatusAtivo.DISPONIVEL) {
      throw new CommerceException(ErrorCode.ATIVO_NAO_DISPONIVEL);
    }
  }

  protected void preValidarAdmin(Usuario usuario) {
    if (!usuario.isAdmin()) {
      throw new CommerceException(ErrorCode.ACAO_APENAS_ADMIN);
    }
  }

  protected void preValidarCliente(Usuario usuario) {
    if (usuario.isAdmin()) {
      throw new CommerceException(ErrorCode.ACAO_APENAS_CLIENTE_DONO_COMPRA);
    }

    var cliente = (Cliente) usuario;
    if (getCliente().getId() != cliente.getId()) {
      throw new CommerceException(ErrorCode.ACAO_APENAS_CLIENTE_DONO_COMPRA);
    }
  }

  protected void setStatus(CompraStatusEnum status) {
    this.compra.setStatus(status);
    this.compra.setState(CompraStateFactory.getState(this.compra));
  }

  protected Ativo getAtivo() {
    return this.compra.getAtivo();
  }

  protected Cliente getCliente() {
    return this.compra.getCliente();
  }
}
