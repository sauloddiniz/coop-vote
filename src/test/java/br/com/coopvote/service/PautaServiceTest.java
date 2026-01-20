package br.com.coopvote.service;

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
}
