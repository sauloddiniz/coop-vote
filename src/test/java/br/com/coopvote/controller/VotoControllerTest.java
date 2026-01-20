package br.com.coopvote.controller;

import br.com.coopvote.dto.VotoRequestDto;
import br.com.coopvote.enums.EscolhaVoto;
import br.com.coopvote.service.VotoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VotoController.class)
class VotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VotoService votoService;

    private static final String API_URL = "/api/v1/voto";

    @Test
    @DisplayName("Deve registrar um voto com sucesso e retornar 202")
    void deveRegistrarVotoComSucesso() throws Exception {
        VotoRequestDto request = new VotoRequestDto(1L, "associado-1", EscolhaVoto.SIM);

        doNothing().when(votoService).votar(any(VotoRequestDto.class));

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("Deve retornar 400 quando o ID da pauta for nulo")
    void deveRetornar400QuandoPautaIdNulo() throws Exception {
        VotoRequestDto request = new VotoRequestDto(null, "associado-1", EscolhaVoto.SIM);

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 quando o ID do associado for em branco")
    void deveRetornar400QuandoAssociadoIdEmBranco() throws Exception {
        VotoRequestDto request = new VotoRequestDto(1L, "", EscolhaVoto.SIM);

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 quando a escolha do voto for nula")
    void deveRetornar400QuandoEscolhaNula() throws Exception {
        VotoRequestDto request = new VotoRequestDto(1L, "associado-1", null);

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
