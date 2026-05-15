package br.edu.unicid.controledeacesso.repository;

import br.edu.unicid.controledeacesso.model.Leitura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface LeituraRepository extends JpaRepository<Leitura, Long> {

    @Query("SELECT l FROM Leitura l WHERE l.ingresso.evento.id = :eventoId ORDER BY l.lidoEm DESC")
    List<Leitura> findByEventoIdOrderByLidoEmDesc(@Param("eventoId") Long eventoId);
}
