package br.edu.unicid.controledeacesso;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.adapter.MeusIngressosAdapter;
import br.edu.unicid.controledeacesso.adapter.SolicitacaoAdapter;
import br.edu.unicid.controledeacesso.api.ApiClient;
import br.edu.unicid.controledeacesso.model.Evento;
import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.model.Participante;
import br.edu.unicid.controledeacesso.model.Solicitacao;
import br.edu.unicid.controledeacesso.model.SolicitacaoRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ParticipanteLoginActivity extends AppCompatActivity {

    // — Identificação
    private EditText etEmail;

    // — Cadastro (bloco amarelo)
    private View layoutCadastro;
    private EditText etNomeCadastro, etTelefoneCadastro;

    // — Blocos pós-login
    private View layoutSolicitar;
    private LinearLayout layoutMinhasSolicitacoes, layoutIngressos;

    // — Solicitar vaga
    private Spinner spinnerEventos;
    private List<Evento> eventos = new ArrayList<>();

    // — Minhas solicitações
    private RecyclerView rvMinhasSolicitacoes;
    private TextView tvSemSolicitacoesP;
    private SolicitacaoAdapter solicitacaoAdapter;

    // — Meus ingressos
    private RecyclerView rvMeusIngressos;
    private TextView tvSemIngressos;
    private MeusIngressosAdapter ingressoAdapter;

    // — Participante logado
    private Participante participanteAtual = null;

    // — Auto-refresh
    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private static final long REFRESH_INTERVAL_MS = 10_000; // 10 segundos
    private final Runnable refreshTask = new Runnable() {
        @Override public void run() {
            if (participanteAtual != null) {
                carregarMinhasSolicitacoes();
                carregarMeusIngressos();
                refreshHandler.postDelayed(this, REFRESH_INTERVAL_MS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participante_login);

        // Header
        ImageButton btnVoltar = findViewById(R.id.btn_voltar);
        btnVoltar.setOnClickListener(v -> finish());

        // Bloco 1 — identificação
        etEmail = findViewById(R.id.et_email);
        Button btnBuscar = findViewById(R.id.btn_buscar);
        btnBuscar.setOnClickListener(v -> buscarOuSugerirCadastro());

        // Bloco 2 — cadastro
        layoutCadastro       = findViewById(R.id.layout_cadastro);
        etNomeCadastro       = findViewById(R.id.et_nome_cadastro);
        etTelefoneCadastro   = findViewById(R.id.et_telefone_cadastro);
        Button btnCadastrarSe = findViewById(R.id.btn_cadastrar_se);
        btnCadastrarSe.setOnClickListener(v -> cadastrarSe());

        // Bloco 3 — solicitar vaga
        layoutSolicitar = findViewById(R.id.layout_solicitar);
        spinnerEventos  = findViewById(R.id.spinner_eventos);
        Button btnSolicitar = findViewById(R.id.btn_solicitar);
        btnSolicitar.setOnClickListener(v -> solicitarVaga());

        // Bloco 4 — minhas solicitações
        layoutMinhasSolicitacoes = findViewById(R.id.layout_minhas_solicitacoes);
        tvSemSolicitacoesP       = findViewById(R.id.tv_sem_solicitacoes_p);
        rvMinhasSolicitacoes     = findViewById(R.id.rv_minhas_solicitacoes);
        rvMinhasSolicitacoes.setLayoutManager(new LinearLayoutManager(this));
        solicitacaoAdapter = new SolicitacaoAdapter();
        rvMinhasSolicitacoes.setAdapter(solicitacaoAdapter);

        // Bloco 5 — meus ingressos
        layoutIngressos = findViewById(R.id.layout_ingressos);
        tvSemIngressos  = findViewById(R.id.tv_sem_ingressos);
        rvMeusIngressos = findViewById(R.id.rv_meus_ingressos);
        rvMeusIngressos.setLayoutManager(new LinearLayoutManager(this));
        ingressoAdapter = new MeusIngressosAdapter();
        rvMeusIngressos.setAdapter(ingressoAdapter);

        ingressoAdapter.setOnVerQrCodeListener(ingresso -> {
            String token = ingresso.getToken() != null ? ingresso.getToken() : "";
            String nome  = (ingresso.getParticipante() != null && ingresso.getParticipante().getNome() != null)
                    ? ingresso.getParticipante().getNome() : "";
            Intent intent = new Intent(this, QrCodeActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("nomeParticipante", nome);
            startActivity(intent);
        });

        // Pré-carrega eventos para o spinner
        carregarEventos();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Busca participante por e-mail; se não existir, exibe bloco de cadastro
    // ─────────────────────────────────────────────────────────────────────────
    private void buscarOuSugerirCadastro() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            toast("Digite seu e-mail"); return;
        }

        ApiClient.get().buscarParticipantePorEmail(email).enqueue(new Callback<Participante>() {
            @Override
            public void onResponse(@NonNull Call<Participante> call, @NonNull Response<Participante> r) {
                if (r.isSuccessful() && r.body() != null) {
                    // Participante encontrado → mostra blocos pós-login
                    runOnUiThread(() -> entrarComoParticipante(r.body()));
                } else {
                    // Não encontrado → sugere cadastro
                    runOnUiThread(() -> {
                        layoutCadastro.setVisibility(View.VISIBLE);
                        ocultarBlocosPosLogin();
                    });
                }
            }
            @Override public void onFailure(@NonNull Call<Participante> c, @NonNull Throwable t) {
                runOnUiThread(() -> toast("Falha de conexão: " + t.getMessage()));
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cadastro próprio do participante
    // ─────────────────────────────────────────────────────────────────────────
    private void cadastrarSe() {
        String nome     = etNomeCadastro.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String telefone = etTelefoneCadastro.getText().toString().trim();

        if (nome.isEmpty()) { toast("Informe seu nome"); return; }

        Participante p = new Participante();
        p.setNome(nome);
        p.setEmail(email);
        p.setTelefone(telefone);

        ApiClient.get().criarParticipante(p).enqueue(new Callback<Participante>() {
            @Override
            public void onResponse(@NonNull Call<Participante> call, @NonNull Response<Participante> r) {
                if (r.isSuccessful() && r.body() != null) {
                    runOnUiThread(() -> {
                        layoutCadastro.setVisibility(View.GONE);
                        toast("Cadastro realizado! Bem-vindo(a), " + r.body().getNome() + "!");
                        entrarComoParticipante(r.body());
                    });
                } else {
                    runOnUiThread(() -> toast("Erro ao cadastrar. Verifique se o e-mail já está em uso."));
                }
            }
            @Override public void onFailure(@NonNull Call<Participante> c, @NonNull Throwable t) {
                runOnUiThread(() -> toast("Falha: " + t.getMessage()));
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Ativa o modo "logado": mostra todos os blocos e carrega os dados
    // ─────────────────────────────────────────────────────────────────────────
    private void entrarComoParticipante(Participante p) {
        participanteAtual = p;
        layoutCadastro.setVisibility(View.GONE);
        layoutSolicitar.setVisibility(View.VISIBLE);
        layoutMinhasSolicitacoes.setVisibility(View.VISIBLE);
        layoutIngressos.setVisibility(View.VISIBLE);

        carregarMinhasSolicitacoes();
        carregarMeusIngressos();

        // Inicia o auto-refresh a cada 10 segundos
        refreshHandler.removeCallbacksAndMessages(null);
        refreshHandler.postDelayed(refreshTask, REFRESH_INTERVAL_MS);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Solicita vaga no evento selecionado no spinner
    // ─────────────────────────────────────────────────────────────────────────
    private void solicitarVaga() {
        if (participanteAtual == null) { toast("Faça login primeiro"); return; }
        if (eventos.isEmpty())         { toast("Nenhum evento disponível"); return; }

        Evento ev = (Evento) spinnerEventos.getSelectedItem();
        if (ev == null) { toast("Selecione um evento"); return; }

        SolicitacaoRequest req = new SolicitacaoRequest(participanteAtual.getId(), ev.getId());

        ApiClient.get().criarSolicitacao(req).enqueue(new Callback<Solicitacao>() {
            @Override
            public void onResponse(@NonNull Call<Solicitacao> call, @NonNull Response<Solicitacao> r) {
                if (r.isSuccessful()) {
                    runOnUiThread(() -> {
                        new AlertDialog.Builder(ParticipanteLoginActivity.this)
                                .setTitle("✅ Solicitação enviada!")
                                .setMessage("Sua solicitação para o evento \"" + ev.getNome()
                                        + "\" foi registrada.\n\nAguarde a aprovação do organizador. "
                                        + "Você poderá acompanhar o status em \"Minhas solicitações\".")
                                .setPositiveButton("OK", null)
                                .show();
                        carregarMinhasSolicitacoes();
                    });
                } else if (r.code() == 409) {
                    runOnUiThread(() -> toast("Você já solicitou vaga neste evento."));
                } else {
                    runOnUiThread(() -> toast("Erro ao enviar solicitação (" + r.code() + ")"));
                }
            }
            @Override public void onFailure(@NonNull Call<Solicitacao> c, @NonNull Throwable t) {
                runOnUiThread(() -> toast("Falha: " + t.getMessage()));
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Carregamentos
    // ─────────────────────────────────────────────────────────────────────────
    private void carregarEventos() {
        ApiClient.get().listarEventos().enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Evento>> call, @NonNull Response<List<Evento>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    eventos = r.body();
                    runOnUiThread(() -> {
                        ArrayAdapter<Evento> adapter = new ArrayAdapter<>(
                                ParticipanteLoginActivity.this,
                                android.R.layout.simple_spinner_item, eventos);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerEventos.setAdapter(adapter);
                    });
                }
            }
            @Override public void onFailure(@NonNull Call<List<Evento>> c, @NonNull Throwable t) {}
        });
    }

    private void carregarMinhasSolicitacoes() {
        if (participanteAtual == null) return;
        ApiClient.get().listarSolicitacoesPorParticipante(participanteAtual.getId())
                .enqueue(new Callback<List<Solicitacao>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Solicitacao>> call,
                                           @NonNull Response<List<Solicitacao>> r) {
                        runOnUiThread(() -> {
                            if (r.isSuccessful() && r.body() != null && !r.body().isEmpty()) {
                                solicitacaoAdapter.setItems(r.body());
                                tvSemSolicitacoesP.setVisibility(View.GONE);
                                rvMinhasSolicitacoes.setVisibility(View.VISIBLE);
                            } else {
                                solicitacaoAdapter.setItems(null);
                                tvSemSolicitacoesP.setVisibility(View.VISIBLE);
                                rvMinhasSolicitacoes.setVisibility(View.GONE);
                            }
                        });
                    }
                    @Override public void onFailure(@NonNull Call<List<Solicitacao>> c, @NonNull Throwable t) {}
                });
    }

    private void carregarMeusIngressos() {
        if (participanteAtual == null) return;
        ApiClient.get().listarIngressosPorParticipante(participanteAtual.getId())
                .enqueue(new Callback<List<Ingresso>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Ingresso>> call,
                                           @NonNull Response<List<Ingresso>> r) {
                        runOnUiThread(() -> {
                            if (r.isSuccessful() && r.body() != null && !r.body().isEmpty()) {
                                ingressoAdapter.setItems(r.body());
                                rvMeusIngressos.setVisibility(View.VISIBLE);
                                tvSemIngressos.setVisibility(View.GONE);
                            } else {
                                ingressoAdapter.setItems(null);
                                rvMeusIngressos.setVisibility(View.GONE);
                                tvSemIngressos.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    @Override public void onFailure(@NonNull Call<List<Ingresso>> c, @NonNull Throwable t) {}
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────
    private void ocultarBlocosPosLogin() {
        layoutSolicitar.setVisibility(View.GONE);
        layoutMinhasSolicitacoes.setVisibility(View.GONE);
        layoutIngressos.setVisibility(View.GONE);
        participanteAtual = null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Ciclo de vida — inicia/para o auto-refresh
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    protected void onResume() {
        super.onResume();
        if (participanteAtual != null) {
            // Atualiza imediatamente ao voltar para a tela e agenda o ciclo
            carregarMinhasSolicitacoes();
            carregarMeusIngressos();
            refreshHandler.postDelayed(refreshTask, REFRESH_INTERVAL_MS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshHandler.removeCallbacksAndMessages(null);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
