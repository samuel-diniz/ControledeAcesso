package br.edu.unicid.controledeacesso.service;

import br.edu.unicid.controledeacesso.model.Participante;
import br.edu.unicid.controledeacesso.repository.ParticipanteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;

    public ParticipanteService(ParticipanteRepository participanteRepository) {
        this.participanteRepository = participanteRepository;
    }

    public Participante criar(Participante participante) {
        return participanteRepository.save(participante);
    }

    public List<Participante> listar() {
        return participanteRepository.findAll();
    }

    public Participante buscar(Long id) {
        return participanteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participante não encontrado"));
    }
}
