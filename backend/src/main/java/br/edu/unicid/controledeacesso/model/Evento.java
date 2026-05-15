package br.edu.unicid.controledeacesso.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = true)
    private LocalDateTime data;

    @Column(length = 200)
    private String local;

    @Column(nullable = false)
    private int capacidade;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void prePersist() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
    }

    public Long getId()                { return id; }
    public String getNome()            { return nome; }
    public String getDescricao()       { return descricao; }
    public LocalDateTime getData()     { return data; }
    public String getLocal()           { return local; }
    public int getCapacidade()         { return capacidade; }
    public LocalDateTime getCriadoEm() { return criadoEm; }

    public void setId(Long id)               { this.id = id; }
    public void setNome(String nome)         { this.nome = nome; }
    public void setDescricao(String d)       { this.descricao = d; }
    public void setData(LocalDateTime data)  { this.data = data; }
    public void setLocal(String local)       { this.local = local; }
    public void setCapacidade(int cap)       { this.capacidade = cap; }
    public void setCriadoEm(LocalDateTime t) { this.criadoEm = t; }
}
