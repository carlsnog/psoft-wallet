
package com.ufcg.psoft.commerce.http.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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
