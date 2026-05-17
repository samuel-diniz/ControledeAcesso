package br.edu.unicid.controledeacesso.service;

import br.edu.unicid.controledeacesso.model.Participante;
import br.edu.unicid.controledeacesso.repository.IngressoRepository;
import br.edu.unicid.controledeacesso.repository.LeituraRepository;
import br.edu.unicid.controledeacesso.repository.ParticipanteRepository;
import br.edu.unicid.controledeacesso.repository.SolicitacaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ParticipanteService {

    private final ParticipanteRepository  participanteRepository;
    private final IngressoRepository      ingressoRepository;
    private final LeituraRepository       leituraRepository;
    private final SolicitacaoRepository   solicitacaoRepository;

    public ParticipanteService(ParticipanteRepository participanteRepository,
                               IngressoRepository ingressoRepository,
                               LeituraRepository leituraRepository,
                               SolicitacaoRepository solicitacaoRepository) {
        this.participanteRepository = participanteRepository;
        this.ingressoRepository     = ingressoRepository;
        this.leituraRepository      = leituraRepository;
        this.solicitacaoRepository  = solicitacaoRepository;
    }

    public Participante criar(Participante participante) {
        return participanteRepository.save(participante);
    }

    public List<Participante> listar() {
        return participanteRepository.findAll();
    }

    public Participante buscar(Long id) {
        return participanteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participante nao encontrado"));
    }

    public Participante buscarPorEmail(String email) {
        return participanteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Participante nao encontrado para o e-mail: " + email));
    }

    public Participante atualizar(Long id, Participante dados) {
        Participante p = buscar(id);
        p.setNome(dados.getNome());
        p.setEmail(dados.getEmail());
        if (dados.getTelefone() != null) p.setTelefone(dados.getTelefone());
        return participanteRepository.save(p);
    }

    @Transactional
    public void deletar(Long id) {
        buscar(id); // valida existência

        // 1. Leituras (dependem de ingressos do participante)
        leituraRepository.deleteByIngressoParticipanteId(id);

        // 2. Ingressos do participante
        ingressoRepository.deleteByParticipanteId(id);

        // 3. Solicitações do participante
        solicitacaoRepository.deleteByParticipanteId(id);

        // 4. O próprio participante
        participanteRepository.deleteById(id);
    }
}
