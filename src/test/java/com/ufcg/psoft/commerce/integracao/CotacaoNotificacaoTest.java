package com.ufcg.psoft.commerce.integracao;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.ufcg.psoft.commerce.dto.CotacaoUpsertDTO;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.enums.PlanoEnum;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.interesse.notificacao.NotificacaoService;
import java.math.BigDecimal;
import java.util.Objects;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class CotacaoNotificacaoTest {

  @Autowired AtivoRepository ativoRepository;
  @Autowired ClienteRepository clienteRepository;
  @Autowired InteresseRepository interesseRepository;
  @Autowired AtivoService ativoService;

  @MockBean NotificacaoService notificacaoService;

  @AfterEach
  void tearDown() {
    interesseRepository.deleteAll();
    ativoRepository.deleteAll();
    clienteRepository.deleteAll();
    reset(notificacaoService);
  }

  @Test
  @DisplayName("Cotação: +12% deve notificar")
  void variacaoAcimaDe10PorCentoSobe_notifica() {
    Cliente cliente = criarClienteNormal();

    var ativo = criarCriptoComCotacao("100.00");
    criarInteresseCotacao(cliente, ativo.getId());

    // +12%
    ativoService.atualizarCotacao(ativo.getId(), cotacao("112.00"));

    verify(notificacaoService, times(1))
        .notificar(
            argThat(c -> Objects.equals(c.getId(), cliente.getId())), contains("variou 12.00%"));
  }

  @Test
  @DisplayName("Cotação: -10% deve notificar")
  void variacaoMenosDezPorCentoCai_notifica() {
    Cliente cliente = criarClienteNormal();
    var ativo = criarCriptoComCotacao("100.00");
    criarInteresseCotacao(cliente, ativo.getId());

    // -10%
    ativoService.atualizarCotacao(ativo.getId(), cotacao("90.00"));

    verify(notificacaoService, times(1))
        .notificar(
            argThat(c -> Objects.equals(c.getId(), cliente.getId())), contains("variou 10.00%"));
  }

  @Test
  @DisplayName("Cotação: +9% NÃO deve notificar")
  void variacaoAbaixoDe10PorCento_naoNotifica() {
    Cliente cliente = criarClienteNormal();
    var ativo = criarCriptoComCotacao("100.00");
    criarInteresseCotacao(cliente, ativo.getId());

    // +9%
    ativoService.atualizarCotacao(ativo.getId(), cotacao("109.00"));

    verify(notificacaoService, never()).notificar(any(), anyString());
  }

  @Test
  @DisplayName("Cotação: exatamente +10% deve notificar")
  void variacaoExatamenteDezPorCento_notifica() {
    Cliente cliente = criarClienteNormal();
    var ativo = criarCriptoComCotacao("100.00");
    criarInteresseCotacao(cliente, ativo.getId());

    // +10%
    ativoService.atualizarCotacao(ativo.getId(), cotacao("110.00"));

    verify(notificacaoService, times(1))
        .notificar(
            argThat(c -> Objects.equals(c.getId(), cliente.getId())), contains("variou 10.00%"));
  }

  @Test
  @DisplayName("Cotação: com 2 interessados no mesmo ativo, ambos devem ser notificados (≥10%)")
  void doisInteressadosMesmaCotacao_ambosRecebemNotificacao() {
    var c1 = criarClienteNormal();
    var c2 = criarClienteNormal();
    var ativo = criarCriptoComCotacao("100.00");

    criarInteresseCotacao(c1, ativo.getId());
    criarInteresseCotacao(c2, ativo.getId());

    //  +12%
    ativoService.atualizarCotacao(ativo.getId(), cotacao("112.00"));

    verify(notificacaoService, times(2))
        .notificar(
            argThat(
                cli ->
                    cli != null
                        && (Objects.equals(cli.getId(), c1.getId())
                            || Objects.equals(cli.getId(), c2.getId()))),
            contains("variou 12.00%"));
  }

  @Test
  @DisplayName("Cotação: sem interessados não deve notificar")
  void semInteressados_naoNotifica() {
    var ativo = criarCriptoComCotacao("100.00");

    // +12% sem criar interesse
    ativoService.atualizarCotacao(ativo.getId(), cotacao("112.00"));

    verify(notificacaoService, never()).notificar(any(), anyString());
  }

  // ===== helpers =====

  private CotacaoUpsertDTO cotacao(String valor) {
    return CotacaoUpsertDTO.builder().cotacao(new BigDecimal(valor)).build();
  }

  private com.ufcg.psoft.commerce.model.Ativo criarCriptoComCotacao(String valor) {
    return ativoRepository.save(
        Cripto.builder()
            .nome("Doge")
            .descricao("Moeda")
            .cotacao(new BigDecimal(valor))
            .status(StatusAtivo.DISPONIVEL)
            .tipo(AtivoTipo.CRIPTO)
            .build());
  }

  private void criarInteresseCotacao(Cliente c, Long ativoId) {
    var ativo = ativoRepository.findById(ativoId).orElseThrow();
    interesseRepository.save(
        Interesse.builder().cliente(c).ativo(ativo).tipo(TipoInteresseEnum.COTACAO).build());
  }

  private Cliente criarClienteNormal() {
    return clienteRepository.save(
        Cliente.builder()
            .nome("Cliente Normal da Silva")
            .plano(PlanoEnum.NORMAL)
            .endereco("Rua dos Testes Normal, 456")
            .codigoAcesso("654321")
            .build());
  }
}
