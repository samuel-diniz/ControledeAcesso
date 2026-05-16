package br.edu.unicid.controledeacesso.service;

import br.edu.unicid.controledeacesso.model.Evento;
import br.edu.unicid.controledeacesso.repository.EventoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public Evento criar(Evento evento) {
        return eventoRepository.save(evento);
    }

    public List<Evento> listar() {
        return eventoRepository.findAll();
    }

    public Evento buscar(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
    }

    public Evento atualizar(Long id, Evento dados) {
        Evento evento = buscar(id);
        evento.setNome(dados.getNome());
        evento.setDescricao(dados.getDescricao());
        evento.setData(dados.getData());
        evento.setLocal(dados.getLocal());
        evento.setCapacidade(dados.getCapacidade());
        return eventoRepository.save(evento);
    }

    public void deletar(Long id) {
        buscar(id);
        eventoRepository.deleteById(id);
    }
}
