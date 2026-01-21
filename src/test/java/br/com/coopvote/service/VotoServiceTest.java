package br.com.coopvote.service;

import br.com.coopvote.client.UserInfoClient;
import br.com.coopvote.dto.ResultadoVotacaoResponseDto;
import br.com.coopvote.dto.UserInfoResponseDto;
import br.com.coopvote.dto.VotoQueue;
import br.com.coopvote.dto.VotoRequestDto;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.entity.SessaoVotacao;
import br.com.coopvote.enums.EscolhaVoto;
import br.com.coopvote.exceptions.AssociadoNaoAutorizadoException;
import br.com.coopvote.exceptions.PautaFechadaException;
import br.com.coopvote.exceptions.PautaNaoEncontradaException;
import br.com.coopvote.exceptions.SessaoFechadaException;
import br.com.coopvote.repository.PautaRepository;
import br.com.coopvote.repository.SessaoVotacaoRepository;
import br.com.coopvote.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
    private VotoRepository votoRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private UserInfoClient userInfoClient;

    @InjectMocks
    private VotoService votoService;

    @BeforeEach
    void setUp() {
        lenient().when(userInfoClient.getInfo(anyString())).thenReturn(new UserInfoResponseDto("ABLE_TO_VOTE"));
    }

    @Test
    @DisplayName("Deve registrar um voto com sucesso enviando para a fila")
    void deveRegistrarVotoComSucesso() {
        Long pautaId = 1L;
        String cpf = "12345678901";
        VotoRequestDto request = new VotoRequestDto(pautaId, cpf, EscolhaVoto.SIM);
        Pauta pauta = new Pauta(pautaId, "Desc", "Título", true);
        SessaoVotacao sessao = new SessaoVotacao(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1), pauta);

        when(votoRepository.existsById(anyString())).thenReturn(false);
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        assertDoesNotThrow(() -> votoService.votar(request));

        verify(rabbitTemplate).convertAndSend(eq(VotoService.VOTO_QUEUE), any(VotoQueue.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a pauta estiver com status fechada")
    void deveLancarExcecaoQuandoPautaFechadaNoStatus() {
        Long pautaId = 1L;
        String cpf = "12345678901";
        VotoRequestDto request = new VotoRequestDto(pautaId, cpf, EscolhaVoto.SIM);
        Pauta pauta = new Pauta("Desc", "Título", false);

        when(votoRepository.existsById(anyString())).thenReturn(false);
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));

        PautaFechadaException exception = assertThrows(PautaFechadaException.class, () -> votoService.votar(request));

        assertEquals("Não é possível votar em uma pauta fechada.", exception.getMessage());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a sessão de votação expirou pelo tempo")
    void deveLancarExcecaoQuandoSessaoExpirouPeloTempo() {
        Long pautaId = 1L;
        String cpf = "12345678901";
        VotoRequestDto request = new VotoRequestDto(pautaId, cpf, EscolhaVoto.SIM);
        Pauta pauta = new Pauta(pautaId, "Desc", "Título", true);
        // Sessão expirada há 1 segundo
        SessaoVotacao sessao = new SessaoVotacao(LocalDateTime.now().minusMinutes(2), LocalDateTime.now().minusSeconds(1), pauta);

        when(votoRepository.existsById(anyString())).thenReturn(false);
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
        String cpf = "12345678901";
        VotoRequestDto request = new VotoRequestDto(pautaId, cpf, EscolhaVoto.SIM);
        Pauta pauta = new Pauta(pautaId, "Desc", "Título", true);

        when(votoRepository.existsById(anyString())).thenReturn(false);
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(pautaId)).thenReturn(Optional.empty());

        SessaoFechadaException exception = assertThrows(SessaoFechadaException.class, () -> votoService.votar(request));

        assertEquals("Não existe sessão de votação aberta para esta pauta.", exception.getMessage());
        verifyNoInteractions(rabbitTemplate);
    }
    @Test
    @DisplayName("Deve lançar exceção quando o associado já votou na pauta")
    void deveLancarExcecaoQuandoAssociadoJaVotou() {
        Long pautaId = 1L;
        String cpf = "12345678901";
        VotoRequestDto request = new VotoRequestDto(pautaId, cpf, EscolhaVoto.SIM);

        when(votoRepository.existsById(anyString())).thenReturn(true);

        AssociadoNaoAutorizadoException exception = assertThrows(AssociadoNaoAutorizadoException.class, () -> votoService.votar(request));

        assertEquals("Este associado já votou nesta pauta.", exception.getMessage());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o associado não está autorizado a votar")
    void deveLancarExcecaoQuandoAssociadoNaoAutorizado() {
        Long pautaId = 1L;
        String cpf = "12345678901";
        VotoRequestDto request = new VotoRequestDto(pautaId, cpf, EscolhaVoto.SIM);

        when(votoRepository.existsById(anyString())).thenReturn(false);
        when(userInfoClient.getInfo(cpf)).thenReturn(new UserInfoResponseDto("UNABLE_TO_VOTE"));

        AssociadoNaoAutorizadoException exception = assertThrows(AssociadoNaoAutorizadoException.class, () -> votoService.votar(request));

        assertEquals("Associado não autorizado a votar.", exception.getMessage());
        verifyNoInteractions(rabbitTemplate);
    }

    @ParameterizedTest
    @DisplayName("Deve contabilizar votos com diferentes resultados")
    @CsvSource({
        "10, 5, APROVADA",
        "5, 10, REPROVADA",
        "10, 10, EMPATE",
        "0, 0, SEM VOTOS"
    })
    void deveContabilizarVotosComSucesso(Long votosSim, Long votosNao, String resultadoEsperado) {
        Long pautaId = 1L;
        Pauta pauta = new Pauta(pautaId, "Desc", "Título", true);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndEscolha(pautaId, EscolhaVoto.SIM)).thenReturn(votosSim);
        when(votoRepository.countByPautaIdAndEscolha(pautaId, EscolhaVoto.NAO)).thenReturn(votosNao);

        ResultadoVotacaoResponseDto resultado = votoService.contabilizarVotos(pautaId);

        assertNotNull(resultado);
        assertEquals(pautaId, resultado.pautaId());
        assertEquals("Título", resultado.titulo());
        assertEquals(votosSim, resultado.totalSim());
        assertEquals(votosNao, resultado.totalNao());
        assertEquals(votosSim + votosNao, resultado.totalVotos());
        assertEquals(resultadoEsperado, resultado.resultado());
    }

    @Test
    @DisplayName("Deve lançar exceção ao contabilizar votos de pauta inexistente")
    void deveLancarExcecaoContabilizarPautaInexistente() {
        Long pautaId = 1L;
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        assertThrows(PautaNaoEncontradaException.class, () -> votoService.contabilizarVotos(pautaId));
    }
}
