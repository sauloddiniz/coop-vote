package br.com.coopvote.service;

import br.com.coopvote.dto.VotoRequestDto;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.entity.SessaoVotacao;
import br.com.coopvote.exceptions.PautaFechadaException;
import br.com.coopvote.exceptions.PautaNaoEncontradaException;
import br.com.coopvote.exceptions.SessaoFechadaException;
import br.com.coopvote.repository.PautaRepository;
import br.com.coopvote.repository.SessaoVotacaoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VotoService {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final RabbitTemplate rabbitTemplate;

    public static final String VOTO_QUEUE = "votos.queue";

    public VotoService(PautaRepository pautaRepository,
                       SessaoVotacaoRepository sessaoVotacaoRepository,
                       RabbitTemplate rabbitTemplate) {
        this.pautaRepository = pautaRepository;
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional(readOnly = true)
    public void votar(VotoRequestDto request) {
        Pauta pauta = pautaRepository.findById(request.pautaId())
                .orElseThrow(() -> new PautaNaoEncontradaException("Pauta não encontrada com ID: " + request.pautaId()));

        if (!pauta.isAberta()) {
            throw new PautaFechadaException("Não é possível votar em uma pauta fechada.");
        }

        SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(pauta.getId())
                .orElseThrow(() -> new SessaoFechadaException("Não existe sessão de votação aberta para esta pauta."));

        if (LocalDateTime.now().isAfter(sessao.getDataFechamento())) {
            throw new SessaoFechadaException("A sessão de votação para esta pauta já expirou.");
        }

        rabbitTemplate.convertAndSend(VOTO_QUEUE, request);
    }
}
