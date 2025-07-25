package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.auth.Autenticado;
import com.ufcg.psoft.commerce.auth.TipoAutenticacao;
import com.ufcg.psoft.commerce.auth.Usuario;
import com.ufcg.psoft.commerce.dto.ClienteUpsertDTO;
import com.ufcg.psoft.commerce.http.request.RequestUser;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/clientes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClienteController {

    @Autowired
    ClienteService clienteService;

    @GetMapping("/{id}")
    public ResponseEntity<?> recuperarCliente(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.recuperar(id));
    }

    @GetMapping("")
    @Autenticado(TipoAutenticacao.ADMIN)
    public ResponseEntity<?> listarClientes(
            @RequestParam(required = false, defaultValue = "") String nome) {

        if (nome != null && !nome.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(clienteService.listarPorNome(nome));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.listar());
    }

    @PostMapping()
    public ResponseEntity<?> criarCliente(
            @RequestBody @Valid ClienteUpsertDTO clienteDto) {

        var response = clienteService.criar(clienteDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    @Autenticado()
    public ResponseEntity<?> atualizarCliente(
            @PathVariable Long id,
            @RequestParam String codigo,
            @RequestUser Usuario usuario,
            @RequestBody @Valid ClienteUpsertDTO clienteDto) {
        return ResponseEntity.ok(clienteService.alterar(id, codigo, clienteDto));
    }

    @DeleteMapping("/{id}")
    @Autenticado(TipoAutenticacao.NORMAL)
    public ResponseEntity<?> excluirCliente(
            @PathVariable Long id,
            @RequestParam String codigo) {

        clienteService.remover(id, codigo);
        return ResponseEntity.noContent().build();
    }
}