package com.ufcg.psoft.commerce.integracao;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.ufcg.psoft.commerce.dto.CotacaoUpsertDTO;
import com.ufcg.psoft.commerce.dto.InteresseCreateDTO;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.enums.PlanoEnum;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.interesse.InteresseService;
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
  @Autowired InteresseService interesseService;

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
    Cliente cliente = criarClientePremium();
    Usuario usuario = cliente;
    var ativo = criarCriptoComCotacao("100.00");

    criarInteresseCotacao(usuario, cliente.getId(), ativo.getId());

    // +12%
    ativoService.atualizarCotacao(ativo.getId(), cotacao("112.00"));

    verify(notificacaoService, times(1))
        .notificar(
            argThat(c -> Objects.equals(c.getId(), cliente.getId())), contains("variou 12.00%"));
  }

  @Test
  @DisplayName("Cotação: -10% deve notificar")
  void variacaoMenosDezPorCentoCai_notifica() {
    Cliente cliente = criarClientePremium();
    Usuario usuario = cliente;
    var ativo = criarCriptoComCotacao("100.00");

    criarInteresseCotacao(usuario, cliente.getId(), ativo.getId());

    // -10%
    ativoService.atualizarCotacao(ativo.getId(), cotacao("90.00"));

    verify(notificacaoService, times(1))
        .notificar(
            argThat(c -> Objects.equals(c.getId(), cliente.getId())), contains("variou 10.00%"));
  }

  @Test
  @DisplayName("Cotação: +9% NÃO deve notificar")
  void variacaoAbaixoDe10PorCento_naoNotifica() {
    Cliente cliente = criarClientePremium();
    Usuario usuario = cliente;

    var ativo = criarCriptoComCotacao("100.00");
    criarInteresseCotacao(usuario, cliente.getId(), ativo.getId());

    // +9%
    ativoService.atualizarCotacao(ativo.getId(), cotacao("109.00"));

    verify(notificacaoService, never()).notificar(any(), anyString());
  }

  @Test
  @DisplayName("Cotação: exatamente +10% deve notificar")
  void variacaoExatamenteDezPorCento_notifica() {
    Cliente cliente = criarClientePremium();
    Usuario usuario = cliente;
    var ativo = criarCriptoComCotacao("100.00");
    criarInteresseCotacao(usuario, cliente.getId(), ativo.getId());

    // +10%
    ativoService.atualizarCotacao(ativo.getId(), cotacao("110.00"));

    verify(notificacaoService, times(1))
        .notificar(
            argThat(c -> Objects.equals(c.getId(), cliente.getId())), contains("variou 10.00%"));
  }

  @Test
  @DisplayName("Cotação: com 2 interessados no mesmo ativo, ambos devem ser notificados (≥10%)")
  void doisInteressadosMesmaCotacao_ambosRecebemNotificacao() {
    var c1 = criarClientePremium();
    var c2 = criarClientePremium();
    Usuario u1 = c1;
    Usuario u2 = c2;

    var ativo = criarCriptoComCotacao("100.00");

    criarInteresseCotacao(u1, c1.getId(), ativo.getId());
    criarInteresseCotacao(u2, c2.getId(), ativo.getId());

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
            .nome("Doges")
            .descricao("Moeda")
            .cotacao(new BigDecimal(valor))
            .status(StatusAtivo.DISPONIVEL)
            .tipo(AtivoTipo.CRIPTO)
            .build());
  }

  private void criarInteresseCotacao(Usuario usuario, Long clienteId, Long ativoId) {
    interesseService.criarInteresseCotacao(usuario, new InteresseCreateDTO(clienteId, ativoId));
  }

  private Cliente criarClientePremium() {
    return clienteRepository.save(
        Cliente.builder()
            .nome("Cliente Premium da Silva")
            .plano(PlanoEnum.PREMIUM)
            .endereco("Rua dos TestesPremium, 456")
            .codigoAcesso("654321")
            .build());
  }
}
