package com.ufcg.psoft.commerce.http.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    UNAUTHORIZED("Codigo de acesso invalido", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("Acesso negado", HttpStatus.FORBIDDEN),
    BAD_REQUEST("Requisicao invalida", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("Um erro inesperado aconteceu", HttpStatus.INTERNAL_SERVER_ERROR),

    CLIENTE_NAO_EXISTE("Cliente nao existe", HttpStatus.NOT_FOUND),
    COD_ACESSO_INVALIDO("O codigo acesso deve ser composto por 6 digitos", HttpStatus.BAD_REQUEST);

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
