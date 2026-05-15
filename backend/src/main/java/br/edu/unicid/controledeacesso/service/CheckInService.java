package br.edu.unicid.controledeacesso.service;

import br.edu.unicid.controledeacesso.dto.CheckInResponse;
import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.model.Leitura;
import br.edu.unicid.controledeacesso.repository.IngressoRepository;
import br.edu.unicid.controledeacesso.repository.LeituraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final IngressoRepository ingressoRepository;
    private final LeituraRepository leituraRepository;

    @Transactional
    public CheckInResponse validar(String tokenStr, String dispositivo) {
        UUID token;
        try {
            token = UUID.fromString(tokenStr);
        } catch (IllegalArgumentException e) {
            salvarLeitura(null, tokenStr, "INVALIDO", dispositivo);
            return CheckInResponse.builder()
                    .resultado("INVALIDO")
                    .mensagem("Token com formato inválido")
                    .build();
        }

        Optional<Ingresso> opt = ingressoRepository.findByToken(token);

        if (opt.isEmpty()) {
            salvarLeitura(null, tokenStr, "INVALIDO", dispositivo);
            return CheckInResponse.builder()
                    .resultado("INVALIDO")
                    .mensagem("Token não encontrado")
                    .build();
        }

        Ingresso ingresso = opt.get();

        if ("USADO".equals(ingresso.getStatus())) {
            salvarLeitura(ingresso, tokenStr, "JA_USADO", dispositivo);
            return CheckInResponse.builder()
                    .resultado("JA_USADO")
                    .mensagem("Ingresso já utilizado")
                    .participante(ingresso.getParticipante())
                    .evento(ingresso.getEvento())
                    .build();
        }

        long usados = ingressoRepository.countByEventoIdAndStatus(
                ingresso.getEvento().getId(), "USADO");
        if (usados >= ingresso.getEvento().getCapacidade()) {
            salvarLeitura(ingresso, tokenStr, "LOTADO", dispositivo);
            return CheckInResponse.builder()
                    .resultado("LOTADO")
                    .mensagem("Evento lotado — capacidade máxima atingida")
                    .evento(ingresso.getEvento())
                    .build();
        }

        ingresso.setStatus("USADO");
        ingressoRepository.save(ingresso);
        salvarLeitura(ingresso, tokenStr, "VALIDO", dispositivo);

        return CheckInResponse.builder()
                .resultado("VALIDO")
                .mensagem("Check-in realizado com sucesso")
                .participante(ingresso.getParticipante())
                .evento(ingresso.getEvento())
                .build();
    }

    private void salvarLeitura(Ingresso ingresso, String tokenLido, String resultado, String dispositivo) {
        Leitura leitura = new Leitura();
        leitura.setIngresso(ingresso);
        leitura.setTokenLido(tokenLido);
        leitura.setResultado(resultado);
        leitura.setDispositivo(dispositivo);
        leituraRepository.save(leitura);
    }
}
