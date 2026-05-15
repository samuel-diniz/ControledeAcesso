package br.edu.unicid.controledeacesso.model;

public class Leitura {
    private Long id;
    private Ingresso ingresso;
    private String tokenLido;
    private String resultado;
    private String lidoEm;
    private String dispositivo;

    public Long getId()             { return id; }
    public Ingresso getIngresso()   { return ingresso; }
    public String getTokenLido()    { return tokenLido; }
    public String getResultado()    { return resultado; }
    public String getLidoEm()       { return lidoEm; }
    public String getDispositivo()  { return dispositivo; }
}
