package br.edu.unicid.controledeacesso.model;

import com.google.gson.annotations.SerializedName;

public class Evento {
    private Long id;
    private String nome;
    private String descricao;
    private String data;
    private String local;
    private int capacidade;

    @SerializedName("criadoEm")
    private String criadoEm;

    public Long getId()           { return id; }
    public String getNome()       { return nome; }
    public String getDescricao()  { return descricao; }
    public String getData()       { return data; }
    public String getLocal()      { return local; }
    public int getCapacidade()    { return capacidade; }
    public String getCriadoEm()   { return criadoEm; }

    public void setId(Long id)              { this.id = id; }
    public void setNome(String nome)        { this.nome = nome; }
    public void setDescricao(String d)      { this.descricao = d; }
    public void setData(String data)        { this.data = data; }
    public void setLocal(String local)      { this.local = local; }
    public void setCapacidade(int cap)      { this.capacidade = cap; }

    @Override
    public String toString() { return nome != null ? nome : ""; }
}
