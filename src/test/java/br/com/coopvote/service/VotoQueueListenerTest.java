package br.com.coopvote.service;

import br.com.coopvote.dto.VotoQueue;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.entity.SessaoVotacao;
import br.com.coopvote.entity.Voto;
import br.com.coopvote.enums.EscolhaVoto;
import br.com.coopvote.repository.VotoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoQueueListenerTest {

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private VotoQueueListener votoQueueListener;

    @Test
    @DisplayName("Deve receber e salvar o voto com sucesso")
    void deveReceberESalvarVoto() {
        Pauta pauta = new Pauta(1L, "Desc", "Título", true);
        String cpf = "12345678901";
        Voto voto = new Voto(pauta, cpf, EscolhaVoto.SIM);
        SessaoVotacao sessao = new SessaoVotacao(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1), pauta);
        VotoQueue votoQueue = new VotoQueue(voto, sessao);

        votoQueueListener.receberVoto(votoQueue);

        verify(votoRepository, times(1)).save(voto);
    }

    @Test
    @DisplayName("Deve descartar o voto quando a sessão já estiver fechada no momento do processamento")
    void deveDescartarVotoQuandoSessaoFechada() {
        Pauta pauta = new Pauta(1L, "Desc", "Título", true);
        String cpf = "12345678901";
        Voto voto = new Voto(pauta, cpf, EscolhaVoto.SIM);
        // Sessão fechada há 1 minuto
        SessaoVotacao sessao = new SessaoVotacao(LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(1), pauta);
        VotoQueue votoQueue = new VotoQueue(voto, sessao);

        votoQueueListener.receberVoto(votoQueue);

        verifyNoInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve logar erro mas não propagar exceção ao falhar no salvamento")
    void deveTratarErroAoSalvarVoto() {
        Pauta pauta = new Pauta(1L, "Desc", "Título", true);
        String cpf = "12345678901";
        Voto voto = new Voto(pauta, cpf, EscolhaVoto.SIM);
        SessaoVotacao sessao = new SessaoVotacao(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1), pauta);
        VotoQueue votoQueue = new VotoQueue(voto, sessao);

        when(votoRepository.save(any(Voto.class))).thenThrow(new RuntimeException("Erro de banco"));

        votoQueueListener.receberVoto(votoQueue);

        verify(votoRepository, times(1)).save(voto);
    }
}
