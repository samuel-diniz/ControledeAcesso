package br.edu.unicid.controledeacesso.dto;

import br.edu.unicid.controledeacesso.model.Evento;
import br.edu.unicid.controledeacesso.model.Participante;

public class CheckInResponse {

    private String resultado;
    private String mensagem;
    private Participante participante;
    private Evento evento;
    private String tipo;

    public CheckInResponse() {}

    public String getResultado()          { return resultado; }
    public String getMensagem()           { return mensagem; }
    public Participante getParticipante() { return participante; }
    public Evento getEvento()             { return evento; }
    public String getTipo()               { return tipo; }

    public void setResultado(String resultado)          { this.resultado = resultado; }
    public void setMensagem(String mensagem)            { this.mensagem = mensagem; }
    public void setParticipante(Participante p)         { this.participante = p; }
    public void setEvento(Evento evento)                { this.evento = evento; }
    public void setTipo(String tipo)                    { this.tipo = tipo; }

    // Factory helper — substitui o @Builder do Lombok
    public static CheckInResponse of(String resultado, String mensagem,
                                     Participante participante, Evento evento, String tipo) {
        CheckInResponse r = new CheckInResponse();
        r.resultado    = resultado;
        r.mensagem     = mensagem;
        r.participante = participante;
        r.evento       = evento;
        r.tipo         = tipo;
        return r;
    }
}
