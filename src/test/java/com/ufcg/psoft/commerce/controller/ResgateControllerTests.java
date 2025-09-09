package com.ufcg.psoft.commerce.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.ResgateConfirmacaoDTO;
import com.ufcg.psoft.commerce.dto.ResgateCreateDTO;
import com.ufcg.psoft.commerce.dto.ResgateResponseDTO;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.http.exception.ErrorDTO;
import com.ufcg.psoft.commerce.model.Acao;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.AtivoCarteira;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;
import com.ufcg.psoft.commerce.model.transacao.resgate.ResgateStatusEnum;
import com.ufcg.psoft.commerce.repository.AtivoCarteiraRepository;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.repository.ResgateRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.utils.CustomDriver;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Resgates")
@ActiveProfiles("test")
public class ResgateControllerTests {

  final String URI_RESGATES = "/resgates";

  @Autowired MockMvc mvcDriver;
  @Autowired ClienteRepository clienteRepository;
  @Autowired ResgateRepository resgateRepository;
  @Autowired AtivoCarteiraRepository ativoCarteiraRepository;
  @Autowired CompraRepository compraRepository;
  @Autowired AtivoRepository ativoRepository;
  @Autowired AtivoService ativoService;

  ObjectMapper objectMapper = new ObjectMapper();
  CustomDriver driver;

  @BeforeEach
  void setup() {
    objectMapper.registerModule(new JavaTimeModule());
    driver = new CustomDriver(mvcDriver, objectMapper);
    resgateRepository.deleteAll();
    ativoCarteiraRepository.deleteAll();
    compraRepository.deleteAll();
  }

  // Métodos auxiliares para criar entidades de teste
  private Cliente criarCliente(String nome, String codigoAcesso) {
    return clienteRepository.save(
        Cliente.builder()
            .nome(nome)
            .codigoAcesso(codigoAcesso)
            .endereco("Endereço de " + nome)
            .plano(com.ufcg.psoft.commerce.enums.PlanoEnum.PREMIUM)
            .build());
  }

  private Ativo criarAtivo(String nome, String descricao, BigDecimal cotacao) {
    return ativoRepository.save(
        Acao.builder()
            .nome(nome + "_TEST_" + System.currentTimeMillis())
            .descricao(descricao)
            .cotacao(cotacao)
            .status(com.ufcg.psoft.commerce.enums.StatusAtivo.DISPONIVEL)
            .tipo(com.ufcg.psoft.commerce.enums.AtivoTipo.ACAO)
            .build());
  }

  private Compra criarCompra(Cliente cliente, Ativo ativo, int quantidade) {
    return compraRepository.save(
        Compra.builder()
            .cliente(cliente)
            .ativo(ativo)
            .quantidade(quantidade)
            .valorUnitario(ativo.getCotacao())
            .abertaEm(LocalDateTime.now())
            .status(com.ufcg.psoft.commerce.model.transacao.compra.CompraStatusEnum.EM_CARTEIRA)
            .finalizadaEm(LocalDateTime.now())
            .build());
  }

  private AtivoCarteira criarAtivoCarteira(Cliente cliente, Ativo ativo, int quantidade) {
    Compra compra = criarCompra(cliente, ativo, quantidade);
    return ativoCarteiraRepository.save(
        AtivoCarteira.builder()
            .cliente(cliente)
            .ativo(ativo)
            .quantidade(quantidade)
            .compra(compra)
            .build());
  }

  private void criarMultiplasLinhasAtivoCarteira(Cliente cliente, Ativo ativo, int... quantidades) {
    for (int quantidade : quantidades) {
      criarAtivoCarteira(cliente, ativo, quantidade);
    }
  }

