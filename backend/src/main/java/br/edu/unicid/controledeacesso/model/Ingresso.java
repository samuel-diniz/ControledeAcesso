package br.edu.unicid.controledeacesso.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ingresso")
public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "participante_id", nullable = false)
    private Participante participante;

    @Column(nullable = false, unique = true)
    private UUID token;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void prePersist() {
        if (token    == null) token    = UUID.randomUUID();
        if (status   == null) status   = "PENDENTE";
        if (criadoEm == null) criadoEm = LocalDateTime.now();
    }

    public Long getId()                  { return id; }
    public Evento getEvento()            { return evento; }
    public Participante getParticipante(){ return participante; }
    public UUID getToken()               { return token; }
    public String getStatus()            { return status; }
    public LocalDateTime getCriadoEm()   { return criadoEm; }

    public void setId(Long id)                       { this.id = id; }
    public void setEvento(Evento evento)             { this.evento = evento; }
    public void setParticipante(Participante p)      { this.participante = p; }
    public void setToken(UUID token)                 { this.token = token; }
    public void setStatus(String status)             { this.status = status; }
    public void setCriadoEm(LocalDateTime t)         { this.criadoEm = t; }
}
