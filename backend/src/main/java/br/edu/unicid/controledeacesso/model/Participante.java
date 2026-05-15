package br.edu.unicid.controledeacesso.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participante")
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String nome;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void prePersist() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
    }

    public Long getId()                { return id; }
    public String getNome()            { return nome; }
    public String getEmail()           { return email; }
    public String getTelefone()        { return telefone; }
    public LocalDateTime getCriadoEm() { return criadoEm; }

    public void setId(Long id)               { this.id = id; }
    public void setNome(String nome)         { this.nome = nome; }
    public void setEmail(String email)       { this.email = email; }
    public void setTelefone(String tel)      { this.telefone = tel; }
    public void setCriadoEm(LocalDateTime t) { this.criadoEm = t; }
}
