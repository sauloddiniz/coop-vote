package br.com.coopvote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa o resultado da votação de uma pauta")
public record ResultadoVotacaoResponseDto(
        @Schema(description = "ID da pauta")
        Long pautaId,
        @Schema(description = "Título da pauta")
        String titulo,
        @Schema(description = "Total de votos 'SIM'")
        Long totalSim,
        @Schema(description = "Total de votos 'NAO'")
        Long totalNao,
        @Schema(description = "Total geral de votos")
        Long totalVotos,
        @Schema(description = "Resultado final da votação")
        String resultado
) {
}
