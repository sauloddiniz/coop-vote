package br.com.coopvote.enums;

public enum StatusVotacao {
    APROVADA, REPROVADA, EMPATE, SEM_VOTOS;

    public static StatusVotacao obterResultado(long totalSim, long totalNao) {
        if (totalSim == 0 && totalNao == 0) return SEM_VOTOS;
        if (totalSim > totalNao) return APROVADA;
        if (totalNao > totalSim) return REPROVADA;
        return EMPATE;
    }
}
