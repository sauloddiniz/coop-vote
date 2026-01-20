package br.com.coopvote.repository;

import br.com.coopvote.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {
    Pauta findByTituloIgnoreCase(String titulo);
}
