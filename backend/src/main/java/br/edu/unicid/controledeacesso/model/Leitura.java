package br.edu.unicid.controledeacesso.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leitura")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ingresso_id")
    private Ingresso ingresso; // null quando token não existe

    @Column(name = "token_lido", nullable = false, length = 36)
    private String tokenLido;

    @Column(nullable = false, length = 20)
    private String resultado; // VALIDO | INVALIDO | JA_USADO | LOTADO

    @Column(name = "lido_em", updatable = false)
    private LocalDateTime lidoEm;

    @Column(length = 100)
    private String dispositivo;

    @PrePersist
    void prePersist() {
        if (lidoEm == null) lidoEm = LocalDateTime.now();
    }
}
