package com.ufcg.psoft.commerce.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.*;
import com.ufcg.psoft.commerce.enums.*;
import com.ufcg.psoft.commerce.http.exception.*;
import com.ufcg.psoft.commerce.model.Acao;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Ativos")
public class AtivoControllerTests {

  // URI base para os endpoints do AtivoController
  final String URI_ATIVOS = "/ativos";

  // Objeto para simular requisições HTTP para o controller
  @Autowired MockMvc driver;

  // Repositório de clientes
  @Autowired ClienteRepository clienteRepository;

  // Mock do AtivoService para controlar o comportamento da camada de serviço
  @MockBean AtivoService ativoService;

  // Objeto para serializar e desserializar JSON
  ObjectMapper objectMapper = new ObjectMapper();

  // DTOs de resposta e requisição para uso nos testes
  // TODO: escopar ativoDTO em classe especifica
  AtivoResponseDTO ativoResponseDTO;
  AtivoUpsertDTO ativoUpsertDTO;

  /**
   * Configuração inicial para cada teste. Inicializa o {@link ObjectMapper} para suportar tipos de
   * data/hora do Java 8. Prepara instâncias de {@link AtivoResponseDTO} e {@link AtivoUpsertDTO}
   * com dados padrão para serem usados nos testes.
   */
  @BeforeEach
  void setup() {
    objectMapper.registerModule(new JavaTimeModule());

    ativoResponseDTO =
        AtivoResponseDTO.builder()
            .id(1L)
            .nome("PETR4")
            .descricao("Petrobras PN")
            .status(StatusAtivo.DISPONIVEL)
            .valor(BigDecimal.valueOf(30.50))
            .tipo(AtivoTipo.ACAO)
            .build();

    ativoUpsertDTO =
        AtivoUpsertDTO.builder()
            .nome("PETR4")
            .descricao("Petrobras PN")
            .status(StatusAtivo.DISPONIVEL)
            .valor(BigDecimal.valueOf(30.50))
            .tipo(AtivoTipo.ACAO)
            .build();
  }

  /**
   * Limpeza após cada teste. Reseta o mock do {@link AtivoService} para garantir que cada teste
   * comece com um mock limpo e sem interações anteriores.
   */
  @AfterEach
  void tearDown() {
    Mockito.reset(ativoService);
  }

  /**
   * Conjunto de testes para a operação de criação de ativo (POST /ativos). Cobre cenários de
   * sucesso para diferentes tipos de ativo e falhas devido a validações de entrada (campos nulos ou
   * vazios).
   */
  @Nested
  @DisplayName("Testes de Criação de Ativo (POST /ativos)")
  class CriarAtivoTests {

