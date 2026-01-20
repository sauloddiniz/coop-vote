package br.com.coopvote.controller.api;

import br.com.coopvote.dto.ErrorResponseDto;
import br.com.coopvote.dto.SessaoVotacaoRequestDto;
import br.com.coopvote.dto.SessaoVotacaoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Sessões de Votação", description = "Endpoints para gerenciamento de sessões de votação")
@RequestMapping("/api/v1/sessao-votacao")
public interface SessaoVotacaoApi {

    @Operation(
            summary = "Abre uma nova sessão de votação",
            description = "Inicia uma sessão de votação para uma pauta específica. Se o tempo de fechamento não for informado, o padrão é 1 minuto."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sessão aberta com sucesso",
                    content = @Content(schema = @Schema(implementation = SessaoVotacaoResponseDto.class))),
            
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    ResponseEntity<SessaoVotacaoResponseDto> abrirSessao(@RequestBody @Valid SessaoVotacaoRequestDto request);
}
