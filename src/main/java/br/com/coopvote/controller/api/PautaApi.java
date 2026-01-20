package br.com.coopvote.controller.api;

import br.com.coopvote.dto.PautaRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Tag(name = "Pautas", description = "Endpoints para gerenciamento de pautas de votação")
@RequestMapping("/api/v1/pauta")
public interface PautaApi {

    @Operation(
            summary = "Cria uma nova pauta",
            description = "Este endpoint registra uma nova pauta no sistema para posterior abertura de sessão de votação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    ResponseEntity<Void> salvarPauta(@RequestBody @Valid PautaRequestDto pautaRequest);
}
