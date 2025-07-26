package com.ufcg.psoft.commerce.http.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ErrorDTO {
    @JsonProperty("code")
    private ErrorCode code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private Object data;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    public ErrorDTO(ErrorCode code, String message) {
        this(code, message, null);
    }

    public ErrorDTO(ErrorCode code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}