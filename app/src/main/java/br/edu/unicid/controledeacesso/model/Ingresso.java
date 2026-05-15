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
}