    /**
     * Testa a criação bem-sucedida de um ativo do tipo Ação. Verifica se o status HTTP é 201
     * Created e se os dados retornados estão corretos.
     */
    @Test
    @DisplayName(
        "1. Sucesso: Criar um ativo válido (Ação) com todos os campos preenchidos corretamente")
    void quandoCriarAtivoValidoAcaoRetornaSucesso() throws Exception {
      // Configura o mock do serviço para retornar um DTO de resposta quando o método
      // 'criar' for
      // chamado
      Mockito.when(ativoService.criar(Mockito.any(AtivoUpsertDTO.class)))
          .thenReturn(ativoResponseDTO);

      // Executa a requisição POST para /ativos com o DTO de criação e o código de
      // acesso de admin
      driver
          .perform(
              post(URI_ATIVOS)
                  .param(
                      "codigoAcesso",
                      "admin@123") // Supondo um código de acesso de admin para autenticação
                  .contentType(
                      MediaType
                          .APPLICATION_JSON) // Define o tipo de conteúdo da requisição como JSON
                  .content(
                      objectMapper.writeValueAsString(
                          ativoUpsertDTO))) // Converte o DTO para JSON e o define como corpo da
          // requisição
          .andExpect(status().isCreated()) // Espera um status HTTP 201 Created
          .andExpect(
              jsonPath("$.nome")
                  .value("PETR4")) // Verifica se o campo 'nome' no JSON de resposta é 'PETR4'
          .andExpect(
              jsonPath("$.tipo")
                  .value("ACAO")) // Verifica se o campo 'tipo' no JSON de resposta é 'ACAO'
          .andDo(print()); // Imprime os detalhes da requisição e resposta no console (útil para
      // depuração)

      // Verifica se o método 'criar' do serviço foi chamado exatamente uma vez com
      // qualquer DTO de
      // criação
      Mockito.verify(ativoService, Mockito.times(1)).criar(Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a criação bem-sucedida de um ativo do tipo Cripto. Similar ao teste de Ação, mas com
     * dados específicos para Cripto.
     */
    @Test
    @DisplayName(
        "2. Sucesso: Criar um ativo válido (Cripto) com todos os campos preenchidos corretamente")
    void quandoCriarAtivoValidoCriptoRetornaSucesso() throws Exception {
      // Cria DTOs específicos para o tipo Cripto
      AtivoUpsertDTO criptoUpsertDTO =
          ativoUpsertDTO.toBuilder()
              .tipo(AtivoTipo.CRIPTO)
              .nome("BTC")
              .descricao("Bitcoin")
              .build();
      AtivoResponseDTO criptoResponseDTO =
          ativoResponseDTO.toBuilder()
              .tipo(AtivoTipo.CRIPTO)
              .nome("BTC")
              .descricao("Bitcoin")
              .build();

      // Configura o mock do serviço para retornar o DTO de resposta de Cripto
      Mockito.when(ativoService.criar(Mockito.any(AtivoUpsertDTO.class)))
          .thenReturn(criptoResponseDTO);

      // Executa a requisição e verifica o status e os campos do JSON de resposta
      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(criptoUpsertDTO)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.nome").value("BTC"))
          .andExpect(jsonPath("$.tipo").value("CRIPTO"))
          .andDo(print());

      // Verifica a chamada ao serviço
      Mockito.verify(ativoService, Mockito.times(1)).criar(Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a criação bem-sucedida de um ativo do tipo Tesouro. Similar aos testes anteriores, mas
     * com dados específicos para Tesouro.
     */
    @Test
    @DisplayName(
        "3. Sucesso: Criar um ativo válido (Tesouro) com todos os campos preenchidos corretamente")
    void quandoCriarAtivoValidoTesouroRetornaSucesso() throws Exception {
      // Cria DTOs específicos para o tipo Tesouro
      AtivoUpsertDTO tesouroUpsertDTO =
          ativoUpsertDTO.toBuilder()
              .tipo(AtivoTipo.TESOURO)
              .nome("Tesouro Selic")
              .descricao("Tesouro Direto Selic")
              .build();
      AtivoResponseDTO tesouroResponseDTO =
          ativoResponseDTO.toBuilder()
              .tipo(AtivoTipo.TESOURO)
              .nome("Tesouro Selic")
              .descricao("Tesouro Direto Selic")
              .build();

      // Configura o mock do serviço para retornar o DTO de resposta de Tesouro
      Mockito.when(ativoService.criar(Mockito.any(AtivoUpsertDTO.class)))
          .thenReturn(tesouroResponseDTO);

      // Executa a requisição e verifica o status e os campos do JSON de resposta
      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(tesouroUpsertDTO)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.nome").value("Tesouro Selic"))
          .andExpect(jsonPath("$.tipo").value("TESOURO"))
          .andDo(print());

      // Verifica a chamada ao serviço
      Mockito.verify(ativoService, Mockito.times(1)).criar(Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na criação de um ativo quando o nome é nulo. Espera um status HTTP 400 Bad
     * Request devido à validação {@code @NotBlank}. Verifica que o método 'criar' do serviço não
     * foi chamado.
     */
    @Test
    @DisplayName("4. Falha: Tentar criar um ativo com nome nulo")
    void quandoCriarAtivoComNomeNuloRetornaBadRequest() throws Exception {
      ativoUpsertDTO.setNome(null);

      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      // Verifica que o método 'criar' do serviço NUNCA foi chamado, pois a validação
      // deve ocorrer
      // antes
      Mockito.verify(ativoService, Mockito.never()).criar(Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na criação de um ativo quando o nome é vazio. Espera um status HTTP 400 Bad
     * Request devido à validação {@code @NotBlank}.
     */
    @Test
    @DisplayName("5. Falha: Tentar criar um ativo com nome vazio")
    void quandoCriarAtivoComNomeVazioRetornaBadRequest() throws Exception {
      ativoUpsertDTO.setNome("");

      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never()).criar(Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na criação de um ativo quando a descrição é nula. Espera um status HTTP 400 Bad
     * Request.
     */
    @Test
    @DisplayName("6. Falha: Tentar criar um ativo com descrição nula")
    void quandoCriarAtivoComDescricaoNulaRetornaBadRequest() throws Exception {
      ativoUpsertDTO.setDescricao(null);

      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never()).criar(Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na criação de um ativo quando a descrição é vazia. Espera um status HTTP 400
     * Bad Request.
     */
    @Test
    @DisplayName("7. Falha: Tentar criar um ativo com descrição vazia")
    void quandoCriarAtivoComDescricaoVaziaRetornaBadRequest() throws Exception {
      ativoUpsertDTO.setDescricao("");

      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never()).criar(Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na criação de um ativo quando o status é nulo. Espera um status HTTP 400 Bad
     * Request.
     */
    @Test
    @DisplayName("8. Falha: Tentar criar um ativo com status nulo")
    void quandoCriarAtivoComStatusNuloRetornaBadRequest() throws Exception {
      ativoUpsertDTO.setStatus(null);

      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never()).criar(Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na criação de um ativo quando o valor é nulo. Espera um status HTTP 400 Bad
     * Request.
     */
    @Test
    @DisplayName("9. Falha: Tentar criar um ativo com valor nulo")
    void quandoCriarAtivoComValorNuloRetornaBadRequest() throws Exception {
      ativoUpsertDTO.setValor(null);

      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never()).criar(Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na criação de um ativo quando o tipo é nulo. Espera um status HTTP 400 Bad
     * Request.
     */
    @Test
    @DisplayName("10. Falha: Tentar criar um ativo com tipo nulo")
    void quandoCriarAtivoComTipoNuloRetornaBadRequest() throws Exception {
      ativoUpsertDTO.setTipo(null);

      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never()).criar(Mockito.any(AtivoUpsertDTO.class));
    }
  }

  /**
   * Conjunto de testes para a operação de atualização de ativo (PUT /ativos/{id}). Cobre cenários
   * de sucesso para atualização parcial e completa, e falhas devido a ativo não encontrado,
   * validações de entrada e regras de negócio.
   */
  @Nested
  @DisplayName("Testes de Atualização de Ativo (PUT /ativos/{id})")
  class AtualizarAtivoTests {

    /**
     * Testa a atualização bem-sucedida de todos os campos de um ativo existente. Verifica se o
     * status HTTP é 200 OK e se o nome do ativo foi atualizado corretamente.
     */
    @Test
    @DisplayName("1. Sucesso: Atualizar todos os campos de um ativo existente com dados válidos")
    void quandoAtualizarAtivoValidoRetornaSucesso() throws Exception {
      // Configura o mock do serviço para retornar o DTO de resposta atualizado
      Mockito.when(ativoService.atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class)))
          .thenReturn(ativoResponseDTO);

      // Executa a requisição PUT e verifica o status e o nome no JSON de resposta
      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("PETR4"))
          .andDo(print());

      // Verifica a chamada ao serviço
      Mockito.verify(ativoService, Mockito.times(1))
          .atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a atualização apenas do nome de um ativo. Garante que outros campos não alterados não
     * causem problemas.
     */
    @Test
    @DisplayName("2. Sucesso: Atualizar apenas o nome de um ativo")
    void quandoAtualizarApenasNomeAtivoRetornaSucesso() throws Exception {
      // Prepara o DTO com o novo nome e outros campos inalterados
      ativoUpsertDTO.setDescricao("Nova Descrição");
      ativoUpsertDTO.setStatus(StatusAtivo.INDISPONIVEL);
      ativoUpsertDTO.setValor(BigDecimal.valueOf(40.00));
      ativoUpsertDTO.setTipo(AtivoTipo.ACAO);

      AtivoResponseDTO updatedResponseDTO =
          ativoResponseDTO.toBuilder().nome("PETR4_UPDATED").build();
      Mockito.when(ativoService.atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class)))
          .thenReturn(updatedResponseDTO);

      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("PETR4_UPDATED"))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1))
          .atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class));
    }

    /** Testa a atualização apenas da descrição de um ativo. */
    @Test
    @DisplayName("3. Sucesso: Atualizar apenas a descrição de um ativo")
    void quandoAtualizarApenasDescricaoAtivoRetornaSucesso() throws Exception {
      ativoUpsertDTO.setNome("PETR4");
      ativoUpsertDTO.setStatus(StatusAtivo.INDISPONIVEL);
      ativoUpsertDTO.setValor(BigDecimal.valueOf(40.00));
      ativoUpsertDTO.setTipo(AtivoTipo.ACAO);

      AtivoResponseDTO updatedResponseDTO =
          ativoResponseDTO.toBuilder().descricao("Nova Descrição").build();
      Mockito.when(ativoService.atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class)))
          .thenReturn(updatedResponseDTO);

      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.descricao").value("Nova Descrição"))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1))
          .atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class));
    }

