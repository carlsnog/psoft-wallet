package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.AlterarStatusDTO;
import com.ufcg.psoft.commerce.dto.AtivoCreateDTO;
import com.ufcg.psoft.commerce.dto.AtivoUpdateDTO;
import com.ufcg.psoft.commerce.dto.CotacaoUpsertDTO;
import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.http.auth.Autenticado;
import com.ufcg.psoft.commerce.http.request.RequestUser;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/ativos")
@Autenticado(TipoAutenticacao.ADMIN)
@Tag(name = "Ativos", description = "API para gerenciamento de ativos financeiros")
public class AtivoController {

  @Autowired AtivoService ativoService;

  @PostMapping
  @Operation(summary = "Criar ativo", description = "Cria um novo ativo financeiro no sistema")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Ativo criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> criarAtivo(@RequestBody @Valid AtivoCreateDTO ativoDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ativoService.criar(ativoDto));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Atualizar ativo", description = "Atualiza os dados de um ativo existente")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Ativo atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Ativo não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> atualizarAtivo(
      @Parameter(description = "ID do ativo") @PathVariable Long id,
      @RequestBody @Valid AtivoUpdateDTO ativoDto) {
    return ResponseEntity.status(HttpStatus.OK).body(ativoService.atualizar(id, ativoDto));
  }

  @PutMapping("/{id}/cotacao")
  @Operation(
      summary = "Atualizar cotação",
      description = "Atualiza a cotação de um ativo específico")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Cotação atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Ativo não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> atualizarCotacao(
      @Parameter(description = "ID do ativo") @PathVariable Long id,
      @RequestBody @Valid CotacaoUpsertDTO cotacaoUpsertDto) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(ativoService.atualizarCotacao(id, cotacaoUpsertDto));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Excluir ativo", description = "Remove um ativo do sistema")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Ativo removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Ativo não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> excluirAtivo(
      @Parameter(description = "ID do ativo") @PathVariable Long id) {
    ativoService.remover(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  @Autenticado(TipoAutenticacao.NORMAL)
  @Operation(summary = "Recuperar ativo", description = "Busca um ativo específico por ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Ativo encontrado"),
        @ApiResponse(responseCode = "404", description = "Ativo não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> recuperarAtivo(
      @Parameter(description = "ID do ativo") @PathVariable Long id, @RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.OK).body(ativoService.recuperar(id, usuario));
  }

  @GetMapping("")
  @Autenticado(TipoAutenticacao.NORMAL)
  @Operation(
      summary = "Listar ativos",
      description = "Lista todos os ativos disponíveis para o usuário")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Lista de ativos retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> listarAtivos(@RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.OK).body(ativoService.listar(usuario));
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "Alterar status", description = "Altera o status de um ativo")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Status inválido"),
        @ApiResponse(responseCode = "404", description = "Ativo não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> atualizarStatus(
      @Parameter(description = "ID do ativo") @PathVariable Long id,
      @RequestBody @Valid AlterarStatusDTO dto) {
    return ResponseEntity.ok(ativoService.alterarStatus(id, dto.getNovoStatus()));
  }
}
