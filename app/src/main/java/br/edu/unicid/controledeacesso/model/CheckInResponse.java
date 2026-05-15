package br.edu.unicid.controledeacesso.model;

public class CheckInResponse {
    private String resultado;
    private String mensagem;
    private Participante participante;
    private Evento evento;

    public String getResultado()          { return resultado; }
    public String getMensagem()           { return mensagem; }
    public Participante getParticipante() { return participante; }
    public Evento getEvento()             { return evento; }
}
