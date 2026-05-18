package br.edu.unicid.controledeacesso.service;

import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.repository.EventoRepository;
import br.edu.unicid.controledeacesso.repository.IngressoRepository;
import br.edu.unicid.controledeacesso.repository.ParticipanteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;

    public IngressoService(IngressoRepository ingressoRepository,
                           EventoRepository eventoRepository,
                           ParticipanteRepository participanteRepository) {
        this.ingressoRepository    = ingressoRepository;
        this.eventoRepository      = eventoRepository;
        this.participanteRepository = participanteRepository;
    }

    public Ingresso gerar(Long eventoId, Long participanteId) {
        eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento nao encontrado"));
        participanteRepository.findById(participanteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participante nao encontrado"));

        // Prevent duplicate ingressos: return existing one if already present
        Optional<Ingresso> existing = ingressoRepository
                .findByEventoIdAndParticipanteId(eventoId, participanteId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Ingresso ingresso = new Ingresso();
        ingresso.setEvento(eventoRepository.findById(eventoId).get());
        ingresso.setParticipante(participanteRepository.findById(participanteId).get());
        return ingressoRepository.save(ingresso);
    }

    public List<Ingresso> listar() {
        return ingressoRepository.findAll();
    }

    public List<Ingresso> listarPorEvento(Long eventoId) {
        return ingressoRepository.findByEventoId(eventoId);
    }

    public List<Ingresso> listarPorParticipante(Long participanteId) {
        return ingressoRepository.findByParticipanteId(participanteId);
    }

    public Ingresso buscar(Long id) {
        return ingressoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingresso nao encontrado"));
    }
}
