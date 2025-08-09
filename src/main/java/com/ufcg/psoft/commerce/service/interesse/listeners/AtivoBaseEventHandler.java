package com.ufcg.psoft.commerce.service.interesse.listeners;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Interesse;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.interesse.notificacao.NotificacaoService;
import java.util.List;
import org.springframework.context.event.EventListener;

public abstract class AtivoBaseEventHandler<TNotificacao extends AtivoBaseEvent> {

  private final InteresseRepository interesseRepository;
  private final NotificacaoService notificacaoService;

  public AtivoBaseEventHandler(
      InteresseRepository interesseRepository, NotificacaoService notificacaoService) {
    this.interesseRepository = interesseRepository;
    this.notificacaoService = notificacaoService;
  }

  @EventListener
  public void handle(TNotificacao event) {
    Ativo ativo = event.getAtivo();
    List<Interesse> interesses = buscarInteresses(ativo);

    for (Interesse interesse : interesses) {
      notificacaoService.notificar(interesse.getCliente(), formatarMensagem(event));
    }

    interesseRepository.deleteAll(interesses);
  }

  private List<Interesse> buscarInteresses(Ativo ativo) {
    return interesseRepository.findByTipoAndAtivo_Id(getTipoInteresse(), ativo.getId());
  }

  protected abstract String formatarMensagem(TNotificacao event);

  protected abstract TipoInteresseEnum getTipoInteresse();
}
