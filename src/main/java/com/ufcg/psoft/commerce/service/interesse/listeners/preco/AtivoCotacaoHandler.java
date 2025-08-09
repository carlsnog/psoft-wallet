package com.ufcg.psoft.commerce.service.interesse.listeners.preco;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.interesse.listeners.AtivoBaseEventListener;
import com.ufcg.psoft.commerce.service.interesse.notificacao.NotificacaoService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class AtivoCotacaoHandler extends AtivoBaseEventListener<AtivoCotacaoEvent> {
  private static final BigDecimal LIMIAR = new BigDecimal("0.10"); // 10%

  public AtivoCotacaoHandler(
      InteresseRepository interesseRepository, NotificacaoService notificacaoService) {
    super(interesseRepository, notificacaoService);
  }

  @Override
  protected boolean deveNotificar(AtivoCotacaoEvent e) {
    var antiga = e.getCotacaoAntiga();
    var nova = e.getNovaCotacao();
    if (antiga == null || nova == null || antiga.signum() == 0) return false;

    // |nova - antiga| / antiga >= 0.10
    var fracao = nova.subtract(antiga).abs().divide(antiga, 6, RoundingMode.HALF_UP);
    return fracao.compareTo(LIMIAR) >= 0;
  }

  @Override
  protected String formatarMensagem(AtivoCotacaoEvent e) {
    var antiga = e.getCotacaoAntiga();
    var nova = e.getNovaCotacao();
    var pct =
        nova.subtract(antiga)
            .abs()
            .divide(antiga, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    return "a cotação do ativo "
        + e.getAtivo().getNome()
        + " variou "
        + pct.setScale(2, RoundingMode.HALF_UP)
        + "% (de "
        + antiga
        + " para "
        + nova
        + ").";
  }

  @Override
  protected TipoInteresseEnum getTipoInteresse() {
    return TipoInteresseEnum.COTACAO;
  }
}
