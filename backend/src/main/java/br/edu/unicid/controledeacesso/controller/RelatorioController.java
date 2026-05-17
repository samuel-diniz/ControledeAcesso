package br.edu.unicid.controledeacesso.controller;

import br.edu.unicid.controledeacesso.dto.ParticipantePresencaDTO;
import br.edu.unicid.controledeacesso.dto.RelatorioEventoDTO;
import br.edu.unicid.controledeacesso.model.Evento;
import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.model.Leitura;
import br.edu.unicid.controledeacesso.repository.EventoRepository;
import br.edu.unicid.controledeacesso.repository.IngressoRepository;
import br.edu.unicid.controledeacesso.repository.LeituraRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/relatorio")
@CrossOrigin(origins = "*")
public class RelatorioController {

    private final EventoRepository eventoRepository;
    private final IngressoRepository ingressoRepository;
    private final LeituraRepository leituraRepository;

    public RelatorioController(EventoRepository eventoRepository,
                               IngressoRepository ingressoRepository,
                               LeituraRepository leituraRepository) {
        this.eventoRepository  = eventoRepository;
        this.ingressoRepository = ingressoRepository;
        this.leituraRepository  = leituraRepository;
    }

    @GetMapping("/evento/{eventoId}")
    public RelatorioEventoDTO relatorio(@PathVariable Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));

        List<Ingresso> ingressos = ingressoRepository.findByEventoId(eventoId);
        List<Leitura> leituras   = leituraRepository.findByEventoIdOrderByLidoEmDesc(eventoId);

        int totalAprovados = ingressos.size();
        int totalDentro = 0, totalSaiu = 0, totalAusente = 0;

        List<ParticipantePresencaDTO> participantes = new ArrayList<>();
        for (Ingresso ing : ingressos) {
            String status = ing.getStatus() != null ? ing.getStatus() : "PENDENTE";
            switch (status) {
                case "DENTRO": totalDentro++;  break;
                case "SAIU":   totalSaiu++;    break;
                default:       totalAusente++; break;
            }

            // busca timestamps de entrada e saída nas leituras válidas
            String entradaEm = null, saidaEm = null;
            for (Leitura l : leituras) {
                if (l.getIngresso() == null) continue;
                if (!l.getIngresso().getId().equals(ing.getId())) continue;
                if (!"VALIDO".equals(l.getResultado())) continue;
                if ("ENTRADA".equals(l.getTipo()) && entradaEm == null)
                    entradaEm = l.getLidoEm() != null ? l.getLidoEm().toString() : null;
                if ("SAIDA".equals(l.getTipo()) && saidaEm == null)
                    saidaEm = l.getLidoEm() != null ? l.getLidoEm().toString() : null;
            }

            String nome  = ing.getParticipante() != null ? ing.getParticipante().getNome()  : "—";
            String email = ing.getParticipante() != null ? ing.getParticipante().getEmail() : "—";
            participantes.add(new ParticipantePresencaDTO(
                    ing.getParticipante() != null ? ing.getParticipante().getId() : null,
                    nome, email, status, entradaEm, saidaEm));
        }

        double pct = evento.getCapacidade() > 0
                ? (double) totalDentro / evento.getCapacidade() * 100.0 : 0;

        String dataStr = evento.getData() != null ? evento.getData().toString() : "";

        RelatorioEventoDTO dto = new RelatorioEventoDTO();
        dto.setEventoId(evento.getId());
        dto.setEventoNome(evento.getNome());
        dto.setEventoLocal(evento.getLocal() != null ? evento.getLocal() : "");
        dto.setEventoData(dataStr);
        dto.setCapacidade(evento.getCapacidade());
        dto.setTotalAprovados(totalAprovados);
        dto.setTotalDentro(totalDentro);
        dto.setTotalSaiu(totalSaiu);
        dto.setTotalAusente(totalAusente);
        dto.setPercentualOcupacao(Math.round(pct * 10.0) / 10.0);
        dto.setParticipantes(participantes);
        return dto;
    }
}