  private Resgate criarResgate(Cliente cliente, Ativo ativo, ResgateStatusEnum status) {
    return resgateRepository.save(
        Resgate.builder()
            .cliente(cliente)
            .ativo(ativo)
            .quantidade(1)
            .valorUnitario(ativo.getCotacao())
            .abertaEm(LocalDateTime.now())
            .status(status)
            .build());
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de confirmação de resgate")
  class ResgateConfirmacaoVerificacao {

    // Cenário 1: Admin confirma resgate que está `SOLICITADO`
    @Test
    @DisplayName("Quando admin confirma resgate que está SOLICITADO")
    void quandoAdminConfirmaResgateSolicitado() throws Exception {
      // Arrange
      Cliente cliente = criarCliente("João", "123456");
      Ativo ativo = criarAtivo("PETR4", "Petrobras", BigDecimal.valueOf(30.0));
      criarAtivoCarteira(cliente, ativo, 10); // Criar saldo na carteira
      Resgate resgate = criarResgate(cliente, ativo, ResgateStatusEnum.SOLICITADO);

      ResgateConfirmacaoDTO confirmacaoDto = new ResgateConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(ResgateStatusEnum.SOLICITADO);

      // Act
      String responseJsonString =
          driver
              .post(
                  URI_RESGATES + "/" + resgate.getId() + "/confirmar",
                  confirmacaoDto,
                  Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resultado =
          objectMapper.readValue(responseJsonString, ResgateResponseDTO.class);

      // Assert
      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(ResgateStatusEnum.EM_CONTA, resultado.getStatus()));
    }

    // Cenário 2: Admin tenta confirmar resgate já `CONFIRMADO`
    @Test
    @DisplayName("Quando admin tenta confirmar resgate já CONFIRMADO")
    void quandoAdminTentaConfirmarResgateConfirmado() throws Exception {
      // Arrange
      Cliente cliente = criarCliente("Maria", "654321");
      Ativo ativo = criarAtivo("VALE3", "Vale", BigDecimal.valueOf(70.0));
      criarAtivoCarteira(cliente, ativo, 5); // Criar saldo na carteira
      Resgate resgate = criarResgate(cliente, ativo, ResgateStatusEnum.EM_CONTA);

      ResgateConfirmacaoDTO confirmacaoDto = new ResgateConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(ResgateStatusEnum.EM_CONTA);

      // Act
      String responseJsonString =
          driver
              .post(
                  URI_RESGATES + "/" + resgate.getId() + "/confirmar",
                  confirmacaoDto,
                  Admin.getInstance())
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.RESGATE_JA_ESTA_EM_CONTA, resultado.getCode());
    }

    // Cenário 3: Cliente tenta confirmar resgate
    @Test
    @DisplayName("Quando cliente tenta confirmar resgate")
    void quandoClienteTentaConfirmarResgate() throws Exception {
      // Arrange
      Cliente cliente = criarCliente("Ana", "345678");
      Ativo ativo = criarAtivo("BBAS3", "Banco do Brasil", BigDecimal.valueOf(55.0));
      criarAtivoCarteira(cliente, ativo, 12); // Criar saldo na carteira
      Resgate resgate = criarResgate(cliente, ativo, ResgateStatusEnum.SOLICITADO);

      ResgateConfirmacaoDTO confirmacaoDto = new ResgateConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(ResgateStatusEnum.SOLICITADO);

      // Act
      String responseJsonString =
          driver
              .post(URI_RESGATES + "/" + resgate.getId() + "/confirmar", confirmacaoDto, cliente)
              .andExpect(status().isForbidden())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.ACAO_APENAS_ADMIN, resultado.getCode());
    }

    // Cenário 4: Admin tenta confirmar resgate inexistente
    @Test
    @DisplayName("Quando admin tenta confirmar resgate inexistente")
    void quandoAdminTentaConfirmarResgateInexistente() throws Exception {
      // Arrange
      ResgateConfirmacaoDTO confirmacaoDto = new ResgateConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(ResgateStatusEnum.SOLICITADO);

      // Act
      String responseJsonString =
          driver
              .post(URI_RESGATES + "/999/confirmar", confirmacaoDto, Admin.getInstance())
              .andExpect(status().isNotFound())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.RESGATE_NAO_ENCONTRADO, resultado.getCode());
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de solicitação de resgate")
  class ResgateSolicitacaoTests {

    @Autowired AtivoRepository ativoRepository;

