package br.edu.unicid.controledeacesso.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngressoRequest {
    private Long eventoId;
    private Long participanteId;
}
