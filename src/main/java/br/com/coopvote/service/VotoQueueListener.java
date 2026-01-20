package br.com.coopvote.service;

import br.com.coopvote.dto.VotoQueue;
import br.com.coopvote.entity.Voto;
import br.com.coopvote.repository.VotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VotoQueueListener {

    private final VotoRepository votoRepository;

    public VotoQueueListener(VotoRepository votoRepository) {
        this.votoRepository = votoRepository;
    }

    @RabbitListener(queues = VotoService.VOTO_QUEUE)
    public void receberVoto(VotoQueue votoQueue) {
        log.info("Recebendo voto da fila para o associado: {} na pauta: {}", 
                votoQueue.voto().getAssociadoId(), votoQueue.voto().getPauta().getId());
        try {
            votoRepository.save(votoQueue.voto());
            log.info("Voto salvo com sucesso para o associado: {}", votoQueue.voto().getAssociadoId());
        } catch (Exception e) {
            log.error("Erro ao salvar voto do associado: {}. Erro: {}", 
                    votoQueue.voto().getAssociadoId(), e.getMessage());
        }
    }
}
