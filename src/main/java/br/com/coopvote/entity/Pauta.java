package br.com.coopvote.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pauta")
public class Pauta {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;
    private boolean aberta;

    public Pauta() {
    }

    public Pauta(String descricao, String titulo) {
        this.descricao = descricao;
        this.titulo = titulo;
    }

    public Pauta(String descricao, String titulo, boolean aberta) {
        this.descricao = descricao;
        this.titulo = titulo;
        this.aberta = aberta;
    }


    public Long getId() {
        return id;
    }

}
