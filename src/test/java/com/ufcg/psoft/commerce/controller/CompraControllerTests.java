package com.ufcg.psoft.commerce.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.CompraConfirmacaoDTO;
import com.ufcg.psoft.commerce.dto.CompraCreateDTO;
import com.ufcg.psoft.commerce.dto.CompraResponseDTO;
import com.ufcg.psoft.commerce.dto.CotacaoUpsertDTO;
import com.ufcg.psoft.commerce.enums.CompraStatusEnum;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.http.exception.ErrorDTO;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.compra.Compra;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.utils.CustomDriver;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Compras")
@ActiveProfiles("test")
public class CompraControllerTests {

  final String URI_COMPRAS = "/compras";

  @Autowired MockMvc mvcDriver;
  @Autowired ClienteRepository clienteRepository;
  @Autowired CompraRepository compraRepository;
  @Autowired AtivoService ativoService;

  ObjectMapper objectMapper = new ObjectMapper();
  CustomDriver driver;

  @BeforeEach
  void setup() {
    objectMapper.registerModule(new JavaTimeModule());
    driver = new CustomDriver(mvcDriver, objectMapper);
  }

  @AfterEach
  void tearDown() {
    compraRepository.deleteAll();
  }

  // Métodos auxiliares para criar entidades de teste
  private Compra criarCompra(Cliente cliente, Ativo ativo, CompraStatusEnum status) {
    return compraRepository.save(
        Compra.builder()
            .cliente(cliente)
            .ativo(ativo)
            .quantidade(1)
            .valorUnitario(ativo.getCotacao())
            .abertaEm(LocalDateTime.now())
            .status(status)
            .build());
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de confirmação de compra")
  class CompraConfirmacaoVerificacao {

    // Cenário 1: Admin libera compra que está `SOLICITADO`
    @Test
    @DisplayName("Quando admin libera compra que está SOLICITADO")
    void quandoAdminLiberaCompraSolicitada() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4
      Compra compra = criarCompra(cliente, ativo, CompraStatusEnum.SOLICITADO);

      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.SOLICITADO);

      // Act
      String responseJsonString =
          driver
              .post(
                  URI_COMPRAS + "/" + compra.getId() + "/liberar",
                  confirmacaoDto,
                  Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      CompraResponseDTO resultado =
          objectMapper.readValue(responseJsonString, CompraResponseDTO.class);

      // Assert
      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(CompraStatusEnum.DISPONIVEL, resultado.getStatus()));
    }

