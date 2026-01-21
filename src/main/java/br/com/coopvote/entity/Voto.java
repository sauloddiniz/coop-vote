package br.com.coopvote.entity;

import br.com.coopvote.enums.EscolhaVoto;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "voto")
public class Voto {

    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;
    private String associadoId;
    @Enumerated(EnumType.STRING)
    private EscolhaVoto escolha;
    private LocalDateTime dataHoraVotacao;

    public Voto() {
    }

    public Voto(Pauta pauta, String associadoId, EscolhaVoto escolha) {
        this.pauta = pauta;
        this.associadoId = associadoId;
        this.escolha = escolha;
        this.dataHoraVotacao = LocalDateTime.now();
    }

    public Voto(Long pautaId, String associadoId, EscolhaVoto escolha) {
        this.id = criarIdVoto(pautaId, associadoId);
        this.associadoId = associadoId;
        this.escolha = escolha;
        this.dataHoraVotacao = LocalDateTime.now();
    }

    private String criarIdVoto(Long pautaId, String associadoId) {
        return (pautaId.toString().concat("-").concat(associadoId));
    }

    @PrePersist
    public void prePersist() {
        id = criarIdVoto(pauta.getId(), associadoId);
    }

    public String getId() {
        return id;
    }

    public Pauta getPauta() {
        return pauta;
    }

    public String getAssociadoId() {
        return associadoId;
    }

    public EscolhaVoto getEscolha() {
        return escolha;
    }

    public LocalDateTime getDataHoraVotacao() {
        return dataHoraVotacao;
    }

    public void setPauta(Pauta pauta) {
        this.pauta = pauta;
    }
}
