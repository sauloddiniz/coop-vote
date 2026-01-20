package br.com.coopvote.entity;

import br.com.coopvote.enums.EscolhaVoto;
import jakarta.persistence.*;

@Entity
@Table(name = "voto")
public class Voto {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;
    private String associadoId;
    @Enumerated(EnumType.STRING)
    private EscolhaVoto escolha;

}
