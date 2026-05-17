package br.edu.unicid.controledeacesso.api;

import br.edu.unicid.controledeacesso.model.CheckInRequest;
import br.edu.unicid.controledeacesso.model.CheckInResponse;
import br.edu.unicid.controledeacesso.model.Evento;
import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.model.IngressoRequest;
import br.edu.unicid.controledeacesso.model.Leitura;
import br.edu.unicid.controledeacesso.model.Participante;
import br.edu.unicid.controledeacesso.model.RelatorioEvento;
import br.edu.unicid.controledeacesso.model.Solicitacao;
import br.edu.unicid.controledeacesso.model.SolicitacaoRequest;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface ApiService {

    // ── Eventos ──────────────────────────────────────────────────────────────
    @POST("api/eventos")
    Call<Evento> criarEvento(@Body Evento evento);

    @GET("api/eventos")
    Call<List<Evento>> listarEventos();

    @GET("api/eventos/{id}")
    Call<Evento> buscarEvento(@Path("id") long id);

    @PUT("api/eventos/{id}")
    Call<Evento> atualizarEvento(@Path("id") Long id, @Body Evento evento);

    @DELETE("api/eventos/{id}")
    Call<Void> deletarEvento(@Path("id") Long id);

    // ── Participantes ─────────────────────────────────────────────────────────
    @POST("api/participantes")
    Call<Participante> criarParticipante(@Body Participante participante);

    @GET("api/participantes")
    Call<List<Participante>> listarParticipantes();

    @GET("api/participantes/email/{email}")
    Call<Participante> buscarParticipantePorEmail(@Path("email") String email);

    @PUT("api/participantes/{id}")
    Call<Participante> atualizarParticipante(@Path("id") Long id, @Body Participante participante);

    @DELETE("api/participantes/{id}")
    Call<Void> deletarParticipante(@Path("id") Long id);

    // ── Ingressos ─────────────────────────────────────────────────────────────
    @POST("api/ingressos")
    Call<Ingresso> gerarIngresso(@Body IngressoRequest request);

    @GET("api/ingressos/evento/{eventoId}")
    Call<List<Ingresso>> listarIngressosPorEvento(@Path("eventoId") long eventoId);

    @GET("api/ingressos/participante/{participanteId}")
    Call<List<Ingresso>> listarIngressosPorParticipante(@Path("participanteId") long participanteId);

    // ── Check-in ──────────────────────────────────────────────────────────────
    @POST("api/checkin")
    Call<CheckInResponse> validarToken(@Body CheckInRequest request);

    // ── Leituras (dashboard) ──────────────────────────────────────────────────
    @GET("api/leituras/evento/{eventoId}")
    Call<List<Leitura>> listarLeituras(@Path("eventoId") long eventoId);

    // ── Solicitações ──────────────────────────────────────────────────────────
    @POST("api/solicitacoes")
    Call<Solicitacao> criarSolicitacao(@Body SolicitacaoRequest request);

    @GET("api/solicitacoes/pendentes")
    Call<List<Solicitacao>> listarSolicitacoesPendentes();

    @GET("api/solicitacoes/participante/{participanteId}")
    Call<List<Solicitacao>> listarSolicitacoesPorParticipante(@Path("participanteId") long participanteId);

    @PUT("api/solicitacoes/{id}/aprovar")
    Call<Ingresso> aprovarSolicitacao(@Path("id") long id);

    @PUT("api/solicitacoes/{id}/rejeitar")
    Call<Void> rejeitarSolicitacao(@Path("id") long id);

    // ── Relatório de Presença ──────────────────────────────────────────────────
    @GET("api/relatorio/evento/{eventoId}")
    Call<RelatorioEvento> getRelatorioEvento(@Path("eventoId") long eventoId);
}
