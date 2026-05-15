package br.edu.unicid.controledeacesso.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leitura")
public class Leitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ingresso_id")
    private Ingresso ingresso;

    @Column(name = "token_lido", nullable = false, length = 36)
    private String tokenLido;

    @Column(nullable = false, length = 20)
    private String resultado;

    @Column(name = "lido_em", updatable = false)
    private LocalDateTime lidoEm;

    @Column(length = 100)
    private String dispositivo;

    @Column(name = "tipo", length = 20, nullable = true)
    private String tipo;

    @PrePersist
    void prePersist() {
        if (lidoEm == null) lidoEm = LocalDateTime.now();
    }

    public Long getId()             { return id; }
    public Ingresso getIngresso()   { return ingresso; }
    public String getTokenLido()    { return tokenLido; }
    public String getResultado()    { return resultado; }
    public LocalDateTime getLidoEm(){ return lidoEm; }
    public String getDispositivo()  { return dispositivo; }
    public String getTipo()         { return tipo; }

    public void setId(Long id)                  { this.id = id; }
    public void setIngresso(Ingresso ingresso)  { this.ingresso = ingresso; }
    public void setTokenLido(String tokenLido)  { this.tokenLido = tokenLido; }
    public void setResultado(String resultado)  { this.resultado = resultado; }
    public void setLidoEm(LocalDateTime t)      { this.lidoEm = t; }
    public void setDispositivo(String d)        { this.dispositivo = d; }
    public void setTipo(String tipo)            { this.tipo = tipo; }
}
