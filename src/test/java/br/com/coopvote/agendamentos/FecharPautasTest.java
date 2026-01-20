package br.com.coopvote.agendamentos;

import br.com.coopvote.dto.PautaSessaoVotacao;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.repository.PautaRepository;
import br.com.coopvote.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FecharPautasTest {

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @InjectMocks
    private FecharPautas fecharPautas;

    @Test
    @DisplayName("Deve fechar pautas com sessões expiradas com sucesso")
    void deveFecharPautasComSessoesExpiradasComSucesso() {
        LocalDateTime agora = LocalDateTime.now();
        PautaSessaoVotacao pautaSessao = new PautaSessaoVotacao(
                1L, 1L, "Título", "Descrição", true, agora.minusMinutes(5), agora.minusMinutes(1)
        );
        
        when(sessaoVotacaoRepository.buscarPautasSessoesExpiradas(any(LocalDateTime.class)))
                .thenReturn(List.of(pautaSessao));

        fecharPautas.executarFechamentoDePautas();

        ArgumentCaptor<List<Pauta>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(pautaRepository).saveAll(argumentCaptor.capture());
        
        List<Pauta> pautasSalvas = argumentCaptor.getValue();
        assertEquals(1, pautasSalvas.size());
        assertFalse(pautasSalvas.get(0).isAberta());
        assertEquals(1L, pautasSalvas.get(0).getId());
    }

    @Test
    @DisplayName("Não deve salvar nada quando não houver pautas para fechar")
    void naoDeveSalvarNadaQuandoNaoHouverPautasParaFechar() {
        when(sessaoVotacaoRepository.buscarPautasSessoesExpiradas(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        fecharPautas.executarFechamentoDePautas();

        verify(pautaRepository, never()).saveAll(any());
    }
}
