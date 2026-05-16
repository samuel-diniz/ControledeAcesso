package br.edu.unicid.controledeacesso.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao")
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "participante_id", nullable = false)
    private Participante participante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Column(nullable = false)
    private String status;

    @Column(name = "solicitado_em", updatable = false)
    private LocalDateTime solicitadoEm;

    @PrePersist
    void prePersist() {
        if (solicitadoEm == null) solicitadoEm = LocalDateTime.now();
        if (status == null) status = "PENDENTE";
    }

    public Long getId()                    { return id; }
    public Participante getParticipante()  { return participante; }
    public Evento getEvento()              { return evento; }
    public String getStatus()              { return status; }
    public LocalDateTime getSolicitadoEm() { return solicitadoEm; }

    public void setId(Long id)                       { this.id = id; }
    public void setParticipante(Participante p)       { this.participante = p; }
    public void setEvento(Evento e)                  { this.evento = e; }
    public void setStatus(String status)             { this.status = status; }
    public void setSolicitadoEm(LocalDateTime t)     { this.solicitadoEm = t; }
}
