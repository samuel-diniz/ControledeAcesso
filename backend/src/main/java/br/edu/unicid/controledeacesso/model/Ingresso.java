package br.edu.unicid.controledeacesso.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ingresso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
        if (token == null)    token    = UUID.randomUUID();
        if (status == null)   status   = "PENDENTE";
        if (criadoEm == null) criadoEm = LocalDateTime.now();
    }
}
