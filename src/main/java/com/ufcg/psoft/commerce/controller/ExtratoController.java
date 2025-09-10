package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.*;
import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.http.auth.Autenticado;
import com.ufcg.psoft.commerce.http.request.RequestUser;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.service.compra.CompraService;
import com.ufcg.psoft.commerce.service.extrato.AdminExtratoService;
import com.ufcg.psoft.commerce.service.resgate.ResgateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/extrato")
@Autenticado(TipoAutenticacao.NORMAL)
@Tag(
    name = "Extrato",
    description = "API para consulta de operações financeiras (compras e resgates)")
public class ExtratoController {

  private final CompraService compraService;
  private final ResgateService resgateService;
  private final AdminExtratoService adminExtratoService;

  public ExtratoController(
      CompraService compraService,
      ResgateService resgateService,
      AdminExtratoService adminExtratoService) {
    this.compraService = compraService;
    this.resgateService = resgateService;
    this.adminExtratoService = adminExtratoService;
  }

  @GetMapping("/compras")
  @Operation(
      summary = "Listar compras",
      description = "Lista todas as compras realizadas pelo usuário autenticado")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Lista de compras retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<Page<CompraResponseDTO>> listarCompras(
      @RequestUser Usuario usuario,
      @Parameter(description = "Filtros de busca de compras") CompraFilterDTO filter,
      @Parameter(description = "Paginação e ordenação") Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(compraService.listar(usuario, filter, pageable));
  }

  @GetMapping("/resgates")
  @Operation(
      summary = "Listar resgates",
      description = "Lista todos os resgates realizados pelo usuário autenticado")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Lista de resgates retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<Page<ResgateResponseDTO>> listarResgates(
      @RequestUser Usuario usuario,
      @Parameter(description = "Filtros de busca de resgates") ResgateFilterDTO filter,
      @Parameter(description = "Paginação e ordenação") Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(resgateService.listar(usuario, filter, pageable));
  }

  @Autenticado(TipoAutenticacao.ADMIN)
  @GetMapping("/admin/operacoes")
  @Operation(
      summary = "Buscar operações (admin)",
      description = "Permite que administradores consultem todas as operações do sistema")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Operações retornadas com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<Page<OperacaoResponseDTO>> buscarOperacoesAdmin(
      @Parameter(description = "Filtros de busca de operações") OperacaoFilterDTO filter,
      @Parameter(description = "Paginação e ordenação") Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(adminExtratoService.buscarOperacoes(filter, pageable));
  }
}
