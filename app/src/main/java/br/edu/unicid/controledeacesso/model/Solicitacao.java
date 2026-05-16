package br.edu.unicid.controledeacesso.model;

public class Solicitacao {
    private Long id;
    private Participante participante;
    private Evento evento;
    private String status;
    private String solicitadoEm;

    public Long getId()                  { return id; }
    public Participante getParticipante(){ return participante; }
    public Evento getEvento()            { return evento; }
    public String getStatus()            { return status; }
    public String getSolicitadoEm()      { return solicitadoEm; }

    public void setId(Long id)                     { this.id = id; }
    public void setParticipante(Participante p)     { this.participante = p; }
    public void setEvento(Evento e)                { this.evento = e; }
    public void setStatus(String status)           { this.status = status; }
    public void setSolicitadoEm(String s)          { this.solicitadoEm = s; }
}
