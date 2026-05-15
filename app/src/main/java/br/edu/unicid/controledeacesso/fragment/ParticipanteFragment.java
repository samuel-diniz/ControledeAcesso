package br.edu.unicid.controledeacesso.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;

public class ParticipanteFragment extends Fragment {

    private EditText etNome, etEmail, etTelefone;
    private ParticipanteAdapter adapter;
    private List<Evento> eventos = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_participante, container, false);

        etNome     = v.findViewById(R.id.et_p_nome);
        etEmail    = v.findViewById(R.id.et_p_email);
        etTelefone = v.findViewById(R.id.et_p_telefone);

        RecyclerView rv = v.findViewById(R.id.rv_participantes);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ParticipanteAdapter();
        adapter.setListener(this::mostrarDialogIngresso);
        rv.setAdapter(adapter);

        v.findViewById(R.id.btn_cadastrar_p).setOnClickListener(x -> cadastrarParticipante());

        carregarEventos();
        carregarParticipantes();
        return v;
    }

    private void cadastrarParticipante() {
        String nome  = etNome.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        if (nome.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Nome e e-mail são obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }
        Participante p = new Participante();
        p.setNome(nome);
        p.setEmail(email);
        p.setTelefone(etTelefone.getText().toString().trim());

        ApiClient.get().criarParticipante(p).enqueue(new Callback<Participante>() {
            @Override
            public void onResponse(@NonNull Call<Participante> call, @NonNull Response<Participante> r) {
                if (r.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Participante cadastrado!", Toast.LENGTH_SHORT).show();
                        etNome.setText(""); etEmail.setText(""); etTelefone.setText("");
                        carregarParticipantes();
                    });
                } else {
                    showError("Erro " + r.code());
                }
            }
            @Override public void onFailure(@NonNull Call<Participante> c, @NonNull Throwable t) {
                showError("Falha: " + t.getMessage());
            }
        });
    }

    private void mostrarDialogIngresso(Participante participante) {
        if (eventos.isEmpty()) {
            Toast.makeText(getContext(), "Nenhum evento disponível", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayAdapter<Evento> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, eventos);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = new Spinner(requireContext());
        spinner.setAdapter(spinnerAdapter);

        new AlertDialog.Builder(requireContext())
                .setTitle("Gerar ingresso para " + participante.getNome())
                .setMessage("Selecione o evento:")
                .setView(spinner)
                .setPositiveButton("Gerar", (dialog, which) -> {
                    Evento eventoSelecionado = (Evento) spinner.getSelectedItem();
                    if (eventoSelecionado != null) {
                        gerarIngresso(participante, eventoSelecionado);
                    }
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
                    requireActivity().runOnUiThread(() -> {
                        Intent intent = new Intent(requireContext(), QrCodeActivity.class);
                        intent.putExtra("token", ingresso.getToken());
                        intent.putExtra("nomeParticipante", participante.getNome());
                        startActivity(intent);
                    });
                } else {
                    showError("Erro ao gerar ingresso");
                }
            }
            @Override public void onFailure(@NonNull Call<Ingresso> c, @NonNull Throwable t) {
                showError("Falha: " + t.getMessage());
            }
        });
    }

    private void carregarParticipantes() {
        ApiClient.get().listarParticipantes().enqueue(new Callback<List<Participante>>() {
            @Override
            public void onResponse(@NonNull Call<List<Participante>> call, @NonNull Response<List<Participante>> r) {
                if (r.isSuccessful() && r.body() != null)
                    requireActivity().runOnUiThread(() -> adapter.setItems(r.body()));
            }
            @Override public void onFailure(@NonNull Call<List<Participante>> c, @NonNull Throwable t) {}
        });
    }

    private void carregarEventos() {
        ApiClient.get().listarEventos().enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Evento>> call, @NonNull Response<List<Evento>> r) {
                if (r.isSuccessful() && r.body() != null) eventos = r.body();
            }
            @Override public void onFailure(@NonNull Call<List<Evento>> c, @NonNull Throwable t) {}
        });
    }

    private void showError(String msg) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show());
    }
}
