package br.com.coopvote.repository;

import br.com.coopvote.entity.Voto;
import br.com.coopvote.enums.EscolhaVoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepository extends JpaRepository<Voto, String> {
    long countByPautaIdAndEscolha(Long pautaId, EscolhaVoto escolha);
}
