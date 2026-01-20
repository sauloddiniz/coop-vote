package br.com.coopvote.controller;

import br.com.coopvote.agendamentos.FecharPautas;
import br.com.coopvote.dto.ListaPautaResponseDto;
import br.com.coopvote.dto.PautaRequestDto;
import br.com.coopvote.dto.PautaResponse;
import br.com.coopvote.service.PautaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PautaController.class)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PautaService pautaService;

    @MockitoBean
    private FecharPautas fecharPautas;

    private static final String API_URL = "/api/v1/pauta";

    @Test
    @DisplayName("Deve criar uma nova pauta com sucesso e retornar 201")
    void deveCriarPautaComSucesso() throws Exception {
        PautaRequestDto request = new PautaRequestDto("Título da Pauta", "Descrição da Pauta");
        Long idGerado = 1L;

        when(pautaService.salvar(any(PautaRequestDto.class))).thenReturn(idGerado);

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/api/v1/pauta/1"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando o título da pauta estiver em branco")
    void deveRetornar400QuandoTituloEmBranco() throws Exception {
        PautaRequestDto request = new PautaRequestDto("", "Descrição");

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray())
                .andExpect(jsonPath("$.erros[0].campo").value("titulo"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando o título da pauta for muito curto")
    void deveRetornar400QuandoTituloMuitoCurto() throws Exception {
        PautaRequestDto request = new PautaRequestDto("Oi", "Descrição");

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("titulo"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando o título da pauta for muito longo")
    void deveRetornar400QuandoTituloMuitoLongo() throws Exception {
        String tituloLongo = "a".repeat(201);
        PautaRequestDto request = new PautaRequestDto(tituloLongo, "Descrição");

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("titulo"));
    }

    @Test
    @DisplayName("Deve listar todas as pautas com sucesso e retornar 200")
    void deveListarPautasComSucesso() throws Exception {
        PautaResponse aberta = new PautaResponse(1L, "Pauta Aberta", "Desc", true);
        PautaResponse fechada = new PautaResponse(2L, "Pauta Fechada", "Desc", false);
        ListaPautaResponseDto responseDto = new ListaPautaResponseDto(List.of(aberta), List.of(fechada));

        when(pautaService.listarPautas(null)).thenReturn(responseDto);

        mockMvc.perform(get(API_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautas_abertas").isArray())
                .andExpect(jsonPath("$.pautas_encerradas").isArray())
                .andExpect(jsonPath("$.pautas_abertas[0].titulo").value("Pauta Aberta"))
                .andExpect(jsonPath("$.pautas_encerradas[0].titulo").value("Pauta Fechada"));
    }

    @Test
    @DisplayName("Deve listar pautas filtradas por status aberta com sucesso")
    void deveListarPautasFiltradasComSucesso() throws Exception {
        PautaResponse aberta = new PautaResponse(1L, "Pauta Aberta", "Desc", true);
        ListaPautaResponseDto responseDto = new ListaPautaResponseDto(List.of(aberta), List.of());

        when(pautaService.listarPautas("true")).thenReturn(responseDto);

        mockMvc.perform(get(API_URL)
                        .param("aberta", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautas_abertas[0].titulo").value("Pauta Aberta"))
                .andExpect(jsonPath("$.pautas_encerradas").isEmpty());
    }
}