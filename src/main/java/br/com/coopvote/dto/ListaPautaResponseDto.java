package br.com.coopvote.dto;

import java.util.List;

public record ListaPautaResponseDto(
        List<PautaResponse> pautas_abertas,
        List<PautaResponse> pautas_encerradas
) {
}
