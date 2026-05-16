package br.edu.unicid.controledeacesso.model;

public class SolicitacaoRequest {
    private Long participanteId;
    private Long eventoId;

    public SolicitacaoRequest(Long participanteId, Long eventoId) {
        this.participanteId = participanteId;
        this.eventoId       = eventoId;
    }

    public Long getParticipanteId() { return participanteId; }
    public Long getEventoId()       { return eventoId; }

    public void setParticipanteId(Long participanteId) { this.participanteId = participanteId; }
    public void setEventoId(Long eventoId)             { this.eventoId = eventoId; }
}
