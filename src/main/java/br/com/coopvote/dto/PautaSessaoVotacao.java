package br.com.coopvote.dto;

import br.com.coopvote.entity.Pauta;

import java.time.LocalDateTime;

public record PautaSessaoVotacao(
    Long idPauta,
    Long idSessaoVotacao,
    String tituloPauta,
    String descricaoPauta,
    boolean abertaPauta,
    LocalDateTime dataAberturaSessaoVotacao,
    LocalDateTime dataFechamentoSessaoVotacao
) {
    public Pauta getPauta() {
        return new Pauta(idPauta, descricaoPauta, tituloPauta, abertaPauta);
    }
}
