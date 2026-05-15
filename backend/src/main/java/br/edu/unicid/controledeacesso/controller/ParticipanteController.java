package br.edu.unicid.controledeacesso.controller;

import br.edu.unicid.controledeacesso.model.Participante;
import br.edu.unicid.controledeacesso.service.ParticipanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/participantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ParticipanteController {

    private final ParticipanteService participanteService;

    @PostMapping
    public ResponseEntity<Participante> criar(@RequestBody @Valid Participante participante) {
        return ResponseEntity.status(HttpStatus.CREATED).body(participanteService.criar(participante));
    }

    @GetMapping
    public List<Participante> listar() {
        return participanteService.listar();
    }

    @GetMapping("/{id}")
    public Participante buscar(@PathVariable Long id) {
        return participanteService.buscar(id);
    }
}
