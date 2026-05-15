package br.edu.unicid.controledeacesso.model;

public class Participante {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String criadoEm;

    public Long getId()           { return id; }
    public String getNome()       { return nome; }
    public String getEmail()      { return email; }
    public String getTelefone()   { return telefone; }
    public String getCriadoEm()   { return criadoEm; }

    public void setId(Long id)              { this.id = id; }
    public void setNome(String nome)        { this.nome = nome; }
    public void setEmail(String email)      { this.email = email; }
    public void setTelefone(String tel)     { this.telefone = tel; }

    @Override
    public String toString() { return nome != null ? nome : ""; }
}
