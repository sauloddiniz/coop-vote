package br.com.coopvote.service;

import br.com.coopvote.client.UserInfoClient;
import br.com.coopvote.dto.UserInfoResponseDto;
import br.com.coopvote.dto.VotoQueue;
import br.com.coopvote.dto.VotoRequestDto;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.entity.SessaoVotacao;
import br.com.coopvote.entity.Voto;
import br.com.coopvote.exceptions.*;
import br.com.coopvote.repository.PautaRepository;
import br.com.coopvote.repository.SessaoVotacaoRepository;
import br.com.coopvote.repository.VotoRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class VotoService {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final VotoRepository votoRepository;
    private final RabbitTemplate rabbitTemplate;
    private final UserInfoClient userInfoClient;

    public static final String VOTO_QUEUE = "votos.queue";

    public VotoService(PautaRepository pautaRepository,
                       SessaoVotacaoRepository sessaoVotacaoRepository,
                       VotoRepository votoRepository,
                       RabbitTemplate rabbitTemplate,
                       UserInfoClient userInfoClient) {
        this.pautaRepository = pautaRepository;
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
        this.votoRepository = votoRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.userInfoClient = userInfoClient;
    }

    @Transactional(readOnly = true)
    public void votar(VotoRequestDto request) {

        Voto voto = VotoRequestDto.toVoto(request);

        if (votoRepository.existsById(voto.getId())) {
            throw new AssociadoNaoAutorizadoException("Este associado já votou nesta pauta.");
        }

        validarAssociado(request.associadoId());

        Pauta pauta = pautaRepository.findById(request.pautaId())
                .orElseThrow(() -> new PautaNaoEncontradaException("Pauta não encontrada com ID: " + request.pautaId()));

        if (isPautaFechada(pauta)) {
            throw new PautaFechadaException("Não é possível votar em uma pauta fechada.");
        }

        SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(pauta.getId())
                .orElseThrow(() -> new SessaoFechadaException("Não existe sessão de votação aberta para esta pauta."));

        if (LocalDateTime.now().isAfter(sessao.getDataFechamento())) {
            throw new SessaoFechadaException("A sessão de votação para esta pauta já expirou.");
        }

        voto.setPauta(pauta);
        VotoQueue votoQueue = new VotoQueue(voto, sessao);
        rabbitTemplate.convertAndSend(VOTO_QUEUE, votoQueue);
        
        log.info("Voto do associado {} para a pauta {} enviado para processamento assíncrono.", 
                request.associadoId(), request.pautaId());
    }

    private void validarAssociado(String associadoId) {
        try {
            UserInfoResponseDto userInfo = userInfoClient.getInfo(associadoId);
            if ("UNABLE_TO_VOTE".equals(userInfo.status())) {
                throw new AssociadoNaoAutorizadoException("Associado não autorizado a votar.");
            }
        } catch (FeignException.NotFound e) {
            throw new AssociadoNaoAutorizadoException("CPF inválido ou associado não encontrado.");
        } catch (FeignException e) {
            throw new RuntimeException("Erro ao consultar serviço de validação de CPF.");
        }
    }

    private static boolean isPautaFechada(Pauta pauta) {
        return !pauta.isAberta();
    }
}
