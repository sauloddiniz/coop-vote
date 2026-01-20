package br.com.coopvote.service;

import br.com.coopvote.dto.VotoQueue;
import br.com.coopvote.repository.VotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class VotoQueueListener {

    private final VotoRepository votoRepository;

    public VotoQueueListener(VotoRepository votoRepository) {
        this.votoRepository = votoRepository;
    }

    @RabbitListener(queues = VotoService.VOTO_QUEUE)
    @Transactional
    public void receberVoto(VotoQueue votoQueue) {
        log.info("Recebendo voto da fila para o associado: {} na pauta: {}", 
                votoQueue.voto().getAssociadoId(), votoQueue.voto().getPauta().getId());

        try {
            if (isVotoAposFechamento(votoQueue)) {
                log.error("Voto descartado: A votação ocorreu após o fechamento da sessão para o associado: {}", 
                        votoQueue.voto().getAssociadoId());
                return;
            }

            votoRepository.save(votoQueue.voto());
            log.info("Voto salvo com sucesso para o associado: {}", votoQueue.voto().getAssociadoId());
        } catch (Exception e) {
            log.error("Erro ao processar/salvar voto do associado: {}. Erro: {}",
                    votoQueue.voto().getAssociadoId(), e.getMessage());
        }
    }

    private static boolean isVotoAposFechamento(VotoQueue votoQueue) {
        return votoQueue.voto().getDataHoraVotacao().isAfter(votoQueue.sessaoVotacao().getDataFechamento());
    }
}
