package br.edu.unicid.controledeacesso.service;

import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.model.Solicitacao;
import br.edu.unicid.controledeacesso.repository.EventoRepository;
import br.edu.unicid.controledeacesso.repository.IngressoRepository;
import br.edu.unicid.controledeacesso.repository.ParticipanteRepository;
import br.edu.unicid.controledeacesso.repository.SolicitacaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final ParticipanteRepository participanteRepository;
    private final EventoRepository eventoRepository;
    private final IngressoRepository ingressoRepository;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository,
                              ParticipanteRepository participanteRepository,
                              EventoRepository eventoRepository,
                              IngressoRepository ingressoRepository) {
        this.solicitacaoRepository  = solicitacaoRepository;
        this.participanteRepository = participanteRepository;
        this.eventoRepository       = eventoRepository;
        this.ingressoRepository     = ingressoRepository;
    }

    @Transactional
    public Solicitacao criar(Long participanteId, Long eventoId) {
        if (solicitacaoRepository.existsByParticipanteIdAndEventoId(participanteId, eventoId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Você já possui uma solicitação para este evento");
        }
        var participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participante não encontrado"));
        var evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));

        Solicitacao s = new Solicitacao();
        s.setParticipante(participante);
        s.setEvento(evento);
        s.setStatus("PENDENTE");
        return solicitacaoRepository.save(s);
    }

    public List<Solicitacao> listarPendentes() {
        return solicitacaoRepository.findByStatusOrderBySolicitadoEmDesc("PENDENTE");
    }

    public List<Solicitacao> listarPorParticipante(Long participanteId) {
        return solicitacaoRepository.findByParticipanteIdOrderBySolicitadoEmDesc(participanteId);
    }

    @Transactional
    public Ingresso aprovar(Long solicitacaoId) {
        Solicitacao s = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada"));
        s.setStatus("APROVADO");
        solicitacaoRepository.save(s);

        Ingresso ingresso = new Ingresso();
        ingresso.setParticipante(s.getParticipante());
        ingresso.setEvento(s.getEvento());
        return ingressoRepository.save(ingresso);
    }

    @Transactional
    public void rejeitar(Long solicitacaoId) {
        Solicitacao s = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada"));
        s.setStatus("REJEITADO");
        solicitacaoRepository.save(s);
    }
}
