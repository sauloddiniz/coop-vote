package br.com.coopvote.dto;

import br.com.coopvote.entity.Pauta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PautaRequestDto(
        @NotBlank(message = "O título da pauta é obrigatório")
        @Size(min = 5, max = 200, message = "O título da pauta deve ter entre 5 e 200 caracteres")
        String titulo,
        String descricao
) {

    public Pauta toEntity() {
        return new Pauta(descricao, titulo);
    }
}
