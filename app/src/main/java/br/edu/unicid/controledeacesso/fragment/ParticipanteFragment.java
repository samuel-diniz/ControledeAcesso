package br.edu.unicid.controledeacesso.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.QrCodeActivity;
import br.edu.unicid.controledeacesso.R;
import br.edu.unicid.controledeacesso.adapter.ParticipanteAdapter;
import br.edu.unicid.controledeacesso.api.ApiClient;
import br.edu.unicid.controledeacesso.model.Evento;
import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.model.IngressoRequest;
import br.edu.unicid.controledeacesso.model.Participante;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipanteFragment extends Fragment {

    private static final int REFRESH_INTERVAL_MS = 8000;

    private EditText etNome, etEmail, etTelefone;
    private ParticipanteAdapter adapter;
    private Spinner spinnerEvento;
    private List<Evento> eventos = new ArrayList<>();
    private Long selectedEventoId = null;

    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_participante, container, false);

        etNome        = v.findViewById(R.id.et_p_nome);
        etEmail       = v.findViewById(R.id.et_p_email);
        etTelefone    = v.findViewById(R.id.et_p_telefone);
        spinnerEvento = v.findViewById(R.id.spinner_evento_filtro);

        RecyclerView rv = v.findViewById(R.id.rv_participantes);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ParticipanteAdapter();
        adapter.setListener(this::mostrarDialogIngresso);
        adapter.setOnEditarListener(this::mostrarDialogEdicao);
        adapter.setOnDeletarListener(this::confirmarDelecao);
        rv.setAdapter(adapter);

        v.findViewById(R.id.btn_cadastrar_p).setOnClickListener(x -> cadastrarParticipante());

        // Polling runnable — refreshes participants + status badge every 8 s
        refreshRunnable = () -> {
            carregarParticipantes();
            if (selectedEventoId != null) carregarStatusIngressos(selectedEventoId);
            refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL_MS);
        };

        carregarEventos();
        carregarParticipantes();
        return v;
    }

    // ── Event filter spinner ──────────────────────────────────────────────────

    /** Populate the spinner after events are loaded from the server. */
    private void popularSpinnerEventos() {
        List<String> nomes = new ArrayList<>();
        nomes.add("— Todos os participantes —");
        for (Evento e : eventos) nomes.add(e.getNome());

        ArrayAdapter<String> sa = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nomes);
        sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEvento.setAdapter(sa);

        spinnerEvento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedEventoId = null;
                    adapter.setStatusMap(null);   // hide status badges
                } else {
                    selectedEventoId = eventos.get(position - 1).getId();
                    carregarStatusIngressos(selectedEventoId);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /** Fetch ingressos for the selected event and build a participanteId→status map. */
    private void carregarStatusIngressos(long eventoId) {
        ApiClient.get().listarIngressosPorEvento(eventoId)
                .enqueue(new Callback<List<Ingresso>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ingresso>> call,
                                   @NonNull Response<List<Ingresso>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    Map<Long, String> map = new HashMap<>();
                    for (Ingresso ing : r.body()) {
                        if (ing.getParticipante() != null
                                && ing.getParticipante().getId() != null) {
                            map.put(ing.getParticipante().getId(),
                                    ing.getStatus() != null ? ing.getStatus() : "PENDENTE");
                        }
                    }
                    runUI(() -> adapter.setStatusMap(map));
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Ingresso>> c, @NonNull Throwable t) {}
        });
    }

    // ── Lifecycle: start / stop polling ──────────────────────────────────────

    @Override
    public void onResume() {
        super.onResume();
        // Start polling 8 s after the fragment becomes visible
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL_MS);
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshHandler.removeCallbacks(refreshRunnable);
    }

    // ── CRUD actions ──────────────────────────────────────────────────────────

    private void cadastrarParticipante() {
        String nome  = etNome.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        if (nome.isEmpty() || email.isEmpty()) {
            toast("Nome e e-mail são obrigatórios"); return;
        }
        Participante p = new Participante();
        p.setNome(nome); p.setEmail(email); p.setTelefone(etTelefone.getText().toString().trim());

        ApiClient.get().criarParticipante(p).enqueue(new Callback<Participante>() {
            @Override
            public void onResponse(@NonNull Call<Participante> call,
                                   @NonNull Response<Participante> r) {
                if (r.isSuccessful()) {
                    runUI(() -> {
                        toast("Participante cadastrado com sucesso!");
                        etNome.setText(""); etEmail.setText(""); etTelefone.setText("");
                        carregarParticipantes();
                    });
                } else showError("Erro ao cadastrar participante (" + r.code() + ")");
            }
            @Override public void onFailure(@NonNull Call<Participante> c, @NonNull Throwable t) {
                showError("Falha: " + t.getMessage());
            }
        });
    }

    private void mostrarDialogIngresso(Participante participante) {
        if (eventos.isEmpty()) { toast("Nenhum evento disponível. Crie um evento primeiro."); return; }

        ArrayAdapter<Evento> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, eventos);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = new Spinner(requireContext());
        spinner.setAdapter(spinnerAdapter);

        new AlertDialog.Builder(requireContext())
                .setTitle("🎫 Gerar ingresso para " + participante.getNome())
                .setMessage("Selecione o evento:")
                .setView(spinner)
                .setPositiveButton("Gerar", (dialog, which) -> {
                    Evento ev = (Evento) spinner.getSelectedItem();
                    if (ev != null) gerarIngresso(participante, ev);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void gerarIngresso(Participante participante, Evento evento) {
        IngressoRequest req = new IngressoRequest(evento.getId(), participante.getId());
        ApiClient.get().gerarIngresso(req).enqueue(new Callback<Ingresso>() {
            @Override
            public void onResponse(@NonNull Call<Ingresso> call, @NonNull Response<Ingresso> r) {
                if (r.isSuccessful() && r.body() != null) {
                    Ingresso ingresso = r.body();
                    runUI(() -> new AlertDialog.Builder(requireContext())
                            .setTitle("✅ Ingresso Gerado!")
                            .setMessage("QR Code gerado para " + participante.getNome()
                                    + " no evento \"" + evento.getNome() + "\".\n\n"
                                    + "O participante já pode acessar seu QR Code pelo app.")
                            .setPositiveButton("Ver QR Code", (d, w) -> {
                                Intent intent = new Intent(requireContext(), QrCodeActivity.class);
                                intent.putExtra("token", ingresso.getToken());
                                intent.putExtra("nomeParticipante", participante.getNome());
                                startActivity(intent);
                            })
                            .setNegativeButton("Fechar", null)
                            .show());
                } else {
                    showError("❌ Erro ao gerar ingresso (" + r.code() + ")");
                }
            }
            @Override public void onFailure(@NonNull Call<Ingresso> c, @NonNull Throwable t) {
                runUI(() -> new AlertDialog.Builder(requireContext())
                        .setTitle("❌ Erro ao Gerar Ingresso")
                        .setMessage("Falha de conexão.\n" + t.getMessage())
                        .setPositiveButton("OK", null)
                        .show());
            }
        });
    }

    private void mostrarDialogEdicao(Participante p) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 0);

        EditText etN = new EditText(requireContext()); etN.setHint("Nome *"); etN.setText(p.getNome());
        EditText etE = new EditText(requireContext()); etE.setHint("E-mail *"); etE.setText(p.getEmail());
        etE.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText etT = new EditText(requireContext()); etT.setHint("Telefone"); etT.setText(p.getTelefone());
        etT.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        layout.addView(etN); layout.addView(etE); layout.addView(etT);

        new AlertDialog.Builder(requireContext())
                .setTitle("✏ Editar Participante")
                .setView(layout)
                .setPositiveButton("Salvar", (d, w) -> {
                    String nome  = etN.getText().toString().trim();
                    String email = etE.getText().toString().trim();
                    if (nome.isEmpty() || email.isEmpty()) { toast("Nome e e-mail são obrigatórios"); return; }
                    Participante atualizado = new Participante();
                    atualizado.setNome(nome); atualizado.setEmail(email);
                    atualizado.setTelefone(etT.getText().toString().trim());
                    ApiClient.get().atualizarParticipante(p.getId(), atualizado)
                            .enqueue(new Callback<Participante>() {
                        @Override
                        public void onResponse(@NonNull Call<Participante> call,
                                               @NonNull Response<Participante> r) {
                            if (r.isSuccessful()) runUI(() -> {
                                toast("Participante atualizado!");
                                carregarParticipantes();
                            });
                            else showError("Erro ao atualizar participante");
                        }
                        @Override public void onFailure(@NonNull Call<Participante> c, @NonNull Throwable t) {
                            showError("Falha: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarDelecao(Participante p) {
        new AlertDialog.Builder(requireContext())
                .setTitle("🗑 Excluir Participante")
                .setMessage("Deseja excluir \"" + p.getNome() + "\"?\nTodos os ingressos associados também serão excluídos.")
                .setPositiveButton("Excluir", (d, w) ->
                        ApiClient.get().deletarParticipante(p.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> r) {
                                if (r.isSuccessful()) runUI(() -> {
                                    toast("Participante excluído!");
                                    carregarParticipantes();
                                });
                                else showError("Erro ao excluir participante");
                            }
                            @Override public void onFailure(@NonNull Call<Void> c, @NonNull Throwable t) {
                                showError("Falha: " + t.getMessage());
                            }
                        }))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    private void carregarParticipantes() {
        ApiClient.get().listarParticipantes().enqueue(new Callback<List<Participante>>() {
            @Override
            public void onResponse(@NonNull Call<List<Participante>> call,
                                   @NonNull Response<List<Participante>> r) {
                if (r.isSuccessful() && r.body() != null) runUI(() -> adapter.setItems(r.body()));
            }
            @Override public void onFailure(@NonNull Call<List<Participante>> c, @NonNull Throwable t) {}
        });
    }

    private void carregarEventos() {
        ApiClient.get().listarEventos().enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Evento>> call,
                                   @NonNull Response<List<Evento>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    eventos = r.body();
                    runUI(this::populateSpinner);
                }
            }
            private void populateSpinner() { popularSpinnerEventos(); }
            @Override public void onFailure(@NonNull Call<List<Evento>> c, @NonNull Throwable t) {}
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void toast(String msg)      { Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show(); }
    private void showError(String msg)  { runUI(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show()); }
    private void runUI(Runnable r)      { if (getActivity() != null) requireActivity().runOnUiThread(r); }
}
