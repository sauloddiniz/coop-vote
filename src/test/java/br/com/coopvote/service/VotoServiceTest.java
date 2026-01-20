package br.com.coopvote.service;

import br.com.coopvote.dto.VotoRequestDto;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.entity.SessaoVotacao;
import br.com.coopvote.entity.Voto;
import br.com.coopvote.enums.EscolhaVoto;
import br.com.coopvote.exceptions.PautaFechadaException;
import br.com.coopvote.exceptions.SessaoFechadaException;
import br.com.coopvote.repository.PautaRepository;
import br.com.coopvote.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private VotoService votoService;

    @Test
    @DisplayName("Deve registrar um voto com sucesso enviando para a fila")
    void deveRegistrarVotoComSucesso() {
        Long pautaId = 1L;
        VotoRequestDto request = new VotoRequestDto(pautaId, "associado-1", EscolhaVoto.SIM);
        Pauta pauta = new Pauta(pautaId, "Desc", "Título", true);
        SessaoVotacao sessao = new SessaoVotacao(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1), pauta);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        assertDoesNotThrow(() -> votoService.votar(request));

        verify(rabbitTemplate).convertAndSend(eq(VotoService.VOTO_QUEUE), any(Voto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a pauta estiver com status fechada")
    void deveLancarExcecaoQuandoPautaFechadaNoStatus() {
        Long pautaId = 1L;
        VotoRequestDto request = new VotoRequestDto(pautaId, "associado-1", EscolhaVoto.SIM);
        Pauta pauta = new Pauta("Desc", "Título", false);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));

        PautaFechadaException exception = assertThrows(PautaFechadaException.class, () -> votoService.votar(request));

        assertEquals("Não é possível votar em uma pauta fechada.", exception.getMessage());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a sessão de votação expirou pelo tempo")
    void deveLancarExcecaoQuandoSessaoExpirouPeloTempo() {
        Long pautaId = 1L;
        VotoRequestDto request = new VotoRequestDto(pautaId, "associado-1", EscolhaVoto.SIM);
        Pauta pauta = new Pauta(pautaId, "Desc", "Título", true);
        // Sessão expirada há 1 segundo
        SessaoVotacao sessao = new SessaoVotacao(LocalDateTime.now().minusMinutes(2), LocalDateTime.now().minusSeconds(1), pauta);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        SessaoFechadaException exception = assertThrows(SessaoFechadaException.class, () -> votoService.votar(request));

        assertEquals("A sessão de votação para esta pauta já expirou.", exception.getMessage());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    @DisplayName("Deve lançar exceção quando não existe sessão para a pauta")
    void deveLancarExcecaoQuandoNaoExisteSessao() {
        Long pautaId = 1L;
        VotoRequestDto request = new VotoRequestDto(pautaId, "associado-1", EscolhaVoto.SIM);
        Pauta pauta = new Pauta(pautaId, "Desc", "Título", true);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(pautaId)).thenReturn(Optional.empty());

        SessaoFechadaException exception = assertThrows(SessaoFechadaException.class, () -> votoService.votar(request));

        assertEquals("Não existe sessão de votação aberta para esta pauta.", exception.getMessage());
        verifyNoInteractions(rabbitTemplate);
    }
}
