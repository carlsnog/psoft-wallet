package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.AtivoUpsertDTO;
import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.http.auth.Autenticado;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/ativos")
public class AtivoController {

  @Autowired AtivoService ativoService;

  @PostMapping
  @Autenticado(TipoAutenticacao.ADMIN)
  public ResponseEntity<?> criarAtivo(@RequestBody @Valid AtivoUpsertDTO ativoDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ativoService.criar(ativoDto));
  }

  @PutMapping("/{id}")
  @Autenticado(TipoAutenticacao.ADMIN)
  public ResponseEntity<?> atualizarAtivo(
      @PathVariable Long id, @RequestBody @Valid AtivoUpsertDTO ativoDto) {
    return ResponseEntity.status(HttpStatus.OK).body(ativoService.atualizar(id, ativoDto));
  }

  @DeleteMapping("/{id}")
  @Autenticado(TipoAutenticacao.ADMIN)
  public ResponseEntity<?> excluirAtivo(@PathVariable Long id) {
    ativoService.remover(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  @Autenticado(TipoAutenticacao.ADMIN)
  public ResponseEntity<?> recuperarAtivo(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(ativoService.buscarPorId(id));
  }

  @GetMapping("")
  @Autenticado(TipoAutenticacao.ADMIN)
  public ResponseEntity<?> listarAtivos() {
    return ResponseEntity.status(HttpStatus.OK).body(ativoService.listarTodos());
  }
}
