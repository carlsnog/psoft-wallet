package com.ufcg.psoft.commerce.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.ClienteUpsertDTO;
import com.ufcg.psoft.commerce.dto.ExtratoDTO;
import com.ufcg.psoft.commerce.dto.ExtratoFiltrosDTO;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.enums.PlanoEnum;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.http.exception.ErrorDTO;
import com.ufcg.psoft.commerce.model.Acao;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.compra.CompraStatusEnum;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;
import com.ufcg.psoft.commerce.model.transacao.resgate.ResgateStatusEnum;
import com.ufcg.psoft.commerce.repository.AtivoCarteiraRepository;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.repository.ResgateRepository;
import com.ufcg.psoft.commerce.utils.CustomDriver;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Clientes")
public class ClienteControllerTests {

  final String URI_CLIENTES = "/clientes";

  @Autowired MockMvc mvcDriver;
  CustomDriver driver;

  @Autowired ClienteRepository clienteRepository;
  @Autowired AtivoRepository ativoRepository;
  @Autowired CompraRepository compraRepository;
  @Autowired ResgateRepository resgateRepository;
  @Autowired AtivoCarteiraRepository ativoCarteiraRepository;

  ObjectMapper objectMapper = new ObjectMapper();

  Cliente cliente;

  ClienteUpsertDTO upsertDto;

  @BeforeEach
  void setup() {
    // Object Mapper suporte para LocalDateTime
    objectMapper.registerModule(new JavaTimeModule());
    driver = new CustomDriver(mvcDriver, objectMapper);

    cliente =
        clienteRepository.save(
            Cliente.builder()
                .nome("Cliente Um da Silva")
                .plano(PlanoEnum.NORMAL)
                .endereco("Rua dos Testes, 123")
                .codigoAcesso("123456")
                .build());
    upsertDto =
        ClienteUpsertDTO.builder()
            .nome(cliente.getNome())
            .plano(PlanoEnum.NORMAL)
            .endereco(cliente.getEndereco())
            .codigoAcesso(cliente.getCodigoAcesso())
            .build();
  }

