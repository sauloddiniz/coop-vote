package br.com.coopvote.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessao_votacao")
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;

    @OneToOne
    @JoinColumn(name = "pauta_id")
    private Pauta pauta;

    public SessaoVotacao() {
    }

    public SessaoVotacao(LocalDateTime dataAbertura, LocalDateTime dataFechamento, Pauta pauta) {
        this.dataAbertura = dataAbertura;
        this.dataFechamento = dataFechamento;
        this.pauta = pauta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public Pauta getPauta() {
        return pauta;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public void setPauta(Pauta pauta) {
        this.pauta = pauta;
    }

    public boolean estaAberta() {
        return LocalDateTime.now().isBefore(dataFechamento);
    }

    public LocalDateTime tempoPadraoParaFechamento() {
        return LocalDateTime.now().plusMinutes(1);
    }

}