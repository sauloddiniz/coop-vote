package br.com.coopvote.controller;

import br.com.coopvote.controller.api.SessaoVotacaoApi;
import br.com.coopvote.dto.SessaoVotacaoRequestDto;
import br.com.coopvote.dto.SessaoVotacaoResponseDto;
import br.com.coopvote.service.SessaoVotacaoService;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessaoVotacaoController implements SessaoVotacaoApi {

    private final SessaoVotacaoService sessaoVotacaoService;

    public SessaoVotacaoController(SessaoVotacaoService sessaoVotacaoService) {
        this.sessaoVotacaoService = sessaoVotacaoService;
    }

    @Override
    @Timed("SessaoVotacaoController.abrirSessao")
    public ResponseEntity<SessaoVotacaoResponseDto> abrirSessao(SessaoVotacaoRequestDto request) {
        SessaoVotacaoResponseDto response = sessaoVotacaoService.abrirSessao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
