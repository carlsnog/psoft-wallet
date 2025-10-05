package com.ufcg.psoft.commerce.model.transacao.resgate.state;

import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.transacao.TransacaoState;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;
import com.ufcg.psoft.commerce.model.transacao.resgate.ResgateStatusEnum;

public abstract class ResgateState implements TransacaoState {

  private final Resgate resgate;

  public ResgateState(Resgate resgate) {
    this.resgate = resgate;
  }

  @Override
  public abstract void confirmar(Usuario usuario);

  @Override
  public boolean deveFinalizar() {
    return false;
  }

  protected void preValidarAdmin(Usuario usuario) {
    if (!usuario.isAdmin()) {
      throw new CommerceException(ErrorCode.ACAO_APENAS_ADMIN);
    }
  }

  protected void preValidarCliente(Usuario usuario) {
    if (usuario.isAdmin()) {
      throw new CommerceException(ErrorCode.ACAO_APENAS_CLIENTE_DONO);
    }

    var cliente = (Cliente) usuario;
    if (!this.resgate.getClienteId().equals(cliente.getId())) {
      throw new CommerceException(ErrorCode.ACAO_APENAS_CLIENTE_DONO);
    }
  }

  protected void preValidarSaldo() {
    var saldoDisponivel = getCliente().getSaldo(getAtivo().getId());
    var quantidadeSolicitada = this.resgate.getQuantidade();

    if (saldoDisponivel < quantidadeSolicitada) {
      throw new CommerceException(ErrorCode.SALDO_INSUFICIENTE);
    }
  }

  protected void setStatus(ResgateStatusEnum status) {
    this.resgate.setStatus(status);
  }

  protected Ativo getAtivo() {
    return this.resgate.getAtivo();
  }

  protected Cliente getCliente() {
    return this.resgate.getCliente();
  }
}
