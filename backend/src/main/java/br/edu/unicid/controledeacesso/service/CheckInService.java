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
    public CheckInResponse validar(String tokenStr, String dispositivo, String tipo) {
        if (tipo == null || tipo.isBlank()) tipo = "ENTRADA";

        UUID token;
        try {
            token = UUID.fromString(tokenStr);
        } catch (IllegalArgumentException e) {
            salvarLeitura(null, tokenStr, "INVALIDO", dispositivo, tipo);
            return CheckInResponse.of("INVALIDO", "Token com formato invalido", null, null, tipo);
        }

        Optional<Ingresso> opt = ingressoRepository.findByToken(token);

        if (opt.isEmpty()) {
            salvarLeitura(null, tokenStr, "INVALIDO", dispositivo, tipo);
            return CheckInResponse.of("INVALIDO", "Token nao encontrado", null, null, tipo);
        }

        Ingresso ingresso = opt.get();
        String status = ingresso.getStatus();

        if ("ENTRADA".equals(tipo)) {
            // Participante já está dentro — não pode entrar duas vezes
            if ("DENTRO".equals(status)) {
                salvarLeitura(ingresso, tokenStr, "JA_USADO", dispositivo, tipo);
                return CheckInResponse.of("JA_USADO", "Participante ja esta dentro do evento",
                        ingresso.getParticipante(), ingresso.getEvento(), tipo);
            }
            // Participante já entrou e saiu — ingresso encerrado, reentrada não permitida
            if ("SAIU".equals(status) || "USADO".equals(status)) {
                salvarLeitura(ingresso, tokenStr, "JA_USADO", dispositivo, tipo);
                return CheckInResponse.of("JA_USADO", "Ingresso encerrado — participante ja entrou e saiu. Reentrada nao permitida.",
                        ingresso.getParticipante(), ingresso.getEvento(), tipo);
            }

            // Check capacity
            long dentro = ingressoRepository.countByEventoIdAndStatus(
                    ingresso.getEvento().getId(), "DENTRO");

            if (dentro >= ingresso.getEvento().getCapacidade()) {
                salvarLeitura(ingresso, tokenStr, "LOTADO", dispositivo, tipo);
                return CheckInResponse.of("LOTADO", "Evento lotado — capacidade maxima atingida",
                        null, ingresso.getEvento(), tipo);
            }

            ingresso.setStatus("DENTRO");
            ingressoRepository.save(ingresso);
            salvarLeitura(ingresso, tokenStr, "VALIDO", dispositivo, tipo);
            return CheckInResponse.of("VALIDO", "Entrada registrada com sucesso",
                    ingresso.getParticipante(), ingresso.getEvento(), tipo);

        } else {
            // SAIDA: requires DENTRO
            if ("PENDENTE".equals(status)) {
                salvarLeitura(ingresso, tokenStr, "NAO_ENTROU", dispositivo, tipo);
                return CheckInResponse.of("NAO_ENTROU", "Participante nao registrou entrada",
                        ingresso.getParticipante(), ingresso.getEvento(), tipo);
            }
            if ("SAIU".equals(status) || "USADO".equals(status)) {
                salvarLeitura(ingresso, tokenStr, "JA_USADO", dispositivo, tipo);
                return CheckInResponse.of("JA_USADO", "Saida ja registrada anteriormente",
                        ingresso.getParticipante(), ingresso.getEvento(), tipo);
            }

            ingresso.setStatus("SAIU");
            ingressoRepository.save(ingresso);
            salvarLeitura(ingresso, tokenStr, "VALIDO", dispositivo, tipo);
            return CheckInResponse.of("VALIDO", "Saida registrada com sucesso",
                    ingresso.getParticipante(), ingresso.getEvento(), tipo);
        }
    }

    private void salvarLeitura(Ingresso ingresso, String tokenLido,
                                String resultado, String dispositivo, String tipo) {
        Leitura leitura = new Leitura();
        leitura.setIngresso(ingresso);
        leitura.setTokenLido(tokenLido);
        leitura.setResultado(resultado);
        leitura.setDispositivo(dispositivo);
        leitura.setTipo(tipo);
        leituraRepository.save(leitura);
    }
}
