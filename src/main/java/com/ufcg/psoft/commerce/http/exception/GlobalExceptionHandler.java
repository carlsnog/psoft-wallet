package com.ufcg.psoft.commerce.http.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

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

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ErrorDTO> onHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    log.warn("Corpo da requisição inválido ou malformado", ex);

    ErrorDTO errorDTO =
        new ErrorDTO(ErrorCode.JSON_INVALID, "Corpo da requisição inválido ou malformado");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ErrorDTO> onMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    log.warn("Parâmetro de rota/query inválido: {}", ex.getName(), ex);
    ErrorDTO errorDTO = new ErrorDTO(ErrorCode.BAD_REQUEST, "Parâmetro inválido: " + ex.getName());
    return ResponseEntity.badRequest().body(errorDTO);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  @ResponseBody
  public ResponseEntity<ErrorDTO> onMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
    log.warn("Método HTTP não permitido: {}", ex.getMethod(), ex);
    ErrorDTO errorDTO =
        new ErrorDTO(ErrorCode.FORBIDDEN, "Método HTTP não permitido: " + ex.getMethod());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorDTO);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ResponseEntity<ErrorDTO> onNoHandlerFound(NoHandlerFoundException ex) {
    log.warn("Rota não encontrada: {}", ex.getRequestURL(), ex);
    ErrorDTO errorDTO =
        new ErrorDTO(ErrorCode.BAD_REQUEST, "Rota não encontrada: " + ex.getRequestURL());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ErrorDTO> onMissingRequestHeader(MissingRequestHeaderException ex) {
    log.warn("Header obrigatório ausente: {}", ex.getHeaderName(), ex);
    ErrorDTO errorDTO =
        new ErrorDTO(ErrorCode.BAD_REQUEST, "Header obrigatório ausente: " + ex.getHeaderName());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
  }
}
