package br.com.coopvote;

import br.com.coopvote.client.UserInfoClient;
import br.com.coopvote.dto.UserInfoResponseDto;
import br.com.coopvote.dto.VotoRequestDto;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.entity.SessaoVotacao;
import br.com.coopvote.enums.EscolhaVoto;
import br.com.coopvote.repository.PautaRepository;
import br.com.coopvote.repository.SessaoVotacaoRepository;
import br.com.coopvote.repository.VotoRepository;
import br.com.coopvote.service.VotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class VotoPerformanceTest extends ConfigContainersTest {

    @Autowired
    private VotoService votoService;

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @MockitoBean
    private UserInfoClient userInfoClient;

    private Long pautaId;

    @BeforeEach
    void setup() {
        votoRepository.deleteAll();
        sessaoVotacaoRepository.deleteAll();
        pautaRepository.deleteAll();

        Pauta pauta = new Pauta("Pauta de Teste Performance", "Descrição", true);
        pauta = pautaRepository.save(pauta);
        pautaId = pauta.getId();

        SessaoVotacao sessao = new SessaoVotacao(LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), pauta);
        sessaoVotacaoRepository.save(sessao);

        when(userInfoClient.getInfo(anyString())).thenReturn(new UserInfoResponseDto("ABLE_TO_VOTE"));
    }

    @Test
    @DisplayName("Deve processar 500 votos concorrentes com sucesso usando RabbitMQ e Oracle")
    void deveProcessarVotosConcorrentes() {
        int totalVotos = 500;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < totalVotos; i++) {
            String associadoId = "associado_" + i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                VotoRequestDto request = new VotoRequestDto(pautaId, associadoId, EscolhaVoto.SIM);
                votoService.votar(request);
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        await()
            .atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> {
                long count = votoRepository.count();
                assertEquals(totalVotos, count, "O número de votos salvos deve ser igual ao número de votos enviados");
            });
    }
}
