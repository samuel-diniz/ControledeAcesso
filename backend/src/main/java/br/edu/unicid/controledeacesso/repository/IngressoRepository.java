package br.edu.unicid.controledeacesso.repository;

import br.edu.unicid.controledeacesso.model.Ingresso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngressoRepository extends JpaRepository<Ingresso, Long> {
    Optional<Ingresso> findByToken(UUID token);
    List<Ingresso> findByEventoId(Long eventoId);
    List<Ingresso> findByParticipanteId(Long participanteId);
    long countByEventoIdAndStatus(Long eventoId, String status);
}
