package br.edu.unicid.controledeacesso.service;

import br.edu.unicid.controledeacesso.dto.CheckInResponse;
import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.model.Leitura;
import br.edu.unicid.controledeacesso.repository.IngressoRepository;
import br.edu.unicid.controledeacesso.repository.LeituraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class CheckInService {

    private final IngressoRepository ingressoRepository;
    private final LeituraRepository leituraRepository;

    public CheckInService(IngressoRepository ingressoRepository,
                          LeituraRepository leituraRepository) {
        this.ingressoRepository = ingressoRepository;
        this.leituraRepository  = leituraRepository;
    }

    @Transactional
    public CheckInResponse validar(String tokenStr, String dispositivo) {
        UUID token;
        try {
            token = UUID.fromString(tokenStr);
        } catch (IllegalArgumentException e) {
            salvarLeitura(null, tokenStr, "INVALIDO", dispositivo);
            return CheckInResponse.of("INVALIDO", "Token com formato inválido", null, null);
        }

        Optional<Ingresso> opt = ingressoRepository.findByToken(token);

        if (opt.isEmpty()) {
            salvarLeitura(null, tokenStr, "INVALIDO", dispositivo);
            return CheckInResponse.of("INVALIDO", "Token não encontrado", null, null);
        }

        Ingresso ingresso = opt.get();

        if ("USADO".equals(ingresso.getStatus())) {
            salvarLeitura(ingresso, tokenStr, "JA_USADO", dispositivo);
            return CheckInResponse.of("JA_USADO", "Ingresso já utilizado",
                    ingresso.getParticipante(), ingresso.getEvento());
        }

        long usados = ingressoRepository.countByEventoIdAndStatus(
                ingresso.getEvento().getId(), "USADO");

        if (usados >= ingresso.getEvento().getCapacidade()) {
            salvarLeitura(ingresso, tokenStr, "LOTADO", dispositivo);
            return CheckInResponse.of("LOTADO", "Evento lotado — capacidade máxima atingida",
                    null, ingresso.getEvento());
        }

        ingresso.setStatus("USADO");
        ingressoRepository.save(ingresso);
        salvarLeitura(ingresso, tokenStr, "VALIDO", dispositivo);

        return CheckInResponse.of("VALIDO", "Check-in realizado com sucesso",
                ingresso.getParticipante(), ingresso.getEvento());
    }

    private void salvarLeitura(Ingresso ingresso, String tokenLido,
                                String resultado, String dispositivo) {
        Leitura leitura = new Leitura();
        leitura.setIngresso(ingresso);
        leitura.setTokenLido(tokenLido);
        leitura.setResultado(resultado);
        leitura.setDispositivo(dispositivo);
        leituraRepository.save(leitura);
    }
}
