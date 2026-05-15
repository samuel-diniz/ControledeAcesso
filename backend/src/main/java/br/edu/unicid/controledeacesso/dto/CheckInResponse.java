package br.edu.unicid.controledeacesso.dto;

import br.edu.unicid.controledeacesso.model.Evento;
import br.edu.unicid.controledeacesso.model.Participante;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponse {
    private String resultado;   // VALIDO | INVALIDO | JA_USADO | LOTADO
    private String mensagem;
    private Participante participante;
    private Evento evento;
}
