package br.com.coopvote.agendamentos;

import br.com.coopvote.dto.PautaSessaoVotacao;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.repository.PautaRepository;
import br.com.coopvote.repository.SessaoVotacaoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FecharPautas {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    public FecharPautas(PautaRepository pautaRepository, SessaoVotacaoRepository sessaoVotacaoRepository) {
        this.pautaRepository = pautaRepository;
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    @Modifying
    public void executarFechamentoDePautas() {
        log.info("Executando fechamento de pautas");
        LocalDateTime agora = LocalDateTime.now();
        log.info("Data atual: {}", agora);
        List<Pauta> pautas =
                sessaoVotacaoRepository
                        .buscarPautasSessoesExpiradas(agora)
                        .stream()
                        .map(PautaSessaoVotacao::getPauta)
                        .map(Pauta::setAbertaFalse)
                        .collect(Collectors.toUnmodifiableList());

        if (isContemPauta(pautas)) {
            pautaRepository.saveAll(pautas);
            log.info("Pautas fechadas com sucesso: total de {} pautas", pautas.size());
        }
    }

    private static boolean isContemPauta(List<Pauta> pautas) {
        return !pautas.isEmpty();
    }
}
