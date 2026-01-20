package br.com.coopvote.service;

import br.com.coopvote.client.UserInfoClient;
import br.com.coopvote.dto.UserInfoResponseDto;
import br.com.coopvote.dto.VotoQueue;
import br.com.coopvote.exceptions.AssociadoNaoAutorizadoException;
import br.com.coopvote.repository.VotoRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VotoQueueListener {

    private final VotoRepository votoRepository;
    private final UserInfoClient userInfoClient;

    public VotoQueueListener(VotoRepository votoRepository, UserInfoClient userInfoClient) {
        this.votoRepository = votoRepository;
        this.userInfoClient = userInfoClient;
    }

    @RabbitListener(queues = VotoService.VOTO_QUEUE)
    public void receberVoto(VotoQueue votoQueue) {
        log.info("Recebendo voto da fila para o associado: {} na pauta: {}", 
                votoQueue.voto().getAssociadoId(), votoQueue.voto().getPauta().getId());

        try {
            validarAssociado(votoQueue.voto().getAssociadoId());

            if (isVotoAposFechamento(votoQueue)) {
                log.error("Data de votação inválida para o associado: {}", votoQueue.voto().getAssociadoId());
                return;
            }

            votoRepository.save(votoQueue.voto());
            log.info("Voto salvo com sucesso para o associado: {}", votoQueue.voto().getAssociadoId());
        } catch (AssociadoNaoAutorizadoException e) {
            log.warn("Voto descartado: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao processar/salvar voto do associado: {}. Erro: {}",
                    votoQueue.voto().getAssociadoId(), e.getMessage());
        }
    }

    private void validarAssociado(String associadoId) {
        try {
            UserInfoResponseDto userInfo = userInfoClient.getInfo(associadoId);
            if ("UNABLE_TO_VOTE".equals(userInfo.status())) {
                throw new AssociadoNaoAutorizadoException("Associado não autorizado a votar.");
            }
        } catch (FeignException.NotFound e) {
            throw new AssociadoNaoAutorizadoException("CPF inválido ou associado não encontrado.");
        } catch (AssociadoNaoAutorizadoException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao validar associado: " + e.getMessage());
        }
    }

    private static boolean isVotoAposFechamento(VotoQueue votoQueue) {
        return votoQueue.voto().getDataHoraVotacao().isAfter(votoQueue.voto().getDataHoraVotacao());
    }
}