    @Test
    @DisplayName("Sanidade: controller /resgates cria e persiste resgate (cliente PREMIUM em Ação)")
    void sanidadeControllerCriar_premiumAcao_persisteNoRepository() throws Exception {
      Cliente clientePremium = criarCliente("Nívea", "premium123");
      Ativo acao = criarAtivo("PETR4", "Petrobras", BigDecimal.valueOf(30.0));
      criarAtivoCarteira(clientePremium, acao, 10); // Criar saldo na carteira

      ResgateCreateDTO dto = ResgateCreateDTO.builder().ativoId(acao.getId()).quantidade(3).build();

      String responseJson =
          driver
              .post(URI_RESGATES, dto, clientePremium)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resposta = objectMapper.readValue(responseJson, ResgateResponseDTO.class);

      assertAll(
          () -> assertNotNull(resposta.getId()),
          () -> assertEquals(acao.getId(), resposta.getAtivoId()),
          () -> assertEquals(ResgateStatusEnum.SOLICITADO, resposta.getStatus()),
          () -> assertTrue(resgateRepository.findById(resposta.getId()).isPresent()));
    }

    @Test
    @DisplayName("Admin tenta criar resgate e falha")
    void adminCriaResgate_deveFalhar() throws Exception {

      // Admin tentando resgatar PETR4
      ResgateCreateDTO dto = new ResgateCreateDTO();
      dto.setAtivoId(1L);
      dto.setQuantidade(1);

      String responseJson =
          driver
              .post(URI_RESGATES, dto, Admin.getInstance())
              .andExpect(status().isForbidden())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO erro = objectMapper.readValue(responseJson, ErrorDTO.class);
      assertEquals(ErrorCode.ACAO_APENAS_CLIENTE_DONO, erro.getCode());
    }

    @Test
    @DisplayName("Quando cliente PREMIUM solicita resgate de Ação deve ser criada com sucesso")
    void quandoClientePremiumResgataAcao() throws Exception {
      // Arrange
      Cliente clientePremium = criarCliente("Carlos", "premium456");
      Ativo ativo = criarAtivo("VALE3", "Vale", BigDecimal.valueOf(70.0));
      criarAtivoCarteira(clientePremium, ativo, 15); // Criar saldo na carteira

      ResgateCreateDTO resgateDto = new ResgateCreateDTO();
      resgateDto.setAtivoId(ativo.getId());
      resgateDto.setQuantidade(5);

      // Act
      String responseJson =
          driver
              .post(URI_RESGATES, resgateDto, clientePremium)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resultado = objectMapper.readValue(responseJson, ResgateResponseDTO.class);

      // Assert
      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(ativo.getId(), resultado.getAtivoId()),
          () -> assertEquals(ResgateStatusEnum.SOLICITADO, resultado.getStatus()));
    }

