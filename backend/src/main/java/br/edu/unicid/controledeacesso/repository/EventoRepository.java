package br.edu.unicid.controledeacesso.repository;

import br.edu.unicid.controledeacesso.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {
}
