
package com.ufcg.psoft.commerce.http.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    UNAUTHORIZED("Codigo de acesso invalido", HttpStatus.UNAUTHORIZED), FORBIDDEN("Acesso negado",
            HttpStatus.FORBIDDEN), BAD_REQUEST("Requisicao invalida", HttpStatus.BAD_REQUEST), INTERNAL_ERROR(
                    "Um erro inesperado aconteceu", HttpStatus.INTERNAL_SERVER_ERROR),

    ATIVO_NAO_ENCONTRADO("Ativo não encontrado", HttpStatus.NOT_FOUND), ALTERACAO_TIPO_NAO_PERMITIDA(
            "Não é permitido alterar o tipo do ativo",
            HttpStatus.BAD_REQUEST), TIPO_ATIVO_INVALIDO("Tipo de ativo inválido", HttpStatus.BAD_REQUEST),

    CLIENTE_NAO_EXISTE("Cliente não existe", HttpStatus.NOT_FOUND), COD_ACESSO_INVALIDO(
            "O código acesso deve ser composto por 6 digítos", HttpStatus.BAD_REQUEST);

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
