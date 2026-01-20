package br.com.coopvote.dto;

import br.com.coopvote.entity.SessaoVotacao;
import java.time.LocalDateTime;

public record SessaoVotacaoResponseDto(
        Long id,
        LocalDateTime dataAbertura,
        LocalDateTime dataFechamento,
        Long pautaId
) {
    public static SessaoVotacaoResponseDto de(SessaoVotacao sessao) {
        return new SessaoVotacaoResponseDto(
                sessao.getId(),
                sessao.getDataAbertura(),
                sessao.getDataFechamento(),
                sessao.getPauta() != null ? sessao.getPauta().getId() : null
        );
    }
}
