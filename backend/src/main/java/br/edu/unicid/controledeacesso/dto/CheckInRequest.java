package br.edu.unicid.controledeacesso.dto;

public class CheckInRequest {
    private String token;
    private String dispositivo;
    private String tipo;

    public CheckInRequest() {}

    public String getToken()       { return token; }
    public String getDispositivo() { return dispositivo; }
    public String getTipo()        { return tipo != null ? tipo : "ENTRADA"; }

    public void setToken(String token)             { this.token = token; }
    public void setDispositivo(String dispositivo) { this.dispositivo = dispositivo; }
    public void setTipo(String tipo)               { this.tipo = tipo; }
}
