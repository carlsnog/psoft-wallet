package com.ufcg.psoft.commerce.service.interesse.listeners;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Interesse;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.interesse.notificacao.NotificacaoService;
import java.util.List;
import org.springframework.context.ApplicationListener;

public abstract class AtivoBaseEventListener<TNotificacao extends AtivoBaseEvent>
    implements ApplicationListener<TNotificacao> {

  protected final InteresseRepository interesseRepository;
  protected final NotificacaoService notificacaoService;

  public AtivoBaseEventListener(
      InteresseRepository interesseRepository, NotificacaoService notificacaoService) {
    this.interesseRepository = interesseRepository;
    this.notificacaoService = notificacaoService;
  }

  @Override
  public void onApplicationEvent(TNotificacao event) {
    if (!deveNotificar(event)) return;

    Ativo ativo = event.getAtivo();
    List<Interesse> interesses = buscarInteresses(ativo);

    for (Interesse interesse : interesses) {
      notificacaoService.notificar(interesse.getCliente(), formatarMensagem(event));
    }

    depoisDeNotificar(interesses);
  }

  private List<Interesse> buscarInteresses(Ativo ativo) {
    return interesseRepository.findByTipoAndAtivo_Id(getTipoInteresse(), ativo.getId());
  }

  protected abstract boolean deveNotificar(TNotificacao event);

  protected abstract String formatarMensagem(TNotificacao event);

  protected abstract TipoInteresseEnum getTipoInteresse();

  protected void depoisDeNotificar(List<Interesse> interesses) {}
}