  @AfterEach
  void tearDown() {
    ativoCarteiraRepository.deleteAll();
    resgateRepository.deleteAll();
    compraRepository.deleteAll();
    ativoRepository.deleteAll();
    clienteRepository.deleteAll();
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de nome")
  class ClienteVerificacaoNome {

    @Test
    @DisplayName("Quando recuperamos um cliente com dados válidos")
    void quandoRecuperamosNomeDoClienteValido() throws Exception {
      // Act
      String responseJsonString =
          driver
              .get(URI_CLIENTES + "/" + cliente.getId(), cliente)
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ClienteResponseDTO resultado =
          objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

      // Assert
      assertEquals("Cliente Um da Silva", resultado.getNome());
    }

    @Test
    @DisplayName("Quando alteramos o nome do cliente com dados válidos")
    void quandoAlteramosNomeDoClienteValido() throws Exception {
      // Arrange
      upsertDto.setNome("Cliente Um Alterado");

      // Act
      String responseJsonString =
          driver
              .put(URI_CLIENTES + "/" + cliente.getId(), upsertDto, cliente)
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ClienteResponseDTO resultado =
          objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

      // Assert
      assertEquals("Cliente Um Alterado", resultado.getNome());
    }

    @Test
    @DisplayName("Quando alteramos o nome do cliente nulo")
    void quandoAlteramosNomeDoClienteNulo() throws Exception {
      // Arrange
      upsertDto.setNome(null);

      // Act
      String responseJsonString =
          driver
              .put(URI_CLIENTES + "/" + cliente.getId(), upsertDto, cliente)
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.BAD_REQUEST, resultado.getCode());

      assertInstanceOf(ArrayList.class, resultado.getData());
      ArrayList<String> errors = (ArrayList<String>) resultado.getData();

      // Assert
      assertAll(
          () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
          () -> assertEquals("Nome obrigatorio", errors.get(0)));
    }

    @Test
    @DisplayName("Quando alteramos o nome do cliente vazio")
    void quandoAlteramosNomeDoClienteVazio() throws Exception {
      // Arrange
      upsertDto.setNome("");

      // Act
      String responseJsonString =
          driver
              .put(URI_CLIENTES + "/" + cliente.getId(), upsertDto, cliente)
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.BAD_REQUEST, resultado.getCode());

      assertInstanceOf(ArrayList.class, resultado.getData());
      ArrayList<String> errors = (ArrayList<String>) resultado.getData();

      // Assert
      assertAll(
          () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
          () -> assertEquals("Nome obrigatorio", errors.get(0)));
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação do endereço")
  class ClienteVerificacaoEndereco {

    @Test
    @DisplayName("Quando alteramos o endereço do cliente com dados válidos")
    void quandoAlteramosEnderecoDoClienteValido() throws Exception {
      // Arrange
      upsertDto.setEndereco("Endereco Alterado");

      // Act
      String responseJsonString =
          driver
              .put(URI_CLIENTES + "/" + cliente.getId(), upsertDto, cliente)
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ClienteResponseDTO resultado =
          objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

      // Assert
      assertEquals("Endereco Alterado", resultado.getEndereco());
    }

    @Test
    @DisplayName("Quando alteramos o endereço do cliente nulo")
    void quandoAlteramosEnderecoDoClienteNulo() throws Exception {
      // Arrange
      upsertDto.setEndereco(null);

      // Act
      String responseJsonString =
          driver
              .put(URI_CLIENTES + "/" + cliente.getId(), upsertDto, cliente)
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.BAD_REQUEST, resultado.getCode());

      assertInstanceOf(ArrayList.class, resultado.getData());
      ArrayList<String> errors = (ArrayList<String>) resultado.getData();

      // Assert
      assertAll(
          () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
          () -> assertEquals("Endereco obrigatorio", errors.get(0)));
    }

    @Test
    @DisplayName("Quando alteramos o endereço do cliente vazio")
    void quandoAlteramosEnderecoDoClienteVazio() throws Exception {
      // Arrange
      upsertDto.setEndereco("");

      // Act
      String responseJsonString =
          driver
              .put(URI_CLIENTES + "/" + cliente.getId(), upsertDto, cliente)
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.BAD_REQUEST, resultado.getCode());

      assertInstanceOf(ArrayList.class, resultado.getData());
      ArrayList<String> errors = (ArrayList<String>) resultado.getData();

      // Assert
      assertAll(
          () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
          () -> assertEquals("Endereco obrigatorio", errors.get(0)));
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação do código de acesso")
  class ClienteVerificacaoCodigoAcesso {

    @Test
    @DisplayName("Quando alteramos o código de acesso do cliente nulo")
    void quandoAlteramosCodigoAcessoDoClienteNulo() throws Exception {
      // Arrange
      upsertDto.setCodigoAcesso(null);

      // Act
      String responseJsonString =
          driver
              .put(URI_CLIENTES + "/" + cliente.getId(), upsertDto, cliente)
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.BAD_REQUEST, resultado.getCode());

      assertInstanceOf(ArrayList.class, resultado.getData());
      ArrayList<String> errors = (ArrayList<String>) resultado.getData();

      // Assert
      assertAll(
          () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
          () -> assertEquals("Codigo de acesso obrigatorio", errors.get(0)));
    }

    @Test
    @DisplayName("Quando alteramos o código de acesso do cliente mais de 6 digitos")
    void quandoAlteramosCodigoAcessoDoClienteMaisDe6Digitos() throws Exception {
      // Arrange
      upsertDto.setCodigoAcesso("1234567");

      // Act
      String responseJsonString =
          driver
              .put(URI_CLIENTES + "/" + cliente.getId(), upsertDto, cliente)
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.BAD_REQUEST, resultado.getCode());

      assertInstanceOf(ArrayList.class, resultado.getData());
      ArrayList<String> errors = (ArrayList<String>) resultado.getData();

      // Assert
      assertAll(
          () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
          () ->
              assertEquals(
                  "Codigo de acesso deve ter exatamente 6 digitos numericos", errors.get(0)));
    }

    @Test
    @DisplayName("Quando alteramos o código de acesso do cliente menos de 6 digitos")
    void quandoAlteramosCodigoAcessoDoClienteMenosDe6Digitos() throws Exception {
      // Arrange
      upsertDto.setCodigoAcesso("12345");

      // Act
      String responseJsonString =
          driver
              .put(URI_CLIENTES + "/" + cliente.getId(), upsertDto, cliente)
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.BAD_REQUEST, resultado.getCode());

      assertInstanceOf(ArrayList.class, resultado.getData());
      ArrayList<String> errors = (ArrayList<String>) resultado.getData();

