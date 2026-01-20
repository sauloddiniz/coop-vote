package br.com.coopvote.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record SessaoVotacaoRequestDto(
        @NotNull(message = "O ID da pauta é obrigatório")
        Long pautaId,
        LocalDateTime dataFechamento
) {
}
