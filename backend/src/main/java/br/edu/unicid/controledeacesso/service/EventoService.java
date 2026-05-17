package br.edu.unicid.controledeacesso.service;

import br.edu.unicid.controledeacesso.model.Evento;
import br.edu.unicid.controledeacesso.repository.EventoRepository;
import br.edu.unicid.controledeacesso.repository.IngressoRepository;
import br.edu.unicid.controledeacesso.repository.LeituraRepository;
import br.edu.unicid.controledeacesso.repository.SolicitacaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository     eventoRepository;
    private final IngressoRepository   ingressoRepository;
    private final LeituraRepository    leituraRepository;
    private final SolicitacaoRepository solicitacaoRepository;

    public EventoService(EventoRepository eventoRepository,
                         IngressoRepository ingressoRepository,
                         LeituraRepository leituraRepository,
                         SolicitacaoRepository solicitacaoRepository) {
        this.eventoRepository      = eventoRepository;
        this.ingressoRepository    = ingressoRepository;
        this.leituraRepository     = leituraRepository;
        this.solicitacaoRepository = solicitacaoRepository;
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

    @Transactional
    public void deletar(Long id) {
        buscar(id); // valida existência

        // 1. Leituras (dependem de ingressos que dependem do evento)
        leituraRepository.deleteByIngressoEventoId(id);

        // 2. Ingressos do evento
        ingressoRepository.deleteByEventoId(id);

        // 3. Solicitações do evento
        solicitacaoRepository.deleteByEventoId(id);

        // 4. O próprio evento
        eventoRepository.deleteById(id);
    }
}
