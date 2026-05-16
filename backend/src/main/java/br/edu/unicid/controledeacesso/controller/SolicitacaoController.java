package br.edu.unicid.controledeacesso.controller;

import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.model.Solicitacao;
import br.edu.unicid.controledeacesso.service.SolicitacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitacoes")
@CrossOrigin(origins = "*")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping
    public ResponseEntity<Solicitacao> criar(@RequestBody Map<String, Long> body) {
        Long participanteId = body.get("participanteId");
        Long eventoId       = body.get("eventoId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solicitacaoService.criar(participanteId, eventoId));
    }

    @GetMapping("/pendentes")
    public List<Solicitacao> listarPendentes() {
        return solicitacaoService.listarPendentes();
    }

    @GetMapping("/participante/{participanteId}")
    public List<Solicitacao> listarPorParticipante(@PathVariable Long participanteId) {
        return solicitacaoService.listarPorParticipante(participanteId);
    }

    @PutMapping("/{id}/aprovar")
    public ResponseEntity<Ingresso> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoService.aprovar(id));
    }

    @PutMapping("/{id}/rejeitar")
    public ResponseEntity<Void> rejeitar(@PathVariable Long id) {
        solicitacaoService.rejeitar(id);
        return ResponseEntity.noContent().build();
    }
}
