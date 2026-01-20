package br.com.coopvote.controller;

import br.com.coopvote.controller.api.PautaApi;
import br.com.coopvote.dto.ListaPautaResponseDto;
import br.com.coopvote.dto.PautaRequestDto;
import br.com.coopvote.dto.ResultadoVotacaoResponseDto;
import br.com.coopvote.service.PautaService;
import br.com.coopvote.service.VotoService;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class PautaController implements PautaApi {

    private final PautaService pautaService;
    private final VotoService votoService;

    public PautaController(PautaService pautaService, VotoService votoService) {
        this.pautaService = pautaService;
        this.votoService = votoService;
    }

    @Override
    @Timed("PautaController.salvarPauta")
    public ResponseEntity<Void> salvarPauta(PautaRequestDto pautaRequest) {

        Long id = pautaService.salvar(pautaRequest);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity
                .created(uri).build();
    }

    @Override
    @Timed("PautaController.listarPautasComFiltro")
    public ResponseEntity<ListaPautaResponseDto> listarPautasComFiltro(String aberta) {
        ListaPautaResponseDto pautaResponseDto = pautaService.listarPautas(aberta);
        return ResponseEntity.ok(pautaResponseDto);
    }

    @Override
    @Timed("PautaController.obterResultado")
    public ResponseEntity<ResultadoVotacaoResponseDto> obterResultado(Long id) {
        ResultadoVotacaoResponseDto resultado = votoService.contabilizarVotos(id);
        return ResponseEntity.ok(resultado);
    }

}
