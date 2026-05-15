package br.edu.unicid.controledeacesso.controller;

import br.edu.unicid.controledeacesso.model.Leitura;
import br.edu.unicid.controledeacesso.repository.LeituraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leituras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LeituraController {

    private final LeituraRepository leituraRepository;

    @GetMapping("/evento/{eventoId}")
    public List<Leitura> listarPorEvento(@PathVariable Long eventoId) {
        return leituraRepository.findByEventoIdOrderByLidoEmDesc(eventoId);
    }
}
