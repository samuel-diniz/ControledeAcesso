package br.edu.unicid.controledeacesso.dto;

public class CheckInRequest {
    private String token;
    private String dispositivo;

    public CheckInRequest() {}

    public String getToken()       { return token; }
    public String getDispositivo() { return dispositivo; }

    public void setToken(String token)             { this.token = token; }
    public void setDispositivo(String dispositivo) { this.dispositivo = dispositivo; }
}
