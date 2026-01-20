package br.com.coopvote.dto;

import br.com.coopvote.enums.EscolhaVoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VotoRequestDto(
        @NotNull(message = "O ID da pauta é obrigatório")
        Long pautaId,
        @NotBlank(message = "O ID do associado é obrigatório")
        String associadoId,
        @NotNull(message = "O voto (SIM/NAO) é obrigatório")
        EscolhaVoto escolha
) {
}
