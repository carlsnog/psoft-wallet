package com.ufcg.psoft.commerce.service.compra.listeners.liberada;

// CompraDisponivelHandler.java
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.service.notificacao.NotificacaoService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CompraLiberadaHandler implements ApplicationListener<CompraLiberadaEvent> {

  private final NotificacaoService notificacaoService;

  public CompraLiberadaHandler(NotificacaoService notificacaoService) {
    this.notificacaoService = notificacaoService;
  }

  @Override
  public void onApplicationEvent(CompraLiberadaEvent event) {
    Compra compra = event.getCompra();
    Cliente cliente = compra.getCliente();

    // Formata a mensagem — inclui motivo e identificação do destinatário implícita pelo cliente
    String mensagem =
        String.format(
            "Sua solicitação de compra (id=%d) para o ativo '%s' (id=%d) — quantidade=%d — foi liberada e está DISPONÍVEL.",
            compra.getId(),
            compra.getAtivo().getNome(),
            compra.getAtivo().getId(),
            compra.getQuantidade());

    // Chama o serviço de notificação (que já imprime/loga a notificação em JSON)
    notificacaoService.notificar(cliente, mensagem);
  }
}
