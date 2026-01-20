package br.com.coopvote.service;

import br.com.coopvote.dto.SessaoVotacaoRequestDto;
import br.com.coopvote.dto.SessaoVotacaoResponseDto;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.entity.SessaoVotacao;
import br.com.coopvote.exceptions.PautaFechadaException;
import br.com.coopvote.exceptions.PautaNaoEncontradaException;
import br.com.coopvote.repository.PautaRepository;
import br.com.coopvote.repository.SessaoVotacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;

    public SessaoVotacaoService(SessaoVotacaoRepository sessaoVotacaoRepository, PautaRepository pautaRepository) {
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
        this.pautaRepository = pautaRepository;
    }

    @Transactional
    public SessaoVotacaoResponseDto abrirSessao(SessaoVotacaoRequestDto request) {

        Pauta pauta = pautaRepository.findById(request.pautaId())
                .orElseThrow(() -> new PautaNaoEncontradaException("Pauta com ID " + request.pautaId() + " não encontrada."));

        if (pautaFechada(pauta)) {
            throw new PautaFechadaException("A pauta com ID " + request.pautaId() + " está fechada e não pode ser modificada.");
        }

        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setPauta(pauta);
        sessao.setDataAbertura(LocalDateTime.now());
        sessao.setDataFechamento(request.dataFechamento() != null
                ? request.dataFechamento()
                : sessao.tempoPadraoParaFechamento());

        return SessaoVotacaoResponseDto.de(sessaoVotacaoRepository.save(sessao));
    }

    private static boolean pautaFechada(Pauta pauta) {
        return !pauta.isAberta();
    }
}
