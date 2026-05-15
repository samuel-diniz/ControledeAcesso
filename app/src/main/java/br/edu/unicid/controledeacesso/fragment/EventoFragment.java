package br.edu.unicid.controledeacesso.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.R;
import br.edu.unicid.controledeacesso.adapter.EventoAdapter;
import br.edu.unicid.controledeacesso.api.ApiClient;
import br.edu.unicid.controledeacesso.model.Evento;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class EventoFragment extends Fragment {

    private EditText etNome, etDescricao, etData, etLocal, etCapacidade;
    private EventoAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_evento, container, false);

        etNome       = v.findViewById(R.id.et_evento_nome);
        etDescricao  = v.findViewById(R.id.et_evento_descricao);
        etData       = v.findViewById(R.id.et_evento_data);
        etLocal      = v.findViewById(R.id.et_evento_local);
        etCapacidade = v.findViewById(R.id.et_evento_capacidade);

        RecyclerView rv = v.findViewById(R.id.rv_eventos);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventoAdapter();
        rv.setAdapter(adapter);

        Button btnCriar = v.findViewById(R.id.btn_criar_evento);
        btnCriar.setOnClickListener(x -> criarEvento());

        carregarEventos();
        return v;
    }

    private void criarEvento() {
        String nome = etNome.getText().toString().trim();
        String cap  = etCapacidade.getText().toString().trim();
        String data = etData.getText().toString().trim();
        if (nome.isEmpty() || cap.isEmpty() || data.isEmpty()) {
            Toast.makeText(getContext(), "Nome, data e capacidade são obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        Evento evento = new Evento();
        evento.setNome(nome);
        evento.setDescricao(etDescricao.getText().toString().trim());
        evento.setData(data);
        evento.setLocal(etLocal.getText().toString().trim());
        evento.setCapacidade(Integer.parseInt(cap));

        ApiClient.get().criarEvento(evento).enqueue(new Callback<Evento>() {
            @Override
            public void onResponse(@NonNull Call<Evento> call, @NonNull Response<Evento> response) {
                if (response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Evento criado!", Toast.LENGTH_SHORT).show();
                        limparFormulario();
                        carregarEventos();
                    });
                } else {
                    showError("Erro ao criar evento: " + response.code());
                }
            }
            @Override public void onFailure(@NonNull Call<Evento> call, @NonNull Throwable t) {
                showError("Falha de conexão: " + t.getMessage());
            }
        });
    }

    private void carregarEventos() {
        ApiClient.get().listarEventos().enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Evento>> call, @NonNull Response<List<Evento>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    requireActivity().runOnUiThread(() -> adapter.setItems(r.body()));
                }
            }
            @Override public void onFailure(@NonNull Call<List<Evento>> call, @NonNull Throwable t) {}
        });
    }

    private void limparFormulario() {
        etNome.setText(""); etDescricao.setText(""); etData.setText("");
        etLocal.setText(""); etCapacidade.setText("");
    }

    private void showError(String msg) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show());
    }
}
