package br.edu.unicid.controledeacesso.model;

public class CheckInRequest {
    private String token;
    private String dispositivo;

    public CheckInRequest(String token, String dispositivo) {
        this.token = token;
        this.dispositivo = dispositivo;
    }

    public String getToken()       { return token; }
    public String getDispositivo() { return dispositivo; }
}
