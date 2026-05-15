package br.edu.unicid.controledeacesso.controller;

import br.edu.unicid.controledeacesso.model.Leitura;
import br.edu.unicid.controledeacesso.repository.LeituraRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leituras")
@CrossOrigin(origins = "*")
public class LeituraController {

    private final LeituraRepository leituraRepository;

    public LeituraController(LeituraRepository leituraRepository) {
        this.leituraRepository = leituraRepository;
    }

    @GetMapping("/evento/{eventoId}")
    public List<Leitura> listarPorEvento(@PathVariable Long eventoId) {
        return leituraRepository.findByEventoIdOrderByLidoEmDesc(eventoId);
    }
}
