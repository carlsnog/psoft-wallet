package com.ufcg.psoft.commerce.http.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
  UNAUTHORIZED("Nao autorizado", HttpStatus.UNAUTHORIZED),
  FORBIDDEN("Acesso negado", HttpStatus.FORBIDDEN),
  BAD_REQUEST("Requisicao invalida", HttpStatus.BAD_REQUEST),
  INTERNAL_ERROR("Um erro inesperado aconteceu", HttpStatus.INTERNAL_SERVER_ERROR),

  ATIVO_NAO_ENCONTRADO("Ativo nao encontrado", HttpStatus.NOT_FOUND),
  TIPO_ATIVO_INVALIDO("Tipo de ativo invalido", HttpStatus.BAD_REQUEST),
  OPERACAO_INVALIDA_PARA_O_TIPO("O tipo do ativo não permite a operação", HttpStatus.BAD_REQUEST),
  ATUALIZA_COTACAO_NAO_ATENDE_REQUISITO(
      "O novo valor não respeita a faixa de variação de no mínimo 1% em relação ao valor antigo",
      HttpStatus.BAD_REQUEST),
  ATIVO_JA_EXISTE("Ativo com este nome ja existe", HttpStatus.CONFLICT),

  CLIENTE_NAO_ENCONTRADO("Cliente nao existe", HttpStatus.NOT_FOUND),

  INTERESSE_NAO_ENCONTRADO("Interesse nao encontrado", HttpStatus.NOT_FOUND);

  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }

  public String getMessage() {
    return message;
  }

  public HttpStatus getHttpStatus() {
    return this.httpStatus;
  }

  public ErrorDTO withData(Object data) {
    return new ErrorDTO(this, message, data);
  }
}
