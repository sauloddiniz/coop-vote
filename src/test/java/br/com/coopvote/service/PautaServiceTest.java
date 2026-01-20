package br.com.coopvote.service;

import br.com.coopvote.dto.ListaPautaResponseDto;
import br.com.coopvote.dto.PautaRequestDto;
import br.com.coopvote.entity.Pauta;
import br.com.coopvote.exceptions.PautaExistenteException;
import br.com.coopvote.repository.PautaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    @Test
    @DisplayName("Deve salvar uma pauta com sucesso")
    void deveSalvarPautaComSucesso() {
        PautaRequestDto request = new PautaRequestDto("Título da Pauta", "Descrição da Pauta");
        Pauta pautaSalva = request.createEntityToSave();
        ReflectionTestUtils.setField(pautaSalva, "id", 1L);

        when(pautaRepository.findByTituloIgnoreCase(request.titulo())).thenReturn(null);
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pautaSalva);

        Long id = pautaService.salvar(request);

        assertNotNull(id);
        assertEquals(1L, id);
        verify(pautaRepository).findByTituloIgnoreCase(request.titulo());
        verify(pautaRepository).save(any(Pauta.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a pauta já existir")
    void deveLancarexcecaoQuandoPautaJaExistir() {
        PautaRequestDto request = new PautaRequestDto("Título Existente", "Descrição");
        Pauta pautaExistente = new Pauta("Descrição", "Título Existente");

        when(pautaRepository.findByTituloIgnoreCase(request.titulo())).thenReturn(pautaExistente);

        PautaExistenteException exception = assertThrows(PautaExistenteException.class, () -> {
            pautaService.salvar(request);
        });

        assertEquals("Pauta com título 'Título Existente' já existe.", exception.getMessage());
        verify(pautaRepository).findByTituloIgnoreCase(request.titulo());
        verify(pautaRepository, never()).save(any(Pauta.class));
    }

    @Test
    @DisplayName("Deve listar todas as pautas quando o filtro for nulo")
    void deveListarTodasAsPautasQuandoFiltroForNulo() {
        Pauta pautaAberta = new Pauta("Desc 1", "Pauta 1", true);
        ReflectionTestUtils.setField(pautaAberta, "id", 1L);
        Pauta pautaFechada = new Pauta("Desc 2", "Pauta 2", false);
        ReflectionTestUtils.setField(pautaFechada, "id", 2L);

        when(pautaRepository.findAll()).thenReturn(List.of(pautaAberta, pautaFechada));

        ListaPautaResponseDto response = pautaService.listarPautas(null);

        assertNotNull(response);
        assertEquals(1, response.pautas_abertas().size());
        assertEquals(1, response.pautas_encerradas().size());
        assertEquals("Pauta 1", response.pautas_abertas().get(0).titulo());
        assertEquals("Pauta 2", response.pautas_encerradas().get(0).titulo());
        verify(pautaRepository).findAll();
        verify(pautaRepository, never()).findAllByAberta(anyBoolean());
    }

    @Test
    @DisplayName("Deve listar apenas pautas abertas quando o filtro for true")
    void deveListarPautasAbertasQuandoFiltroForTrue() {
        Pauta pautaAberta = new Pauta("Desc 1", "Pauta 1", true);
        ReflectionTestUtils.setField(pautaAberta, "id", 1L);

        when(pautaRepository.findAllByAberta(true)).thenReturn(List.of(pautaAberta));

        ListaPautaResponseDto response = pautaService.listarPautas("true");

        assertNotNull(response);
        assertEquals(1, response.pautas_abertas().size());
        assertTrue(response.pautas_encerradas().isEmpty());
        verify(pautaRepository).findAllByAberta(true);
    }

    @Test
    @DisplayName("Deve listar apenas pautas fechadas quando o filtro for false")
    void deveListarPautasFechadasQuandoFiltroForFalse() {
        Pauta pautaFechada = new Pauta("Desc 2", "Pauta 2", false);
        ReflectionTestUtils.setField(pautaFechada, "id", 2L);

        when(pautaRepository.findAllByAberta(false)).thenReturn(List.of(pautaFechada));

        ListaPautaResponseDto response = pautaService.listarPautas("false");

        assertNotNull(response);
        assertTrue(response.pautas_abertas().isEmpty());
        assertEquals(1, response.pautas_encerradas().size());
        verify(pautaRepository).findAllByAberta(false);
    }
}
