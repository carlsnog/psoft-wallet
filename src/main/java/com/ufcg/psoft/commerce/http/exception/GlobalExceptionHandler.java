package com.ufcg.psoft.commerce.http.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(CommerceException.class)
  public ResponseEntity<ErrorDTO> handleCommerceException(CommerceException ex) {
    return ResponseEntity.status(ex.getStatus()).body(ex.getErrorDTO());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDTO> handleGeneric(Exception ex) {
    log.error("Erro inesperado", ex);

    var erro = new CommerceException(ErrorCode.INTERNAL_ERROR);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro.getErrorDTO());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ErrorDTO> onMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    ErrorDTO errorDTO = new ErrorDTO(ErrorCode.BAD_REQUEST, "Erros de validacao encontrados");

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
    ErrorDTO errorDTO = new ErrorDTO(ErrorCode.BAD_REQUEST, "Erros de validacao encontrados");

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
    ErrorDTO errorDTO = new ErrorDTO(ErrorCode.BAD_REQUEST, "Erros de validacao encontrados");

    var errors = new ArrayList<String>();
    errors.add(e.getMessage());
    errorDTO.setData(errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
  }
}
