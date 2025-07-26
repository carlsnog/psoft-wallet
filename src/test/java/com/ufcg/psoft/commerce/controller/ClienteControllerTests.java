package com.ufcg.psoft.commerce.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.ClienteUpsertDTO;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.http.exception.ErrorDTO;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.PlanoEnum;
import com.ufcg.psoft.commerce.repository.ClienteRepository;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Clientes")
public class ClienteControllerTests {

    final String URI_CLIENTES = "/clientes";

    @Autowired
    MockMvc driver;

    @Autowired
    ClienteRepository clienteRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Cliente cliente;

    ClienteUpsertDTO clientePostPutRequestDTO;

    @BeforeEach
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente Um da Silva")
                .plano(PlanoEnum.NORMAL)
                .endereco("Rua dos Testes, 123")
                .codigoAcesso("123456")
                .build());
        clientePostPutRequestDTO = ClienteUpsertDTO.builder()
                .nome(cliente.getNome())
                .plano(PlanoEnum.NORMAL)
                .endereco(cliente.getEndereco())
                .codigoAcesso(cliente.getCodigoAcesso())
                .build();
    }

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
    }

    String getUrlCliente(String path, Cliente cliente) {
        return path + "?userId=" + cliente.getId()
                + "&codigoAcesso=" + cliente.getCodigoAcesso();
    }

    String getUrlAdmin(String path) {
        return path + "?codigoAcesso=" + Admin.getInstance().getCodigoAcesso();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de nome")
    class ClienteVerificacaoNome {

        @Test
        @DisplayName("Quando recuperamos um cliente com dados válidos")
        void quandoRecuperamosNomeDoClienteValido() throws Exception {

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId())
                    .param("codigoAcesso", Admin.getInstance().getCodigoAcesso()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertEquals("Cliente Um da Silva", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o nome do cliente com dados válidos")
        void quandoAlteramosNomeDoClienteValido() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setNome("Cliente Um Alterado");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertEquals("Cliente Um Alterado", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o nome do cliente nulo")
        void quandoAlteramosNomeDoClienteNulo() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setNome(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

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
            clientePostPutRequestDTO.setNome("");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

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
            clientePostPutRequestDTO.setEndereco("Endereco Alterado");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertEquals("Endereco Alterado", resultado.getEndereco());
        }

        @Test
        @DisplayName("Quando alteramos o endereço do cliente nulo")
        void quandoAlteramosEnderecoDoClienteNulo() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setEndereco(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

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
            clientePostPutRequestDTO.setEndereco("");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

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
            clientePostPutRequestDTO.setCodigoAcesso(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

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
            clientePostPutRequestDTO.setCodigoAcesso("1234567");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
            assertEquals(ErrorCode.BAD_REQUEST, resultado.getCode());

            assertInstanceOf(ArrayList.class, resultado.getData());
            ArrayList<String> errors = (ArrayList<String>) resultado.getData();

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos",
                            errors.get(0)));
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente menos de 6 digitos")
        void quandoAlteramosCodigoAcessoDoClienteMenosDe6Digitos() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setCodigoAcesso("12345");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
            assertEquals(ErrorCode.BAD_REQUEST, resultado.getCode());

            assertInstanceOf(ArrayList.class, resultado.getData());
            ArrayList<String> errors = (ArrayList<String>) resultado.getData();

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos",
                            errors.get(0)));
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente caracteres não numéricos")
        void quandoAlteramosCodigoAcessoDoClienteCaracteresNaoNumericos() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setCodigoAcesso("a*c4e@");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
            assertEquals(ErrorCode.BAD_REQUEST, resultado.getCode());

            assertInstanceOf(ArrayList.class, resultado.getData());
            ArrayList<String> errors = (ArrayList<String>) resultado.getData();

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos",
                            errors.get(0)));
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
            Cliente cliente1 = Cliente.builder()
                    .nome("Cliente Dois Almeida")
                    .endereco("Av. da Pits A, 100")
                    .codigoAcesso("246810")
                    .plano(PlanoEnum.NORMAL)
                    .build();
            Cliente cliente2 = Cliente.builder()
                    .nome("Cliente Três Lima")
                    .endereco("Distrito dos Testadores, 200")
                    .codigoAcesso("135790")
                    .plano(PlanoEnum.NORMAL)
                    .build();
            clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES)
                    .param("codigoAcesso", Admin.getInstance().getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cliente> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(() -> assertEquals(3, resultado.size()));
        }

        @Test
        @DisplayName("Quando buscamos um cliente salvo pelo id")
        void quandoBuscamosPorUmClienteSalvo() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId())
                    .param("codigoAcesso", Admin.getInstance().getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString,
                    new TypeReference<>() {
                    });

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
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + 999999999)
                    .param("codigoAcesso", Admin.getInstance().getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
            assertEquals(ErrorCode.CLIENTE_NAO_EXISTE, resultado.getCode());

            assertEquals("Cliente nao existe", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com dados válidos")
        void quandoCriarClienteValido() throws Exception {
            // Arrange
            var url = URI_CLIENTES;

            // Act
            String responseJsonString = driver.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isCreated()) // Codigo 201
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome()));

        }

        @Test
        @DisplayName("Quando alteramos o cliente com dados válidos")
        void quandoAlteramosClienteValido() throws Exception {
            // Arrange
            Long clienteId = cliente.getId();

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(clienteId, resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome()));
        }

        @Test
        @DisplayName("Quando alteramos o cliente inexistente")
        void quandoAlteramosClienteInexistente() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + 99999L)
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isForbidden())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

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
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + clienteId)
                    .param("userId", String.valueOf(clienteId))
                    .param("codigoAcesso", "invalido")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
            assertEquals(ErrorCode.UNAUTHORIZED, resultado.getCode());

            assertEquals("Codigo de acesso invalido", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando excluímos um cliente salvo")
        void quandoExcluimosClienteValido() throws Exception {
            // Arrange

            // Act
            driver.perform(delete(URI_CLIENTES + "/" + cliente.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Quando excluímos um cliente inexistente")
        void quandoExcluimosClienteInexistente() throws Exception {
            // Arrange

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + 999999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("userId", String.valueOf(cliente.getId()))
                    .param("codigoAcesso", cliente.getCodigoAcesso()))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
            assertEquals(ErrorCode.CLIENTE_NAO_EXISTE, resultado.getCode());

            assertEquals("Cliente nao existe", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando excluímos um cliente salvo passando código de acesso inválido")
        void quandoExcluimosClienteCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("codigoAcesso", "invalido"))
                    .andExpect(status().isUnauthorized()) // Codigo 401
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
            assertEquals(ErrorCode.UNAUTHORIZED, resultado.getCode());

            assertEquals("Codigo de acesso invalido", resultado.getMessage());
        }
    }
}
