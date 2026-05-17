package br.edu.unicid.controledeacesso.dto;

public class ParticipantePresencaDTO {
    private Long id;
    private String nome;
    private String email;
    private String status;       // PENDENTE, DENTRO, SAIU
    private String entradaEm;    // timestamp ISO ou null
    private String saidaEm;      // timestamp ISO ou null

    public ParticipantePresencaDTO() {}

    public ParticipantePresencaDTO(Long id, String nome, String email,
                                    String status, String entradaEm, String saidaEm) {
        this.id = id; this.nome = nome; this.email = email;
        this.status = status; this.entradaEm = entradaEm; this.saidaEm = saidaEm;
    }

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
