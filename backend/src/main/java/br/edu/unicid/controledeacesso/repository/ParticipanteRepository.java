package br.edu.unicid.controledeacesso.repository;

import br.edu.unicid.controledeacesso.model.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    Optional<Participante> findByEmail(String email);
}
