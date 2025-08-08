package com.ufcg.psoft.commerce.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.InteresseCreateDTO;
import com.ufcg.psoft.commerce.dto.InteresseResponseDTO;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.enums.PlanoEnum;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.http.exception.ErrorDTO;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Cripto;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.utils.CustomDriver;
import java.math.BigDecimal;
import java.util.ArrayList;
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
@DisplayName("Testes do controlador de Interesses")
public class InteresseControllerTests {

  final String URI_INTERESSES = "/interesses";

  @Autowired MockMvc mvcDriver;
  CustomDriver driver;

  @Autowired ClienteRepository clienteRepository;
  @Autowired AtivoRepository ativoRepository;

  ObjectMapper objectMapper = new ObjectMapper();

  Cliente clientePremium;
  Cliente clienteNormal;
  Ativo ativo;

  InteresseCreateDTO interesseCreateDTO;

  @BeforeEach
  void setup() {
    // Object Mapper suporte para LocalDateTime
    objectMapper.registerModule(new JavaTimeModule());
    driver = new CustomDriver(mvcDriver, objectMapper);

    // Criar cliente premium para os testes
    clientePremium =
        clienteRepository.save(
            Cliente.builder()
                .nome("Cliente Premium da Silva")
                .plano(PlanoEnum.PREMIUM)
                .endereco("Rua dos Testes Premium, 123")
                .codigoAcesso("123456")
                .build());

    // Criar cliente normal para os testes
    clienteNormal =
        clienteRepository.save(
            Cliente.builder()
                .nome("Cliente Normal da Silva")
                .plano(PlanoEnum.NORMAL)
                .endereco("Rua dos Testes Normal, 456")
                .codigoAcesso("654321")
                .build());

    // Criar ativo para testes
    ativo =
        ativoRepository.save(
            Cripto.builder()
                .nome("Doge")
                .descricao("Moeda dogecoin")
                .valor(BigDecimal.valueOf(100.00))
                .status(StatusAtivo.DISPONIVEL)
                .tipo(AtivoTipo.CRIPTO)
                .build());

    interesseCreateDTO =
        InteresseCreateDTO.builder()
            .tipo(TipoInteresseEnum.DISPONIBILIDADE)
            .clienteId(clientePremium.getId())
            .ativoId(ativo.getId())
            .build();
  }

  @AfterEach
  void tearDown() {
    clienteRepository.deleteAll();
    ativoRepository.deleteAll();
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de criação de interesse")
  class InteresseVerificacaoCriacao {

    @Test
    @DisplayName("Quando criamos um interesse válido com tipo DISPONIBILIDADE")
    void quandoCriarInteresseValidoDisponibilidade() throws Exception {
      // Act
      String responseJsonString =
          driver
              .post(URI_INTERESSES, interesseCreateDTO, clientePremium)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      InteresseResponseDTO resultado =
          objectMapper.readValue(responseJsonString, InteresseResponseDTO.class);

      // Assert
      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(TipoInteresseEnum.DISPONIBILIDADE, resultado.getTipo()),
          () -> assertEquals(clientePremium.getId(), resultado.getClienteId()),
          () -> assertEquals(ativo.getId(), resultado.getAtivoId()));
    }

    @Test
    @DisplayName("Quando criamos um interesse válido com tipo PRECO")
    void quandoCriarInteresseValidoPreco() throws Exception {
      // Arrange
      InteresseCreateDTO interessePrecoDTO =
          InteresseCreateDTO.builder()
              .tipo(TipoInteresseEnum.PRECO)
              .clienteId(clientePremium.getId())
              .ativoId(ativo.getId())
              .build();

      // Act
      String responseJsonString =
          driver
              .post(URI_INTERESSES, interessePrecoDTO, clientePremium)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      InteresseResponseDTO resultado =
          objectMapper.readValue(responseJsonString, InteresseResponseDTO.class);

      // Assert
      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(TipoInteresseEnum.PRECO, resultado.getTipo()),
          () -> assertEquals(clientePremium.getId(), resultado.getClienteId()),
          () -> assertEquals(ativo.getId(), resultado.getAtivoId()));
    }

