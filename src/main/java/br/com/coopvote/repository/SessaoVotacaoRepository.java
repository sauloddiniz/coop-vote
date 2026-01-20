package br.com.coopvote.repository;

import br.com.coopvote.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.coopvote.dto.PautaSessaoVotacao;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {
    
    @Query("""
           SELECT new br.com.coopvote.dto.PautaSessaoVotacao(
               p.id, 
               s.id, 
               p.titulo, 
               p.descricao, 
               p.aberta, 
               s.dataAbertura, 
               s.dataFechamento
           ) 
           FROM SessaoVotacao s 
           INNER JOIN s.pauta p 
           WHERE s.dataFechamento <= :agora 
           AND p.aberta = true
           """)
    List<PautaSessaoVotacao> buscarPautasSessoesExpiradas(LocalDateTime agora);

    Optional<SessaoVotacao> findByPautaId(Long pautaId);
}
