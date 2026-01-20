package br.com.coopvote.service;

import br.com.coopvote.dto.PautaRequestDto;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.exceptions.PautaExistenteException;
import br.com.coopvote.repository.PautaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PautaService {

    private final PautaRepository pautaRepository;

    public PautaService(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
    }

    public Long salvar(final PautaRequestDto pautaRequest) {
        Optional.ofNullable(pautaRepository.findByTituloIgnoreCase(pautaRequest.titulo()))
                .ifPresent(pautaExistente -> {
                    throw new PautaExistenteException("Pauta com título '" + pautaRequest.titulo() + "' já existe.");
                });
        final Pauta pauta = pautaRepository.save(pautaRequest.toEntity());
        return pauta.getId();
    }


}
