package br.edu.unicid.controledeacesso.api;

import br.edu.unicid.controledeacesso.model.CheckInRequest;
import br.edu.unicid.controledeacesso.model.CheckInResponse;
import br.edu.unicid.controledeacesso.model.Evento;
import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.model.IngressoRequest;
import br.edu.unicid.controledeacesso.model.Leitura;
import br.edu.unicid.controledeacesso.model.Participante;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface ApiService {

    // Eventos
    @POST("api/eventos")
    Call<Evento> criarEvento(@Body Evento evento);

    @GET("api/eventos")
    Call<List<Evento>> listarEventos();

    @GET("api/eventos/{id}")
    Call<Evento> buscarEvento(@Path("id") long id);

    // Participantes
    @POST("api/participantes")
    Call<Participante> criarParticipante(@Body Participante participante);

    @GET("api/participantes")
    Call<List<Participante>> listarParticipantes();

    // Ingressos
    @POST("api/ingressos")
    Call<Ingresso> gerarIngresso(@Body IngressoRequest request);

    @GET("api/ingressos/evento/{eventoId}")
    Call<List<Ingresso>> listarIngressosPorEvento(@Path("eventoId") long eventoId);

    // Check-in
    @POST("api/checkin")
    Call<CheckInResponse> validarToken(@Body CheckInRequest request);

    // Leituras (dashboard)
    @GET("api/leituras/evento/{eventoId}")
    Call<List<Leitura>> listarLeituras(@Path("eventoId") long eventoId);
}
