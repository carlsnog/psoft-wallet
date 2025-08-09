package com.ufcg.psoft.commerce.service.interesse.listeners.preco;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.interesse.listeners.AtivoBaseEventListener;
import com.ufcg.psoft.commerce.service.interesse.notificacao.NotificacaoService;
import org.springframework.stereotype.Component;

@Component
public class AtivoCotacaoHandler extends AtivoBaseEventListener<AtivoCotacaoEvent> {

  public AtivoCotacaoHandler(
      InteresseRepository interesseRepository, NotificacaoService notificacaoService) {
    super(interesseRepository, notificacaoService);
  }

  @Override
  protected String formatarMensagem(AtivoCotacaoEvent event) {
    return "A cotação do ativo "
        + event.getAtivo().getNome()
        + " foi atualizado para "
        + event.getNovaCotacao()
        + "!";
  }

  @Override
  protected TipoInteresseEnum getTipoInteresse() {
    return TipoInteresseEnum.COTACAO;
  }
}
