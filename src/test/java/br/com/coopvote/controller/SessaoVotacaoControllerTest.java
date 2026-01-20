package br.com.coopvote.controller;

import br.com.coopvote.dto.SessaoVotacaoRequestDto;
import br.com.coopvote.dto.SessaoVotacaoResponseDto;
import br.com.coopvote.exceptions.PautaFechadaException;
import br.com.coopvote.exceptions.PautaNaoEncontradaException;
import br.com.coopvote.service.SessaoVotacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SessaoVotacaoController.class)
class SessaoVotacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SessaoVotacaoService sessaoVotacaoService;

    private static final String API_URL = "/api/v1/sessao-votacao";

    @Test
    @DisplayName("Deve abrir uma sessão de votação com sucesso e retornar 201")
    void deveAbrirSessaoComSucesso() throws Exception {
        Long pautaId = 1L;
        SessaoVotacaoRequestDto request = new SessaoVotacaoRequestDto(pautaId, null);
        SessaoVotacaoResponseDto response = new SessaoVotacaoResponseDto(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(1), pautaId);

        when(sessaoVotacaoService.abrirSessao(any(SessaoVotacaoRequestDto.class))).thenReturn(response);

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.pautaId").value(pautaId));
    }

    @Test
    @DisplayName("Deve retornar 400 quando o pautaId for nulo")
    void deveRetornar400QuandoPautaIdNulo() throws Exception {
        SessaoVotacaoRequestDto request = new SessaoVotacaoRequestDto(null, null);

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("pautaId"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando a pauta não for encontrada")
    void deveRetornar404QuandoPautaNaoEncontrada() throws Exception {
        Long pautaId = 1L;
        SessaoVotacaoRequestDto request = new SessaoVotacaoRequestDto(pautaId, null);

        when(sessaoVotacaoService.abrirSessao(any(SessaoVotacaoRequestDto.class)))
                .thenThrow(new PautaNaoEncontradaException("Pauta com ID " + pautaId + " não encontrada."));

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Pauta com ID 1 não encontrada."));
    }

    @Test
    @DisplayName("Deve retornar 400 quando a pauta estiver fechada")
    void deveRetornar400QuandoPautaEstiverFechada() throws Exception {
        Long pautaId = 1L;
        SessaoVotacaoRequestDto request = new SessaoVotacaoRequestDto(pautaId, null);

        when(sessaoVotacaoService.abrirSessao(any(SessaoVotacaoRequestDto.class)))
                .thenThrow(new PautaFechadaException("A pauta com ID " + pautaId + " está fechada e não pode ser modificada."));

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A pauta com ID 1 está fechada e não pode ser modificada."));
    }
}
