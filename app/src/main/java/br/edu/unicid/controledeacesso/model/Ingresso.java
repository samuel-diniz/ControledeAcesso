package br.edu.unicid.controledeacesso.model;

public class Ingresso {
    private Long id;
    private Evento evento;
    private Participante participante;
    private String token;
    private String status;
    private String criadoEm;

    public Long getId()                    { return id; }
    public Evento getEvento()              { return evento; }
    public Participante getParticipante()  { return participante; }
    public String getToken()               { return token; }
    public String getStatus()              { return status; }
    public String getCriadoEm()            { return criadoEm; }

    public void setId(Long id)                         { this.id = id; }
    public void setEvento(Evento evento)               { this.evento = evento; }
    public void setParticipante(Participante p)        { this.participante = p; }
    public void setToken(String token)                 { this.token = token; }
    public void setStatus(String status)               { this.status = status; }
    public void setCriadoEm(String criadoEm)           { this.criadoEm = criadoEm; }
}
