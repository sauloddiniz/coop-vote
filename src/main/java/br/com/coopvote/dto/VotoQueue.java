package br.com.coopvote.dto;

import br.com.coopvote.entity.SessaoVotacao;
import br.com.coopvote.entity.Voto;

public record VotoQueue(Voto voto, SessaoVotacao sessaoVotacao) {
}
