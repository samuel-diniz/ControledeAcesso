package br.edu.unicid.controledeacesso.controller;

import br.edu.unicid.controledeacesso.model.Evento;
import br.edu.unicid.controledeacesso.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<Evento> criar(@RequestBody @Valid Evento evento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.criar(evento));
    }

    @GetMapping
    public List<Evento> listar() {
        return eventoService.listar();
    }

    @GetMapping("/{id}")
    public Evento buscar(@PathVariable Long id) {
        return eventoService.buscar(id);
    }
}
