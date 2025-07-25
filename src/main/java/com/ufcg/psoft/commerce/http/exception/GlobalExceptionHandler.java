package com.ufcg.psoft.commerce.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommerceException.class)
    public ResponseEntity<ErrorDTO> handleCommerceException(CommerceException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getErrorDTO());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGeneric(Exception ex) {
        var erro = new CommerceException(ErrorCode.INTERNAL_ERROR);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro.getErrorDTO());
    }

}
