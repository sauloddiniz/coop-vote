package br.com.coopvote.dto;

import br.com.coopvote.entity.Pauta;

public record PautaResponse(
        Long id,
        String titulo,
        String descricao,
        boolean aberta
) {
    public static PautaResponse de(Pauta pauta) {
        return new PautaResponse(pauta.getId(), pauta.getTitulo(), pauta.getDescricao(), pauta.isAberta());
    }
}