package com.ufcg.psoft.commerce.http.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
  UNAUTHORIZED("Nao autorizado", HttpStatus.UNAUTHORIZED),
  FORBIDDEN("Acesso negado", HttpStatus.FORBIDDEN),
  BAD_REQUEST("Requisicao invalida", HttpStatus.BAD_REQUEST),
  INTERNAL_ERROR("Um erro inesperado aconteceu", HttpStatus.INTERNAL_SERVER_ERROR),
  JSON_INVALID("Corpo da requisição inválido ou malformado", HttpStatus.BAD_REQUEST),
  ACAO_APENAS_ADMIN("Apenas administradores podem realizar esta ação", HttpStatus.FORBIDDEN),
  ACAO_APENAS_CLIENTE_DONO_COMPRA("Apenas o cliente proprietário pode realizar esta ação", HttpStatus.FORBIDDEN),
  CLIENTE_NAO_ENCONTRADO("Cliente nao existe", HttpStatus.NOT_FOUND),

  ATIVO_NAO_ENCONTRADO("Ativo nao encontrado", HttpStatus.NOT_FOUND),
  TIPO_ATIVO_INVALIDO("Tipo de ativo invalido", HttpStatus.BAD_REQUEST),
  ATIVO_NAO_DISPONIVEL("O ativo nao esta disponivel", HttpStatus.BAD_REQUEST),
  ATIVO_JA_ESTA_NO_STATUS("O ativo ja esta no status informado", HttpStatus.BAD_REQUEST),
  OPERACAO_INVALIDA_PARA_O_TIPO("O tipo do ativo não permite a operação", HttpStatus.BAD_REQUEST),
  ATUALIZA_COTACAO_NAO_ATENDE_REQUISITO(
      "O novo cotacao não respeita a faixa de variação de no mínimo 1% em relação ao cotacao antigo",
      HttpStatus.BAD_REQUEST),
  ATIVO_JA_EXISTE("Ativo com este nome ja existe", HttpStatus.CONFLICT),

  CONFIRMANDO_COMPRA_FINALIZADA("A compra ja foi finalizada", HttpStatus.BAD_REQUEST),


  INTERESSE_NAO_ENCONTRADO("Interesse nao encontrado", HttpStatus.NOT_FOUND),
  INTERESSE_COTACAO_ATIVO_NAO_DISPONIVEL(
      "O ativo nao esta disponivel, nao e possivel demonstrar interesse por cotacao",
      HttpStatus.BAD_REQUEST),
  INTERESSE_DISPONIBILIDADE_ATIVO_JA_DISPONIVEL(
      "O ativo ja esta disponivel, nao e possivel demonstrar interesse por disponibilidade",
      HttpStatus.BAD_REQUEST);

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
