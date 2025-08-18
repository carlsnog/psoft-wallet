package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.CompraConfirmacaoDTO;
import com.ufcg.psoft.commerce.dto.CompraCreateDTO;
import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.http.auth.Autenticado;
import com.ufcg.psoft.commerce.http.request.RequestUser;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.service.compra.CompraService;
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
@RequestMapping(value = "/compras")
@Autenticado(TipoAutenticacao.NORMAL)
@Tag(name = "Compras", description = "API para gerenciamento de compras de ativos")
public class CompraController {

  @Autowired CompraService compraService;

  @PostMapping("")
  @Operation(summary = "Solicitar compra", description = "Solicita uma nova compra de ativos")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Compra solicitada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Ativo não encontrado")
      })
  public ResponseEntity<?> criarCompra(
      @RequestBody @Valid CompraCreateDTO compraDto, @RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.CREATED).body(compraService.criar(usuario, compraDto));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Recuperar compra", description = "Busca uma compra específica por ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Compra encontrada"),
        @ApiResponse(responseCode = "404", description = "Compra não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> recuperarCompra(
      @Parameter(description = "ID da compra") @PathVariable Long id,
      @RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.OK).body(compraService.recuperar(usuario, id));
  }

  @GetMapping("")
  @Operation(summary = "Listar compras", description = "Lista todas as compras do usuário")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Lista de compras retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> listarCompras(@RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.OK).body(compraService.listar(usuario));
  }

  @PostMapping("/{id}/confirmar")
  @Operation(summary = "Confirmar compra", description = "Confirma uma compra específica")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Compra confirmada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Compra não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "409", description = "Conflito entre compras")
      })
  public ResponseEntity<?> confirmarCompra(
      @Parameter(description = "ID da compra") @PathVariable Long id,
      @RequestBody @Valid CompraConfirmacaoDTO confirmacaoDto,
      @RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(compraService.confirmar(usuario, id, confirmacaoDto));
  }
}
