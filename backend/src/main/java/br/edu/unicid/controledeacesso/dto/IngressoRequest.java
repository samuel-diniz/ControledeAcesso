package br.edu.unicid.controledeacesso.dto;

public class IngressoRequest {
    private Long eventoId;
    private Long participanteId;

    public IngressoRequest() {}

    public Long getEventoId()       { return eventoId; }
    public Long getParticipanteId() { return participanteId; }

    public void setEventoId(Long eventoId)             { this.eventoId = eventoId; }
    public void setParticipanteId(Long participanteId) { this.participanteId = participanteId; }
}
