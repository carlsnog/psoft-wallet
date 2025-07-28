package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.AlterarStatusDTO;
import com.ufcg.psoft.commerce.dto.AtivoCreateDTO;
import com.ufcg.psoft.commerce.dto.AtivoUpdateDTO;
import com.ufcg.psoft.commerce.dto.ValorUpsertDTO;
import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.http.auth.Autenticado;
import com.ufcg.psoft.commerce.http.request.RequestUser;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/ativos")
@Autenticado(TipoAutenticacao.ADMIN)
public class AtivoController {

  @Autowired AtivoService ativoService;

  @PostMapping
  public ResponseEntity<?> criarAtivo(@RequestBody @Valid AtivoCreateDTO ativoDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ativoService.criar(ativoDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> atualizarAtivo(
      @PathVariable Long id, @RequestBody @Valid AtivoUpdateDTO ativoDto) {
    return ResponseEntity.status(HttpStatus.OK).body(ativoService.atualizar(id, ativoDto));
  }

  @PutMapping("/{id}/cotacao")
  @Autenticado(TipoAutenticacao.ADMIN)
  public ResponseEntity<?> atualizarCotacao(
      @PathVariable Long id, @RequestBody @Valid ValorUpsertDTO valorUpsertDto) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(ativoService.atualizarCotacao(id, valorUpsertDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> excluirAtivo(@PathVariable Long id) {
    ativoService.remover(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> recuperarAtivo(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(ativoService.buscarPorId(id));
  }

  @GetMapping("")
  @Autenticado(TipoAutenticacao.NORMAL)
  public ResponseEntity<?> listarAtivos(@RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.OK).body(ativoService.listar(usuario));
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<?> atualizarStatus(
      @PathVariable Long id, @RequestBody @Valid AlterarStatusDTO dto) {
    return ResponseEntity.ok(ativoService.alterarStatus(id, dto.getNovoStatus()));
  }
}
