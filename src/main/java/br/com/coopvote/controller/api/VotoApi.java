package br.com.coopvote.controller.api;

import br.com.coopvote.dto.VotoRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Votos", description = "Endpoints para registro de votos")
@RequestMapping("/api/v1/voto")
public interface VotoApi {

    @Operation(
            summary = "Registra um novo voto",
            description = "Este endpoint registra o voto de um associado em uma pauta específica. O processamento do voto pode ser assíncrono."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Voto recebido e enviado para processamento"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "404", description = "Pauta, sessão ou associado não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    ResponseEntity<Void> votar(@RequestBody @Valid VotoRequestDto votoRequest);
}
