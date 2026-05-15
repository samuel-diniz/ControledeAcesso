package br.edu.unicid.controledeacesso.controller;

import br.edu.unicid.controledeacesso.dto.IngressoRequest;
import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.service.IngressoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ingressos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IngressoController {

    private final IngressoService ingressoService;

    @PostMapping
    public ResponseEntity<Ingresso> gerar(@RequestBody IngressoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ingressoService.gerar(request.getEventoId(), request.getParticipanteId()));
    }

    @GetMapping
    public List<Ingresso> listar() {
        return ingressoService.listar();
    }

    @GetMapping("/evento/{eventoId}")
    public List<Ingresso> listarPorEvento(@PathVariable Long eventoId) {
        return ingressoService.listarPorEvento(eventoId);
    }

    @GetMapping("/{id}")
    public Ingresso buscar(@PathVariable Long id) {
        return ingressoService.buscar(id);
    }
}
