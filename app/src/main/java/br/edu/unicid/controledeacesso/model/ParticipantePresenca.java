package br.edu.unicid.controledeacesso.model;

public class ParticipantePresenca {
    private Long id;
    private String nome;
    private String email;
    private String status;
    private String entradaEm;
    private String saidaEm;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getEntradaEm() { return entradaEm; }
    public String getSaidaEm() { return saidaEm; }
    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setStatus(String status) { this.status = status; }
    public void setEntradaEm(String entradaEm) { this.entradaEm = entradaEm; }
    public void setSaidaEm(String saidaEm) { this.saidaEm = saidaEm; }
}
