package com.ufcg.psoft.commerce.http.exception;

import org.springframework.http.HttpStatus;

public class CommerceException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final ErrorDTO errorDTO;
  private final HttpStatus status;

  public CommerceException(ErrorDTO errorDTO) {
    super(errorDTO.getMessage());
    this.errorDTO = errorDTO;
    this.status = errorDTO.getCode().getHttpStatus();
  }

  public CommerceException(ErrorCode errorType) {
    this(errorType.withData(null));
  }

  public CommerceException(ErrorCode errorType, Object data) {
    this(errorType.withData(data));
  }

  @Override
  public String getMessage() {
    return errorDTO.getMessage();
  }

  public ErrorDTO getErrorDTO() {
    return errorDTO;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
