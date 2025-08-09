package com.ufcg.psoft.commerce.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.*;
import com.ufcg.psoft.commerce.enums.*;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.http.exception.ErrorDTO;
import com.ufcg.psoft.commerce.model.Acao;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Cripto;
import com.ufcg.psoft.commerce.model.Tesouro;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.utils.CustomDriver;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Ativos")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AtivoControllerTests {

  // URI base para os endpoints do AtivoController
  final String URI_ATIVOS = "/ativos";

  // Objeto para simular requisições HTTP para o controller
  @Autowired MockMvc mvcDriver;
  CustomDriver driver;

  // Repositório de clientes
  @Autowired ClienteRepository clienteRepository;

  // Service real para interagir com o banco
  @Autowired AtivoService ativoService;

  // Objeto para serializar e desserializar JSON
  ObjectMapper objectMapper = new ObjectMapper();

  // DTOs de resposta e requisição para uso nos testes
  // TODO: escopar ativoDTO em classe especifica
  AtivoResponseDTO ativoResponseDTO;
  AtivoCreateDTO ativoUpsertDTO;
  AtivoUpdateDTO ativoUpdateDTO;

  /**
   * Configuração inicial para cada teste. Inicializa o {@link ObjectMapper} para suportar tipos de
   * data/hora do Java 8. Prepara instâncias de {@link AtivoResponseDTO} e {@link AtivoCreateDTO}
   * com dados padrão para serem usados nos testes.
   */
  @BeforeEach
  void setup() {
    objectMapper.registerModule(new JavaTimeModule());
    driver = new CustomDriver(mvcDriver, objectMapper);

    ativoResponseDTO =
        AtivoResponseDTO.builder()
            .id(1L)
            .nome("PETR4")
            .descricao("Petrobras PN")
            .status(StatusAtivo.DISPONIVEL)
            .cotacao(BigDecimal.valueOf(30.50))
            .tipo(AtivoTipo.ACAO)
            .build();

    ativoUpsertDTO =
        AtivoCreateDTO.builder()
            .nome("PETR4")
            .descricao("Petrobras PN")
            .status(StatusAtivo.DISPONIVEL)
            .cotacao(BigDecimal.valueOf(30.50))
            .tipo(AtivoTipo.ACAO)
            .build();

    ativoUpdateDTO = AtivoUpdateDTO.builder().nome("PETR4").descricao("Petrobras PN").build();
  }

  /** Limpeza após cada teste. O banco é resetado automaticamente pela anotação @DirtiesContext. */
  @AfterEach
  void tearDown() {
    // O banco é resetado automaticamente após cada teste
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
      // Executa a requisição POST para /ativos com o DTO de criação e o código de
      // acesso de admin
      driver
          .post(URI_ATIVOS, ativoUpsertDTO, Admin.getInstance())
          .andExpect(status().isCreated()) // Espera um status HTTP 201 Created
          .andExpect(
              jsonPath("$.nome")
                  .value("PETR4")) // Verifica se o campo 'nome' no JSON de resposta é 'PETR4'
          .andExpect(
              jsonPath("$.tipo")
                  .value("ACAO")) // Verifica se o campo 'tipo' no JSON de resposta é 'ACAO'
          .andDo(print()); // Imprime os detalhes da requisição e resposta no console (útil para
      // depuração)
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
      AtivoCreateDTO criptoUpsertDTO =
          ativoUpsertDTO.toBuilder()
              .tipo(AtivoTipo.CRIPTO)
              .nome("BTC")
              .descricao("Bitcoin")
              .build();

      // Executa a requisição e verifica o status e os campos do JSON de resposta
      driver
          .post(URI_ATIVOS, criptoUpsertDTO, Admin.getInstance())
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.nome").value("BTC"))
          .andExpect(jsonPath("$.tipo").value("CRIPTO"))
          .andDo(print());
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
      AtivoCreateDTO tesouroUpsertDTO =
          ativoUpsertDTO.toBuilder()
              .tipo(AtivoTipo.TESOURO)
              .nome("Tesouro Selic")
              .descricao("Tesouro Direto Selic")
              .build();

      // Executa a requisição e verifica o status e os campos do JSON de resposta
      driver
          .post(URI_ATIVOS, tesouroUpsertDTO, Admin.getInstance())
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.nome").value("Tesouro Selic"))
          .andExpect(jsonPath("$.tipo").value("TESOURO"))
          .andDo(print());
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
          .post(URI_ATIVOS, ativoUpsertDTO, Admin.getInstance())
          .andExpect(status().isBadRequest())
          .andDo(print());
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
          .post(URI_ATIVOS, ativoUpsertDTO, Admin.getInstance())
          .andExpect(status().isBadRequest())
          .andDo(print());
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
          .post(URI_ATIVOS, ativoUpsertDTO, Admin.getInstance())
          .andExpect(status().isBadRequest())
          .andDo(print());
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
          .post(URI_ATIVOS, ativoUpsertDTO, Admin.getInstance())
          .andExpect(status().isBadRequest())
          .andDo(print());
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
          .post(URI_ATIVOS, ativoUpsertDTO, Admin.getInstance())
          .andExpect(status().isBadRequest())
          .andDo(print());
    }

    /**
     * Testa a falha na criação de um ativo quando o cotacao é nulo. Espera um status HTTP 400 Bad
     * Request.
     */
    @Test
    @DisplayName("9. Falha: Tentar criar um ativo com cotacao nulo")
    void quandoCriarAtivoComCotacaoNuloRetornaBadRequest() throws Exception {
      ativoUpsertDTO.setCotacao(null);

      driver
          .post(URI_ATIVOS, ativoUpsertDTO, Admin.getInstance())
          .andExpect(status().isBadRequest())
          .andDo(print());
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
          .post(URI_ATIVOS, ativoUpsertDTO, Admin.getInstance())
          .andExpect(status().isBadRequest())
          .andDo(print());
    }
  }

  @Test
  @DisplayName(
      "11. Falha: Enviar JSON de Ativo com sintaxe malformada (HttpMessageNotReadableException)")
  void quandoEnviarJsonMalformadoRetornaBadRequest() throws Exception {
    String malformedJson =
        "{\"nome\": \"Teste Ativo\", \"descricao\": \"Descricao Teste\", \"cotacao\": 100.00,"; // JSON incompleto

    mvcDriver
        .perform(
            post(URI_ATIVOS)
                .header(
                    "Authorization",
                    driver.createBasicAuthHeader(
                        String.valueOf(Admin.getInstance().getUserId()),
                        Admin.getInstance().getCodigoAcesso()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("JSON_INVALID"))
        .andExpect(jsonPath("$.message").value("Corpo da requisição inválido ou malformado"))
        .andDo(print());
  }

  @Test
  @DisplayName(
      "12. Falha: Enviar JSON de Ativo com tipo de dado incorreto para campo (HttpMessageNotReadableException)")
  void quandoEnviarJsonComTipoDeDadoIncorretoRetornaBadRequest() throws Exception {
    String invalidTypeJson =
        "{\"nome\": \"Teste Ativo\", \"descricao\": \"Descricao Teste\", \"cotacao\": \"cem\"}"; // 'cotacao' como string

    mvcDriver
        .perform(
            post(URI_ATIVOS)
                .header(
                    "Authorization",
                    driver.createBasicAuthHeader(
                        String.valueOf(Admin.getInstance().getUserId()),
                        Admin.getInstance().getCodigoAcesso()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidTypeJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("JSON_INVALID"))
        .andExpect(jsonPath("$.message").value("Corpo da requisição inválido ou malformado"))
        .andDo(print());
  }

  @Test
  @DisplayName(
      "13. Falha: Parâmetro de rota de Ativo com tipo inválido (MethodArgumentTypeMismatchException)")
  void quandoParametroDeRotaInvalidoRetornaBadRequest() throws Exception {
    driver
        .get(URI_ATIVOS + "/naoNumerico", Admin.getInstance()) // ID não numérico
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("Parâmetro inválido: id"))
        .andDo(print());
  }

  @Test
  @DisplayName("14. Falha: Tentar recuperar um ativo com ID de formato inválido")
  void quandoRecuperarAtivoComIdNegativoRetornaBadRequest() throws Exception {
    driver
        .get(URI_ATIVOS + "/-1hdla", Admin.getInstance())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("Parâmetro inválido: id"))
        .andDo(print());
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
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      // Executa a requisição PUT e verifica o status e o nome no JSON de resposta
      driver
          .put(URI_ATIVOS + "/" + ativoId, ativoUpdateDTO, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("PETR4"))
          .andDo(print());
    }

    /**
     * Testa a atualização apenas do nome de um ativo. Garante que outros campos não alterados não
     * causem problemas.
     */
    @Test
    @DisplayName("2. Sucesso: Atualizar apenas o nome de um ativo")
    void quandoAtualizarApenasNomeAtivoRetornaSucesso() throws Exception {
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      // Prepara o DTO com o novo nome e outros campos inalterados
      AtivoUpdateDTO ativoParaAtualizar =
          ativoUpdateDTO.toBuilder().nome("PETR4_UPDATED").descricao("Nova Descrição").build();

      driver
          .put(URI_ATIVOS + "/" + ativoId, ativoParaAtualizar, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("PETR4_UPDATED"))
          .andDo(print());
    }

    /** Testa a atualização apenas da descrição de um ativo. */
    @Test
    @DisplayName("3. Sucesso: Atualizar apenas a descrição de um ativo")
    void quandoAtualizarApenasDescricaoAtivoRetornaSucesso() throws Exception {
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      AtivoUpdateDTO ativoParaAtualizar =
          ativoUpdateDTO.toBuilder().nome("PETR4").descricao("Nova Descrição").build();

      driver
          .put(URI_ATIVOS + "/" + ativoId, ativoParaAtualizar, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.descricao").value("Nova Descrição"))
          .andDo(print());
    }

    /**
     * Testa a falha na atualização de um ativo que não existe. Espera um status HTTP 404 Not Found,
     * simulando a exceção {@code ATIVO_NAO_ENCONTRADO}.
     */
    @Test
    @DisplayName("4. Falha: Tentar atualizar um ativo que não existe (ID inválido)")
    void quandoAtualizarAtivoInexistenteRetornaNotFound() throws Exception {
      driver
          .put(URI_ATIVOS + "/99", ativoUpsertDTO, Admin.getInstance())
          .andExpect(status().isNotFound())
          .andDo(print());
    }

    /**
     * Testa a falha na atualização de um ativo quando o nome é nulo. Espera um status HTTP 400 Bad
     * Request.
     */
    @Test
    @DisplayName("5. Falha: Tentar atualizar um ativo com nome nulo")
    void quandoAtualizarAtivoComNomeNuloRetornaBadRequest() throws Exception {
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      ativoUpsertDTO.setNome(null);

      driver
          .put(URI_ATIVOS + "/" + ativoId, ativoUpsertDTO, Admin.getInstance())
          .andExpect(status().isBadRequest())
          .andDo(print());
    }

    /**
     * Testa a falha na atualização de um ativo quando a descrição é vazia. Espera um status HTTP
     * 400 Bad Request.
     */
    @Test
    @DisplayName("6. Falha: Tentar atualizar um ativo com descrição vazia")
    void quandoAtualizarAtivoComDescricaoVaziaRetornaBadRequest() throws Exception {
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      ativoUpsertDTO.setDescricao("");

      driver
          .put(URI_ATIVOS + "/" + ativoId, ativoUpsertDTO, Admin.getInstance())
          .andExpect(status().isBadRequest())
          .andDo(print());
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
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      // Executa a requisição DELETE e verifica o status
      driver
          .delete(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
          .andExpect(status().isNoContent())
          .andDo(print());
    }

    /**
     * Testa a falha na exclusão de um ativo que não existe. Espera um status HTTP 404 Not Found.
     */
    @Test
    @DisplayName("2. Falha: Tentar excluir um ativo que não existe (ID inválido)")
    void quandoExcluirAtivoInexistenteRetornaNotFound() throws Exception {
      driver
          .delete(URI_ATIVOS + "/99", Admin.getInstance())
          .andExpect(status().isNotFound())
          .andDo(print());
    }

    /**
     * Testa a exclusão de um ativo e verifica que ele não pode mais ser buscado. Simula um fluxo de
     * exclusão e posterior tentativa de busca.
     */
    @Test
    @DisplayName("3. Sucesso: Excluir um ativo e verificar que ele não pode mais ser buscado")
    void quandoExcluirAtivoVerificaQueNaoPodeSerBuscado() throws Exception {
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      // Exclui o ativo
      driver
          .delete(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
          .andExpect(status().isNoContent())
          .andDo(print());

      // Tenta buscar o ativo excluído
      driver
          .get(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
          .andExpect(status().isNotFound())
          .andDo(print());
    }

    /** Testa a exclusão sequencial de múltiplos ativos. */
    @Test
    @DisplayName("4. Sucesso: Excluir múltiplos ativos sequencialmente")
    void quandoExcluirMultiplosAtivosSequencialmenteRetornaNoContent() throws Exception {
      // Cria dois ativos
      AtivoResponseDTO ativo1 = ativoService.criar(ativoUpsertDTO);
      AtivoResponseDTO ativo2 =
          ativoService.criar(ativoUpsertDTO.toBuilder().nome("VALE3").build());

      // Exclui o primeiro ativo
      driver
          .delete(URI_ATIVOS + "/" + ativo1.getId(), Admin.getInstance())
          .andExpect(status().isNoContent());

      // Exclui o segundo ativo
      driver
          .delete(URI_ATIVOS + "/" + ativo2.getId(), Admin.getInstance())
          .andExpect(status().isNoContent());
    }

    /**
     * Testa a falha ao tentar excluir um ativo que já foi excluído. Simula a exceção {@code
     * ATIVO_NAO_ENCONTRADO} na segunda tentativa.
     */
    @Test
    @DisplayName("5. Falha: Tentar excluir um ativo que já foi excluído")
    void quandoExcluirAtivoJaExcluidoRetornaNotFound() throws Exception {
      // Cria um ativo
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      // Primeira exclusão com sucesso
      driver
          .delete(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
          .andExpect(status().isNoContent());

      // Segunda exclusão do mesmo ID, agora já excluído
      driver
          .delete(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
          .andExpect(status().isNotFound())
          .andDo(print());
    }

    /** Testa se a exclusão de um ativo retorna o status HTTP 204 No Content. */
    @Test
    @DisplayName("6. Sucesso: Excluir um ativo e verificar o status HTTP 204 No Content")
    void quandoExcluirAtivoVerificaStatus204() throws Exception {
      // Cria um ativo
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      driver
          .delete(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
          .andExpect(status().isNoContent())
          .andDo(print());
    }

    /**
     * Testa a falha na exclusão de um ativo sem autenticação. Espera um status HTTP 401
     * Unauthorized.
     */
    @Test
    @DisplayName("7. Falha: Tentar excluir um ativo sem autenticação")
    void quandoExcluirAtivoSemAutenticacaoRetornaUnauthorized() throws Exception {
      mvcDriver
          .perform(delete(URI_ATIVOS + "/1"))
          .andExpect(status().isUnauthorized())
          .andDo(print());
    }

    /**
     * Testa a falha na exclusão de um ativo com autenticação inválida. Espera um status HTTP 401
     * Unauthorized.
     */
    @Test
    @DisplayName("8. Falha: Tentar excluir um ativo com autenticação inválida")
    void quandoExcluirAtivoComAutenticacaoInvalidaRetornaUnauthorized() throws Exception {
      mvcDriver
          .perform(delete(URI_ATIVOS + "/1").param("codigoAcesso", "invalid_code"))
          .andExpect(status().isUnauthorized())
          .andDo(print());
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
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      // Executa a requisição GET e verifica o status e os campos do JSON de resposta
      driver
          .get(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(ativoId))
          .andExpect(jsonPath("$.nome").value("PETR4"))
          .andDo(print());
    }

    /**
     * Testa se os dados do ativo recuperado correspondem exatamente aos dados esperados. Realiza
     * uma desserialização completa da resposta JSON para validação detalhada.
     */
    @Test
    @DisplayName(
        "2. Sucesso: Verificar se os dados do ativo recuperado correspondem aos dados esperados")
    void quandoRecuperarAtivoExistentePorIdVerificaDados() throws Exception {
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      String responseJsonString =
          driver
              .get(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      AtivoResponseDTO resultado =
          objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

      // Compara cada campo do DTO esperado com o DTO retornado
      assertEquals(ativoCriado.getId(), resultado.getId());
      assertEquals(ativoCriado.getNome(), resultado.getNome());
      assertEquals(ativoCriado.getDescricao(), resultado.getDescricao());
      assertEquals(ativoCriado.getStatus(), resultado.getStatus());
      assertEquals(ativoCriado.getTipo(), resultado.getTipo());
      assertTrue(
          BigDecimal.valueOf(ativoCriado.getCotacao().doubleValue()).compareTo(resultado.getCotacao())
              == 0);
    }

    /**
     * Testa a falha na recuperação de um ativo que não existe. Espera um status HTTP 404 Not Found.
     */
    @Test
    @DisplayName("3. Falha: Tentar recuperar um ativo que não existe (ID inválido)")
    void quandoRecuperarAtivoInexistenteRetornaNotFound() throws Exception {
      driver
          .get(URI_ATIVOS + "/99", Admin.getInstance())
          .andExpect(status().isNotFound())
          .andDo(print());
    }

    /**
     * Testa a recuperação de um ativo recém-criado. Simula o fluxo de criação e posterior busca.
     */
    @Test
    @DisplayName("4. Sucesso: Recuperar um ativo recém-criado")
    void quandoRecuperarAtivoRecemCriadoRetornaSucesso() throws Exception {
      // Cria um novo ativo
      AtivoCreateDTO novoAtivoDTO = ativoUpsertDTO.toBuilder().nome("NOVO_ATIVO").build();
      AtivoResponseDTO novoAtivo = ativoService.criar(novoAtivoDTO);

      // Simula a busca pelo ativo recém-criado
      driver
          .get(URI_ATIVOS + "/" + novoAtivo.getId(), Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("NOVO_ATIVO"))
          .andDo(print());
    }

    /**
     * Testa a recuperação de um ativo após sua atualização. Simula o fluxo de atualização e
     * posterior busca.
     */
    @Test
    @DisplayName("5. Sucesso: Recuperar um ativo após sua atualização")
    void quandoRecuperarAtivoAposAtualizacaoRetornaSucesso() throws Exception {
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      // Atualiza o ativo
      AtivoUpdateDTO ativoParaAtualizar = ativoUpdateDTO.toBuilder().nome("PETR4_UPDATED").build();
      ativoService.atualizar(ativoId, ativoParaAtualizar);

      // Simula a busca pelo ativo atualizado
      driver
          .get(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("PETR4_UPDATED"))
          .andDo(print());
    }

    /**
     * Testa a falha na recuperação de um ativo que foi excluído. Simula o fluxo de exclusão e
     * posterior tentativa de busca.
     */
    @Test
    @DisplayName("6. Falha: Tentar recuperar um ativo excluído")
    void quandoRecuperarAtivoExcluidoRetornaNotFound() throws Exception {
      // Cria um ativo
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      // Simula a exclusão do ativo
      driver
          .delete(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
          .andExpect(status().isNoContent());

      // Simula a busca pelo ativo excluído
      driver
          .get(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
          .andExpect(status().isNotFound())
          .andDo(print());
    }

    /**
     * Testa a falha na recuperação de um ativo sem autenticação. Espera um status HTTP 401
     * Unauthorized.
     */
    @Test
    @DisplayName("7. Falha: Tentar recuperar um ativo sem autenticação")
    void quandoRecuperarAtivoSemAutenticacaoRetornaUnauthorized() throws Exception {
      mvcDriver.perform(get(URI_ATIVOS + "/1")).andExpect(status().isUnauthorized()).andDo(print());
    }

    /**
     * Testa a falha na recuperação de um ativo com autenticação inválida. Espera um status HTTP 401
     * Unauthorized.
     */
    @Test
    @DisplayName("8. Falha: Tentar recuperar um ativo com autenticação inválida")
    void quandoRecuperarAtivoComAutenticacaoInvalidaRetornaUnauthorized() throws Exception {
      mvcDriver
          .perform(get(URI_ATIVOS + "/1").param("codigoAcesso", "invalid_code"))
          .andExpect(status().isUnauthorized())
          .andDo(print());
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
      // Insere ativos reais no banco
      ativoService.criar(ativoUpsertDTO);
      ativoService.criar(ativoUpsertDTO.toBuilder().nome("VALE3").build());
      ativoService.criar(ativoUpsertDTO.toBuilder().nome("GOOGL34").build());

      driver
          .get(URI_ATIVOS, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[0].nome").value("PETR4"))
          .andExpect(jsonPath("$[1].nome").value("VALE3"))
          .andExpect(jsonPath("$[2].nome").value("GOOGL34"))
          .andDo(print());
    }

    /**
     * Testa a listagem de ativos quando não há nenhum ativo cadastrado. Espera uma lista vazia e
     * status HTTP 200 OK.
     */
    @Test
    @DisplayName("2. Sucesso: Listar ativos quando não há nenhum ativo cadastrado (lista vazia)")
    void quandoListarAtivosSemAtivosCadastradosRetornaListaVazia() throws Exception {
      driver
          .get(URI_ATIVOS, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0))
          .andDo(print());
    }

    /** Testa se o número de ativos retornados na lista está correto. */
    @Test
    @DisplayName("3. Sucesso: Verificar o número correto de ativos retornados na lista")
    void quandoListarAtivosVerificaNumeroCorreto() throws Exception {
      // Insere dois ativos reais no banco
      ativoService.criar(ativoUpsertDTO);
      ativoService.criar(ativoUpsertDTO.toBuilder().nome("VALE3").build());

      driver
          .get(URI_ATIVOS, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andDo(print());
    }

    /**
     * Testa se os dados dos ativos na lista estão corretos, realizando uma desserialização
     * completa.
     */
    @Test
    @DisplayName("4. Sucesso: Verificar se os dados dos ativos na lista estão corretos")
    void quandoListarAtivosVerificaDadosCorretos() throws Exception {
      // Insere dois ativos reais no banco
      ativoService.criar(ativoUpsertDTO);
      ativoService.criar(
          ativoUpsertDTO.toBuilder()
              .nome("VALE3")
              .descricao("Vale S.A.")
              .status(StatusAtivo.DISPONIVEL)
              .cotacao(BigDecimal.valueOf(70.00))
              .tipo(AtivoTipo.ACAO)
              .build());

      String responseJsonString =
          driver
              .get(URI_ATIVOS, Admin.getInstance())
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
      assertEquals("PETR4", resultados.get(0).getNome());
      assertEquals("VALE3", resultados.get(1).getNome());
    }

    /**
     * Testa a listagem de ativos após a criação de um novo ativo. Simula a criação e verifica se o
     * novo ativo aparece na lista.
     */
    @Test
    @DisplayName("5. Sucesso: Listar ativos após a criação de um novo ativo")
    void quandoListarAtivosAposCriacaoRetornaSucesso() throws Exception {
      // Cria um ativo inicial
      ativoService.criar(ativoUpsertDTO);

      // Criação do novo ativo via API
      driver
          .post(
              URI_ATIVOS,
              ativoUpsertDTO.toBuilder().nome("NOVO_ATIVO_LIST").build(),
              Admin.getInstance())
          .andExpect(status().isCreated());

      // Listagem após criação
      driver
          .get(URI_ATIVOS, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[1].nome").value("NOVO_ATIVO_LIST"))
          .andDo(print());
    }

    /**
     * Testa a listagem de ativos após a exclusão de um ativo. Simula a exclusão e verifica se o
     * ativo removido não aparece mais na lista.
     */
    @Test
    @DisplayName("6. Sucesso: Listar ativos após a exclusão de um ativo")
    void quandoListarAtivosAposExclusaoRetornaSucesso() throws Exception {
      // Cria dois ativos
      AtivoResponseDTO ativo1 = ativoService.criar(ativoUpsertDTO);
      ativoService.criar(ativoUpsertDTO.toBuilder().nome("VALE3").build());

      // Simula a exclusão de um ativo
      driver
          .delete(URI_ATIVOS + "/" + ativo1.getId(), Admin.getInstance())
          .andExpect(status().isNoContent());

      // Listagem após exclusão
      driver
          .get(URI_ATIVOS, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].nome").value("VALE3"))
          .andDo(print());
    }

    /**
     * Testa a listagem de ativos após a atualização de um ativo. Simula a atualização e verifica se
     * os dados atualizados aparecem na lista.
     */
    @Test
    @DisplayName("7. Sucesso: Listar ativos após a atualização de um ativo")
    void quandoListarAtivosAposAtualizacaoRetornaSucesso() throws Exception {
      // Cria um ativo
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long ativoId = ativoCriado.getId();

      // Simula a atualização de um ativo
      driver
          .put(
              URI_ATIVOS + "/" + ativoId,
              ativoUpsertDTO.toBuilder().nome("PETR4_UPDATED_LIST").build(),
              Admin.getInstance())
          .andExpect(status().isOk());

      // Listagem após atualização
      driver
          .get(URI_ATIVOS, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].nome").value("PETR4_UPDATED_LIST"))
          .andDo(print());
    }

    /**
     * Testa a falha na listagem de ativos sem autenticação. Espera um status HTTP 401 Unauthorized.
     */
    @Test
    @DisplayName("8. Falha: Tentar listar ativos sem autenticação")
    void quandoListarAtivosSemAutenticacaoRetornaUnauthorized() throws Exception {
      mvcDriver.perform(get(URI_ATIVOS)).andExpect(status().isUnauthorized()).andDo(print());
    }

    /**
     * Testa a falha na listagem de ativos com autenticação inválida. Espera um status HTTP 401
     * Unauthorized.
     */
    @Test
    @DisplayName("9. Falha: Tentar listar ativos com autenticação inválida")
    void quandoListarAtivosComAutenticacaoInvalidaRetornaUnauthorized() throws Exception {
      mvcDriver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "invalid_code"))
          .andExpect(status().isUnauthorized())
          .andDo(print());
    }

    /**
     * Testa a ordem dos ativos retornados na listagem. Assume que a ordem é a mesma da lista
     * mockada para este teste.
     */
    @Test
    @DisplayName(
        "10. Sucesso: Verificar a ordem dos ativos retornados (se houver uma ordem definida)")
    void quandoListarAtivosVerificaOrdemDefinida() throws Exception {
      // Insere três ativos em ordem específica
      ativoService.criar(ativoUpsertDTO);
      ativoService.criar(ativoUpsertDTO.toBuilder().nome("AAPL34").build());
      ativoService.criar(ativoUpsertDTO.toBuilder().nome("MSFT34").build());

      driver
          .get(URI_ATIVOS, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].nome").value("PETR4"))
          .andExpect(jsonPath("$[1].nome").value("AAPL34"))
          .andExpect(jsonPath("$[2].nome").value("MSFT34"))
          .andDo(print());
    }

    /** Testa a visualização de ativos por plano mockada para este teste. */
    @Test
    @DisplayName("11. Sucesso: Verificar a visualização de ativos por plano")
    void quandoListarAtivosVerificaVisualizacaoPorPlano() throws Exception {
      // Cria um ativo do tipo Tesouro
      ativoService.criar(ativoUpsertDTO.toBuilder().nome("CDB").tipo(AtivoTipo.TESOURO).build());
      ativoService.criar(ativoUpsertDTO.toBuilder().nome("BTC").tipo(AtivoTipo.CRIPTO).build());

      var cliente =
          clienteRepository.save(
              Cliente.builder()
                  .nome("Cliente Um da Silva")
                  .plano(PlanoEnum.NORMAL)
                  .endereco("Rua dos Testes, 123")
                  .codigoAcesso("123456")
                  .build());

      var clientePremium =
          clienteRepository.save(
              Cliente.builder()
                  .nome("Cliente Premium da Silva")
                  .plano(PlanoEnum.PREMIUM)
                  .endereco("Rua dos Testes, 123")
                  .codigoAcesso("123456")
                  .build());

      driver
          .get(URI_ATIVOS, cliente)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].nome").value("CDB"))
          .andDo(print());

      driver
          .get(URI_ATIVOS, clientePremium)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].nome").value("CDB"))
          .andExpect(jsonPath("$[0].nome").value("CDB"))
          .andDo(print());
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
      // Cria um ativo primeiro
      ativoService.criar(ativoUpsertDTO);

      // Tenta criar outro ativo com o mesmo nome
      driver
          .post(URI_ATIVOS, ativoUpsertDTO, Admin.getInstance())
          .andExpect(status().isConflict())
          .andDo(print());
    }

    /**
     * Testa o acesso não autorizado a todas as operações CRUD sem fornecer o parâmetro de
     * autenticação. Espera um status HTTP 401 Unauthorized para cada operação.
     */
    @Test
    @DisplayName(
        "2. Autenticação: Testar o acesso a todas as operações CRUD sem a anotação @Autenticado(TipoAutenticacao.ADMIN)")
    void quandoAcessarOperacoesSemAutenticacaoRetornaUnauthorized() throws Exception {
      // Teste para POST sem código de acesso
      driver.post(URI_ATIVOS, ativoUpsertDTO).andExpect(status().isUnauthorized()).andDo(print());

      // Teste para PUT sem código de acesso
      mvcDriver
          .perform(
              put(URI_ATIVOS + "/1")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Teste para DELETE sem código de acesso
      mvcDriver
          .perform(delete(URI_ATIVOS + "/1"))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Teste para GET por ID sem código de acesso
      mvcDriver.perform(get(URI_ATIVOS + "/1")).andExpect(status().isUnauthorized()).andDo(print());

      // Teste para GET listar todos sem código de acesso
      mvcDriver.perform(get(URI_ATIVOS)).andExpect(status().isUnauthorized()).andDo(print());
    }

    /**
     * Testa o acesso não autorizado a todas as operações CRUD com um código de acesso de Admin
     * inválido. Espera um status HTTP 401 Unauthorized para cada operação.
     */
    @Test
    @DisplayName(
        "3. Autenticação: Testar o acesso a todas as operações CRUD com um codigoAcesso inválido para o Admin")
    void quandoAcessarOperacoesComAutenticacaoInvalidaRetornaUnauthorized() throws Exception {
      // Teste para POST com código de acesso inválido
      mvcDriver
          .perform(
              post(URI_ATIVOS)
                  .param("codigoAcesso", "invalid_admin_code")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Teste para PUT com código de acesso inválido
      mvcDriver
          .perform(
              put(URI_ATIVOS + "/1")
                  .param("codigoAcesso", "invalid_admin_code")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(ativoUpsertDTO)))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Teste para DELETE com código de acesso inválido
      mvcDriver
          .perform(delete(URI_ATIVOS + "/1").param("codigoAcesso", "invalid_admin_code"))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Teste para GET por ID com código de acesso inválido
      mvcDriver
          .perform(get(URI_ATIVOS + "/1").param("codigoAcesso", "invalid_admin_code"))
          .andExpect(status().isUnauthorized())
          .andDo(print());

      // Teste para GET listar todos com código de acesso inválido
      mvcDriver
          .perform(get(URI_ATIVOS).param("codigoAcesso", "invalid_admin_code"))
          .andExpect(status().isUnauthorized())
          .andDo(print());
    }

    /**
     * Testa um fluxo completo de integração: criar, atualizar, recuperar, listar e excluir um
     * ativo. Verifica o comportamento do controller em cada etapa do ciclo de vida do ativo.
     */
    @Test
    @DisplayName(
        "4. Integração: Criar um ativo, atualizá-lo, recuperá-lo, listar todos e depois excluí-lo, verificando o fluxo completo")
    void quandoExecutarFluxoCompletoCRUDRetornaSucesso() throws Exception {
      // 1. Criar o ativo
      driver
          .post(
              URI_ATIVOS,
              ativoUpsertDTO.toBuilder().nome("ATIVO_FLUXO").build(),
              Admin.getInstance())
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.nome").value("ATIVO_FLUXO"));

      // Busca o ID do ativo criado
      String responseJsonString =
          driver
              .get(URI_ATIVOS, Admin.getInstance())
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      List<AtivoResponseDTO> ativos =
          objectMapper.readValue(
              responseJsonString,
              objectMapper
                  .getTypeFactory()
                  .constructCollectionType(List.class, AtivoResponseDTO.class));

      Long ativoId = ativos.get(0).getId();

      // 2. Atualizar o ativo
      driver
          .put(
              URI_ATIVOS + "/" + ativoId,
              ativoUpsertDTO.toBuilder().nome("ATIVO_FLUXO_UPDATED").build(),
              Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("ATIVO_FLUXO_UPDATED"));

      // 3. Recuperar o ativo atualizado
      driver
          .get(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("ATIVO_FLUXO_UPDATED"));

      // 4. Listar todos os ativos (deve conter o ativo atualizado)
      driver
          .get(URI_ATIVOS, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].nome").value("ATIVO_FLUXO_UPDATED"));

      // 5. Excluir o ativo
      driver
          .delete(URI_ATIVOS + "/" + ativoId, Admin.getInstance())
          .andExpect(status().isNoContent());

      // 6. Listar todos os ativos novamente (deve estar vazio após a exclusão)
      driver
          .get(URI_ATIVOS, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0));
    }
  }

  @Nested
  @DisplayName("Alterar status do ativo")
  class AlterarStatusTests {

    private static final String URI_STATUS = "/ativos/%d/status";

    @Test
    @DisplayName("Altera status com sucesso e retorna 200 OK")
    void alterarStatusComSucesso() throws Exception {
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long id = ativoCriado.getId();

      AlterarStatusDTO dto = new AlterarStatusDTO();
      dto.setNovoStatus(StatusAtivo.INDISPONIVEL);

      driver
          .put(URI_STATUS.formatted(id), dto, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id))
          .andExpect(jsonPath("$.status").value("INDISPONIVEL"))
          .andDo(print());
    }

    @Test
    @DisplayName("Tenta alterar status de ativo inexistente, retorna 404")
    void alterarStatusAtivoNaoEncontrado() throws Exception {
      Long id = 999L;
      AlterarStatusDTO dto = new AlterarStatusDTO();
      dto.setNovoStatus(StatusAtivo.DISPONIVEL);

      driver
          .put(URI_STATUS.formatted(id), dto, Admin.getInstance())
          .andExpect(status().isNotFound())
          .andDo(print());
    }

    @Test
    @DisplayName("Tenta alterar status sem autenticação, retorna 401 Unauthorized")
    void alterarStatusSemAutenticacao() throws Exception {
      Long id = 1L;
      AlterarStatusDTO dto = new AlterarStatusDTO();
      dto.setNovoStatus(StatusAtivo.DISPONIVEL);

      driver.put(URI_STATUS.formatted(id), dto).andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    @DisplayName("Tenta alterar status com dto inválido, retorna 400 Bad Request")
    void alterarStatusComDtoInvalido() throws Exception {
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long id = ativoCriado.getId();

      // DTO com novoStatus null - violação @NotNull
      AlterarStatusDTO dto = new AlterarStatusDTO();
      dto.setNovoStatus(null);

      driver
          .put(URI_STATUS.formatted(id), dto, Admin.getInstance())
          .andExpect(status().isBadRequest())
          .andDo(print());
    }

    @Test
    @DisplayName("Muda de DISPONIVEL para INDISPONIVEL com sucesso")
    void mudaDeDisponivelParaIndisponivel() throws Exception {
      // Cria um ativo primeiro
      AtivoResponseDTO ativoCriado = ativoService.criar(ativoUpsertDTO);
      Long id = ativoCriado.getId();

      // DTO com novo status
      AlterarStatusDTO dto = new AlterarStatusDTO();
      dto.setNovoStatus(StatusAtivo.INDISPONIVEL);

      driver
          .put(URI_STATUS.formatted(id), dto, Admin.getInstance())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id))
          .andExpect(jsonPath("$.status").value("INDISPONIVEL"))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("Testes de Atualização de Ativo (PUT /ativos/{id})")
  class AtualizarCotacaoAtivoTests {
    @Autowired private AtivoRepository ativoRepository;

    Cripto cripto;
    Acao acao;
    Tesouro tesouro;

    @BeforeEach
    void setup() {
      cripto =
          ativoRepository.save(
              Cripto.builder()
                  .nome("Doge")
                  .descricao("Moeda dogecoin")
                  .cotacao(BigDecimal.valueOf(100.00))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.CRIPTO)
                  .build());

      acao =
          ativoRepository.save(
              Acao.builder()
                  .nome("PETR4")
                  .descricao("Acao petrobras")
                  .cotacao(BigDecimal.valueOf(100.00))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.ACAO)
                  .build());
      tesouro =
          ativoRepository.save(
              Tesouro.builder()
                  .nome("Selic")
                  .descricao("tesouro selic")
                  .cotacao(BigDecimal.valueOf(100.00))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.TESOURO)
                  .build());
    }

    @Test
    @DisplayName("Atualiza cotação de cripto com cotacao valida")
    void quandoAtualizarCotacaoCriptoValidoRetornaSucesso() throws Exception {
      CotacaoUpsertDTO cotacaoUpsertDTO =
          CotacaoUpsertDTO.builder().cotacao(BigDecimal.valueOf(200)).build();
      String responseJsonString =
          driver
              .put(
                  URI_ATIVOS + "/" + cripto.getId() + "/cotacao",
                      cotacaoUpsertDTO,
                  Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      AtivoResponseDTO resultado =
          objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

      assertTrue(BigDecimal.valueOf(200).compareTo(resultado.getCotacao()) == 0);
    }

    @Test
    @DisplayName("Atualiza cotação de cripto com sucesso")
    void quandoAtualizarCotacaoCriptoExatamenteUmPorcentoRetornaSucesso() throws Exception {
      CotacaoUpsertDTO cotacaoUpsertDTO =
          CotacaoUpsertDTO.builder().cotacao(BigDecimal.valueOf(101)).build();
      String responseJsonString =
          driver
              .put(
                  URI_ATIVOS + "/" + cripto.getId() + "/cotacao",
                      cotacaoUpsertDTO,
                  Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      AtivoResponseDTO resultado =
          objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

      assertTrue(BigDecimal.valueOf(101).compareTo(resultado.getCotacao()) == 0);
    }

    @Test
    @DisplayName("Atualiza cotação de cripto com sucesso")
    void quandoAtualizarCotacaoCriptoMenosUmPorcentoRetornaSucesso() throws Exception {
      CotacaoUpsertDTO cotacaoUpsertDTO =
          CotacaoUpsertDTO.builder().cotacao(BigDecimal.valueOf(100.5)).build();
      String responseJsonString =
          driver
              .put(
                  URI_ATIVOS + "/" + cripto.getId() + "/cotacao",
                      cotacaoUpsertDTO,
                  Admin.getInstance())
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.ATUALIZA_COTACAO_NAO_ATENDE_REQUISITO, resultado.getCode());

      assertTrue(
          BigDecimal.valueOf(100.00)
                  .compareTo(ativoRepository.findById(cripto.getId()).get().getCotacao())
              == 0);
    }

    @Test
    @DisplayName("Atualiza cotação de tesouro retorna erro")
    void quandoTentaAtualizarTesouro() throws Exception {
      CotacaoUpsertDTO cotacaoUpsertDTO =
          CotacaoUpsertDTO.builder().cotacao(BigDecimal.valueOf(200)).build();
      String responseJsonString =
          driver
              .put(
                  URI_ATIVOS + "/" + tesouro.getId() + "/cotacao",
                      cotacaoUpsertDTO,
                  Admin.getInstance())
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.OPERACAO_INVALIDA_PARA_O_TIPO, resultado.getCode());

      assertTrue(
          BigDecimal.valueOf(100.00)
                  .compareTo(ativoRepository.findById(tesouro.getId()).get().getCotacao())
              == 0);
    }

    @Test
    @DisplayName("Atualiza cotação de acao com cotacao valido")
    void quandoAtualizarCotacaoAcaoValidoRetornaSucesso() throws Exception {
      CotacaoUpsertDTO cotacaoUpsertDTO =
          CotacaoUpsertDTO.builder().cotacao(BigDecimal.valueOf(200)).build();
      String responseJsonString =
          driver
              .put(
                  URI_ATIVOS + "/" + acao.getId() + "/cotacao", cotacaoUpsertDTO, Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      AtivoResponseDTO resultado =
          objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

      assertTrue(BigDecimal.valueOf(200).compareTo(resultado.getCotacao()) == 0);
    }

    @Test
    @DisplayName("Atualiza cotação de acao com cotacao exatamente 1% retorna sucesso")
    void quandoAtualizarCotacaoAcaoExatamenteUmPorcentoRetornaSucesso() throws Exception {
      CotacaoUpsertDTO cotacaoUpsertDTO =
          CotacaoUpsertDTO.builder().cotacao(BigDecimal.valueOf(101)).build();
      String responseJsonString =
          driver
              .put(
                  URI_ATIVOS + "/" + acao.getId() + "/cotacao", cotacaoUpsertDTO, Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      AtivoResponseDTO resultado =
          objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

      assertTrue(BigDecimal.valueOf(101).compareTo(resultado.getCotacao()) == 0);
    }

    @Test
    @DisplayName("Atualiza cotação de acao com cotacao menor que 1% retorna erro")
    void quandoAtualizarCotacaoAcaoMenosUmPorcentoRetornaErro() throws Exception {
      CotacaoUpsertDTO cotacaoUpsertDTO =
          CotacaoUpsertDTO.builder().cotacao(BigDecimal.valueOf(100.5)).build();
      String responseJsonString =
          driver
              .put(
                  URI_ATIVOS + "/" + acao.getId() + "/cotacao", cotacaoUpsertDTO, Admin.getInstance())
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.ATUALIZA_COTACAO_NAO_ATENDE_REQUISITO, resultado.getCode());

      assertTrue(
          BigDecimal.valueOf(100.00)
                  .compareTo(ativoRepository.findById(acao.getId()).get().getCotacao())
              == 0);
    }
  }
}
