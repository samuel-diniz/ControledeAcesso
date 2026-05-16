package br.edu.unicid.controledeacesso.repository;

import br.edu.unicid.controledeacesso.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    List<Solicitacao> findByStatusOrderBySolicitadoEmDesc(String status);
    List<Solicitacao> findByParticipanteIdOrderBySolicitadoEmDesc(Long participanteId);
    boolean existsByParticipanteIdAndEventoId(Long participanteId, Long eventoId);
}