    @Test
    @DisplayName("Quando tentamos criar interesse com tipo nulo")
    void quandoCriarInteresseComTipoNulo() throws Exception {
      // Arrange
      interesseCreateDTO.setTipo(null);

      // Act
      String responseJsonString =
          driver
              .post(URI_INTERESSES, interesseCreateDTO, clientePremium)
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
          () -> assertEquals("Tipo e obrigatorio", errors.get(0)));
    }

    @Test
    @DisplayName("Quando tentamos criar interesse com clienteId nulo")
    void quandoCriarInteresseComClienteIdNulo() throws Exception {
      // Arrange
      interesseCreateDTO.setClienteId(null);

      // Act
      String responseJsonString =
          driver
              .post(URI_INTERESSES, interesseCreateDTO, clientePremium)
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
          () -> assertEquals("ID do cliente e obrigatorio", errors.get(0)));
    }

    @Test
    @DisplayName("Quando tentamos criar interesse com ativoId nulo")
    void quandoCriarInteresseComAtivoIdNulo() throws Exception {
      // Arrange
      interesseCreateDTO.setAtivoId(null);

      // Act
      String responseJsonString =
          driver
              .post(URI_INTERESSES, interesseCreateDTO, clientePremium)
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
          () -> assertEquals("ID do ativo e obrigatorio", errors.get(0)));
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de autenticação")
  class InteresseVerificacaoAutenticacao {

    @Test
    @DisplayName("Quando cliente normal tenta criar interesse em Cripto (deve ser negado)")
    void quandoClienteNormalTentaCriarInteresseCripto() throws Exception {
      // Act
      String responseJsonString =
          driver
              .post(URI_INTERESSES, interesseCreateDTO, clienteNormal)
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
    @DisplayName(
        "Quando cliente normal tenta criar interesse em Tesouro Direto (deve ser autorizado)")
    void quandoClienteNormalTentaCriarInteresseTesouro() throws Exception {
      // Act
      ativo =
          ativoRepository.save(
              Cripto.builder()
                  .nome("Tesouro Selic")
                  .descricao("Acompanha a taxa selic")
                  .valor(BigDecimal.valueOf(100.00))
                  .status(StatusAtivo.DISPONIVEL)
                  .tipo(AtivoTipo.TESOURO)
                  .build());

      interesseCreateDTO =
          InteresseCreateDTO.builder()
              .tipo(TipoInteresseEnum.DISPONIBILIDADE)
              .clienteId(clienteNormal.getId())
              .ativoId(ativo.getId())
              .build();

      String responseJsonString =
          driver
              .post(URI_INTERESSES, interesseCreateDTO, clienteNormal)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      InteresseResponseDTO resultado =
          objectMapper.readValue(responseJsonString, InteresseResponseDTO.class);

      // Assert
      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(TipoInteresseEnum.DISPONIBILIDADE, resultado.getTipo()),
          () -> assertEquals(clienteNormal.getId(), resultado.getClienteId()),
          () -> assertEquals(ativo.getId(), resultado.getAtivoId()));
    }

    @Test
    @DisplayName("Quando tentamos criar interesse sem autenticação")
    void quandoCriarInteresseSemAutenticacao() throws Exception {
      // Act
      mvcDriver
          .perform(
              post(URI_INTERESSES)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(interesseCreateDTO)))
          .andExpect(status().isUnauthorized())
          .andDo(print());
    }

    @Test
    @DisplayName("Quando tentamos criar interesse com autenticação inválida")
    void quandoCriarInteresseComAutenticacaoInvalida() throws Exception {
      // Act
      mvcDriver
          .perform(
              post(URI_INTERESSES)
                  .header("Authorization", driver.createBasicAuthHeader("999", "invalid"))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(interesseCreateDTO)))
          .andExpect(status().isUnauthorized())
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de exclusão de interesse")
  class InteresseVerificacaoExclusao {

    @Test
    @DisplayName("Quando excluímos um interesse válido")
    void quandoExcluirInteresseValido() throws Exception {
      // Arrange - Primeiro criar o interesse
      String responseJsonString =
          driver
              .post(URI_INTERESSES, interesseCreateDTO, clientePremium)
              .andExpect(status().isCreated())
              .andReturn()
              .getResponse()
              .getContentAsString();

      InteresseResponseDTO interesseCriado =
          objectMapper.readValue(responseJsonString, InteresseResponseDTO.class);

      // Act - Excluir o interesse
      driver
          .delete(URI_INTERESSES + "/" + interesseCriado.getId(), clientePremium)
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Quando tentamos excluir um interesse inexistente")
    void quandoExcluirInteresseInexistente() throws Exception {
      // Act
      String responseJsonString =
          driver
              .delete(URI_INTERESSES + "/999", clientePremium)
              .andExpect(status().isNotFound())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.INTERESSE_NAO_ENCONTRADO, resultado.getCode());

      assertEquals("Interesse nao encontrado", resultado.getMessage());
    }

    @Test
    @DisplayName("Quando tentamos excluir interesse sem autenticação")
    void quandoExcluirInteresseSemAutenticacao() throws Exception {
      // Act
      mvcDriver
          .perform(delete(URI_INTERESSES + "/1"))
          .andExpect(status().isUnauthorized())
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de recuperação de interesse")
  class InteresseVerificacaoRecuperacao {

    @Test
    @DisplayName("Quando recuperamos um interesse válido")
    void quandoRecuperarInteresseValido() throws Exception {
      // Arrange - Primeiro criar o interesse
      String responseJsonString =
          driver
              .post(URI_INTERESSES, interesseCreateDTO, clientePremium)
              .andExpect(status().isCreated())
              .andReturn()
              .getResponse()
              .getContentAsString();

      InteresseResponseDTO interesseCriado =
          objectMapper.readValue(responseJsonString, InteresseResponseDTO.class);

      // Act - Recuperar o interesse
      String responseGetJsonString =
          driver
              .get(URI_INTERESSES + "/" + interesseCriado.getId(), clientePremium)
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      InteresseResponseDTO resultado =
          objectMapper.readValue(responseGetJsonString, InteresseResponseDTO.class);

      // Assert
      assertAll(
          () -> assertEquals(interesseCriado.getId(), resultado.getId()),
          () -> assertEquals(interesseCriado.getTipo(), resultado.getTipo()),
          () -> assertEquals(interesseCriado.getClienteId(), resultado.getClienteId()),
          () -> assertEquals(interesseCriado.getAtivoId(), resultado.getAtivoId()));
    }

    @Test
    @DisplayName("Quando tentamos recuperar um interesse inexistente")
    void quandoRecuperarInteresseInexistente() throws Exception {
      // Act
      String responseJsonString =
          driver
              .get(URI_INTERESSES + "/999", clientePremium)
              .andExpect(status().isNotFound())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
      assertEquals(ErrorCode.INTERESSE_NAO_ENCONTRADO, resultado.getCode());

      assertEquals("Interesse nao encontrado", resultado.getMessage());
    }

    @Test
    @DisplayName("Quando adm tenta recuperar um interesse de um cliente")
    void quandoClienteNormalTentaRecuperarInteresse() throws Exception {
      // Act
      String responseJsonString =
          driver
              .post(URI_INTERESSES, interesseCreateDTO, clientePremium)
              .andExpect(status().isCreated())
              .andReturn()
              .getResponse()
              .getContentAsString();

      InteresseResponseDTO interesseCriado =
          objectMapper.readValue(responseJsonString, InteresseResponseDTO.class);

      String responseGetJsonString =
          driver
              .get(URI_INTERESSES + "/" + interesseCriado.getId(), Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      InteresseResponseDTO resultado =
          objectMapper.readValue(responseGetJsonString, InteresseResponseDTO.class);

      // Assert
      assertAll(
          () -> assertEquals(interesseCriado.getId(), resultado.getId()),
          () -> assertEquals(interesseCriado.getTipo(), resultado.getTipo()),
          () -> assertEquals(interesseCriado.getClienteId(), resultado.getClienteId()),
          () -> assertEquals(interesseCriado.getAtivoId(), resultado.getAtivoId()));
    }

    @Test
    @DisplayName("Quando tentamos recuperar interesse sem autenticação")
    void quandoRecuperarInteresseSemAutenticacao() throws Exception {
      // Act
      mvcDriver
          .perform(get(URI_INTERESSES + "/1"))
          .andExpect(status().isUnauthorized())
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de listagem de interesses")
  class InteresseVerificacaoListagem {

    @Test
    @DisplayName("Quando admin lista todos os interesses")
    void quandoAdminListaInteresses() throws Exception {
      // Arrange - Criar alguns interesses primeiro
      driver
          .post(URI_INTERESSES, interesseCreateDTO, clientePremium)
          .andExpect(status().isCreated());

      InteresseCreateDTO interesse2 =
          InteresseCreateDTO.builder()
              .tipo(TipoInteresseEnum.PRECO)
              .clienteId(clientePremium.getId())
              .ativoId(ativo.getId())
              .build();

      driver.post(URI_INTERESSES, interesse2, clientePremium).andExpect(status().isCreated());

      // Act - Admin lista todos os interesses
      String responseJsonString =
          driver
              .get(URI_INTERESSES, Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      List<InteresseResponseDTO> resultado =
          objectMapper.readValue(responseJsonString, new TypeReference<>() {});

      // Assert
      assertAll(() -> assertEquals(2, resultado.size()));
    }

    @Test
    @DisplayName("Quando cliente tenta listar interesses (deve ser negado)")
    void quandoClientePremiumTentaListarInteresses() throws Exception {
      // Act
      String responseJsonString =
          driver
              .get(URI_INTERESSES, clientePremium)
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
    @DisplayName("Quando tentamos listar interesses sem autenticação")
    void quandoListarInteressesSemAutenticacao() throws Exception {
      // Act
      mvcDriver.perform(get(URI_INTERESSES)).andExpect(status().isUnauthorized()).andDo(print());
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de fluxos básicos API Rest")
  class InteresseVerificacaoFluxosBasicosApiRest {

    @Test
    @DisplayName("Quando executamos fluxo completo: criar, recuperar, excluir")
    void quandoExecutarFluxoCompletoCRUD() throws Exception {
      // 1. Criar interesse
      String responseJsonString =
          driver
              .post(URI_INTERESSES, interesseCreateDTO, clientePremium)
              .andExpect(status().isCreated())
              .andReturn()
              .getResponse()
              .getContentAsString();

      InteresseResponseDTO interesseCriado =
          objectMapper.readValue(responseJsonString, InteresseResponseDTO.class);

      // 2. Recuperar interesse
      driver
          .get(URI_INTERESSES + "/" + interesseCriado.getId(), clientePremium)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(interesseCriado.getId()))
          .andExpect(jsonPath("$.tipo").value("DISPONIBILIDADE"))
          .andDo(print());

      // 3. Excluir interesse
      driver
          .delete(URI_INTERESSES + "/" + interesseCriado.getId(), clientePremium)
          .andExpect(status().isNoContent());

      // 4. Tentar recuperar interesse excluído
      driver
          .get(URI_INTERESSES + "/" + interesseCriado.getId(), clientePremium)
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Quando criamos múltiplos interesses e verificamos listagem")
    void quandoCriarMultiplosInteressesEVerificarListagem() throws Exception {
      // 1. Criar primeiro interesse
      driver
          .post(URI_INTERESSES, interesseCreateDTO, clientePremium)
          .andExpect(status().isCreated());

      // 2. Criar segundo interesse
      InteresseCreateDTO interesse2 =
          InteresseCreateDTO.builder()
              .tipo(TipoInteresseEnum.PRECO)
              .clienteId(clientePremium.getId())
              .ativoId(ativo.getId())
              .build();

      driver.post(URI_INTERESSES, interesse2, clientePremium).andExpect(status().isCreated());

      // 3. Verificar listagem como admin
      String responseJsonString =
          driver
              .get(URI_INTERESSES, Admin.getInstance())
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      List<InteresseResponseDTO> resultado =
          objectMapper.readValue(responseJsonString, new TypeReference<>() {});

      // Assert
      assertAll(() -> assertEquals(2, resultado.size()));
    }
  }
}
