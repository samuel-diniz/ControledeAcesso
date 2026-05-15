package br.edu.unicid.controledeacesso.model;

public class IngressoRequest {
    private Long eventoId;
    private Long participanteId;

    public IngressoRequest(Long eventoId, Long participanteId) {
        this.eventoId = eventoId;
        this.participanteId = participanteId;
    }

    public Long getEventoId()       { return eventoId; }
    public Long getParticipanteId() { return participanteId; }
}
