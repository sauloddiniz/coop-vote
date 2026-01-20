package br.com.coopvote.controller;

import br.com.coopvote.controller.api.PautaApi;
import br.com.coopvote.dto.PautaRequestDto;
import br.com.coopvote.service.PautaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class PautaController implements PautaApi {

    private final PautaService pautaService;

    public PautaController(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    @Override
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

}
