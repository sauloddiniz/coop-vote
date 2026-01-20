package br.com.coopvote.service;

import br.com.coopvote.dto.SessaoVotacaoRequestDto;
import br.com.coopvote.dto.SessaoVotacaoResponseDto;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.entity.SessaoVotacao;
import br.com.coopvote.exceptions.PautaFechadaException;
import br.com.coopvote.exceptions.PautaNaoEncontradaException;
import br.com.coopvote.repository.PautaRepository;
import br.com.coopvote.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private SessaoVotacaoService sessaoVotacaoService;

    @Test
    @DisplayName("Deve abrir uma sessão de votação com sucesso usando tempo padrão")
    void deveAbrirSessaoComSucessoTempoPadrao() {
        Long pautaId = 1L;
        SessaoVotacaoRequestDto request = new SessaoVotacaoRequestDto(pautaId, null);
        Pauta pauta = new Pauta("Descrição", "Título", true);
        ReflectionTestUtils.setField(pauta, "id", pautaId);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenAnswer(invocation -> {
            SessaoVotacao sessao = invocation.getArgument(0);
            ReflectionTestUtils.setField(sessao, "id", 1L);
            return sessao;
        });

        SessaoVotacaoResponseDto response = sessaoVotacaoService.abrirSessao(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(pautaId, response.pautaId());
        assertNotNull(response.dataAbertura());
        assertNotNull(response.dataFechamento());
        assertTrue(response.dataFechamento().isAfter(response.dataAbertura()));
        
        verify(pautaRepository).findById(pautaId);
        verify(sessaoVotacaoRepository).save(any(SessaoVotacao.class));
    }

    @Test
    @DisplayName("Deve abrir uma sessão de votação com sucesso usando tempo fornecido")
    void deveAbrirSessaoComSucessoTempoFornecido() {
        Long pautaId = 1L;
        LocalDateTime dataFechamento = LocalDateTime.now().plusHours(1);
        SessaoVotacaoRequestDto request = new SessaoVotacaoRequestDto(pautaId, dataFechamento);
        Pauta pauta = new Pauta("Descrição", "Título", true);
        ReflectionTestUtils.setField(pauta, "id", pautaId);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenAnswer(invocation -> {
            SessaoVotacao sessao = invocation.getArgument(0);
            ReflectionTestUtils.setField(sessao, "id", 1L);
            return sessao;
        });

        SessaoVotacaoResponseDto response = sessaoVotacaoService.abrirSessao(request);

        assertNotNull(response);
        assertEquals(dataFechamento, response.dataFechamento());
        
        verify(pautaRepository).findById(pautaId);
        verify(sessaoVotacaoRepository).save(any(SessaoVotacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a pauta não for encontrada")
    void deveLancarExcecaoQuandoPautaNaoEncontrada() {
        Long pautaId = 1L;
        SessaoVotacaoRequestDto request = new SessaoVotacaoRequestDto(pautaId, null);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        assertThrows(PautaNaoEncontradaException.class, () -> sessaoVotacaoService.abrirSessao(request));

        verify(pautaRepository).findById(pautaId);
        verify(sessaoVotacaoRepository, never()).save(any(SessaoVotacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a pauta estiver fechada")
    void deveLancarExcecaoQuandoPautaEstiverFechada() {
        Long pautaId = 1L;
        SessaoVotacaoRequestDto request = new SessaoVotacaoRequestDto(pautaId, null);
        Pauta pauta = new Pauta("Descrição", "Título", false);
        ReflectionTestUtils.setField(pauta, "id", pautaId);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));

        PautaFechadaException exception = assertThrows(PautaFechadaException.class, () -> sessaoVotacaoService.abrirSessao(request));

        assertEquals("A pauta com ID 1 está fechada e não pode ser modificada.", exception.getMessage());
        verify(pautaRepository).findById(pautaId);
        verify(sessaoVotacaoRepository, never()).save(any(SessaoVotacao.class));
    }
}
