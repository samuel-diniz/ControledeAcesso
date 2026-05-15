package br.edu.unicid.controledeacesso.service;

import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.repository.EventoRepository;
import br.edu.unicid.controledeacesso.repository.IngressoRepository;
import br.edu.unicid.controledeacesso.repository.ParticipanteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

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
        var evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
        var participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participante não encontrado"));

        Ingresso ingresso = new Ingresso();
        ingresso.setEvento(evento);
        ingresso.setParticipante(participante);
        return ingressoRepository.save(ingresso);
    }

    public List<Ingresso> listar() {
        return ingressoRepository.findAll();
    }

    public List<Ingresso> listarPorEvento(Long eventoId) {
        return ingressoRepository.findByEventoId(eventoId);
    }

    public Ingresso buscar(Long id) {
        return ingressoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingresso não encontrado"));
    }
}
