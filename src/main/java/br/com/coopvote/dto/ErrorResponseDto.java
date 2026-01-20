package br.com.coopvote.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto(
        LocalDateTime timestamp,
        Integer status,
        String path,
        String mensagem,
        List<FieldError> erros
) {
    public ErrorResponseDto(LocalDateTime now, int value, String message) {
        this(now, value, null, message, null);
    }

    public record FieldError(String campo, String mensagem) {}
}
