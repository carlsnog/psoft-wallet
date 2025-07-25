package com.ufcg.psoft.commerce.http.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    UNAUTHORIZED("Código de acesso inválido", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("Sem permissão para acessar este recurso", HttpStatus.FORBIDDEN),
    BAD_REQUEST("Requisição inválida", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("Um erro inesperado aconteceu", HttpStatus.INTERNAL_SERVER_ERROR),

    CLIENTE_NAO_EXISTE("Cliente não existe", HttpStatus.NOT_FOUND);

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
