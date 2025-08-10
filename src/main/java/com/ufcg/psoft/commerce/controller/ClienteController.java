package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.ClienteUpsertDTO;
import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.http.auth.Autenticado;
import com.ufcg.psoft.commerce.http.request.RequestUser;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
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
@RequestMapping(value = "/clientes")
@Autenticado(TipoAutenticacao.NORMAL)
@Tag(name = "Clientes", description = "API para gerenciamento de clientes")
public class ClienteController {

  @Autowired ClienteService clienteService;

  @GetMapping("/{id}")
  @Operation(summary = "Recuperar cliente", description = "Busca um cliente específico por ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> recuperarCliente(
      @Parameter(description = "ID do cliente") @PathVariable Long id,
      @RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.OK).body(clienteService.recuperar(usuario, id));
  }

  @GetMapping("")
  @Autenticado(TipoAutenticacao.ADMIN)
  @Operation(
      summary = "Listar clientes",
      description = "Lista todos os clientes ou filtra por nome")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> listarClientes(
      @Parameter(description = "Nome do cliente para filtrar (opcional)")
          @RequestParam(required = false, defaultValue = "")
          String nome) {
    if (nome != null && !nome.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).body(clienteService.listarPorNome(nome));
    }
    return ResponseEntity.status(HttpStatus.OK).body(clienteService.listar());
  }

  @PostMapping
  @Autenticado(TipoAutenticacao.PUBLICA)
  @Operation(summary = "Criar cliente", description = "Cria um novo cliente no sistema")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
      })
  public ResponseEntity<?> criarCliente(@RequestBody @Valid ClienteUpsertDTO clienteDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(this.clienteService.criar(clienteDto));
  }

  @PutMapping("/{id}")
  @Autenticado()
  @Operation(
      summary = "Atualizar cliente",
      description = "Atualiza os dados de um cliente existente")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> atualizarCliente(
      @Parameter(description = "ID do cliente") @PathVariable Long id,
      @RequestUser Usuario usuario,
      @RequestBody @Valid ClienteUpsertDTO clienteDto) {
    return ResponseEntity.ok(clienteService.alterar(usuario, id, clienteDto));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Excluir cliente", description = "Remove um cliente do sistema")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
      })
  public ResponseEntity<?> excluirCliente(
      @Parameter(description = "ID do cliente") @PathVariable Long id) {
    clienteService.remover(id);
    return ResponseEntity.noContent().build();
  }
}
