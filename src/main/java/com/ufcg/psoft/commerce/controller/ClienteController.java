package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.ClienteUpsertDTO;
import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.http.auth.Autenticado;
import com.ufcg.psoft.commerce.http.request.RequestUser;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/clientes")
@Autenticado(TipoAutenticacao.NORMAL)
public class ClienteController {

  @Autowired ClienteService clienteService;

  @GetMapping("/{id}")
  public ResponseEntity<?> recuperarCliente(@PathVariable Long id, @RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.OK).body(clienteService.recuperar(usuario, id));
  }

  @GetMapping("")
  @Autenticado(TipoAutenticacao.ADMIN)
  public ResponseEntity<?> listarClientes(
      @RequestParam(required = false, defaultValue = "") String nome) {
    if (nome != null && !nome.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).body(clienteService.listarPorNome(nome));
    }
    return ResponseEntity.status(HttpStatus.OK).body(clienteService.listar());
  }

  @PostMapping
  @Autenticado(TipoAutenticacao.PUBLICA)
  public ResponseEntity<?> criarCliente(@RequestBody @Valid ClienteUpsertDTO clienteDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(this.clienteService.criar(clienteDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> atualizarCliente(
      @PathVariable Long id,
      @RequestUser Usuario usuario,
      @RequestBody @Valid ClienteUpsertDTO clienteDto) {
    return ResponseEntity.ok(clienteService.alterar(usuario, id, clienteDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> excluirCliente(@PathVariable Long id) {
    clienteService.remover(id);
    return ResponseEntity.noContent().build();
  }
}
