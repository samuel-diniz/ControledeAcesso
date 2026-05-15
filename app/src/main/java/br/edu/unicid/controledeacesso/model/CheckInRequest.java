package br.edu.unicid.controledeacesso.model;

public class CheckInRequest {
    private String token;
    private String dispositivo;
    private String tipo;

    public CheckInRequest(String token, String dispositivo, String tipo) {
        this.token       = token;
        this.dispositivo = dispositivo;
        this.tipo        = tipo;
    }

    public String getToken()       { return token; }
    public String getDispositivo() { return dispositivo; }
    public String getTipo()        { return tipo; }

    public void setToken(String token)             { this.token = token; }
    public void setDispositivo(String dispositivo) { this.dispositivo = dispositivo; }
    public void setTipo(String tipo)               { this.tipo = tipo; }
}