    @Test
    @DisplayName("Quando cliente PREMIUM solicita resgate de Cripto deve ser criada com sucesso")
    void quandoClientePremiumResgataCripto() throws Exception {
      // Arrange
      Cliente clientePremium = criarCliente("Fernanda", "premium789");
      Ativo cripto = criarAtivo("BTC", "Bitcoin", BigDecimal.valueOf(200000.0));
      criarAtivoCarteira(clientePremium, cripto, 2); // Criar saldo na carteira

      ResgateCreateDTO resgateDto = new ResgateCreateDTO();
      resgateDto.setAtivoId(cripto.getId());
      resgateDto.setQuantidade(1);

      String responseJson =
          driver
              .post(URI_RESGATES, resgateDto, clientePremium)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resultado = objectMapper.readValue(responseJson, ResgateResponseDTO.class);

      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(cripto.getId(), resultado.getAtivoId()),
          () -> assertEquals(ResgateStatusEnum.SOLICITADO, resultado.getStatus()));
    }

    @Test
    @DisplayName("Quando cliente PREMIUM solicita resgate de Tesouro deve ser criada com sucesso")
    void quandoClientePremiumResgataTesouro() throws Exception {
      Cliente clientePremium = criarCliente("Roberto", "premium012");
      Ativo tesouro = criarAtivo("SELIC", "Tesouro Selic", BigDecimal.valueOf(1.0));
      criarAtivoCarteira(clientePremium, tesouro, 1000); // Criar saldo na carteira

      ResgateCreateDTO resgateDto = new ResgateCreateDTO();
      resgateDto.setAtivoId(tesouro.getId());
      resgateDto.setQuantidade(100);

      String responseJson =
          driver
              .post(URI_RESGATES, resgateDto, clientePremium)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resultado = objectMapper.readValue(responseJson, ResgateResponseDTO.class);

      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(tesouro.getId(), resultado.getAtivoId()),
          () -> assertEquals(ResgateStatusEnum.SOLICITADO, resultado.getStatus()));
    }

    @Test
    @DisplayName("Quando cliente tenta resgatar sem saldo na carteira deve falhar")
    void quandoClienteTentaResgatarSemSaldoDeveFalhar() throws Exception {
      // Arrange
      Cliente cliente = criarCliente("João", "senha123");
      Ativo ativo = criarAtivo("PETR4", "Petrobras", BigDecimal.valueOf(30.0));
      // NÃO criar saldo na carteira

      ResgateCreateDTO resgateDto = new ResgateCreateDTO();
      resgateDto.setAtivoId(ativo.getId());
      resgateDto.setQuantidade(1);

      // Act
      String responseJson =
          driver
              .post(URI_RESGATES, resgateDto, cliente)
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJson, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.SALDO_INSUFICIENTE, resultado.getCode());
    }

    @Test
    @DisplayName("Quando cliente tenta resgatar quantidade maior que saldo disponível deve falhar")
    void quandoClienteTentaResgatarQuantidadeMaiorQueSaldoDeveFalhar() throws Exception {
      // Arrange
      Cliente cliente = criarCliente("Maria", "senha456");
      Ativo ativo = criarAtivo("VALE3", "Vale", BigDecimal.valueOf(70.0));
      criarAtivoCarteira(cliente, ativo, 5); // Criar saldo de apenas 5

      ResgateCreateDTO resgateDto = new ResgateCreateDTO();
      resgateDto.setAtivoId(ativo.getId());
      resgateDto.setQuantidade(10); // Tentar resgatar 10 quando só tem 5

      // Act
      String responseJson =
          driver
              .post(URI_RESGATES, resgateDto, cliente)
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJson, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.SALDO_INSUFICIENTE, resultado.getCode());
    }

    @Test
    @DisplayName("Quando cliente tenta resgatar ativo que não possui na carteira deve falhar")
    void quandoClienteTentaResgatarAtivoQueNaoPossuiDeveFalhar() throws Exception {
      // Arrange
      Cliente cliente = criarCliente("Ana", "senha789");
      Ativo ativo1 = criarAtivo("PETR4", "Petrobras", BigDecimal.valueOf(30.0));
      Ativo ativo2 = criarAtivo("VALE3", "Vale", BigDecimal.valueOf(70.0));
      criarAtivoCarteira(cliente, ativo1, 10); // Criar saldo apenas para ativo1

      ResgateCreateDTO resgateDto = new ResgateCreateDTO();
      resgateDto.setAtivoId(ativo2.getId()); // Tentar resgatar ativo2 que não possui
      resgateDto.setQuantidade(1);

      // Act
      String responseJson =
          driver
              .post(URI_RESGATES, resgateDto, cliente)
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJson, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.SALDO_INSUFICIENTE, resultado.getCode());
    }

    @Test
    @DisplayName("Quando cliente resgata quantidade que consome múltiplas linhas de AtivoCarteira")
    void quandoClienteResgataQuantidadeQueConsomeMultiplasLinhas() throws Exception {
      // Arrange
      Cliente cliente = criarCliente("Pedro", "senha123");
      Ativo ativo = criarAtivo("PETR4", "Petrobras", BigDecimal.valueOf(30.0));

      // Criar múltiplas linhas: 3 + 4 = 7 total
      criarMultiplasLinhasAtivoCarteira(cliente, ativo, 3, 4);

      ResgateCreateDTO resgateDto = new ResgateCreateDTO();
      resgateDto.setAtivoId(ativo.getId());
      resgateDto.setQuantidade(5); // Resgatar 5 (consome linha de 3 + 2 da linha de 4)

      // 1. Cliente cria resgate (status SOLICITADO)
      String responseCreateJson =
          driver
              .post(URI_RESGATES, resgateDto, cliente)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resgateSolicitado =
          objectMapper.readValue(responseCreateJson, ResgateResponseDTO.class);

      // Assert - Verificar que resgate foi criado
      assertAll(
          () -> assertNotNull(resgateSolicitado.getId()),
          () -> assertEquals(ativo.getId(), resgateSolicitado.getAtivoId()),
          () -> assertEquals(ResgateStatusEnum.SOLICITADO, resgateSolicitado.getStatus()));

      // 2. Admin confirma resgate (status EM_CONTA) - AQUI é quando as linhas são removidas
      ResgateConfirmacaoDTO confirmacaoDto = new ResgateConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(ResgateStatusEnum.SOLICITADO);

      String responseConfirmarJson =
          driver
              .post(
                  URI_RESGATES + "/" + resgateSolicitado.getId() + "/confirmar",
                  confirmacaoDto,
                  Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resgateConfirmado =
          objectMapper.readValue(responseConfirmarJson, ResgateResponseDTO.class);

      // Assert - Verificar que resgate foi confirmado
      assertEquals(ResgateStatusEnum.EM_CONTA, resgateConfirmado.getStatus());

      // Verificar que as linhas de AtivoCarteira foram removidas corretamente APÓS confirmação
      var linhasRestantes =
          ativoCarteiraRepository.findAllByCliente_IdAndAtivo_Id(cliente.getId(), ativo.getId());
      assertEquals(1, linhasRestantes.size()); // Deve sobrar apenas 1 linha
      assertEquals(2, linhasRestantes.get(0).getQuantidade()); // Com 2 unidades restantes
    }

    @Test
    @DisplayName(
        "Quando cliente resgata quantidade que consome exatamente múltiplas linhas de AtivoCarteira")
    void quandoClienteResgataQuantidadeQueConsomeExatamenteMultiplasLinhas() throws Exception {
      // Arrange
      Cliente cliente = criarCliente("Carla", "senha456");
      Ativo ativo = criarAtivo("VALE3", "Vale", BigDecimal.valueOf(70.0));

      // Criar múltiplas linhas: 2 + 3 + 1 = 6 total
      criarMultiplasLinhasAtivoCarteira(cliente, ativo, 2, 3, 1);

      ResgateCreateDTO resgateDto = new ResgateCreateDTO();
      resgateDto.setAtivoId(ativo.getId());
      resgateDto.setQuantidade(6); // Resgatar 6 (consome todas as linhas)

      // 1. Cliente cria resgate (status SOLICITADO)
      String responseCreateJson =
          driver
              .post(URI_RESGATES, resgateDto, cliente)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resgateSolicitado =
          objectMapper.readValue(responseCreateJson, ResgateResponseDTO.class);

      // Assert - Verificar que resgate foi criado
      assertAll(
          () -> assertNotNull(resgateSolicitado.getId()),
          () -> assertEquals(ativo.getId(), resgateSolicitado.getAtivoId()),
          () -> assertEquals(ResgateStatusEnum.SOLICITADO, resgateSolicitado.getStatus()));

      // 2. Admin confirma resgate (status EM_CONTA) - AQUI é quando as linhas são removidas
      ResgateConfirmacaoDTO confirmacaoDto = new ResgateConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(ResgateStatusEnum.SOLICITADO);

      String responseConfirmarJson =
          driver
              .post(
                  URI_RESGATES + "/" + resgateSolicitado.getId() + "/confirmar",
                  confirmacaoDto,
                  Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resgateConfirmado =
          objectMapper.readValue(responseConfirmarJson, ResgateResponseDTO.class);

      // Assert - Verificar que resgate foi confirmado
      assertEquals(ResgateStatusEnum.EM_CONTA, resgateConfirmado.getStatus());

      // Verificar que todas as linhas de AtivoCarteira foram removidas APÓS confirmação
      var linhasRestantes =
          ativoCarteiraRepository.findAllByCliente_IdAndAtivo_Id(cliente.getId(), ativo.getId());
      assertEquals(0, linhasRestantes.size()); // Não deve sobrar nenhuma linha
    }

    @Test
    @DisplayName(
        "Quando cliente resgata quantidade que consome parcialmente múltiplas linhas de AtivoCarteira")
    void quandoClienteResgataQuantidadeQueConsomeParcialmenteMultiplasLinhas() throws Exception {
      // Arrange
      Cliente cliente = criarCliente("Roberto", "senha789");
      Ativo ativo = criarAtivo("BBAS3", "Banco do Brasil", BigDecimal.valueOf(55.0));

      // Criar múltiplas linhas: 1 + 5 + 2 = 8 total
      criarMultiplasLinhasAtivoCarteira(cliente, ativo, 1, 5, 2);

      ResgateCreateDTO resgateDto = new ResgateCreateDTO();
      resgateDto.setAtivoId(ativo.getId());
      resgateDto.setQuantidade(7); // Resgatar 7 (consome linha de 1 + linha de 5 + 1 da linha de 2)

      // 1. Cliente cria resgate (status SOLICITADO)
      String responseCreateJson =
          driver
              .post(URI_RESGATES, resgateDto, cliente)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resgateSolicitado =
          objectMapper.readValue(responseCreateJson, ResgateResponseDTO.class);

      // Assert - Verificar que resgate foi criado
      assertAll(
          () -> assertNotNull(resgateSolicitado.getId()),
          () -> assertEquals(ativo.getId(), resgateSolicitado.getAtivoId()),
          () -> assertEquals(ResgateStatusEnum.SOLICITADO, resgateSolicitado.getStatus()));

      // 2. Admin confirma resgate (status EM_CONTA) - AQUI é quando as linhas são removidas
      ResgateConfirmacaoDTO confirmacaoDto = new ResgateConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(ResgateStatusEnum.SOLICITADO);

      String responseConfirmarJson =
          driver
              .post(
                  URI_RESGATES + "/" + resgateSolicitado.getId() + "/confirmar",
                  confirmacaoDto,
                  Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resgateConfirmado =
          objectMapper.readValue(responseConfirmarJson, ResgateResponseDTO.class);

      // Assert - Verificar que resgate foi confirmado
      assertEquals(ResgateStatusEnum.EM_CONTA, resgateConfirmado.getStatus());

      // Verificar que as linhas de AtivoCarteira foram removidas corretamente APÓS confirmação
      var linhasRestantes =
          ativoCarteiraRepository.findAllByCliente_IdAndAtivo_Id(cliente.getId(), ativo.getId());
      assertEquals(1, linhasRestantes.size()); // Deve sobrar apenas 1 linha
      assertEquals(1, linhasRestantes.get(0).getQuantidade()); // Com 1 unidade restante
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de listagem de resgates")
  class ResgateListagemTests {

    @Test
    @DisplayName("Admin lista todos os resgates")
    void quandoAdminListaResgatesRetornaSucesso() throws Exception {
      // Arrange
      Cliente cliente1 = criarCliente("Cliente1", "senha1");
      Cliente cliente2 = criarCliente("Cliente2", "senha2");
      Ativo ativo = criarAtivo("PETR4", "Petrobras", BigDecimal.valueOf(30.0));
      criarAtivoCarteira(cliente1, ativo, 10);
      criarAtivoCarteira(cliente2, ativo, 10);

      criarResgate(cliente1, ativo, ResgateStatusEnum.SOLICITADO);
      criarResgate(cliente2, ativo, ResgateStatusEnum.CONFIRMADO);

      // Act & Assert
      driver
          .get(URI_RESGATES, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andDo(print());
    }

    @Test
    @DisplayName("Cliente lista apenas seus resgates")
    void quandoClienteListaResgatesRetornaApenasSeus() throws Exception {
      // Arrange
      Cliente cliente1 = criarCliente("Cliente1", "senha1");
      Cliente cliente2 = criarCliente("Cliente2", "senha2");
      Ativo ativo = criarAtivo("PETR4", "Petrobras", BigDecimal.valueOf(30.0));
      criarAtivoCarteira(cliente1, ativo, 10);
      criarAtivoCarteira(cliente2, ativo, 10);

      criarResgate(cliente1, ativo, ResgateStatusEnum.SOLICITADO);
      criarResgate(cliente2, ativo, ResgateStatusEnum.CONFIRMADO);

      // Act & Assert
      driver
          .get(URI_RESGATES, cliente1)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andDo(print());
    }

    @Test
    @DisplayName("Lista vazia quando não há resgates")
    void quandoListaResgatesVaziaRetornaSucesso() throws Exception {
      driver
          .get(URI_RESGATES, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de recuperação de resgate")
  class ResgateRecuperacaoTests {

    @Test
    @DisplayName("Admin recupera resgate de qualquer cliente")
    void quandoAdminRecuperaResgateRetornaSucesso() throws Exception {
      // Arrange
      Cliente cliente = criarCliente("Cliente", "senha");
      Ativo ativo = criarAtivo("PETR4", "Petrobras", BigDecimal.valueOf(30.0));
      criarAtivoCarteira(cliente, ativo, 10);
      Resgate resgate = criarResgate(cliente, ativo, ResgateStatusEnum.SOLICITADO);

      // Act
      String responseJsonString =
          driver
              .get(URI_RESGATES + "/" + resgate.getId(), Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resultado =
          objectMapper.readValue(responseJsonString, ResgateResponseDTO.class);

      // Assert
      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(resgate.getId(), resultado.getId()),
          () -> assertEquals(ResgateStatusEnum.SOLICITADO, resultado.getStatus()));
    }

    @Test
    @DisplayName("Cliente recupera seu próprio resgate")
    void quandoClienteRecuperaSeuResgateRetornaSucesso() throws Exception {
      // Arrange
      Cliente cliente = criarCliente("Cliente", "senha");
      Ativo ativo = criarAtivo("PETR4", "Petrobras", BigDecimal.valueOf(30.0));
      criarAtivoCarteira(cliente, ativo, 10);
      Resgate resgate = criarResgate(cliente, ativo, ResgateStatusEnum.SOLICITADO);

      // Act
      String responseJsonString =
          driver
              .get(URI_RESGATES + "/" + resgate.getId(), cliente)
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resultado =
          objectMapper.readValue(responseJsonString, ResgateResponseDTO.class);

      // Assert
      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(resgate.getId(), resultado.getId()),
          () -> assertEquals(ResgateStatusEnum.SOLICITADO, resultado.getStatus()));
    }

    @Test
    @DisplayName("Cliente tenta recuperar resgate de outro cliente")
    void quandoClienteTentaRecuperarResgateDeOutroClienteRetornaForbidden() throws Exception {
      // Arrange
      Cliente cliente1 = criarCliente("Cliente1", "senha1");
      Cliente cliente2 = criarCliente("Cliente2", "senha2");
      Ativo ativo = criarAtivo("PETR4", "Petrobras", BigDecimal.valueOf(30.0));
      criarAtivoCarteira(cliente1, ativo, 10);
      Resgate resgate = criarResgate(cliente1, ativo, ResgateStatusEnum.SOLICITADO);

      // Act
      String responseJsonString =
          driver
              .get(URI_RESGATES + "/" + resgate.getId(), cliente2)
              .andExpect(status().isNotFound())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.RESGATE_NAO_ENCONTRADO, resultado.getCode());
    }

    @Test
    @DisplayName("Tenta recuperar resgate inexistente")
    void quandoTentaRecuperarResgateInexistenteRetornaNotFound() throws Exception {
      // Act
      String responseJsonString =
          driver
              .get(URI_RESGATES + "/999", Admin.getInstance())
              .andExpect(status().isNotFound())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.RESGATE_NAO_ENCONTRADO, resultado.getCode());
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de fluxo completo")
  class ResgateFluxoCompletoTests {

    @Test
    @DisplayName("Fluxo completo: cliente cria resgate, admin confirma")
    void quandoExecutaFluxoCompleto() throws Exception {
      // Arrange
      Cliente cliente = criarCliente("Cliente", "senha");
      Ativo ativo = criarAtivo("PETR4", "Petrobras", BigDecimal.valueOf(30.0));
      criarAtivoCarteira(cliente, ativo, 10); // Criar saldo na carteira

      ResgateCreateDTO resgateCreateDTO =
          ResgateCreateDTO.builder().ativoId(ativo.getId()).quantidade(1).build();

      // 1. Cliente cria resgate (status SOLICITADO)
      String responseCreateJsonString =
          driver
              .post(URI_RESGATES, resgateCreateDTO, cliente)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resgateSolicitado =
          objectMapper.readValue(responseCreateJsonString, ResgateResponseDTO.class);
      assertEquals(ResgateStatusEnum.SOLICITADO, resgateSolicitado.getStatus());

      // 2. Admin confirma resgate (status EM_CONTA)
      ResgateConfirmacaoDTO confirmarDto = new ResgateConfirmacaoDTO();
      confirmarDto.setStatusAtual(ResgateStatusEnum.SOLICITADO);

      String responseConfirmarJsonString =
          driver
              .post(
                  URI_RESGATES + "/" + resgateSolicitado.getId() + "/confirmar",
                  confirmarDto,
                  Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ResgateResponseDTO resgateFinalizado =
          objectMapper.readValue(responseConfirmarJsonString, ResgateResponseDTO.class);
      assertEquals(ResgateStatusEnum.EM_CONTA, resgateFinalizado.getStatus());
    }
  }
}
