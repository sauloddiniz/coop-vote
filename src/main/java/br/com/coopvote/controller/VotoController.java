package br.com.coopvote.controller;

import br.com.coopvote.controller.api.VotoApi;
import br.com.coopvote.dto.VotoRequestDto;
import br.com.coopvote.service.VotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VotoController implements VotoApi {

    private final VotoService votoService;

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @Override
    public ResponseEntity<Void> votar(VotoRequestDto votoRequest) {
        votoService.votar(votoRequest);
        return ResponseEntity.accepted().build();
    }
}
