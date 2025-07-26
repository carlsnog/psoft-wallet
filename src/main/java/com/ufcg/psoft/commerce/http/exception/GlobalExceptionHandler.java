package com.ufcg.psoft.commerce.http.exception;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CommerceException.class)
    public ResponseEntity<ErrorDTO> handleCommerceException(CommerceException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getErrorDTO());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGeneric(Exception ex) {
        logger.error("Erro inesperado", ex.getMessage());

        var erro = new CommerceException(ErrorCode.INTERNAL_ERROR);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro.getErrorDTO());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorDTO> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorDTO errorDTO = new ErrorDTO(
                ErrorCode.BAD_REQUEST,
                "Erros de validacao encontrados");

        var errors = new ArrayList<String>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getDefaultMessage());
        }
        errorDTO.setData(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorDTO> onConstraintViolationException(ConstraintViolationException e) {
        ErrorDTO errorDTO = new ErrorDTO(
                ErrorCode.BAD_REQUEST,
                "Erros de validacao encontrados");

        var errors = new ArrayList<String>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }
        errorDTO.setData(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorDTO> onMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        ErrorDTO errorDTO = new ErrorDTO(
                ErrorCode.BAD_REQUEST,
                "Erros de validacao encontrados");

        var errors = new ArrayList<String>();
        errors.add(e.getMessage());
        errorDTO.setData(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }
}
