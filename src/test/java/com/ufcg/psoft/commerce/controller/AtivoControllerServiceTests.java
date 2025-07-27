package com.ufcg.psoft.commerce.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.commerce.dto.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.ValorUpsertDTO;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.http.exception.ErrorDTO;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.ativo.AtivoServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class AtivoControllerServiceTests {

    final String URI_ATIVOS = "/ativos";
    
    @Autowired
    private AtivoService ativoService;

    // Objeto para simular requisições HTTP para o controller
    @Autowired MockMvc driver;
    
    @Autowired
    private AtivoRepository ativoRepository;

    private Ativo cripto;
    private Ativo acao;
    private Ativo tesouro;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Testes de Atualização de Ativo (PUT /ativos/{id})")
    class AtualizarCotacaoAtivoTests {

        @BeforeEach
        void setup() {
            ativoRepository.deleteAll();
            
            cripto = ativoRepository.save(
                    Cripto.builder()
                            .nome("Doge")
                            .descricao("Moeda dogecoin")
                            .valor(BigDecimal.valueOf(100))
                            .status(StatusAtivo.DISPONIVEL)
                            .tipo(AtivoTipo.CRIPTO)
                            .build()
        
                    );

            acao = ativoRepository.save(
                    Acao.builder()
                            .nome("PETR4")
                            .descricao("Acao petrobras")
                            .valor(BigDecimal.valueOf(100))
                            .status(StatusAtivo.DISPONIVEL)
                            .tipo(AtivoTipo.ACAO)
                            .build()

            );
            tesouro = ativoRepository.save(
                    Tesouro.builder()
                            .nome("Selic")
                            .descricao("tesouro selic")
                            .valor(BigDecimal.valueOf(100))
                            .status(StatusAtivo.DISPONIVEL)
                            .tipo(AtivoTipo.TESOURO)
                            .build()

            );
        }

        @Test
        @DisplayName("TODO")
        void quandoAtualizarCotacaoCriptoValidoRetornaSucesso() throws Exception {
            ValorUpsertDTO valorUpsertDTO =
                    ValorUpsertDTO.builder()
                            .valor(BigDecimal.valueOf(200))
                            .build();
            String responseJsonString =
                driver
                    .perform(
                            put(URI_ATIVOS + "/" + cripto.getId() + "/cotacao")
                                    .param("codigoAcesso",  Admin.getInstance().getCodigoAcesso())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(valorUpsertDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();


            AtivoResponseDTO resultado =
                    objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            assertEquals(BigDecimal.valueOf(200), resultado.getValor());

        }

        @Test
        @DisplayName("TODO")
        void quandoAtualizarCotacaoCriptoExatamenteUmPorcentoRetornaSucesso() throws Exception {
            ValorUpsertDTO valorUpsertDTO =
                    ValorUpsertDTO.builder()
                            .valor(BigDecimal.valueOf(101))
                            .build();
            String responseJsonString =
                    driver
                            .perform(
                                    put(URI_ATIVOS + "/" + cripto.getId() + "/cotacao")
                                            .param("codigoAcesso",  Admin.getInstance().getCodigoAcesso())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(valorUpsertDTO)))
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();


            AtivoResponseDTO resultado =
                    objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            assertEquals(BigDecimal.valueOf(101), resultado.getValor());

        }

        @Test
        @DisplayName("TODO")
        void quandoAtualizarCotacaoCriptoMenosUmPorcentoRetornaSucesso() throws Exception {
            ValorUpsertDTO valorUpsertDTO =
                    ValorUpsertDTO.builder()
                            .valor(BigDecimal.valueOf(100.5))
                            .build();
            String responseJsonString =
                    driver
                            .perform(
                                    put(URI_ATIVOS + "/" + cripto.getId() + "/cotacao")
                                            .param("codigoAcesso",  Admin.getInstance().getCodigoAcesso())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(valorUpsertDTO)))
                            .andExpect(status().isBadRequest())
                            .andDo(print())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
            assertEquals(ErrorCode.ATUALIZA_COTACAO_NAO_ATENDE_REQUISITO, resultado.getCode());

            assertEquals(BigDecimal.valueOf(100), ativoRepository.findById(cripto.getId()).get().getValor());

        }

        @Test
        @DisplayName("TODO")
        void quandoTentaAtualizarTesouro() throws Exception {
            ValorUpsertDTO valorUpsertDTO =
                    ValorUpsertDTO.builder()
                            .valor(BigDecimal.valueOf(200))
                            .build();
            String responseJsonString =
                    driver
                            .perform(
                                    put(URI_ATIVOS + "/" + tesouro.getId() + "/cotacao")
                                            .param("codigoAcesso",  Admin.getInstance().getCodigoAcesso())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(valorUpsertDTO)))
                            .andExpect(status().isBadRequest())
                            .andDo(print())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();


            ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
            assertEquals(ErrorCode.OPERACAO_INVALIDA_PARA_O_TIPO, resultado.getCode());

            assertEquals(BigDecimal.valueOf(100), ativoRepository.findById(tesouro.getId()).get().getValor());
        }

        @Test
        @DisplayName("TODO")
        void quandoAtualizarCotacaoAcaoValidoRetornaSucesso() throws Exception {
            ValorUpsertDTO valorUpsertDTO =
                    ValorUpsertDTO.builder()
                            .valor(BigDecimal.valueOf(200))
                            .build();
            String responseJsonString =
                    driver
                            .perform(
                                    put(URI_ATIVOS + "/" + acao.getId() + "/cotacao")
                                            .param("codigoAcesso",  Admin.getInstance().getCodigoAcesso())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(valorUpsertDTO)))
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();


            AtivoResponseDTO resultado =
                    objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            assertEquals(BigDecimal.valueOf(200), resultado.getValor());

        }

        @Test
        @DisplayName("TODO")
        void quandoAtualizarCotacaoAcaoExatamenteUmPorcentoRetornaSucesso() throws Exception {
            ValorUpsertDTO valorUpsertDTO =
                    ValorUpsertDTO.builder()
                            .valor(BigDecimal.valueOf(101))
                            .build();
            String responseJsonString =
                    driver
                            .perform(
                                    put(URI_ATIVOS + "/" + acao.getId() + "/cotacao")
                                            .param("codigoAcesso",  Admin.getInstance().getCodigoAcesso())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(valorUpsertDTO)))
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();


            AtivoResponseDTO resultado =
                    objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            assertEquals(BigDecimal.valueOf(101), resultado.getValor());

        }

        @Test
        @DisplayName("TODO")
        void quandoAtualizarCotacaoAcaoMenosUmPorcentoRetornaSucesso() throws Exception {
            ValorUpsertDTO valorUpsertDTO =
                    ValorUpsertDTO.builder()
                            .valor(BigDecimal.valueOf(100.5))
                            .build();
            String responseJsonString =
                    driver
                            .perform(
                                    put(URI_ATIVOS + "/" + acao.getId() + "/cotacao")
                                            .param("codigoAcesso",  Admin.getInstance().getCodigoAcesso())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(valorUpsertDTO)))
                            .andExpect(status().isBadRequest())
                            .andDo(print())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            ErrorDTO resultado = objectMapper.readValue(responseJsonString, ErrorDTO.class);
            assertEquals(ErrorCode.ATUALIZA_COTACAO_NAO_ATENDE_REQUISITO, resultado.getCode());

            assertEquals(BigDecimal.valueOf(100), ativoRepository.findById(acao.getId()).get().getValor());

        }
    }

}
