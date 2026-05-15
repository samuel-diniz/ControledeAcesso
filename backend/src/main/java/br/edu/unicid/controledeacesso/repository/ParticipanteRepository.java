package br.edu.unicid.controledeacesso.repository;

import br.edu.unicid.controledeacesso.model.Participante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
}
