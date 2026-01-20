package br.com.coopvote.service;

import br.com.coopvote.client.UserInfoClient;
import br.com.coopvote.dto.UserInfoResponseDto;
import br.com.coopvote.dto.VotoQueue;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.entity.SessaoVotacao;
import br.com.coopvote.entity.Voto;
import br.com.coopvote.enums.EscolhaVoto;
import br.com.coopvote.repository.VotoRepository;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoQueueListenerTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private UserInfoClient userInfoClient;

    @InjectMocks
    private VotoQueueListener votoQueueListener;

    @Test
    @DisplayName("Deve receber e salvar o voto com sucesso quando associado está autorizado")
    void deveReceberESalvarVoto() {
        Pauta pauta = new Pauta(1L, "Desc", "Título", true);
        String cpf = "12345678901";
        Voto voto = new Voto(pauta, cpf, EscolhaVoto.SIM);
        VotoQueue votoQueue = new VotoQueue(voto, new SessaoVotacao());

        when(userInfoClient.getInfo(cpf)).thenReturn(new UserInfoResponseDto("ABLE_TO_VOTE"));

        votoQueueListener.receberVoto(votoQueue);

        verify(votoRepository, times(1)).save(voto);
    }

    @Test
    @DisplayName("Deve descartar o voto quando o associado não estiver autorizado")
    void deveDescartarVotoQuandoAssociadoNaoAutorizado() {
        Pauta pauta = new Pauta(1L, "Desc", "Título", true);
        String cpf = "12345678901";
        Voto voto = new Voto(pauta, cpf, EscolhaVoto.SIM);
        VotoQueue votoQueue = new VotoQueue(voto, new SessaoVotacao());

        when(userInfoClient.getInfo(cpf)).thenReturn(new UserInfoResponseDto("UNABLE_TO_VOTE"));

        votoQueueListener.receberVoto(votoQueue);

        verifyNoInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve descartar o voto quando o CPF for inválido")
    void deveDescartarVotoQuandoCpfInvalido() {
        Pauta pauta = new Pauta(1L, "Desc", "Título", true);
        String cpf = "99999999999";
        Voto voto = new Voto(pauta, cpf, EscolhaVoto.SIM);
        VotoQueue votoQueue = new VotoQueue(voto, new SessaoVotacao());

        when(userInfoClient.getInfo(cpf)).thenThrow(mock(FeignException.NotFound.class));

        votoQueueListener.receberVoto(votoQueue);

        verifyNoInteractions(votoRepository);
    }

    @Test
    @DisplayName("Deve logar erro mas não propagar exceção ao falhar no salvamento")
    void deveTratarErroAoSalvarVoto() {
        Pauta pauta = new Pauta(1L, "Desc", "Título", true);
        String cpf = "12345678901";
        Voto voto = new Voto(pauta, cpf, EscolhaVoto.SIM);
        VotoQueue votoQueue = new VotoQueue(voto, new SessaoVotacao());

        when(userInfoClient.getInfo(cpf)).thenReturn(new UserInfoResponseDto("ABLE_TO_VOTE"));
        when(votoRepository.save(any(Voto.class))).thenThrow(new RuntimeException("Erro de banco"));

        votoQueueListener.receberVoto(votoQueue);

        verify(votoRepository, times(1)).save(voto);
    }
}
