package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.InteresseCreateDTO;
import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.http.auth.Autenticado;
import com.ufcg.psoft.commerce.http.request.RequestUser;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.service.interesse.InteresseService;
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
@RequestMapping(value = "/interesses")
@Autenticado(TipoAutenticacao.NORMAL)
@Tag(name = "Interesses", description = "API para gerenciamento de interesses em ativos")
public class InteresseController {

  @Autowired InteresseService interesseService;

  @Autenticado(TipoAutenticacao.PREMIUM)
  @PostMapping("/cotacao")
  @Operation(
      summary = "Criar interesse por cotação",
      description = "Cria um interesse baseado em cotação (requer usuário premium)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Interesse criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - requer usuário premium")
      })
  public ResponseEntity<?> criarInteressePreco(
      @RequestBody @Valid InteresseCreateDTO interesseDto, @RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(interesseService.criarInteresseCotacao(usuario, interesseDto));
  }

  @PostMapping("/disponibilidade")
  @Operation(
      summary = "Criar interesse por disponibilidade",
      description = "Cria um interesse baseado em disponibilidade do ativo")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Interesse criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> criarInteresseDisponibilidade(
      @RequestBody @Valid InteresseCreateDTO interesseDto, @RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(interesseService.criarInteresseDisponibilidade(usuario, interesseDto));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Excluir interesse", description = "Remove um interesse do sistema")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Interesse removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Interesse não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> excluirInteresse(
      @Parameter(description = "ID do interesse") @PathVariable Long id,
      @RequestUser Usuario usuario) {
    interesseService.remover(id, usuario);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Recuperar interesse", description = "Busca um interesse específico por ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Interesse encontrado"),
        @ApiResponse(responseCode = "404", description = "Interesse não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> recuperarInteresse(
      @Parameter(description = "ID do interesse") @PathVariable Long id,
      @RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.OK).body(interesseService.buscarPorId(id, usuario));
  }

  @GetMapping("")
  @Autenticado(TipoAutenticacao.ADMIN)
  @Operation(
      summary = "Listar interesses",
      description = "Lista todos os interesses (apenas para administradores)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de interesses retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(
            responseCode = "403",
            description = "Acesso negado - requer privilégios de administrador")
      })
  public ResponseEntity<?> listarInteresses() {
    return ResponseEntity.status(HttpStatus.OK).body(interesseService.listar());
  }
}
