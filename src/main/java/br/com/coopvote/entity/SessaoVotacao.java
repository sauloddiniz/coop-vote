package br.com.coopvote.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessao_votacao")
public class SessaoVotacao {

    @Id
    private Long id;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;

    @OneToOne
    @JoinColumn(name = "pauta_id")
    private Pauta pauta;

    public boolean estaAberta() {
        return LocalDateTime.now().isBefore(dataFechamento);
    }

    public void tempoPadraoParaFechamento() {
        if (dataFechamento == null) {
            dataFechamento = LocalDateTime.now().plusMinutes(15);
        }
    }
}