    // Cenário 2: Cliente confirma compra 'DISPONIVEL' que é dele
    @Test
    @DisplayName("Quando cliente confirma compra DISPONIVEL que é dele")
    void quandoClienteConfirmaCompraDisponivel() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4
      Compra compra = criarCompra(cliente, ativo, CompraStatusEnum.DISPONIVEL);

      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.DISPONIVEL);

      // Act
      String responseJsonString =
          driver
              .post(URI_COMPRAS + "/" + compra.getId() + "/confirmar", confirmacaoDto, cliente)
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      CompraResponseDTO resultado =
          objectMapper.readValue(responseJsonString, CompraResponseDTO.class);

      // Assert
      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(CompraStatusEnum.EM_CARTEIRA, resultado.getStatus()));
    }

    // Cenário 3: Admin tenta liberar compra já 'DISPONIVEL'
    @Test
    @DisplayName("Quando admin tenta liberar compra já DISPONIVEL")
    void quandoAdminTentaLiberarCompraDisponivel() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4
      Compra compra = criarCompra(cliente, ativo, CompraStatusEnum.DISPONIVEL);

      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.DISPONIVEL);

      // Act
      String responseJsonString =
          driver
              .post(
                  URI_COMPRAS + "/" + compra.getId() + "/liberar",
                  confirmacaoDto,
                  Admin.getInstance())
              .andExpect(status().isConflict())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.COMPRA_NAO_ESTA_SOLICITADA, resultado.getCode());
    }

    // Cenário 4: Admin tenta liberar compra já 'COMPRADO'
    @Test
    @DisplayName("Quando admin tenta liberar compra já COMPRADO")
    void quandoAdminTentaLiberarCompraComprado() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4
      Compra compra = criarCompra(cliente, ativo, CompraStatusEnum.COMPRADO);

      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.COMPRADO);

      // Act
      String responseJsonString =
          driver
              .post(
                  URI_COMPRAS + "/" + compra.getId() + "/liberar",
                  confirmacaoDto,
                  Admin.getInstance())
              .andExpect(status().isConflict())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.COMPRA_NAO_ESTA_SOLICITADA, resultado.getCode());
    }

    // Cenário 5: Cliente tenta confirmar compra ainda 'SOLICITADO'
    @Test
    @DisplayName("Quando cliente tenta confirmar compra ainda SOLICITADO")
    void quandoClienteTentaConfirmarCompraSolicitado() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4
      Compra compra = criarCompra(cliente, ativo, CompraStatusEnum.SOLICITADO);

      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.SOLICITADO);

      // Act
      String responseJsonString =
          driver
              .post(URI_COMPRAS + "/" + compra.getId() + "/confirmar", confirmacaoDto, cliente)
              .andExpect(status().isConflict())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.COMPRA_NAO_ESTA_DISPONIVEL, resultado.getCode());
    }

    // Cenário 6: Cliente tenta confirmar compra já `COMPRADO`
    @Test
    @DisplayName("Quando cliente tenta confirmar compra já COMPRADO")
    void quandoClienteTentaConfirmarCompraComprado() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4
      Compra compra = criarCompra(cliente, ativo, CompraStatusEnum.COMPRADO);

      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.COMPRADO);

      // Act
      String responseJsonString =
          driver
              .post(URI_COMPRAS + "/" + compra.getId() + "/confirmar", confirmacaoDto, cliente)
              .andExpect(status().isConflict())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.COMPRA_NAO_ESTA_DISPONIVEL, resultado.getCode());
    }

    // Cenário 7: Admin tenta liberar compra inexistente
    @Test
    @DisplayName("Quando admin tenta liberar compra inexistente")
    void quandoAdminTentaLiberarCompraInexistente() throws Exception {
      // Arrange
      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.SOLICITADO);

      // Act
      String responseJsonString =
          driver
              .post(URI_COMPRAS + "/999/liberar", confirmacaoDto, Admin.getInstance())
              .andExpect(status().isNotFound())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.COMPRA_NAO_ENCONTRADA, resultado.getCode());
    }

    // Cenário 8: Cliente tenta confirmar compra inexistente
    @Test
    @DisplayName("Quando cliente tenta confirmar compra inexistente")
    void quandoClienteTentaConfirmarCompraInexistente() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.DISPONIVEL);

      // Act
      String responseJsonString =
          driver
              .post(URI_COMPRAS + "/999/confirmar", confirmacaoDto, cliente)
              .andExpect(status().isNotFound())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.COMPRA_NAO_ENCONTRADA, resultado.getCode());
    }

    // Cenário 9: Cliente tenta liberar compra
    @Test
    @DisplayName("Quando cliente tenta liberar compra")
    void quandoClienteTentaLiberarCompra() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4
      Compra compra = criarCompra(cliente, ativo, CompraStatusEnum.SOLICITADO);

      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.SOLICITADO);

      // Act
      String responseJsonString =
          driver
              .post(URI_COMPRAS + "/" + compra.getId() + "/liberar", confirmacaoDto, cliente)
              .andExpect(status().isUnauthorized())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.UNAUTHORIZED, resultado.getCode());
    }

    // Cenário 10: Admin tenta confirmar compra
    @Test
    @DisplayName("Quando admin tenta confirmar compra")
    void quandoAdminTentaConfirmarCompra() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4
      Compra compra = criarCompra(cliente, ativo, CompraStatusEnum.DISPONIVEL);

      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.DISPONIVEL);

      // Act
      String responseJsonString =
          driver
              .post(
                  URI_COMPRAS + "/" + compra.getId() + "/confirmar",
                  confirmacaoDto,
                  Admin.getInstance())
              .andExpect(status().isForbidden())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.ACAO_APENAS_CLIENTE_DONO_COMPRA, resultado.getCode());
    }

    // Cenário 11: Cliente tenta confirmar compra de outro cliente
    @Test
    @DisplayName("Quando cliente tenta confirmar compra de outro cliente")
    void quandoClienteTentaConfirmarCompraDeOutroCliente() throws Exception {
      // Arrange
      Cliente clienteDono =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Cliente outroCliente =
          clienteRepository
              .findById(2L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4
      Compra compra = criarCompra(clienteDono, ativo, CompraStatusEnum.DISPONIVEL);

      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.DISPONIVEL);

      // Act
      String responseJsonString =
          driver
              .post(URI_COMPRAS + "/" + compra.getId() + "/confirmar", confirmacaoDto, outroCliente)
              .andExpect(status().isForbidden())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.ACAO_APENAS_CLIENTE_DONO_COMPRA, resultado.getCode());
    }

    // Cenário 12: Fluxo completo, cliente criando compra, admin liberando e cliente confirmando
    @Test
    @DisplayName("Quando executa fluxo completo: cliente cria, admin libera, cliente confirma")
    void quandoExecutaFluxoCompleto() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(2L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4

      CompraCreateDTO compraCreateDTO =
          CompraCreateDTO.builder().ativoId(ativo.getId()).quantidade(1).build();

      // 1. Cliente cria compra (status SOLICITADO)
      String responseCreateJsonString =
          driver
              .post(URI_COMPRAS, compraCreateDTO, cliente)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      CompraResponseDTO compraSolicitada =
          objectMapper.readValue(responseCreateJsonString, CompraResponseDTO.class);
      assertEquals(CompraStatusEnum.SOLICITADO, compraSolicitada.getStatus());

      // 2. Admin libera compra (status DISPONIVEL)
      CompraConfirmacaoDTO liberarDto = new CompraConfirmacaoDTO();
      liberarDto.setStatusAtual(CompraStatusEnum.SOLICITADO);

      String responseLiberarJsonString =
          driver
              .post(
                  URI_COMPRAS + "/" + compraSolicitada.getId() + "/liberar",
                  liberarDto,
                  Admin.getInstance())
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      CompraResponseDTO compraDisponivel =
          objectMapper.readValue(responseLiberarJsonString, CompraResponseDTO.class);
      assertEquals(CompraStatusEnum.DISPONIVEL, compraDisponivel.getStatus());

      // 3. Cliente confirma compra (status COMPRADO)
      CompraConfirmacaoDTO confirmarDto = new CompraConfirmacaoDTO();
      confirmarDto.setStatusAtual(CompraStatusEnum.DISPONIVEL);

      String responseConfirmarJsonString =
          driver
              .post(
                  URI_COMPRAS + "/" + compraDisponivel.getId() + "/confirmar",
                  confirmarDto,
                  cliente)
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      CompraResponseDTO compraComprado =
          objectMapper.readValue(responseConfirmarJsonString, CompraResponseDTO.class);
      assertEquals(CompraStatusEnum.EM_CARTEIRA, compraComprado.getStatus());
    }

    // Cenário 13: Admin envia 'statusAtual=SOLICITADO', mas compra já está 'DISPONIVEL'
    @Test
    @DisplayName("Quando admin envia statusAtual SOLICITADO, mas compra já está DISPONIVEL")
    void quandoAdminLiberaComStatusAtualIncorreto() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4
      Compra compra = criarCompra(cliente, ativo, CompraStatusEnum.DISPONIVEL);

      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.SOLICITADO);

      // Act
      String responseJsonString =
          driver
              .post(
                  URI_COMPRAS + "/" + compra.getId() + "/liberar",
                  confirmacaoDto,
                  Admin.getInstance())
              .andExpect(status().isConflict())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.CONFLICT, resultado.getCode());
    }

    // Cenário 14: Cliente envia `statusAtual=DISPONIVEL`, mas compra já está `COMPRADO`
    @Test
    @DisplayName("Quando cliente envia statusAtual DISPONIVEL, mas compra já está COMPRADO")
    void quandoClienteConfirmaComStatusAtualIncorreto() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4
      Compra compra = criarCompra(cliente, ativo, CompraStatusEnum.COMPRADO);

      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(CompraStatusEnum.DISPONIVEL);

      // Act
      String responseJsonString =
          driver
              .post(URI_COMPRAS + "/" + compra.getId() + "/confirmar", confirmacaoDto, cliente)
              .andExpect(status().isConflict())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.CONFLICT, resultado.getCode());
    }

    // Cenário 15: Cliente envia `statusAtual` incorreto
    @Test
    @DisplayName("Quando cliente envia statusAtual incorreto")
    void quandoClienteConfirmaComStatusAtualInvalido() throws Exception {
      // Arrange
      Cliente cliente =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
      Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance()); // PETR4
      Compra compra = criarCompra(cliente, ativo, CompraStatusEnum.DISPONIVEL);

      // Criando um DTO com um status que não corresponde ao esperado para o cenário
      CompraConfirmacaoDTO confirmacaoDto = new CompraConfirmacaoDTO();
      confirmacaoDto.setStatusAtual(
          CompraStatusEnum.SOLICITADO); // Esperava DISPONIVEL, enviou SOLICITADO

      // Act
      String responseJsonString =
          driver
              .post(URI_COMPRAS + "/" + compra.getId() + "/confirmar", confirmacaoDto, cliente)
              .andExpect(status().isConflict())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);

      // Assert
      assertEquals(ErrorCode.CONFLICT, resultado.getCode());
    }
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação de solicitação de compra (US09)")
  class CompraSolicitacaoTests {

    @Autowired AtivoRepository ativoRepository;

    @Test
    @DisplayName("Sanidade: controller /compras cria e persiste compra (cliente PREMIUM em Ação)")
    void sanidadeControllerCriar_premiumAcao_persisteNoRepository() throws Exception {
      Cliente clientePremium =
          clienteRepository
              .findById(2L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO)); // Nívea
      Ativo acao = ativoService.getAtivoDisponivel(1L, clientePremium); // PETR4

      CompraCreateDTO dto = CompraCreateDTO.builder().ativoId(acao.getId()).quantidade(3).build();

      String responseJson =
          driver
              .post(URI_COMPRAS, dto, clientePremium)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      CompraResponseDTO resposta = objectMapper.readValue(responseJson, CompraResponseDTO.class);

      assertAll(
          () -> assertNotNull(resposta.getId()),
          () -> assertEquals(acao.getId(), resposta.getAtivoId()),
          () -> assertEquals(CompraStatusEnum.SOLICITADO, resposta.getStatus()),
          () -> assertTrue(compraRepository.findById(resposta.getId()).isPresent()));
    }

    @Test
    @DisplayName("Admin tenta criar compra e falha")
    void adminCriaCompra_deveFalhar() throws Exception {

      // Admin tentando comprar PETR4
      CompraCreateDTO dto = new CompraCreateDTO();
      dto.setAtivoId(1L);
      dto.setQuantidade(1);

      String responseJson =
          driver
              .post(URI_COMPRAS, dto, Admin.getInstance())
              .andExpect(status().isForbidden())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO erro = objectMapper.readValue(responseJson, ErrorDTO.class);
      assertEquals(ErrorCode.ACAO_APENAS_CLIENTE_DONO_COMPRA, erro.getCode());
    }

    @Test
    @DisplayName(
        "Quando cliente PREMIUM solicita compra de Ação disponível deve ser criada com sucesso")
    void quandoClientePremiumCompraAcaoDisponivel() throws Exception {
      // Arrange
      Cliente clientePremium =
          clienteRepository
              .findById(2L)
              .orElseThrow(
                  () -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO)); // Nívea, PREMIUM

      Ativo ativo = ativoService.getAtivoDisponivel(1L, clientePremium); // PETR4 por ex.

      CompraCreateDTO compraDto = new CompraCreateDTO();
      compraDto.setAtivoId(ativo.getId());
      compraDto.setQuantidade(5);

      // Act
      String responseJson =
          driver
              .post(URI_COMPRAS, compraDto, clientePremium)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      CompraResponseDTO resultado = objectMapper.readValue(responseJson, CompraResponseDTO.class);

      // Assert
      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(ativo.getId(), resultado.getAtivoId()),
          () -> assertEquals(CompraStatusEnum.SOLICITADO, resultado.getStatus()));
    }

    @Test
    @DisplayName(
        "Quando cliente PREMIUM solicita compra de Cripto disponível deve ser criada com sucesso")
    void quandoClientePremiumCompraCriptoDisponivel() throws Exception {
      // Arrange
      Cliente clientePremium =
          clienteRepository
              .findById(2L)
              .orElseThrow(
                  () -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO)); // Nívea, PREMIUM

      Ativo cripto = ativoService.getAtivoDisponivel(6L, clientePremium); // BTC, por ex.

      CompraCreateDTO compraDto = new CompraCreateDTO();
      compraDto.setAtivoId(cripto.getId());
      compraDto.setQuantidade(3);

      String responseJson =
          driver
              .post(URI_COMPRAS, compraDto, clientePremium)
              .andExpect(status().isCreated())
              // .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      CompraResponseDTO resultado = objectMapper.readValue(responseJson, CompraResponseDTO.class);

      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(cripto.getId(), resultado.getAtivoId()),
          () -> assertEquals(CompraStatusEnum.SOLICITADO, resultado.getStatus()));
    }

    @Test
    @DisplayName(
        "Quando cliente PREMIUM solicita compra de Tesouro disponível deve ser criada com sucesso")
    void quandoClientePremiumCompraTesouroDisponivel() throws Exception {
      Cliente clientePremium =
          clienteRepository
              .findById(2L)
              .orElseThrow(
                  () -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO)); // Nívea, PREMIUM

      Ativo tesouro = ativoService.getAtivoDisponivel(11L, clientePremium); // Selic, por ex.

      CompraCreateDTO compraDto = new CompraCreateDTO();
      compraDto.setAtivoId(tesouro.getId());
      compraDto.setQuantidade(2);

      String responseJson =
          driver
              .post(URI_COMPRAS, compraDto, clientePremium)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      CompraResponseDTO resultado = objectMapper.readValue(responseJson, CompraResponseDTO.class);

      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(tesouro.getId(), resultado.getAtivoId()),
          () -> assertEquals(CompraStatusEnum.SOLICITADO, resultado.getStatus()));
    }

    @Test
    @DisplayName("Quando cliente PREMIUM tenta comprar ativo INDISPONIVEL deve falhar")
    void quandoClientePremiumCompraAtivoIndisponivel() throws Exception {

      Cliente clientePremium =
          clienteRepository
              .findById(2L)
              .orElseThrow(
                  () -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO)); // Nívea, PREMIUM

      Ativo ativo =
          ativoRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

      // Deixar ele indisponível e persistir
      ativo.setStatus(StatusAtivo.INDISPONIVEL);
      ativoRepository.saveAndFlush(ativo);

      CompraCreateDTO compraDto = new CompraCreateDTO();
      compraDto.setAtivoId(ativo.getId());
      compraDto.setQuantidade(1);

      String responseJson =
          driver
              .post(URI_COMPRAS, compraDto, clientePremium)
              .andExpect(status().isBadRequest())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      ErrorDTO erro = objectMapper.readValue(responseJson, ErrorDTO.class);
      assertEquals(ErrorCode.ATIVO_NAO_DISPONIVEL, erro.getCode());

      // desfz
      ativo.setStatus(StatusAtivo.DISPONIVEL);
      ativoRepository.saveAndFlush(ativo);
    }

    @Test
    @DisplayName(
        "Quando cliente NORMAL solicita compra de Tesouro Direto disponível deve ser criada com sucesso")
    void quandoClienteNormalCompraTesouroDisponivel() throws Exception {
      // Arrange
      Cliente clienteNormal =
          clienteRepository
              .findById(1L)
              .orElseThrow(
                  () -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO)); // Gustavo, NORMAL

      Ativo ativoTesouro =
          ativoService.getAtivoDisponivel(
              11L, clienteNormal); // supondo que id=10 seja Tesouro Direto

      CompraCreateDTO compraDto = new CompraCreateDTO();
      compraDto.setAtivoId(ativoTesouro.getId());
      compraDto.setQuantidade(2);

      // Act
      String responseJson =
          driver
              .post(URI_COMPRAS, compraDto, clienteNormal)
              .andExpect(status().isCreated())
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();

      CompraResponseDTO resultado = objectMapper.readValue(responseJson, CompraResponseDTO.class);

      // Assert
      assertAll(
          () -> assertNotNull(resultado.getId()),
          () -> assertEquals(ativoTesouro.getId(), resultado.getAtivoId()),
          () -> assertEquals(CompraStatusEnum.SOLICITADO, resultado.getStatus()));
    }

    @Test
    @DisplayName("Quando cliente NORMAL solicita compra de Tesouro indisponível deve falhar")
    void quandoClienteNormalCompraTesouroIndisponivel() throws Exception {
      Cliente clienteNormal =
          clienteRepository
              .findById(1L)
              .orElseThrow(
                  () -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO)); // Gustavo, NORMAL

      Ativo ativoTesouro =
          ativoRepository
              .findById(11L) // Selic, por exemplo
              .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

      // Deixar indisponível e persistir
      ativoTesouro.setStatus(StatusAtivo.INDISPONIVEL);
      ativoRepository.saveAndFlush(ativoTesouro);

      CompraCreateDTO compraDto = new CompraCreateDTO();
      compraDto.setAtivoId(ativoTesouro.getId());
      compraDto.setQuantidade(2);

      // Act + Assert
      driver
          .post(URI_COMPRAS, compraDto, clienteNormal)
          .andExpect(
              status().isBadRequest()) // ou .isConflict() se a regra de negócio usar esse código
          .andDo(print());

      // desfaz
      ativoTesouro.setStatus(StatusAtivo.DISPONIVEL);
      ativoRepository.saveAndFlush(ativoTesouro);
    }

    @Test
    @DisplayName("Quando cliente NORMAL solicita compra de Ação deve falhar")
    void quandoClienteNormalCompraAcaoDeveFalhar() throws Exception {
      // Arrange
      Cliente clienteNormal =
          clienteRepository
              .findById(1L)
              .orElseThrow(
                  () -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO)); // Gustavo, NORMAL

      CompraCreateDTO compraDto = new CompraCreateDTO();
      compraDto.setAtivoId(1L);
      compraDto.setQuantidade(1);

      // Act & Assert
      driver
          .post(URI_COMPRAS, compraDto, clienteNormal)
          .andExpect(status().isBadRequest())
          .andDo(print());
    }

    @Test
    @DisplayName("Quando cliente NORMAL solicita compra de Cripto deve falhar")
    void quandoClienteNormalCompraCriptoDeveFalhar() throws Exception {
      // Arrange
      Cliente clienteNormal =
          clienteRepository
              .findById(1L)
              .orElseThrow(
                  () -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO)); // Gustavo, NORMAL

      CompraCreateDTO compraDto = new CompraCreateDTO();
      compraDto.setAtivoId(6L);
      compraDto.setQuantidade(1);

      // Act & Assert
      driver
          .post(URI_COMPRAS, compraDto, clienteNormal)
          .andExpect(status().isBadRequest())
          .andDo(print());
    }

    @Test
    @DisplayName("Quando cliente tenta comprar ativo inexistente deve retornar NOT FOUND")
    void quandoCompraAtivoInexistente() throws Exception {
      // Arrange
      Cliente clientePremium =
          clienteRepository
              .findById(2L)
              .orElseThrow(
                  () -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO)); // Nívea, PREMIUM

      CompraCreateDTO compraDto = new CompraCreateDTO();
      compraDto.setAtivoId(9999L); // ID que não existe
      compraDto.setQuantidade(1);

      // Act & Assert
      driver
          .post(URI_COMPRAS, compraDto, clientePremium)
          .andExpect(status().isNotFound())
          .andDo(print());
    }
  }

  @Test
  @DisplayName("Quando cria compra sem autenticação deve retornar 401")
  void quandoCriaCompraSemAutenticacao() throws Exception {
    CompraCreateDTO dto = new CompraCreateDTO();
    dto.setAtivoId(1L);
    dto.setQuantidade(1);

    mvcDriver
        .perform(
            post(URI_COMPRAS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Quando cria compra com credencial inválida deve retornar 401")
  void quandoCriaCompraComCredencialInvalida() throws Exception {
    CompraCreateDTO dto = new CompraCreateDTO();
    dto.setAtivoId(1L);
    dto.setQuantidade(1);

    mvcDriver
        .perform(
            post(URI_COMPRAS)
                .header("Authorization", driver.createBasicAuthHeader("999", "invalid"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Quando cria compra com ativoId nulo deve retornar 400")
  void quandoCriaCompraComAtivoIdNulo() throws Exception {
    var cliente =
        clienteRepository
            .findById(2L)
            .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO)); // Nívea

    CompraCreateDTO dto = new CompraCreateDTO();
    dto.setAtivoId(null);
    dto.setQuantidade(1);

    String body =
        driver
            .post(URI_COMPRAS, dto, cliente)
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorDTO erro = objectMapper.readValue(body, ErrorDTO.class);
    assertEquals(ErrorCode.BAD_REQUEST, erro.getCode());
  }

  @Test
  @DisplayName("Quando cria compra com quantidade <= 0 deve retornar 400")
  void quandoCriaCompraComQuantidadeInvalida() throws Exception {
    var cliente =
        clienteRepository
            .findById(2L)
            .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));

    CompraCreateDTO dto = new CompraCreateDTO();
    dto.setAtivoId(1L);
    dto.setQuantidade(0);

    String body =
        driver
            .post(URI_COMPRAS, dto, cliente)
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorDTO erro = objectMapper.readValue(body, ErrorDTO.class);
    assertEquals(ErrorCode.BAD_REQUEST, erro.getCode());
  }

  @Nested
  @DisplayName("Conjunto de casos de verificação da carteira (US13)")
  class ClienteCarteiraVerificacao {
    final String URI_ATIVOS = "/ativos";
    final String URI_CLIENTES = "/clientes";
    final String URI_COMPRAS = "/compras";

    Cliente ClientePREMIUM; // Nívea (id=2)
    Cliente clienteNORMAL; // Gustavo (id=1)
    Ativo ativoPetr4;
    Ativo ativoTesouro; // PETR4
    Ativo ativoCripto;

    @BeforeEach
    void setUpCarteiraFixtures() {
      ClientePREMIUM =
          clienteRepository
              .findById(2L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));

      clienteNORMAL =
          clienteRepository
              .findById(1L)
              .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));

      ativoPetr4 = ativoService.getAtivoDisponivel(1L, ClientePREMIUM); // PETR4
      ativoCripto = ativoService.getAtivoDisponivel(6L, ClientePREMIUM); // BTC
      ativoTesouro = ativoService.getAtivoDisponivel(11L, ClientePREMIUM); // SELIC
    }

    /** Helper: cria/avança compra até EM_CARTEIRA */
    private CompraResponseDTO fluxoCompraConfirmada(Cliente cliente, Ativo ativo, int qtd)
        throws Exception {
      // 1) SOLICITAR
      var criar = CompraCreateDTO.builder().ativoId(ativo.getId()).quantidade(qtd).build();
      var jsonSolicitado =
          driver
              .post(URI_COMPRAS, criar, cliente)
              .andExpect(status().isCreated())
              .andReturn()
              .getResponse()
              .getContentAsString();
      var compraSolicitada = objectMapper.readValue(jsonSolicitado, CompraResponseDTO.class);

      // 2) LIBERAR (admin -> DISPONIVEL)
      var liberar = new CompraConfirmacaoDTO();
      liberar.setStatusAtual(CompraStatusEnum.SOLICITADO);
      driver
          .post(
              URI_COMPRAS + "/" + compraSolicitada.getId() + "/liberar",
              liberar,
              Admin.getInstance())
          .andExpect(status().isOk());

      // 3) CONFIRMAR (cliente -> EM_CARTEIRA)
      var confirmar = new CompraConfirmacaoDTO();
      confirmar.setStatusAtual(CompraStatusEnum.DISPONIVEL);
      var jsonConfirmada =
          driver
              .post(URI_COMPRAS + "/" + compraSolicitada.getId() + "/confirmar", confirmar, cliente)
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      return objectMapper.readValue(jsonConfirmada, CompraResponseDTO.class);
    }

    @Test
    @DisplayName("US13 - Quando consulta carteira sem autenticação deve retornar 401")
    void quandoConsultaCarteiraSemAutenticacao() throws Exception {
      mvcDriver
          .perform(get(URI_CLIENTES + "/" + clienteNORMAL.getId() + "/carteira"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName(
        "US13 - Deve retornar 403 quando o ID não é do cliente autenticado (mesmo que não exista)")
    void quandoConsultaCarteiraDeOutroOuInexistente() throws Exception {
      mvcDriver
          .perform(
              get(URI_CLIENTES + "/" + 999999L + "/carteira")
                  .header(
                      "Authorization",
                      driver.createBasicAuthHeader(
                          clienteNORMAL.getUserId(), clienteNORMAL.getCodigoAcesso())))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName(
        "US13 - Quando cliente consulta carteira vazia deve retornar totais zerados e lista vazia")
    void quandoClienteConsultaCarteiraVazia() throws Exception {
      String json =
          mvcDriver
              .perform(
                  get(URI_CLIENTES + "/" + clienteNORMAL.getId() + "/carteira")
                      .header(
                          "Authorization",
                          driver.createBasicAuthHeader(
                              clienteNORMAL.getUserId(), clienteNORMAL.getCodigoAcesso())))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      var carteira =
          objectMapper.readValue(json, com.ufcg.psoft.commerce.dto.CarteiraResponseDTO.class);

      assertAll(
          () -> assertNotNull(carteira),
          () -> assertEquals(0, carteira.getQuantidadeAtivos()),
          () -> assertTrue(carteira.getAtivos().isEmpty()),
          () -> assertEquals(0, carteira.getSaldo().compareTo(java.math.BigDecimal.ZERO)),
          () -> assertEquals(0, carteira.getLucroTotal().compareTo(java.math.BigDecimal.ZERO)));
    }

    @Test
    @DisplayName(
        "US13 - Quando cliente consulta carteira com múltiplos ativos deve agregar corretamente por ativo")
    void quandoClientePREMIUMConsultaCarteiraComMultiplosAtivos() throws Exception {
      fluxoCompraConfirmada(ClientePREMIUM, ativoPetr4, 3);
      fluxoCompraConfirmada(ClientePREMIUM, ativoCripto, 2);
      fluxoCompraConfirmada(ClientePREMIUM, ativoTesouro, 1);

      BigDecimal cotPetr4 = ativoPetr4.getCotacao();
      BigDecimal cotBtc = ativoCripto.getCotacao();
      BigDecimal cotSelic = ativoTesouro.getCotacao();

      String json =
          mvcDriver
              .perform(
                  get(URI_CLIENTES + "/" + ClientePREMIUM.getId() + "/carteira")
                      .header(
                          "Authorization",
                          driver.createBasicAuthHeader(
                              ClientePREMIUM.getUserId(), ClientePREMIUM.getCodigoAcesso())))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      var carteira =
          objectMapper.readValue(json, com.ufcg.psoft.commerce.dto.CarteiraResponseDTO.class);

      assertAll(
          () -> assertNotNull(carteira),
          () -> assertEquals(3, carteira.getQuantidadeAtivos()),
          () -> assertEquals(3, carteira.getAtivos().size()));

      // Verifica cada ativo agregado
      // Mapeia por ID para facilitar os asserts
      var porId =
          new java.util.HashMap<Long, com.ufcg.psoft.commerce.dto.CarteiraAtivoResponseDTO>();
      for (var a : carteira.getAtivos()) porId.put(a.getAtivoId(), a);

      // PETR4 (id 1) -> quantidade 3
      {
        var a = porId.get(ativoPetr4.getId());
        assertNotNull(a);
        assertEquals(3, a.getQuantidade());
        assertEquals(0, a.getValorUnitario().compareTo(cotPetr4));
        assertEquals(
            0, a.getValorTotal().compareTo(cotPetr4.multiply(java.math.BigDecimal.valueOf(3))));
        assertEquals(0, a.getLucro().compareTo(java.math.BigDecimal.ZERO));
      }

      // BTC (id 6) -> quantidade 2
      {
        var a = porId.get(ativoCripto.getId());
        assertNotNull(a);
        assertEquals(2, a.getQuantidade());
        assertEquals(0, a.getValorUnitario().compareTo(cotBtc));
        assertEquals(
            0, a.getValorTotal().compareTo(cotBtc.multiply(java.math.BigDecimal.valueOf(2))));
        assertEquals(0, a.getLucro().compareTo(java.math.BigDecimal.ZERO));
      }

      // SELIC (id 11) -> quantidade 1
      {
        var a = porId.get(ativoTesouro.getId());
        assertNotNull(a);
        assertEquals(1, a.getQuantidade());
        assertEquals(0, a.getValorUnitario().compareTo(cotSelic));
        assertEquals(0, a.getValorTotal().compareTo(cotSelic.multiply(java.math.BigDecimal.ONE)));
        assertEquals(0, a.getLucro().compareTo(java.math.BigDecimal.ZERO));
      }

      // saldo = soma dos valor_total; lucro_total = soma dos lucros (0)
      var esperadoSaldo =
          cotPetr4
              .multiply(java.math.BigDecimal.valueOf(3))
              .add(cotBtc.multiply(java.math.BigDecimal.valueOf(2)))
              .add(cotSelic);

      assertAll(
          () -> assertEquals(0, carteira.getSaldo().compareTo(esperadoSaldo)),
          () -> assertEquals(0, carteira.getLucroTotal().compareTo(java.math.BigDecimal.ZERO)));
    }

    @Test
    @DisplayName(
        "US13 - Quando cliente NORMAL consulta carteira com múltiplos ativos deve agregar corretamente todos os Tesouros")
    void quandoClienteNORMALConsultaCarteiraComMultiplosAtivos() throws Exception {
      fluxoCompraConfirmada(ClientePREMIUM, ativoTesouro, 3);

      BigDecimal cotSelic = ativoTesouro.getCotacao();

      String json =
          mvcDriver
              .perform(
                  get(URI_CLIENTES + "/" + ClientePREMIUM.getId() + "/carteira")
                      .header(
                          "Authorization",
                          driver.createBasicAuthHeader(
                              ClientePREMIUM.getUserId(), ClientePREMIUM.getCodigoAcesso())))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      var carteira =
          objectMapper.readValue(json, com.ufcg.psoft.commerce.dto.CarteiraResponseDTO.class);

      assertAll(
          () -> assertNotNull(carteira),
          () -> assertEquals(1, carteira.getQuantidadeAtivos()),
          () -> assertEquals(1, carteira.getAtivos().size()));

      // Verifica cada ativo agregado
      // Mapeia por ID para facilitar os asserts
      var porId =
          new java.util.HashMap<Long, com.ufcg.psoft.commerce.dto.CarteiraAtivoResponseDTO>();
      for (var a : carteira.getAtivos()) porId.put(a.getAtivoId(), a);

      // SELIC (id 11) -> quantidade 3
      {
        var a = porId.get(ativoTesouro.getId());
        assertNotNull(a);
        assertEquals(3, a.getQuantidade());
        assertEquals(0, a.getValorUnitario().compareTo(cotSelic));
        assertEquals(0, a.getValorTotal().compareTo(cotSelic.multiply(BigDecimal.valueOf(3))));
        assertEquals(0, a.getLucro().compareTo(java.math.BigDecimal.ZERO));
      }

      var esperadoSaldo = cotSelic.multiply(java.math.BigDecimal.valueOf(3));

      assertAll(
          () -> assertEquals(0, carteira.getSaldo().compareTo(esperadoSaldo)),
          () -> assertEquals(0, carteira.getLucroTotal().compareTo(java.math.BigDecimal.ZERO)));
    }

    @Test
    @DisplayName(
        "US13 - Quando um cliente tenta consultar a carteira de outro cliente deve retornar 403")
    void quandoClienteTentaConsultarCarteiraDeOutroCliente() throws Exception {
      mvcDriver
          .perform(
              get(URI_CLIENTES + "/" + ClientePREMIUM.getId() + "/carteira")
                  .header(
                      "Authorization",
                      driver.createBasicAuthHeader(
                          clienteNORMAL.getUserId(), clienteNORMAL.getCodigoAcesso())))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("US13 - Agrega múltiplas compras do mesmo ativo em um único item")
    void quandoHaMultiplasComprasDoMesmoAtivo() throws Exception {
      fluxoCompraConfirmada(ClientePREMIUM, ativoPetr4, 1);
      fluxoCompraConfirmada(ClientePREMIUM, ativoPetr4, 4);

      String json =
          mvcDriver
              .perform(
                  get(URI_CLIENTES + "/" + ClientePREMIUM.getId() + "/carteira")
                      .header(
                          "Authorization",
                          driver.createBasicAuthHeader(
                              ClientePREMIUM.getUserId(), ClientePREMIUM.getCodigoAcesso())))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      var carteira = objectMapper.readTree(json);
      var ativos = carteira.get("ativos");
      assertEquals(1, ativos.size()); // apenas 1 PETR4 agregado
      var petr = ativos.get(0);
      assertEquals(5, petr.get("quantidade").asInt());
    }

    @Test
    @DisplayName(
        "US13 - Após atualizar cotação, carteira reflete novo valor unitário e lucro_total")
    void quandoAtualizaCotacaoRefleteNaCarteira() throws Exception {

      fluxoCompraConfirmada(ClientePREMIUM, ativoPetr4, 3);
      var cotacaoOriginal = ativoPetr4.getCotacao();

      var novaCotacao = cotacaoOriginal.add(new java.math.BigDecimal("10.00"));
      ativoService.atualizarCotacao(ativoPetr4.getId(), new CotacaoUpsertDTO(novaCotacao));

      String json =
          mvcDriver
              .perform(
                  get(URI_CLIENTES + "/" + ClientePREMIUM.getId() + "/carteira")
                      .header(
                          "Authorization",
                          driver.createBasicAuthHeader(
                              ClientePREMIUM.getUserId(), ClientePREMIUM.getCodigoAcesso())))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      var node = objectMapper.readTree(json);
      var item = node.get("ativos").get(0);

      assertEquals(0, item.get("valor").decimalValue().compareTo(novaCotacao));

      var lucroEsperado =
          novaCotacao.subtract(cotacaoOriginal).multiply(new java.math.BigDecimal("3"));
      assertEquals(0, node.get("lucro_total").decimalValue().compareTo(lucroEsperado));

      // REVERTE
      ativoService.atualizarCotacao(ativoPetr4.getId(), new CotacaoUpsertDTO(cotacaoOriginal));
    }
  }
}
