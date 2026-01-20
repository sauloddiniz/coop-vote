package br.com.coopvote.controller.api;

import br.com.coopvote.dto.ListaPautaResponseDto;
import br.com.coopvote.dto.PautaRequestDto;
import br.com.coopvote.dto.ResultadoVotacaoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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


    @Operation(
            summary = "Lista todas as pautas",
            description = "Retorna uma lista de pautas cadastradas, permitindo filtrar pelo status de abertura (abertas ou fechadas)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pautas retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = ListaPautaResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a listagem")
    })

    @GetMapping
    ResponseEntity<ListaPautaResponseDto> listarPautasComFiltro(
            @Parameter(description = "Filtro para pautas: 'true' para abertas, 'false' para fechadas. Se omitido, retorna todas.",
                    example = "true")
            @RequestParam(required = false) String aberta);

    @Operation(
            summary = "Obtém o resultado da votação de uma pauta",
            description = "Contabiliza os votos e retorna o resultado final da pauta informada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado obtido com sucesso",
                    content = @Content(schema = @Schema(implementation = ResultadoVotacaoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}/resultado")
    ResponseEntity<ResultadoVotacaoResponseDto> obterResultado(@PathVariable Long id);
}
