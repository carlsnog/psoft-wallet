package com.ufcg.psoft.commerce.service.resgate.listeners;

import com.ufcg.psoft.commerce.service.notificacao.NotificacaoService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ResgateConfirmadoEventHandler {
  private final NotificacaoService notificacaoService;

  public ResgateConfirmadoEventHandler(NotificacaoService notificacaoService) {
    this.notificacaoService = notificacaoService;
  }

  @EventListener
  public void handleResgateConfirmado(ResgateConfirmadoEvent event) {
    var resgate = event.getResgate();
    var cliente = resgate.getCliente();
    String mensagem =
        String.format(
            "Notificação: O resgate do ativo %s solicitado pelo cliente %s foi CONFIRMADO.%n",
            resgate.getAtivo().getNome(), cliente.getNome());

    notificacaoService.notificar(cliente, mensagem);
  }
}
