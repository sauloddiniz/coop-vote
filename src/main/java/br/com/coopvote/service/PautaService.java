package br.com.coopvote.service;

import br.com.coopvote.dto.ListaPautaResponseDto;
import br.com.coopvote.dto.PautaRequestDto;
import br.com.coopvote.dto.PautaResponse;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.exceptions.PautaExistenteException;
import br.com.coopvote.repository.PautaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PautaService {

    private final PautaRepository pautaRepository;

    public PautaService(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
    }

    @Transactional
    public Long salvar(final PautaRequestDto pautaRequest) {
        Optional.ofNullable(pautaRepository.findByTituloIgnoreCase(pautaRequest.titulo()))
                .ifPresent(pautaExistente -> {
                    throw new PautaExistenteException("Pauta com título '" + pautaRequest.titulo() + "' já existe.");
                });
        final Pauta pauta = pautaRepository.save(pautaRequest.createEntityToSave());
        return pauta.getId();
    }


    @Transactional(readOnly = true)
    public ListaPautaResponseDto listarPautas(String aberta) {
        List<Pauta> pautas =
                Optional.ofNullable(aberta)
                        .map(Boolean::parseBoolean)
                        .map(abertaBoolean -> pautaRepository.findAllByAberta(abertaBoolean))
                        .orElse(pautaRepository.findAll());

        List<PautaResponse> abertas = pautas
                .stream()
                .filter(Pauta::isAberta)
                .map(PautaResponse::de)
                .toList();

        List<PautaResponse> fechadas = pautas
                .stream()
                .filter(Pauta -> !Pauta.isAberta())
                .map(PautaResponse::de)
                .toList();

        return new ListaPautaResponseDto(abertas, fechadas);
    }
}
