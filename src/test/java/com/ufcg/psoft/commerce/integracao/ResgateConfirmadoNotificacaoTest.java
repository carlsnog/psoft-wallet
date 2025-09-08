package com.ufcg.psoft.commerce.integracao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ufcg.psoft.commerce.dto.CompraConfirmacaoDTO;
import com.ufcg.psoft.commerce.dto.CompraCreateDTO;
import com.ufcg.psoft.commerce.dto.ResgateConfirmacaoDTO;
import com.ufcg.psoft.commerce.dto.ResgateCreateDTO;
import com.ufcg.psoft.commerce.dto.ResgateResponseDTO;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.enums.CompraStatusEnum;
import com.ufcg.psoft.commerce.enums.PlanoEnum;
import com.ufcg.psoft.commerce.enums.ResgateStatusEnum;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Cripto;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.compra.Compra;
import com.ufcg.psoft.commerce.repository.*;
import com.ufcg.psoft.commerce.service.compra.CompraService;
import com.ufcg.psoft.commerce.service.resgate.ResgateService;
import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class ResgateConfirmacaoNotificacaoTest {

  @Autowired ClienteRepository clienteRepository;
  @Autowired AtivoRepository ativoRepository;
  @Autowired CompraRepository compraRepository;
  @Autowired ResgateRepository resgateRepository;
  @Autowired AtivoCarteiraRepository ativoCarteiraRepository;

  @Autowired CompraService compraService;
  @Autowired ResgateService resgateService;

  @AfterEach
  void tearDown() {
    ativoCarteiraRepository.deleteAll();
    resgateRepository.deleteAll();
    compraRepository.deleteAll();
    ativoRepository.deleteAll();
    clienteRepository.deleteAll();
  }

  @Test
  @DisplayName("Resgate: confirmar deve imprimir notificação")
  void confirmarResgate_imprimeNotificacao(CapturedOutput output) {
    Cliente cliente = criarCliente("Cliente Notificação");
    Usuario usuarioCliente = cliente;
    var ativo = criarCripto("Moedinha", "100.00");

    adicionarSaldoNaCarteiraViaCompra(usuarioCliente, ativo.getId(), 5);

    var criado = resgateService.criar(usuarioCliente, new ResgateCreateDTO(ativo.getId(), 3));

    // admin confirma o resgate
    Usuario admin = Admin.getInstance();
    var dto = new ResgateConfirmacaoDTO();
    dto.setStatusAtual(ResgateStatusEnum.SOLICITADO);

    resgateService.confirmar(admin, criado.getId(), dto);

    // listener imprimiu a notificação?
    assertThat(output.getOut())
        .contains("Notificação: O resgate do ativo") // fras inicial
        .contains("CONFIRMADO")
        .contains(cliente.getNome()) // nome do cliente
        .contains(ativo.getNome()); // nome do ativo
  }

  @Test
  @DisplayName("Resgate: statusAtual conflitante -> CONFLICT e não imprime")
  void confirmarResgate_statusConflitante_naoImprime(CapturedOutput output) {
    Cliente cliente = criarCliente("Cliente Conflito");
    Usuario usuarioCliente = cliente;
    var ativo = criarCripto("Doges", "50.00");

    adicionarSaldoNaCarteiraViaCompra(usuarioCliente, ativo.getId(), 2);

    ResgateResponseDTO criado =
        resgateService.criar(usuarioCliente, new ResgateCreateDTO(ativo.getId(), 1));

    Usuario admin = Admin.getInstance();
    var dto = new ResgateConfirmacaoDTO();
    dto.setStatusAtual(ResgateStatusEnum.CONFIRMADO); // errado de proposito

    assertThrows(
        CommerceException.class, () -> resgateService.confirmar(admin, criado.getId(), dto));

    assertThat(output.getOut()).doesNotContain("Notificação");
  }

  @Test
  @DisplayName(
      "Notificação: 2 resgates confirmados (clientes distintos) -> 2 prints, nomes corretos")
  void doisClientes_doisResgates_duasNotificacoes(CapturedOutput output) {
    // cliente 1
    Cliente c1 = criarCliente("Cliente A");
    var ativo = criarCripto("ETH-Test", "10.00");
    adicionarSaldoNaCarteiraViaCompra(c1, ativo.getId(), 4);
    var r1 = resgateService.criar(c1, new ResgateCreateDTO(ativo.getId(), 2));

    // cliente 2
    Cliente c2 = criarCliente("Cliente B");
    adicionarSaldoNaCarteiraViaCompra(c2, ativo.getId(), 3);
    var r2 = resgateService.criar(c2, new ResgateCreateDTO(ativo.getId(), 1));

    var dto = new ResgateConfirmacaoDTO();
    dto.setStatusAtual(ResgateStatusEnum.SOLICITADO);

    resgateService.confirmar(Admin.getInstance(), r1.getId(), dto);

    var dto2 = new ResgateConfirmacaoDTO();
    dto2.setStatusAtual(ResgateStatusEnum.SOLICITADO);
    resgateService.confirmar(Admin.getInstance(), r2.getId(), dto2);

    String out = output.getOut();
    long prints = out.lines().filter(l -> l.contains("Notificação: O resgate do ativo")).count();
    assertThat(prints).isEqualTo(2L);
    assertThat(out).contains("Cliente A").contains("Cliente B"); // nomes corretos
    assertThat(out).contains(ativo.getNome()); // mesmo ativo nos dois prints
  }

  @Test
  @DisplayName("Isolamento: confirmar compra liberada não imprime notificação de resgate")
  void compraLiberada_naoImprimeNotificacaoDeResgate(CapturedOutput output) {

    Cliente c = criarCliente("Cliente Compra");
    var ativo = criarCripto("COMPRA", "12.00");

    // Saldo via compra (vai imprimir coisas da compra? ok, mas não a mensagem de RESGATE)
    adicionarSaldoNaCarteiraViaCompra(c, ativo.getId(), 1);

    // assert: não houve notificação de resgate
    assertThat(output.getOut()).doesNotContain("Notificação: O resgate do ativo");
  }

  // ============ helpers ============

  private Cliente criarCliente(String nome) {
    return clienteRepository.save(
        Cliente.builder()
            .nome(nome)
            .plano(PlanoEnum.PREMIUM)
            .endereco("Rua dos Testes, 123")
            .codigoAcesso("abc123")
            .build());
  }

  private com.ufcg.psoft.commerce.model.Ativo criarCripto(String nome, String cotacao) {
    return ativoRepository.save(
        Cripto.builder()
            .nome(nome)
            .descricao("Cripto " + nome)
            .cotacao(new BigDecimal(cotacao))
            .status(StatusAtivo.DISPONIVEL)
            .tipo(AtivoTipo.CRIPTO)
            .build());
  }

  /**
   * Adiciona saldo na carteira seguindo o fluxo público: 1) cria compra (cliente) 2) marca como
   * DISPONIVEL no repositório (simula liberação do admin) 3) confirma compra (cliente) -> método
   * privado adiciona na carteira
   */
  private void adicionarSaldoNaCarteiraViaCompra(Usuario cliente, Long ativoId, int quantidade) {
    var compraDTO = new CompraCreateDTO(ativoId, quantidade);
    var compraResp = compraService.criar(cliente, compraDTO);

    Compra compra = compraRepository.findById(compraResp.getId()).orElseThrow();
    compra.setStatus(CompraStatusEnum.DISPONIVEL);
    compraRepository.save(compra);

    var confirmDTO = new CompraConfirmacaoDTO();
    confirmDTO.setStatusAtual(CompraStatusEnum.DISPONIVEL);
    compraService.confirmar(cliente, compra.getId(), confirmDTO);
  }
}
