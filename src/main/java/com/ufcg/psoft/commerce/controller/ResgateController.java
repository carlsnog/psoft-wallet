package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.ResgateConfirmacaoDTO;
import com.ufcg.psoft.commerce.dto.ResgateCreateDTO;
import com.ufcg.psoft.commerce.dto.ResgateResponseDTO;
import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.http.auth.Autenticado;
import com.ufcg.psoft.commerce.http.request.RequestUser;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.service.resgate.ResgateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resgates")
@Autenticado(TipoAutenticacao.NORMAL)
@Tag(name = "Resgates", description = "API para gerenciamento de resgates de ativos")
public class ResgateController {

  private final ResgateService resgateService;

  @Autowired
  public ResgateController(ResgateService resgateService) {
    this.resgateService = resgateService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Solicitar resgate", description = "Solicita um novo resgate de ativos")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Resgate solicitado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(
            responseCode = "403",
            description = "Apenas clientes podem solicitar resgates"),
        @ApiResponse(responseCode = "404", description = "Ativo não encontrado")
      })
  public ResponseEntity<ResgateResponseDTO> criar(
      @RequestUser Usuario usuario, @Valid @RequestBody ResgateCreateDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(resgateService.criar(usuario, dto));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Recuperar resgate", description = "Busca um resgate específico por ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Resgate encontrado"),
        @ApiResponse(responseCode = "404", description = "Resgate não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<ResgateResponseDTO> recuperar(
      @Parameter(description = "ID do resgate") @PathVariable Long id,
      @RequestUser Usuario usuario) {
    return ResponseEntity.ok(resgateService.recuperar(usuario, id));
  }

  @GetMapping
  @Operation(summary = "Listar resgates", description = "Lista todos os resgates do usuário")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Lista de resgates retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<List<ResgateResponseDTO>> listar(@RequestUser Usuario usuario) {
    return ResponseEntity.ok(resgateService.listar(usuario));
  }

  @PostMapping("/{id}/confirmar")
  @Operation(summary = "Confirmar resgate", description = "Confirma um resgate específico")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Resgate confirmado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Resgate não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(
            responseCode = "403",
            description = "Apenas administradores podem confirmar resgates"),
        @ApiResponse(responseCode = "409", description = "Conflito de status do resgate")
      })
  public ResponseEntity<ResgateResponseDTO> confirmar(
      @Parameter(description = "ID do resgate") @PathVariable Long id,
      @Valid @RequestBody ResgateConfirmacaoDTO dto,
      @RequestUser Usuario usuario) {
    return ResponseEntity.ok(resgateService.confirmar(usuario, id, dto));
  }
}
