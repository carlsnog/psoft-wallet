package com.ufcg.psoft.commerce.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.CompraConfirmacaoDTO;
import com.ufcg.psoft.commerce.dto.CompraCreateDTO;
import com.ufcg.psoft.commerce.dto.CompraResponseDTO;
import com.ufcg.psoft.commerce.enums.CompraStatusEnum;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.http.exception.ErrorDTO;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.compra.Compra;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.utils.CustomDriver;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
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
    // Não deletar clientes e ativos para usar os do BD
    // clienteRepository.deleteAll();
    // ativoRepository.deleteAll();
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

    // Cenário 2: Cliente confirma compra `DISPONIVEL` que é dele
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

    // Cenário 3: Admin tenta liberar compra já `DISPONIVEL`
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

    // Cenário 4: Admin tenta liberar compra já `COMPRADO`
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

    // Cenário 5: Cliente tenta confirmar compra ainda `SOLICITADO`
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

    // Cenário 13: Admin envia `statusAtual=SOLICITADO`, mas compra já está `DISPONIVEL`
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
}