      // Assert
      assertAll(
          () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
          () ->
              assertEquals(
                  "Codigo de acesso deve ter exatamente 6 digitos numericos", errors.get(0)));
    }

    @Test
    @DisplayName("Quando alteramos o código de acesso do cliente caracteres " + "não numéricos")
    void quandoAlteramosCodigoAcessoDoClienteCaracteresNaoNumericos() throws Exception {
      // Arrange
      upsertDto.setCodigoAcesso("a*c4e@");

      // Act
      String responseJsonString =
          driver
              .put(URI_CLIENTES + "/" + cliente.getId(), upsertDto, cliente)
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.BAD_REQUEST, resultado.getCode());

      assertInstanceOf(ArrayList.class, resultado.getData());
      ArrayList<String> errors = (ArrayList<String>) resultado.getData();

      // Assert
      assertAll(
          () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
          () ->
              assertEquals(
                  "Codigo de acesso deve ter exatamente 6 digitos numericos", errors.get(0)));
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de exportação de extrato CSV")
  class ClienteVerificacaoExtratoCsv {

    @Test
    @DisplayName("Quando cliente tenta solicitar extrato CSV de outro cliente")
    void quandoClienteTentaSolicitarExtratoCsvDeOutroCliente() throws Exception {
      // Arrange
      Cliente outroCliente =
          clienteRepository.save(
              Cliente.builder()
                  .nome("Outro Cliente")
                  .plano(PlanoEnum.NORMAL)
                  .endereco("Rua Outra, 456")
                  .codigoAcesso("654321")
                  .build());

      // Act
      String responseJsonString =
          driver
              .get(URI_CLIENTES + "/" + outroCliente.getId() + "/extrato/csv", cliente)
              .andExpect(status().isForbidden())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.ACAO_APENAS_ADMIN, resultado.getCode());
    }

    @Test
    @DisplayName("Quando cliente tenta solicitar extrato CSV de cliente inexistente")
    void quandoClienteTentaSolicitarExtratoCsvDeClienteInexistente() throws Exception {
      // Act
      String responseJsonString =
          driver
              .get(URI_CLIENTES + "/999999/extrato/csv", cliente)
              .andExpect(status().isForbidden())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.ACAO_APENAS_ADMIN, resultado.getCode());
    }

    @Test
    @DisplayName("Quando admin solicita extrato CSV de qualquer cliente")
    void quandoAdminSolicitaExtratoCsvDeQualquerCliente() throws Exception {
      // Act
      driver
          .get(URI_CLIENTES + "/" + cliente.getId() + "/extrato/csv", Admin.getInstance())
          .andExpect(status().isOk())
          .andDo(print());
    }

    @Test
    @DisplayName("Quando cliente com transações solicita extrato CSV")
    void quandoClienteComTransacoesSolicitaExtratoCsv() throws Exception {
      // Arrange - Criar ativo e transações para o cliente
      Ativo ativo =
          ativoRepository.save(
              Acao.builder()
                  .nome("PETR4_TEST")
                  .descricao("Petrobras Test")
                  .cotacao(BigDecimal.valueOf(30.0))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.ACAO)
                  .build());

      // Criar compra finalizada
      compraRepository.save(
          Compra.builder()
              .cliente(cliente)
              .ativo(ativo)
              .quantidade(5)
              .valorUnitario(BigDecimal.valueOf(30.0))
              .abertaEm(LocalDateTime.now().minusDays(2))
              .status(CompraStatusEnum.EM_CARTEIRA)
              .finalizadaEm(LocalDateTime.now().minusDays(1))
              .build());

      // Criar resgate finalizado
      resgateRepository.save(
          Resgate.builder()
              .cliente(cliente)
              .ativo(ativo)
              .quantidade(2)
              .valorUnitario(BigDecimal.valueOf(35.0))
              .abertaEm(LocalDateTime.now().minusHours(5))
              .status(ResgateStatusEnum.EM_CONTA)
              .finalizadaEm(LocalDateTime.now().minusHours(1))
              .build());

      // Act
      String responseContent =
          driver
              .get(URI_CLIENTES + "/" + cliente.getId() + "/extrato/csv", cliente)
              //   .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      // Assert - Verifica estrutura e conteúdo do CSV
      String[] lines = responseContent.split("\n");

      assertAll(
          () -> assertNotNull(responseContent),
          () ->
              assertTrue(
                  lines.length >= 3, "CSV deve ter pelo menos 3 linhas (header + 2 transações)"),
          () -> assertTrue(lines[0].contains("Tipo"), "Primeira linha deve conter header 'Tipo'"),
          () -> assertTrue(lines[0].contains("Ativo"), "Primeira linha deve conter header 'Ativo'"),
          () ->
              assertTrue(
                  lines[0].contains("Quantidade"),
                  "Primeira linha deve conter header 'Quantidade'"),
          () ->
              assertTrue(
                  lines[0].contains("Valor Unitario"),
                  "Primeira linha deve conter header 'Valor Unitario'"),
          () -> assertTrue(lines[0].contains("Total"), "Primeira linha deve conter header 'Total'"),
          () -> assertTrue(lines[0].contains("Lucro"), "Primeira linha deve conter header 'Lucro'"),
          () ->
              assertTrue(
                  lines[0].contains("Imposto Pago"),
                  "Primeira linha deve conter header 'Imposto Pago'"),
          () ->
              assertTrue(
                  lines[0].contains("Aberta Em"), "Primeira linha deve conter header 'Aberta Em'"),
          () ->
              assertTrue(
                  lines[0].contains("Finalizada Em"),
                  "Primeira linha deve conter header 'Finalizada Em'"),
          () -> assertTrue(lines[1].contains("Compra"), "CSV deve conter transação de COMPRA"),
          () -> assertTrue(lines[1].contains("5"), "CSV deve conter quantidade da compra"),
          () -> assertTrue(lines[1].contains("PETR4_TEST"), "CSV deve conter nome do ativo"),
          () -> assertTrue(lines[2].contains("Resgate"), "CSV deve conter transação de RESGATE"),
          () -> assertTrue(lines[2].contains("PETR4_TEST"), "CSV deve conter nome do ativo"),
          () -> assertTrue(lines[2].contains("2"), "CSV deve conter quantidade do resgate"));
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de extrato com filtros")
  class ClienteVerificacaoExtratoFiltros {

    @Test
    @DisplayName("Quando cliente solicita extrato com filtro por tipo de operação")
    void quandoClienteSolicitaExtratoComFiltroTipoOperacao() throws Exception {
      // Arrange - Criar ativo e transações para o cliente
      Ativo ativo =
          ativoRepository.save(
              Acao.builder()
                  .nome("PETR4_TEST")
                  .descricao("Petrobras Test")
                  .cotacao(BigDecimal.valueOf(30.0))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.ACAO)
                  .build());

      // Criar compra finalizada
      compraRepository.save(
          Compra.builder()
              .cliente(cliente)
              .ativo(ativo)
              .quantidade(5)
              .valorUnitario(BigDecimal.valueOf(30.0))
              .abertaEm(LocalDateTime.now().minusDays(2))
              .status(CompraStatusEnum.EM_CARTEIRA)
              .finalizadaEm(LocalDateTime.now().minusDays(1))
              .build());

      // Criar resgate finalizado
      resgateRepository.save(
          Resgate.builder()
              .cliente(cliente)
              .ativo(ativo)
              .quantidade(2)
              .valorUnitario(BigDecimal.valueOf(35.0))
              .abertaEm(LocalDateTime.now().minusHours(5))
              .status(ResgateStatusEnum.EM_CONTA)
              .finalizadaEm(LocalDateTime.now().minusHours(1))
              .build());

      // Criar filtros para buscar apenas compras
      ExtratoFiltrosDTO filtros = ExtratoFiltrosDTO.builder().tipoOperacao("Compra").build();

      // Act
      String responseJsonString =
          driver
              .post("/clientes/extrato", filtros, cliente)
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      List<ExtratoDTO> resultado =
          objectMapper.readValue(responseJsonString, new TypeReference<List<ExtratoDTO>>() {});

      // Assert - Verifica que só retornou compras
      assertAll(
          () -> assertNotNull(resultado),
          () -> assertEquals(1, resultado.size(), "Deve retornar apenas 1 compra"),
          () -> assertEquals("Compra", resultado.get(0).tipo(), "Deve ser uma transação de COMPRA"),
          () ->
              assertEquals("PETR4_TEST", resultado.get(0).ativo(), "Deve ser do ativo PETR4_TEST"),
          () -> assertEquals(5, resultado.get(0).quantidade(), "Deve ter quantidade 5"));
    }

    @Test
    @DisplayName("Quando cliente solicita extrato com filtro por nome do ativo")
    void quandoClienteSolicitaExtratoComFiltroNomeAtivo() throws Exception {
      // Arrange - Criar múltiplos ativos
      Ativo ativo1 =
          ativoRepository.save(
              Acao.builder()
                  .nome("PETR4_TEST")
                  .descricao("Petrobras Test")
                  .cotacao(BigDecimal.valueOf(30.0))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.ACAO)
                  .build());

      Ativo ativo2 =
          ativoRepository.save(
              Acao.builder()
                  .nome("VALE3_TEST")
                  .descricao("Vale Test")
                  .cotacao(BigDecimal.valueOf(50.0))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.ACAO)
                  .build());

      // Criar compras para ambos os ativos
      compraRepository.save(
          Compra.builder()
              .cliente(cliente)
              .ativo(ativo1)
              .quantidade(3)
              .valorUnitario(BigDecimal.valueOf(30.0))
              .abertaEm(LocalDateTime.now().minusDays(1))
              .status(CompraStatusEnum.EM_CARTEIRA)
              .finalizadaEm(LocalDateTime.now())
              .build());

      compraRepository.save(
          Compra.builder()
              .cliente(cliente)
              .ativo(ativo2)
              .quantidade(2)
              .valorUnitario(BigDecimal.valueOf(50.0))
              .abertaEm(LocalDateTime.now().minusDays(1))
              .status(CompraStatusEnum.EM_CARTEIRA)
              .finalizadaEm(LocalDateTime.now())
              .build());

      // Criar filtros para buscar apenas PETR4
      ExtratoFiltrosDTO filtros = ExtratoFiltrosDTO.builder().nomeAtivo("PETR4").build();

      // Act
      String responseJsonString =
          driver
              .post("/clientes/extrato", filtros, cliente)
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      List<ExtratoDTO> resultado =
          objectMapper.readValue(responseJsonString, new TypeReference<List<ExtratoDTO>>() {});

      // Assert - Verifica que só retornou transações do PETR4
      assertAll(
          () -> assertNotNull(resultado),
          () -> assertEquals(1, resultado.size(), "Deve retornar apenas 1 transação do PETR4"),
          () ->
              assertEquals("PETR4_TEST", resultado.get(0).ativo(), "Deve ser do ativo PETR4_TEST"),
          () -> assertEquals(3, resultado.get(0).quantidade(), "Deve ter quantidade 3"));
    }

    @Test
    @DisplayName("Quando cliente solicita extrato com filtro por range de datas")
    void quandoClienteSolicitaExtratoComFiltroRangeDatas() throws Exception {
      // Arrange - Criar ativo e transações em datas diferentes
      Ativo ativo =
          ativoRepository.save(
              Acao.builder()
                  .nome("PETR4_TEST")
                  .descricao("Petrobras Test")
                  .cotacao(BigDecimal.valueOf(30.0))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.ACAO)
                  .build());

      LocalDateTime dataAntiga = LocalDateTime.now().minusDays(10);
      LocalDateTime dataRecente = LocalDateTime.now().minusDays(2);

      // Criar compra antiga
      compraRepository.save(
          Compra.builder()
              .cliente(cliente)
              .ativo(ativo)
              .quantidade(5)
              .valorUnitario(BigDecimal.valueOf(30.0))
              .abertaEm(dataAntiga)
              .status(CompraStatusEnum.EM_CARTEIRA)
              .finalizadaEm(dataAntiga.plusHours(1))
              .build());

      // Criar compra recente
      compraRepository.save(
          Compra.builder()
              .cliente(cliente)
              .ativo(ativo)
              .quantidade(3)
              .valorUnitario(BigDecimal.valueOf(30.0))
              .abertaEm(dataRecente)
              .status(CompraStatusEnum.EM_CARTEIRA)
              .finalizadaEm(dataRecente.plusHours(1))
              .build());

      // Criar filtros para buscar apenas transações dos últimos 5 dias
      ExtratoFiltrosDTO filtros =
          ExtratoFiltrosDTO.builder()
              .dataInicio(LocalDateTime.now().minusDays(5))
              .dataFim(LocalDateTime.now())
              .build();

      // Act
      String responseJsonString =
          driver
              .post("/clientes/extrato", filtros, cliente)
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      List<ExtratoDTO> resultado =
          objectMapper.readValue(responseJsonString, new TypeReference<List<ExtratoDTO>>() {});

      // Assert - Verifica que só retornou a transação recente
      assertAll(
          () -> assertNotNull(resultado),
          () -> assertEquals(1, resultado.size(), "Deve retornar apenas 1 transação recente"),
          () -> assertEquals(3, resultado.get(0).quantidade(), "Deve ter quantidade 3"));
    }

    @Test
    @DisplayName("Quando admin solicita extrato com filtro por cliente específico")
    void quandoAdminSolicitaExtratoComFiltroClienteEspecifico() throws Exception {
      // Arrange - Criar outro cliente e suas transações
      Cliente outroCliente =
          clienteRepository.save(
              Cliente.builder()
                  .nome("Outro Cliente")
                  .plano(PlanoEnum.NORMAL)
                  .endereco("Rua Outra, 456")
                  .codigoAcesso("654321")
                  .build());

      Ativo ativo =
          ativoRepository.save(
              Acao.builder()
                  .nome("VALE3_TEST")
                  .descricao("Vale Test")
                  .cotacao(BigDecimal.valueOf(50.0))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.ACAO)
                  .build());

      // Criar compra para o outro cliente
      compraRepository.save(
          Compra.builder()
              .cliente(outroCliente)
              .ativo(ativo)
              .quantidade(4)
              .valorUnitario(BigDecimal.valueOf(50.0))
              .abertaEm(LocalDateTime.now().minusDays(1))
              .status(CompraStatusEnum.EM_CARTEIRA)
              .finalizadaEm(LocalDateTime.now())
              .build());

      // Criar compra para o cliente atual
      compraRepository.save(
          Compra.builder()
              .cliente(cliente)
              .ativo(ativo)
              .quantidade(2)
              .valorUnitario(BigDecimal.valueOf(50.0))
              .abertaEm(LocalDateTime.now().minusDays(1))
              .status(CompraStatusEnum.EM_CARTEIRA)
              .finalizadaEm(LocalDateTime.now())
              .build());

      // Criar filtros para buscar transações do outro cliente
      ExtratoFiltrosDTO filtros =
          ExtratoFiltrosDTO.builder().clienteId(outroCliente.getId()).build();

      // Act
      String responseJsonString =
          driver
              .post("/clientes/extrato", filtros, Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      List<ExtratoDTO> resultado =
          objectMapper.readValue(responseJsonString, new TypeReference<List<ExtratoDTO>>() {});

      // Assert - Verifica que retornou transações do outro cliente
      assertAll(
          () -> assertNotNull(resultado),
          () -> assertEquals(1, resultado.size(), "Deve retornar 1 transação do outro cliente"),
          () ->
              assertEquals("VALE3_TEST", resultado.get(0).ativo(), "Deve ser do ativo VALE3_TEST"),
          () -> assertEquals(4, resultado.get(0).quantidade(), "Deve ter quantidade 4"));
    }

    @Test
    @DisplayName("Quando admin solicita extrato sem filtro de cliente (todas as transações)")
    void quandoAdminSolicitaExtratoSemFiltroCliente() throws Exception {
      // Arrange - Criar outro cliente e suas transações
      Cliente outroCliente =
          clienteRepository.save(
              Cliente.builder()
                  .nome("Outro Cliente")
                  .plano(PlanoEnum.NORMAL)
                  .endereco("Rua Outra, 456")
                  .codigoAcesso("654321")
                  .build());

      Ativo ativo =
          ativoRepository.save(
              Acao.builder()
                  .nome("PETR4_TEST")
                  .descricao("Petrobras Test")
                  .cotacao(BigDecimal.valueOf(30.0))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.ACAO)
                  .build());

      // Criar compra para o cliente atual
      compraRepository.save(
          Compra.builder()
              .cliente(cliente)
              .ativo(ativo)
              .quantidade(3)
              .valorUnitario(BigDecimal.valueOf(30.0))
              .abertaEm(LocalDateTime.now().minusDays(1))
              .status(CompraStatusEnum.EM_CARTEIRA)
              .finalizadaEm(LocalDateTime.now())
              .build());

      // Criar compra para o outro cliente
      compraRepository.save(
          Compra.builder()
              .cliente(outroCliente)
              .ativo(ativo)
              .quantidade(2)
              .valorUnitario(BigDecimal.valueOf(30.0))
              .abertaEm(LocalDateTime.now().minusDays(1))
              .status(CompraStatusEnum.EM_CARTEIRA)
              .finalizadaEm(LocalDateTime.now())
              .build());

      // Criar filtros vazios (admin vê todas as transações)
      ExtratoFiltrosDTO filtros = ExtratoFiltrosDTO.builder().build();

      // Act
      String responseJsonString =
          driver
              .post("/clientes/extrato", filtros, Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      List<ExtratoDTO> resultado =
          objectMapper.readValue(responseJsonString, new TypeReference<List<ExtratoDTO>>() {});

      // Assert - Verifica que retornou transações de ambos os clientes
      assertAll(
          () -> assertNotNull(resultado),
          () ->
              assertEquals(2, resultado.size(), "Deve retornar 2 transações (ambos os clientes)"));
    }

    @Test
    @DisplayName("Quando cliente solicita extrato com filtros inválidos deve retornar erro")
    void quandoClienteSolicitaExtratoComFiltrosInvalidos() throws Exception {
      // Arrange - Filtros com tipo de operação inválido
      ExtratoFiltrosDTO filtros =
          ExtratoFiltrosDTO.builder()
              .tipoOperacao("VENDA") // Tipo inválido
              .build();

      // Act
      String responseJsonString =
          driver
              .post("/clientes/extrato", filtros, cliente)
              //   .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.BAD_REQUEST, resultado.getCode());
      assertTrue(resultado.getMessage().contains("Erros de validacao encontrados"));
    }

    @Test
    @DisplayName("Quando cliente solicita extrato sem transações deve retornar lista vazia")
    void quandoClienteSolicitaExtratoSemTransacoes() throws Exception {
      // Arrange - Cliente sem transações
      ExtratoFiltrosDTO filtros = ExtratoFiltrosDTO.builder().build();

      // Act
      String responseJsonString =
          driver
              .post("/clientes/extrato", filtros, cliente)
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      List<ExtratoDTO> resultado =
          objectMapper.readValue(responseJsonString, new TypeReference<List<ExtratoDTO>>() {});

      // Assert
      assertAll(
          () -> assertNotNull(resultado),
          () -> assertEquals(0, resultado.size(), "Deve retornar lista vazia"));
    }

    @Test
    @DisplayName(
        "Quando cliente solicita extrato com filtro por nome parcial do ativo (case-insensitive) retorna múltiplos matches")
    void quandoClienteSolicitaExtratoComFiltroNomeAtivoParcial() throws Exception {
      // Arrange - criar dois ativos com variações no nome
      Ativo a1 =
          ativoRepository.save(
              Acao.builder()
                  .nome("PETR4_TEST")
                  .descricao("Petrobras Test")
                  .cotacao(BigDecimal.valueOf(30.0))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.ACAO)
                  .build());

      Ativo a2 =
          ativoRepository.save(
              Acao.builder()
                  .nome("petR4_SPECIAL")
                  .descricao("Petrobras Special")
                  .cotacao(BigDecimal.valueOf(31.0))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.ACAO)
                  .build());

      // compras para ambos
      compraRepository.save(
          Compra.builder()
              .cliente(cliente)
              .ativo(a1)
              .quantidade(2)
              .valorUnitario(a1.getCotacao())
              .abertaEm(LocalDateTime.now().minusDays(2))
              .status(CompraStatusEnum.EM_CARTEIRA)
              .finalizadaEm(LocalDateTime.now().minusDays(1))
              .build());

      compraRepository.save(
          Compra.builder()
              .cliente(cliente)
              .ativo(a2)
              .quantidade(3)
              .valorUnitario(a2.getCotacao())
              .abertaEm(LocalDateTime.now().minusDays(1))
              .status(CompraStatusEnum.EM_CARTEIRA)
              .finalizadaEm(LocalDateTime.now())
              .build());

      // filtro parcial (lowercase) que deve bater com ambos
      ExtratoFiltrosDTO filtros = ExtratoFiltrosDTO.builder().nomeAtivo("petr4").build();

      // Act
      String responseJsonString =
          driver
              .post("/clientes/extrato", filtros, cliente)
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      List<ExtratoDTO> resultado =
          objectMapper.readValue(responseJsonString, new TypeReference<List<ExtratoDTO>>() {});

      // Assert -> ambos os ativos aparecem (2 transacoes)
      assertAll(
          () -> assertNotNull(resultado),
          () ->
              assertEquals(2, resultado.size(), "Deve retornar 2 transações que contenham 'petr4'"),
          () ->
              assertTrue(
                  resultado.stream().allMatch(e -> e.ativo().toLowerCase().contains("petr4")),
                  "Todos os nomes devem conter 'petr4' (case-insensitive)"));
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
  class ClienteVerificacaoFluxosBasicosApiRest {

    @Test
    @DisplayName("Quando buscamos por todos clientes salvos")
    void quandoBuscamosPorTodosClienteSalvos() throws Exception {
      // Arrange
      // Vamos ter 3 clientes no banco
      Cliente cliente1 =
          Cliente.builder()
              .nome("Cliente Dois Almeida")
              .endereco("Av. da Pits A, 100")
              .codigoAcesso("246810")
              .plano(PlanoEnum.NORMAL)
              .build();
      Cliente cliente2 =
          Cliente.builder()
              .nome("Cliente Três Lima")
              .endereco("Distrito dos Testadores, 200")
              .codigoAcesso("135790")
              .plano(PlanoEnum.NORMAL)
              .build();
      clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));

      // Act
      String responseJsonString =
          driver
              .get(URI_CLIENTES, Admin.getInstance())
              .andExpect(status().isOk()) // Codigo 200
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      List<Cliente> resultado =
          objectMapper.readValue(responseJsonString, new TypeReference<>() {});

      // Assert
      assertAll(() -> assertEquals(3, resultado.size()));
    }

    @Test
    @DisplayName("Quando buscamos um cliente salvo pelo id")
    void quandoBuscamosPorUmClienteSalvo() throws Exception {
      // Arrange

      // Act
      String responseJsonString =
          driver
              .get(URI_CLIENTES + "/" + cliente.getId(), Admin.getInstance())
              .andExpect(status().isOk()) // Codigo 200
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ClienteResponseDTO resultado =
          objectMapper.readValue(responseJsonString, new TypeReference<>() {});

      // Assert
      assertAll(
          () -> assertEquals(cliente.getId(), resultado.getId()),
          () -> assertEquals(cliente.getNome(), resultado.getNome()));
    }

    @Test
    @DisplayName("Quando buscamos um cliente inexistente")
    void quandoBuscamosPorUmClienteInexistente() throws Exception {
      // Arrange

      // Act
      String responseJsonString =
          driver
              .get(URI_CLIENTES + "/" + 999999999, Admin.getInstance())
              .andExpect(status().isNotFound())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.CLIENTE_NAO_ENCONTRADO, resultado.getCode());

      assertEquals("Cliente nao existe", resultado.getMessage());
    }

    @Test
    @DisplayName("Quando criamos um novo cliente com dados válidos")
    void quandoCriarClienteValido() throws Exception {
      // Arrange
      var url = URI_CLIENTES;

      // Act
      String responseJsonString =
          driver
              .post(url, upsertDto)
              .andExpect(status().isCreated()) // Codigo 201
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ClienteResponseDTO resultado =
          objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

      // Assert
      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(upsertDto.getNome(), resultado.getNome()));
    }

    @Test
    @DisplayName("Quando alteramos o cliente com dados válidos")
    void quandoAlteramosClienteValido() throws Exception {
      // Arrange
      Long clienteId = cliente.getId();

      // Act
      String responseJsonString =
          driver
              .put(URI_CLIENTES + "/" + cliente.getId(), upsertDto, cliente)
              .andExpect(status().isOk()) // Codigo 200
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ClienteResponseDTO resultado =
          objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

      // Assert
      assertAll(
          () -> assertEquals(clienteId, resultado.getId()),
          () -> assertEquals(upsertDto.getNome(), resultado.getNome()));
    }

    @Test
    @DisplayName("Quando alteramos o cliente inexistente")
    void quandoAlteramosClienteInexistente() throws Exception {
      // Arrange

      // Act
      String responseJsonString =
          driver
              .put(URI_CLIENTES + "/" + 99999L, upsertDto, cliente)
              .andExpect(status().isForbidden())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.FORBIDDEN, resultado.getCode());

      assertEquals("Acesso negado", resultado.getMessage());
    }

    @Test
    @DisplayName("Quando alteramos o cliente passando código de acesso inválido")
    void quandoAlteramosClienteCodigoAcessoInvalido() throws Exception {
      // Arrange
      Long clienteId = cliente.getId();

      // Act
      String responseJsonString =
          mvcDriver
              .perform(
                  put(URI_CLIENTES + "/" + clienteId)
                      .header(
                          "Authorization",
                          driver.createBasicAuthHeader(clienteId.toString(), "invalido"))
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(upsertDto)))
              .andExpect(status().isUnauthorized())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.UNAUTHORIZED, resultado.getCode());

      assertEquals("Nao autorizado", resultado.getMessage());
    }

    @Test
    @DisplayName("Quando excluímos um cliente salvo")
    void quandoExcluimosClienteValido() throws Exception {
      // Arrange

      // Act
      driver
          .delete(URI_CLIENTES + "/" + cliente.getId(), cliente)
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Quando excluímos um cliente inexistente")
    void quandoExcluimosClienteInexistente() throws Exception {
      // Arrange

      // Act
      String responseJsonString =
          driver
              .delete(URI_CLIENTES + "/" + 999999, cliente)
              .andExpect(status().isNotFound())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.CLIENTE_NAO_ENCONTRADO, resultado.getCode());

      assertEquals("Cliente nao existe", resultado.getMessage());
    }

    @Test
    @DisplayName("Quando excluímos um cliente salvo passando código de acesso inválido")
    void quandoExcluimosClienteCodigoAcessoInvalido() throws Exception {
      // Arrange
      // nenhuma necessidade além do setup()

      // Act
      String responseJsonString =
          mvcDriver
              .perform(
                  delete(URI_CLIENTES + "/" + cliente.getId())
                      .header(
                          "Authorization",
                          driver.createBasicAuthHeader(
                              String.valueOf(cliente.getId()), "invalido")))
              .andExpect(status().isUnauthorized()) // Codigo 401
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.UNAUTHORIZED, resultado.getCode());

      assertEquals("Nao autorizado", resultado.getMessage());
    }
  }
}
