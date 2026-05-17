package br.edu.unicid.controledeacesso.dto;

import java.util.List;

public class RelatorioEventoDTO {
    private Long eventoId;
    private String eventoNome;
    private String eventoLocal;
    private String eventoData;
    private int capacidade;
    private int totalAprovados;
    private int totalDentro;
    private int totalSaiu;
    private int totalAusente;    // aprovados mas nunca entraram (PENDENTE)
    private double percentualOcupacao;
    private List<ParticipantePresencaDTO> participantes;

    public RelatorioEventoDTO() {}

    // getters e setters manuais
    public Long getEventoId() { return eventoId; }
    public String getEventoNome() { return eventoNome; }
    public String getEventoLocal() { return eventoLocal; }
    public String getEventoData() { return eventoData; }
    public int getCapacidade() { return capacidade; }
    public int getTotalAprovados() { return totalAprovados; }
    public int getTotalDentro() { return totalDentro; }
    public int getTotalSaiu() { return totalSaiu; }
    public int getTotalAusente() { return totalAusente; }
    public double getPercentualOcupacao() { return percentualOcupacao; }
    public List<ParticipantePresencaDTO> getParticipantes() { return participantes; }

    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public void setEventoNome(String eventoNome) { this.eventoNome = eventoNome; }
    public void setEventoLocal(String eventoLocal) { this.eventoLocal = eventoLocal; }
    public void setEventoData(String eventoData) { this.eventoData = eventoData; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }
    public void setTotalAprovados(int totalAprovados) { this.totalAprovados = totalAprovados; }
    public void setTotalDentro(int totalDentro) { this.totalDentro = totalDentro; }
    public void setTotalSaiu(int totalSaiu) { this.totalSaiu = totalSaiu; }
    public void setTotalAusente(int totalAusente) { this.totalAusente = totalAusente; }
    public void setPercentualOcupacao(double percentualOcupacao) { this.percentualOcupacao = percentualOcupacao; }
    public void setParticipantes(List<ParticipantePresencaDTO> participantes) { this.participantes = participantes; }
}