    /** Testa a atualização apenas do status de um ativo. */
    @Test
    @DisplayName("4. Sucesso: Atualizar apenas o status de um ativo")
    void quandoAtualizarApenasStatusAtivoRetornaSucesso() throws Exception {
      ativoUpsertDTO.setNome("PETR4");
      ativoUpsertDTO.setDescricao("Petrobras PN");
      ativoUpsertDTO.setValor(BigDecimal.valueOf(40.00));
      ativoUpsertDTO.setTipo(AtivoTipo.ACAO);

      AtivoResponseDTO updatedResponseDTO =
          ativoResponseDTO.toBuilder().status(StatusAtivo.INDISPONIVEL).build();
      Mockito.when(ativoService.atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class)))
          .thenReturn(updatedResponseDTO);

      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("INDISPONIVEL"))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1))
          .atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class));
    }

    /** Testa a atualização apenas do valor de um ativo. */
    @Test
    @DisplayName("5. Sucesso: Atualizar apenas o valor de um ativo")
    void quandoAtualizarApenasValorAtivoRetornaSucesso() throws Exception {
      ativoUpsertDTO.setNome("PETR4");
      ativoUpsertDTO.setDescricao("Petrobras PN");
      ativoUpsertDTO.setStatus(StatusAtivo.DISPONIVEL);
      ativoUpsertDTO.setTipo(AtivoTipo.ACAO);

      AtivoResponseDTO updatedResponseDTO =
          ativoResponseDTO.toBuilder().valor(BigDecimal.valueOf(50.00)).build();
      Mockito.when(ativoService.atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class)))
          .thenReturn(updatedResponseDTO);

      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.valor").value(50.00))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1))
          .atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na atualização de um ativo que não existe. Espera um status HTTP 404 Not Found,
     * simulando a exceção {@code ATIVO_NAO_ENCONTRADO}.
     */
    @Test
    @DisplayName("6. Falha: Tentar atualizar um ativo que não existe (ID inválido)")
    void quandoAtualizarAtivoInexistenteRetornaNotFound() throws Exception {
      // Configura o mock para lançar uma exceção de ativo não encontrado
      Mockito.when(ativoService.atualizar(Mockito.eq(99L), Mockito.any(AtivoUpsertDTO.class)))
          .thenThrow(new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

      driver
          .perform(
              put(URI_ATIVOS + "/99")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isNotFound())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1))
          .atualizar(Mockito.eq(99L), Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na atualização de um ativo quando o nome é nulo. Espera um status HTTP 400 Bad
     * Request.
     */
    @Test
    @DisplayName("7. Falha: Tentar atualizar um ativo com nome nulo")
    void quandoAtualizarAtivoComNomeNuloRetornaBadRequest() throws Exception {
      ativoUpsertDTO.setNome(null);

      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never())
          .atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na atualização de um ativo quando a descrição é vazia. Espera um status HTTP
     * 400 Bad Request.
     */
    @Test
    @DisplayName("8. Falha: Tentar atualizar um ativo com descrição vazia")
    void quandoAtualizarAtivoComDescricaoVaziaRetornaBadRequest() throws Exception {
      ativoUpsertDTO.setDescricao("");

      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never())
          .atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na atualização de um ativo quando há tentativa de alterar seu tipo. Espera um
     * status HTTP 400 Bad Request, simulando a exceção {@code ALTERACAO_TIPO_NAO_PERMITIDA}.
     */
    @Test
    @DisplayName(
        "9. Falha: Tentar alterar o tipo do ativo (regra de negócio ALTERACAO_TIPO_NAO_PERMITIDA)")
    void quandoAtualizarAtivoAlterandoTipoRetornaBadRequest() throws Exception {
      ativoUpsertDTO.setTipo(AtivoTipo.CRIPTO);

      Mockito.when(ativoService.atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class)))
          .thenThrow(new CommerceException(ErrorCode.ALTERACAO_TIPO_NAO_PERMITIDA));

      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1))
          .atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na atualização de um ativo quando o valor é negativo. Espera um status HTTP 400
     * Bad Request (assumindo validação no DTO ou controller).
     */
    @Test
    @DisplayName("10. Falha: Tentar atualizar um ativo com valor negativo")
    void quandoAtualizarAtivoComValorNegativoRetornaBadRequest() throws Exception {
      ativoUpsertDTO.setValor(BigDecimal.valueOf(-10.00));

      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never())
          .atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class));
    }
  }

  /**
   * Conjunto de testes para a operação de exclusão de ativo (DELETE /ativos/{id}). Cobre cenários
   * de sucesso e falha por ativo não encontrado ou autenticação.
   */
  @Nested
  @DisplayName("Testes de Exclusão de Ativo (DELETE /ativos/{id})")
  class ExcluirAtivoTests {

    /**
     * Testa a exclusão bem-sucedida de um ativo existente. Espera um status HTTP 204 No Content.
     */
    @Test
    @DisplayName("1. Sucesso: Excluir um ativo existente com sucesso")
    void quandoExcluirAtivoExistenteRetornaNoContent() throws Exception {
      // Configura o mock do serviço para não fazer nada quando 'remover' for chamado
      Mockito.doNothing().when(ativoService).remover(Mockito.eq(1L));

      // Executa a requisição DELETE e verifica o status
      driver
          .perform(delete(URI_ATIVOS + "/1").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNoContent())
          .andDo(print());

      // Verifica a chamada ao serviço
      Mockito.verify(ativoService, Mockito.times(1)).remover(Mockito.eq(1L));
    }

    /**
     * Testa a falha na exclusão de um ativo que não existe. Espera um status HTTP 404 Not Found.
     */
    @Test
    @DisplayName("2. Falha: Tentar excluir um ativo que não existe (ID inválido)")
    void quandoExcluirAtivoInexistenteRetornaNotFound() throws Exception {
      // Configura o mock para lançar exceção de ativo não encontrado
      Mockito.doThrow(new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO))
          .when(ativoService)
          .remover(Mockito.eq(99L));

      driver
          .perform(delete(URI_ATIVOS + "/99").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNotFound())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).remover(Mockito.eq(99L));
    }

    /**
     * Testa a exclusão de um ativo e verifica que ele não pode mais ser buscado. Simula um fluxo de
     * exclusão e posterior tentativa de busca.
     */
    @Test
    @DisplayName("3. Sucesso: Excluir um ativo e verificar que ele não pode mais ser buscado")
    void quandoExcluirAtivoVerificaQueNaoPodeSerBuscado() throws Exception {
      Mockito.doNothing().when(ativoService).remover(Mockito.eq(1L));
      Mockito.when(ativoService.buscarPorId(Mockito.eq(1L)))
          .thenThrow(new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

      driver
          .perform(delete(URI_ATIVOS + "/1").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNoContent())
          .andDo(print());

      driver
          .perform(get(URI_ATIVOS + "/1").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNotFound())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).remover(Mockito.eq(1L));
      Mockito.verify(ativoService, Mockito.times(1)).buscarPorId(Mockito.eq(1L));
    }

    /** Testa a exclusão sequencial de múltiplos ativos. */
    @Test
    @DisplayName("4. Sucesso: Excluir múltiplos ativos sequencialmente")
    void quandoExcluirMultiplosAtivosSequencialmenteRetornaNoContent() throws Exception {
      Mockito.doNothing().when(ativoService).remover(Mockito.anyLong());

      driver
          .perform(delete(URI_ATIVOS + "/1").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNoContent());

      driver
          .perform(delete(URI_ATIVOS + "/2").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNoContent());

      Mockito.verify(ativoService, Mockito.times(1)).remover(Mockito.eq(1L));
      Mockito.verify(ativoService, Mockito.times(1)).remover(Mockito.eq(2L));
    }

    /**
     * Testa a falha ao tentar excluir um ativo que já foi excluído. Simula a exceção {@code
     * ATIVO_NAO_ENCONTRADO} na segunda tentativa.
     */
    @Test
    @DisplayName("5. Falha: Tentar excluir um ativo que já foi excluído")
    void quandoExcluirAtivoJaExcluidoRetornaNotFound() throws Exception {
      Mockito.doNothing()
          .doThrow(new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO))
          .when(ativoService)
          .remover(Mockito.eq(1L));

      // Primeira exclusão com sucesso
      driver
          .perform(delete(URI_ATIVOS + "/1").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNoContent());

      // Segunda exclusão do mesmo ID, agora já excluído
      driver
          .perform(delete(URI_ATIVOS + "/1").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNotFound())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(2)).remover(Mockito.eq(1L));
    }

    /** Testa se a exclusão de um ativo retorna o status HTTP 204 No Content. */
    @Test
    @DisplayName("6. Sucesso: Excluir um ativo e verificar o status HTTP 204 No Content")
    void quandoExcluirAtivoVerificaStatus204() throws Exception {
      Mockito.doNothing().when(ativoService).remover(Mockito.eq(1L));

      driver
          .perform(delete(URI_ATIVOS + "/1").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNoContent())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).remover(Mockito.eq(1L));
    }

    /**
     * Testa a falha na exclusão de um ativo sem autenticação. Espera um status HTTP 401
     * Unauthorized.
     */
    @Test
    @DisplayName("7. Falha: Tentar excluir um ativo sem autenticação")
    void quandoExcluirAtivoSemAutenticacaoRetornaUnauthorized() throws Exception {
      driver.perform(delete(URI_ATIVOS + "/1")).andExpect(status().isUnauthorized()).andDo(print());

      Mockito.verify(ativoService, Mockito.never()).remover(Mockito.anyLong());
    }

    /**
     * Testa a falha na exclusão de um ativo com autenticação inválida. Espera um status HTTP 401
     * Unauthorized.
     */
    @Test
    @DisplayName("8. Falha: Tentar excluir um ativo com autenticação inválida")
    void quandoExcluirAtivoComAutenticacaoInvalidaRetornaUnauthorized() throws Exception {
      driver
          .perform(delete(URI_ATIVOS + "/1").param("codigoAcesso", "invalid_code"))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never()).remover(Mockito.anyLong());
    }
  }

  /**
   * Conjunto de testes para a operação de recuperação de ativo por ID (GET /ativos/{id}). Cobre
   * cenários de sucesso para recuperação e falhas por ativo não encontrado ou autenticação.
   */
  @Nested
  @DisplayName("Testes de Recuperação de Ativo por ID (GET /ativos/{id})")
  class RecuperarAtivoTests {

    /**
     * Testa a recuperação bem-sucedida de um ativo existente por ID. Verifica o status HTTP 200 OK
     * e se o ID e nome do ativo estão corretos.
     */
    @Test
    @DisplayName("1. Sucesso: Recuperar um ativo existente por ID")
    void quandoRecuperarAtivoExistentePorIdRetornaSucesso() throws Exception {
      // Configura o mock do serviço para retornar o DTO de resposta quando
      // 'buscarPorId' for
      // chamado
      Mockito.when(ativoService.buscarPorId(Mockito.eq(1L))).thenReturn(ativoResponseDTO);

      // Executa a requisição GET e verifica o status e os campos do JSON de resposta
      driver
          .perform(get(URI_ATIVOS + "/1").param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(1L))
          .andExpect(jsonPath("$.nome").value("PETR4"))
          .andDo(print());

      // Verifica a chamada ao serviço
      Mockito.verify(ativoService, Mockito.times(1)).buscarPorId(Mockito.eq(1L));
    }

    /**
     * Testa se os dados do ativo recuperado correspondem exatamente aos dados esperados. Realiza
     * uma desserialização completa da resposta JSON para validação detalhada.
     */
    @Test
    @DisplayName(
        "2. Sucesso: Verificar se os dados do ativo recuperado correspondem aos dados esperados")
    void quandoRecuperarAtivoExistentePorIdVerificaDados() throws Exception {
      Mockito.when(ativoService.buscarPorId(Mockito.eq(1L))).thenReturn(ativoResponseDTO);

      String responseJsonString =
          driver
              .perform(get(URI_ATIVOS + "/1").param("codigoAcesso", "admin@123"))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      AtivoResponseDTO resultado =
          objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

      // Compara cada campo do DTO esperado com o DTO retornado
      assertEquals(ativoResponseDTO.getId(), resultado.getId());
      assertEquals(ativoResponseDTO.getNome(), resultado.getNome());
      assertEquals(ativoResponseDTO.getDescricao(), resultado.getDescricao());
      assertEquals(ativoResponseDTO.getStatus(), resultado.getStatus());
      assertEquals(ativoResponseDTO.getValor(), resultado.getValor());
      assertEquals(ativoResponseDTO.getTipo(), resultado.getTipo());

      Mockito.verify(ativoService, Mockito.times(1)).buscarPorId(Mockito.eq(1L));
    }

    /**
     * Testa a falha na recuperação de um ativo que não existe. Espera um status HTTP 404 Not Found.
     */
    @Test
    @DisplayName("3. Falha: Tentar recuperar um ativo que não existe (ID inválido)")
    void quandoRecuperarAtivoInexistenteRetornaNotFound() throws Exception {
      Mockito.when(ativoService.buscarPorId(Mockito.eq(99L)))
          .thenThrow(new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

      driver
          .perform(get(URI_ATIVOS + "/99").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNotFound())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).buscarPorId(Mockito.eq(99L));
    }

    /**
     * Testa a recuperação de um ativo recém-criado. Simula o fluxo de criação e posterior busca.
     */
    @Test
    @DisplayName("4. Sucesso: Recuperar um ativo recém-criado")
    void quandoRecuperarAtivoRecemCriadoRetornaSucesso() throws Exception {
      AtivoResponseDTO novoAtivo = ativoResponseDTO.toBuilder().id(2L).nome("NOVO_ATIVO").build();
      Mockito.when(ativoService.criar(Mockito.any(AtivoUpsertDTO.class))).thenReturn(novoAtivo);
      Mockito.when(ativoService.buscarPorId(Mockito.eq(2L))).thenReturn(novoAtivo);

      // Simula a criação do ativo
      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      objectMapper.writeValueAsString(
                          ativoUpsertDTO.toBuilder().nome("NOVO_ATIVO").build())))
          .andExpect(status().isCreated());

      // Simula a busca pelo ativo recém-criado
      driver
          .perform(get(URI_ATIVOS + "/2").param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("NOVO_ATIVO"))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).criar(Mockito.any(AtivoUpsertDTO.class));
      Mockito.verify(ativoService, Mockito.times(1)).buscarPorId(Mockito.eq(2L));
    }

    /**
     * Testa a recuperação de um ativo após sua atualização. Simula o fluxo de atualização e
     * posterior busca.
     */
    @Test
    @DisplayName("5. Sucesso: Recuperar um ativo após sua atualização")
    void quandoRecuperarAtivoAposAtualizacaoRetornaSucesso() throws Exception {
      AtivoResponseDTO ativoAtualizado = ativoResponseDTO.toBuilder().nome("PETR4_UPDATED").build();
      Mockito.when(ativoService.atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class)))
          .thenReturn(ativoAtualizado);
      Mockito.when(ativoService.buscarPorId(Mockito.eq(1L))).thenReturn(ativoAtualizado);

      // Simula a atualização do ativo
      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      objectMapper.writeValueAsString(
                          ativoUpsertDTO.toBuilder().nome("PETR4_UPDATED").build())))
          .andExpect(status().isOk());

      // Simula a busca pelo ativo atualizado
      driver
          .perform(get(URI_ATIVOS + "/1").param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("PETR4_UPDATED"))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1))
          .atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class));
      Mockito.verify(ativoService, Mockito.times(1)).buscarPorId(Mockito.eq(1L));
    }

    /**
     * Testa a falha na recuperação de um ativo que foi excluído. Simula o fluxo de exclusão e
     * posterior tentativa de busca.
     */
    @Test
    @DisplayName("6. Falha: Tentar recuperar um ativo excluído")
    void quandoRecuperarAtivoExcluidoRetornaNotFound() throws Exception {
      Mockito.doNothing().when(ativoService).remover(Mockito.eq(1L));
      Mockito.when(ativoService.buscarPorId(Mockito.eq(1L)))
          .thenThrow(new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

      // Simula a exclusão do ativo
      driver
          .perform(delete(URI_ATIVOS + "/1").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNoContent());

      // Simula a busca pelo ativo excluído
      driver
          .perform(get(URI_ATIVOS + "/1").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNotFound())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).remover(Mockito.eq(1L));
      Mockito.verify(ativoService, Mockito.times(1)).buscarPorId(Mockito.eq(1L));
    }

    /**
     * Testa a falha na recuperação de um ativo sem autenticação. Espera um status HTTP 401
     * Unauthorized.
     */
    @Test
    @DisplayName("7. Falha: Tentar recuperar um ativo sem autenticação")
    void quandoRecuperarAtivoSemAutenticacaoRetornaUnauthorized() throws Exception {
      driver.perform(get(URI_ATIVOS + "/1")).andExpect(status().isUnauthorized()).andDo(print());

      Mockito.verify(ativoService, Mockito.never()).buscarPorId(Mockito.anyLong());
    }

    /**
     * Testa a falha na recuperação de um ativo com autenticação inválida. Espera um status HTTP 401
     * Unauthorized.
     */
    @Test
    @DisplayName("8. Falha: Tentar recuperar um ativo com autenticação inválida")
    void quandoRecuperarAtivoComAutenticacaoInvalidaRetornaUnauthorized() throws Exception {
      driver
          .perform(get(URI_ATIVOS + "/1").param("codigoAcesso", "invalid_code"))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never()).buscarPorId(Mockito.anyLong());
    }
  }

  /**
   * Conjunto de testes para a operação de listagem de ativos (GET /ativos). Cobre cenários de
   * sucesso para listas vazias e com múltiplos itens, e falhas relacionadas à autenticação.
   */
  @Nested
  @DisplayName("Testes de Listagem de Ativos (GET /ativos)")
  @Transactional
  class ListarAtivosTests {

    /**
     * Testa a listagem bem-sucedida de múltiplos ativos. Verifica o status HTTP 200 OK e o número e
     * nomes dos ativos na lista.
     */
    @Test
    @DisplayName("1. Sucesso: Listar ativos quando há múltiplos ativos cadastrados")
    void quandoListarMultiplosAtivosRetornaSucesso() throws Exception {
      // Prepara uma lista de DTOs de resposta mockados
      AtivoResponseDTO ativo2 =
          ativoResponseDTO.toBuilder().id(2L).nome("VALE3").tipo(AtivoTipo.ACAO).build();
      AtivoResponseDTO ativo3 =
          ativoResponseDTO.toBuilder().id(3L).nome("GOOGL34").tipo(AtivoTipo.ACAO).build();
      List<AtivoResponseDTO> ativos = Arrays.asList(ativoResponseDTO, ativo2, ativo3);

      // Configura o mock do serviço para retornar a lista de ativos
      Mockito.when(ativoService.listar(Mockito.any(Usuario.class))).thenReturn(ativos);

      // Executa a requisição GET e verifica o status, tamanho da lista e nomes dos
      // ativos
      driver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[0].nome").value("PETR4"))
          .andExpect(jsonPath("$[1].nome").value("VALE3"))
          .andExpect(jsonPath("$[2].nome").value("GOOGL34"))
          .andDo(print());

      // Verifica a chamada ao serviço
      Mockito.verify(ativoService, Mockito.times(1)).listar(Mockito.any(Usuario.class));
    }

    /**
     * Testa a listagem de ativos quando não há nenhum ativo cadastrado. Espera uma lista vazia e
     * status HTTP 200 OK.
     */
    @Test
    @DisplayName("2. Sucesso: Listar ativos quando não há nenhum ativo cadastrado (lista vazia)")
    void quandoListarAtivosSemAtivosCadastradosRetornaListaVazia() throws Exception {
      Mockito.when(ativoService.listar(Mockito.any(Usuario.class)))
          .thenReturn(Collections.emptyList());

      driver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).listar(Mockito.any(Usuario.class));
    }

    /** Testa se o número de ativos retornados na lista está correto. */
    @Test
    @DisplayName("3. Sucesso: Verificar o número correto de ativos retornados na lista")
    void quandoListarAtivosVerificaNumeroCorreto() throws Exception {
      AtivoResponseDTO ativo2 =
          ativoResponseDTO.toBuilder().id(2L).nome("VALE3").tipo(AtivoTipo.ACAO).build();
      List<AtivoResponseDTO> ativos = Arrays.asList(ativoResponseDTO, ativo2);

      Mockito.when(ativoService.listar(Mockito.any(Usuario.class))).thenReturn(ativos);

      driver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).listar(Mockito.any(Usuario.class));
    }

    /**
     * Testa se os dados dos ativos na lista estão corretos, realizando uma desserialização
     * completa.
     */
    @Test
    @DisplayName("4. Sucesso: Verificar se os dados dos ativos na lista estão corretos")
    void quandoListarAtivosVerificaDadosCorretos() throws Exception {
      AtivoResponseDTO ativo2 =
          ativoResponseDTO.toBuilder()
              .id(2L)
              .nome("VALE3")
              .descricao("Vale S.A.")
              .status(StatusAtivo.DISPONIVEL)
              .valor(BigDecimal.valueOf(70.00))
              .tipo(AtivoTipo.ACAO)
              .build();
      List<AtivoResponseDTO> ativos = Arrays.asList(ativoResponseDTO, ativo2);

      Mockito.when(ativoService.listar(Mockito.any(Usuario.class))).thenReturn(ativos);

      String responseJsonString =
          driver
              .perform(get(URI_ATIVOS).param("codigoAcesso", "admin@123"))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      // Desserializa a resposta para uma lista de AtivoResponseDTOs
      List<AtivoResponseDTO> resultados =
          objectMapper.readValue(
              responseJsonString,
              objectMapper
                  .getTypeFactory()
                  .constructCollectionType(List.class, AtivoResponseDTO.class));

      assertEquals(2, resultados.size());
      assertEquals(ativoResponseDTO.getNome(), resultados.get(0).getNome());
      assertEquals(ativo2.getNome(), resultados.get(1).getNome());

      Mockito.verify(ativoService, Mockito.times(1)).listar(Mockito.any(Usuario.class));
    }

    /**
     * Testa a listagem de ativos após a criação de um novo ativo. Simula a criação e verifica se o
     * novo ativo aparece na lista.
     */
    @Test
    @DisplayName("5. Sucesso: Listar ativos após a criação de um novo ativo")
    void quandoListarAtivosAposCriacaoRetornaSucesso() throws Exception {
      AtivoResponseDTO novoAtivo =
          ativoResponseDTO.toBuilder().id(4L).nome("NOVO_ATIVO_LIST").build();

      List<AtivoResponseDTO> ativosDepois = Arrays.asList(ativoResponseDTO, novoAtivo);

      Mockito.when(ativoService.criar(Mockito.any(AtivoUpsertDTO.class))).thenReturn(novoAtivo);

      Mockito.when(ativoService.listar(Mockito.any(Usuario.class)))
          .thenReturn(ativosDepois); // sempre retorna a lista atualizada

      // Criação do ativo
      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      objectMapper.writeValueAsString(
                          ativoUpsertDTO.toBuilder().nome("NOVO_ATIVO_LIST").build())))
          .andExpect(status().isCreated());

      // Listagem após criação
      driver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[1].nome").value("NOVO_ATIVO_LIST"))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).criar(Mockito.any(AtivoUpsertDTO.class));
      Mockito.verify(ativoService, Mockito.times(1)).listar(Mockito.any(Usuario.class));
    }

    /**
     * Testa a listagem de ativos após a exclusão de um ativo. Simula a exclusão e verifica se o
     * ativo removido não aparece mais na lista.
     */
    @Test
    @DisplayName("6. Sucesso: Listar ativos após a exclusão de um ativo")
    void quandoListarAtivosAposExclusaoRetornaSucesso() throws Exception {
      List<AtivoResponseDTO> ativosDepois = Collections.singletonList(ativoResponseDTO);

      Mockito.doNothing().when(ativoService).remover(Mockito.eq(2L));

      // Após a exclusão, a lista já está "atualizada"
      Mockito.when(ativoService.listar(Mockito.any(Usuario.class))).thenReturn(ativosDepois);

      // Simula a exclusão de um ativo
      driver
          .perform(delete(URI_ATIVOS + "/2").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNoContent());

      // Listagem após exclusão
      driver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].nome").value("PETR4"))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).remover(Mockito.eq(2L));
      Mockito.verify(ativoService, Mockito.times(1)).listar(Mockito.any(Usuario.class));
    }

    /**
     * Testa a listagem de ativos após a atualização de um ativo. Simula a atualização e verifica se
     * os dados atualizados aparecem na lista.
     */
    @Test
    @DisplayName("7. Sucesso: Listar ativos após a atualização de um ativo")
    void quandoListarAtivosAposAtualizacaoRetornaSucesso() throws Exception {
      AtivoResponseDTO ativoAtualizado =
          ativoResponseDTO.toBuilder().nome("PETR4_UPDATED_LIST").build();

      List<AtivoResponseDTO> ativosDepois = Collections.singletonList(ativoAtualizado);

      Mockito.when(ativoService.atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class)))
          .thenReturn(ativoAtualizado);

      Mockito.when(ativoService.listar(Mockito.any(Usuario.class)))
          .thenReturn(ativosDepois); // sempre retorna a versão atualizada

      // Simula a atualização de um ativo
      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      objectMapper.writeValueAsString(
                          ativoUpsertDTO.toBuilder().nome("PETR4_UPDATED_LIST").build())))
          .andExpect(status().isOk());

      // Listagem após atualização
      driver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].nome").value("PETR4_UPDATED_LIST"))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1))
          .atualizar(Mockito.eq(1L), Mockito.any(AtivoUpsertDTO.class));
      Mockito.verify(ativoService, Mockito.times(1)).listar(Mockito.any(Usuario.class));
    }

    /**
     * Testa a falha na listagem de ativos sem autenticação. Espera um status HTTP 401 Unauthorized.
     */
    @Test
    @DisplayName("8. Falha: Tentar listar ativos sem autenticação")
    void quandoListarAtivosSemAutenticacaoRetornaUnauthorized() throws Exception {
      driver.perform(get(URI_ATIVOS)).andExpect(status().isUnauthorized()).andDo(print());

      Mockito.verify(ativoService, Mockito.never()).listar(Mockito.any(Usuario.class));
    }

    /**
     * Testa a falha na listagem de ativos com autenticação inválida. Espera um status HTTP 401
     * Unauthorized.
     */
    @Test
    @DisplayName("9. Falha: Tentar listar ativos com autenticação inválida")
    void quandoListarAtivosComAutenticacaoInvalidaRetornaUnauthorized() throws Exception {
      driver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "invalid_code"))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never()).listar(Mockito.any(Usuario.class));
    }

    /**
     * Testa a ordem dos ativos retornados na listagem. Assume que a ordem é a mesma da lista
     * mockada para este teste.
     */
    @Test
    @DisplayName(
        "10. Sucesso: Verificar a ordem dos ativos retornados (se houver uma ordem definida)")
    void quandoListarAtivosVerificaOrdemDefinida() throws Exception {
      AtivoResponseDTO ativo2 =
          ativoResponseDTO.toBuilder().id(2L).nome("AAPL34").tipo(AtivoTipo.ACAO).build();
      AtivoResponseDTO ativo3 =
          ativoResponseDTO.toBuilder().id(3L).nome("MSFT34").tipo(AtivoTipo.ACAO).build();
      List<AtivoResponseDTO> ativosOrdenados = Arrays.asList(ativoResponseDTO, ativo2, ativo3);

      Mockito.when(ativoService.listar(Mockito.any(Usuario.class))).thenReturn(ativosOrdenados);

      driver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].nome").value("PETR4"))
          .andExpect(jsonPath("$[1].nome").value("AAPL34"))
          .andExpect(jsonPath("$[2].nome").value("MSFT34"))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).listar(Mockito.any(Usuario.class));
    }

    /** Testa a visualização de ativos por plano mockada para este teste. */
    @Test
    @DisplayName("11. Sucesso: Verificar a visualização de ativos por plano")
    void quandoListarAtivosVerificaVisualizacaoPorPlano() throws Exception {
      var ativos =
          Arrays.asList(
              ativoResponseDTO.toBuilder().id(1L).nome("CDB").tipo(AtivoTipo.TESOURO).build());

      Mockito.when(ativoService.listar(Mockito.any(Usuario.class))).thenReturn(ativos);

      var cliente =
          clienteRepository.save(
              Cliente.builder()
                  .nome("Cliente Um da Silva")
                  .plano(PlanoEnum.NORMAL)
                  .endereco("Rua dos Testes, 123")
                  .codigoAcesso("123456")
                  .build());

      driver
          .perform(
              get(URI_ATIVOS)
                  .param("userId", String.valueOf(cliente.getId()))
                  .param("codigoAcesso", cliente.getCodigoAcesso()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].nome").value("CDB"))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).listar(Mockito.any(Usuario.class));
    }
  }

  /**
   * Conjunto de testes para casos variantes e fluxos de integração. Inclui testes para nomes
   * duplicados, valores fora do limite, e testes de autenticação.
   */
  @Nested
  @DisplayName("Casos Variantes")
  class CasosVariantesTests {

    /**
     * Testa a falha na criação de um ativo com um nome que já existe. Simula uma exceção de negócio
     * para nome duplicado.
     */
    @Test
    @DisplayName("1. Criação: Tentar criar um ativo com um nome que já existe")
    void quandoCriarAtivoComNomeExistenteRetornaBadRequest() throws Exception {
      Mockito.when(ativoService.criar(Mockito.any(AtivoUpsertDTO.class)))
          .thenThrow(new CommerceException(ErrorCode.ATIVO_JA_EXISTE));

      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isConflict())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).criar(Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa a falha na atualização de um ativo com um valor que excede o limite de precisão/escala.
     * Espera um status HTTP 400 Bad Request, assumindo que a validação ocorre no DTO ou controller.
     */
    @Test
    @DisplayName(
        "2. Atualização: Tentar atualizar um ativo com um valor que excede o limite de precisão/escala")
    void quandoAtualizarAtivoComValorExcedenteRetornaBadRequest() throws Exception {
      // Define um valor muito grande que excederia a precisão/escala do BigDecimal no
      // banco de
      // dados ou validação
      ativoUpsertDTO.setValor(new BigDecimal("12345678901234567890.12345"));

      // A validação deve ocorrer antes de chamar o serviço
      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never())
          .atualizar(Mockito.anyLong(), Mockito.any(AtivoUpsertDTO.class));
    }

    /**
     * Testa o acesso não autorizado a todas as operações CRUD sem fornecer o parâmetro de
     * autenticação. Espera um status HTTP 401 Unauthorized para cada operação.
     */
    @Test
    @DisplayName(
        "3. Autenticação: Testar o acesso a todas as operações CRUD sem a anotação @Autenticado(TipoAutenticacao.ADMIN)")
    void quandoAcessarOperacoesSemAutenticacaoRetornaUnauthorized() throws Exception {
      // Teste para POST sem código de acesso
      driver
          .perform(
              post(URI_ATIVOS)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Teste para PUT sem código de acesso
      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Teste para DELETE sem código de acesso
      driver.perform(delete(URI_ATIVOS + "/1")).andExpect(status().isUnauthorized()).andDo(print());

      // Teste para GET por ID sem código de acesso
      driver.perform(get(URI_ATIVOS + "/1")).andExpect(status().isUnauthorized()).andDo(print());

      // Teste para GET listar todos sem código de acesso
      driver.perform(get(URI_ATIVOS)).andExpect(status().isUnauthorized()).andDo(print());

      // Verifica que nenhum método do serviço foi chamado, pois a autenticação falhou
      // antes
      Mockito.verify(ativoService, Mockito.never()).criar(Mockito.any(AtivoUpsertDTO.class));
      Mockito.verify(ativoService, Mockito.never())
          .atualizar(Mockito.anyLong(), Mockito.any(AtivoUpsertDTO.class));
      Mockito.verify(ativoService, Mockito.never()).remover(Mockito.anyLong());
      Mockito.verify(ativoService, Mockito.never()).buscarPorId(Mockito.anyLong());
      Mockito.verify(ativoService, Mockito.never()).listar(Mockito.any(Usuario.class));
    }

    /**
     * Testa o acesso não autorizado a todas as operações CRUD com um código de acesso de Admin
     * inválido. Espera um status HTTP 401 Unauthorized para cada operação.
     */
    @Test
    @DisplayName(
        "4. Autenticação: Testar o acesso a todas as operações CRUD com um codigoAcesso inválido para o Admin")
    void quandoAcessarOperacoesComAutenticacaoInvalidaRetornaUnauthorized() throws Exception {
      // Teste para POST com código de acesso inválido
      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "invalid_admin_code")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Teste para PUT com código de acesso inválido
      driver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "invalid_admin_code")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Teste para DELETE com código de acesso inválido
      driver
          .perform(delete(URI_ATIVOS + "/1").param("codigoAcesso", "invalid_admin_code"))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Teste para GET por ID com código de acesso inválido
      driver
          .perform(get(URI_ATIVOS + "/1").param("codigoAcesso", "invalid_admin_code"))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Teste para GET listar todos com código de acesso inválido
      driver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "invalid_admin_code"))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Verifica que nenhum método do serviço foi chamado
      Mockito.verify(ativoService, Mockito.never()).criar(Mockito.any(AtivoUpsertDTO.class));
      Mockito.verify(ativoService, Mockito.never())
          .atualizar(Mockito.anyLong(), Mockito.any(AtivoUpsertDTO.class));
      Mockito.verify(ativoService, Mockito.never()).remover(Mockito.anyLong());
      Mockito.verify(ativoService, Mockito.never()).buscarPorId(Mockito.anyLong());
      Mockito.verify(ativoService, Mockito.never()).listar(Mockito.any(Usuario.class));
    }

    /**
     * Testa um fluxo completo de integração: criar, atualizar, recuperar, listar e excluir um
     * ativo. Verifica o comportamento do controller em cada etapa do ciclo de vida do ativo.
     */
    @Test
    @DisplayName(
        "5. Integração: Criar um ativo, atualizá-lo, recuperá-lo, listar todos e depois excluí-lo, verificando o fluxo completo")
    void quandoExecutarFluxoCompletoCRUDRetornaSucesso() throws Exception {
      // DTOs mockados para simular o retorno do serviço em cada etapa
      AtivoResponseDTO createdAtivo =
          ativoResponseDTO.toBuilder().id(10L).nome("ATIVO_FLUXO").build();
      AtivoResponseDTO updatedAtivo = createdAtivo.toBuilder().nome("ATIVO_FLUXO_UPDATED").build();
      List<AtivoResponseDTO> ativosList = Collections.singletonList(updatedAtivo);

      // Configura os mocks para cada operação do serviço
      Mockito.when(ativoService.criar(Mockito.any(AtivoUpsertDTO.class))).thenReturn(createdAtivo);
      Mockito.when(ativoService.atualizar(Mockito.eq(10L), Mockito.any(AtivoUpsertDTO.class)))
          .thenReturn(updatedAtivo);
      Mockito.when(ativoService.buscarPorId(Mockito.eq(10L))).thenReturn(updatedAtivo);
      // Configura listarTodos para retornar a lista com o ativo e depois uma lista
      // vazia após a
      // exclusão
      Mockito.when(ativoService.listar(Mockito.any(Usuario.class)))
          .thenReturn(ativosList)
          .thenReturn(Collections.emptyList());
      Mockito.doNothing().when(ativoService).remover(Mockito.eq(10L));

      // 1. Criar o ativo
      driver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      objectMapper.writeValueAsString(
                          ativoUpsertDTO.toBuilder().nome("ATIVO_FLUXO").build())))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.nome").value("ATIVO_FLUXO"));

      // 2. Atualizar o ativo
      driver
          .perform(
              put(URI_ATIVOS + "/10")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      objectMapper.writeValueAsString(
                          ativoUpsertDTO.toBuilder().nome("ATIVO_FLUXO_UPDATED").build())))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("ATIVO_FLUXO_UPDATED"));

      // 3. Recuperar o ativo atualizado
      driver
          .perform(get(URI_ATIVOS + "/10").param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("ATIVO_FLUXO_UPDATED"));

      // 4. Listar todos os ativos (deve conter o ativo atualizado)
      driver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].nome").value("ATIVO_FLUXO_UPDATED"));

      // 5. Excluir o ativo
      driver
          .perform(delete(URI_ATIVOS + "/10").param("codigoAcesso", "admin@123"))
          .andExpect(status().isNoContent());

      // 6. Listar todos os ativos novamente (deve estar vazio após a exclusão)
      driver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "admin@123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0));

      // Verifica se todos os métodos do serviço foram chamados o número esperado de
      // vezes
      Mockito.verify(ativoService, Mockito.times(1)).criar(Mockito.any(AtivoUpsertDTO.class));
      Mockito.verify(ativoService, Mockito.times(1))
          .atualizar(Mockito.eq(10L), Mockito.any(AtivoUpsertDTO.class));
      Mockito.verify(ativoService, Mockito.times(1)).buscarPorId(Mockito.eq(10L));
      Mockito.verify(ativoService, Mockito.times(2))
          .listar(
              Mockito.any(
                  Usuario.class)); // Uma vez para a lista com o ativo, outra para a lista vazia
      Mockito.verify(ativoService, Mockito.times(1)).remover(Mockito.eq(10L));
    }
  }

  @Nested
  @DisplayName("Alterar status do ativo")
  class AlterarStatusTests {

    private static final String URI_STATUS = "/ativos/{id}/status";

    @Test
    @DisplayName("Altera status com sucesso e retorna 200 OK")
    void alterarStatusComSucesso() throws Exception {
      Long id = 1L;

      AlterarStatusDTO dto = new AlterarStatusDTO();
      dto.setNovoStatus(StatusAtivo.INDISPONIVEL);

      Ativo ativoMock =
          Acao.builder()
              .id(id)
              .nome("Ativo Teste")
              .descricao("Descrição Teste")
              .status(StatusAtivo.INDISPONIVEL)
              .valor(new BigDecimal("100.00"))
              .build();

      AtivoResponseDTO responseDTO = new AtivoResponseDTO(ativoMock);

      Mockito.when(ativoService.alterarStatus(id, dto.getNovoStatus())).thenReturn(responseDTO);

      driver
          .perform(
              put(URI_STATUS, id)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id))
          .andExpect(jsonPath("$.status").value("INDISPONIVEL"))
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).alterarStatus(id, dto.getNovoStatus());
    }

    @Test
    @DisplayName("Tenta alterar status de ativo inexistente, retorna 404")
    void alterarStatusAtivoNaoEncontrado() throws Exception {
      Long id = 999L;
      AlterarStatusDTO dto = new AlterarStatusDTO();
      dto.setNovoStatus(StatusAtivo.DISPONIVEL);

      Mockito.when(ativoService.alterarStatus(id, dto.getNovoStatus()))
          .thenThrow(new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

      driver
          .perform(
              put(URI_STATUS, id)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isNotFound())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.times(1)).alterarStatus(id, dto.getNovoStatus());
    }

    @Test
    @DisplayName("Tenta alterar status sem autenticação, retorna 401 Unauthorized")
    void alterarStatusSemAutenticacao() throws Exception {
      Long id = 1L;
      AlterarStatusDTO dto = new AlterarStatusDTO();
      dto.setNovoStatus(StatusAtivo.DISPONIVEL);

      driver
          .perform(
              put(URI_STATUS, id)
                  // sem param "codigoAcesso"
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never()).alterarStatus(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @DisplayName("Tenta alterar status com dto inválido, retorna 400 Bad Request")
    void alterarStatusComDtoInvalido() throws Exception {
      Long id = 1L;

      // DTO com novoStatus null - violação @NotNull
      AlterarStatusDTO dto = new AlterarStatusDTO();
      dto.setNovoStatus(null);

      driver
          .perform(
              put(URI_STATUS, id)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isBadRequest())
          .andDo(print());

      Mockito.verify(ativoService, Mockito.never()).alterarStatus(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @DisplayName("Muda de DISPONIVEL para INDISPONIVEL com sucesso")
    void mudaDeDisponivelParaIndisponivel() throws Exception {
      Long id = 1L;

      // DTO com novo status
      AlterarStatusDTO dto = new AlterarStatusDTO();
      dto.setNovoStatus(StatusAtivo.INDISPONIVEL);

      Ativo ativo =
          Acao.builder()
              .id(id)
              .nome("Teste Ativo")
              .descricao("Teste descrição")
              .status(StatusAtivo.INDISPONIVEL)
              .valor(new BigDecimal("200.00"))
              .build();

      AtivoResponseDTO responseDTO = new AtivoResponseDTO(ativo);

      Mockito.when(ativoService.alterarStatus(id, dto.getNovoStatus())).thenReturn(responseDTO);

      driver
          .perform(
              put(URI_STATUS, id)
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id))
          .andExpect(jsonPath("$.status").value("INDISPONIVEL"))
          .andDo(print());

      Mockito.verify(ativoService).alterarStatus(id, dto.getNovoStatus());
    }
  }

  @Nested
  @DisplayName("Testes de Atualização de Ativo (PUT /ativos/{id})")
  class AtualizarCotacaoAtivoTests {

    @Test
    @DisplayName("Testa se Controller funciona para atualizar cotação")
    void quandoAtualizarAtivoValidoRetornaSucesso() throws Exception {
      ValorUpsertDTO valorUpsertDTO =
          ValorUpsertDTO.builder().valor(BigDecimal.valueOf(100)).build();

      AtivoResponseDTO ativoResponse =
          AtivoResponseDTO.builder()
              .nome("Dog")
              .descricao("Coisa")
              .valor(BigDecimal.valueOf(100))
              .status(StatusAtivo.DISPONIVEL)
              .id(1L)
              .tipo(AtivoTipo.CRIPTO)
              .build();

      Mockito.when(ativoService.atualizarCotacao(Mockito.eq(1L), Mockito.any(ValorUpsertDTO.class)))
          .thenReturn(ativoResponse);

      driver
          .perform(
              put(URI_ATIVOS + "/1/cotacao")
                  .param("codigoAcesso", "admin@123")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(valorUpsertDTO)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.valor").value(BigDecimal.valueOf(100)))
          .andDo(print());

      // Verifica a chamada ao serviço
      Mockito.verify(ativoService, Mockito.times(1))
          .atualizarCotacao(Mockito.eq(1L), Mockito.any(ValorUpsertDTO.class));
    }
  }
}
