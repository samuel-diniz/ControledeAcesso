package br.edu.unicid.controledeacesso.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void prePersist() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
    }

    public Long getId()              { return id; }
    public String getUsername()      { return username; }
    public String getPasswordHash()  { return passwordHash; }
    public LocalDateTime getCriadoEm() { return criadoEm; }

    public void setId(Long id)                    { this.id = id; }
    public void setUsername(String username)       { this.username = username; }
    public void setPasswordHash(String h)         { this.passwordHash = h; }
    public void setCriadoEm(LocalDateTime t)      { this.criadoEm = t; }
}
