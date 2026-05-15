package br.edu.unicid.controledeacesso.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.List;
import java.io.IOException;

public class EventoFragment extends Fragment {

    private EditText etNome, etDescricao, etLocal, etCapacidade;
    private TextView tvDataHoraSelecionada;
    private EventoAdapter adapter;

    private String selectedDate = null;
    private String selectedTime = null;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_evento, container, false);

        etNome       = v.findViewById(R.id.et_evento_nome);
        etDescricao  = v.findViewById(R.id.et_evento_descricao);
        etLocal      = v.findViewById(R.id.et_evento_local);
        etCapacidade = v.findViewById(R.id.et_evento_capacidade);
        tvDataHoraSelecionada = v.findViewById(R.id.tv_data_hora_selecionada);

        Button btnData = v.findViewById(R.id.btn_data);
        Button btnHora = v.findViewById(R.id.btn_hora);

        btnData.setOnClickListener(x -> abrirDatePicker());
        btnHora.setOnClickListener(x -> abrirTimePicker());

        RecyclerView rv = v.findViewById(R.id.rv_eventos);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventoAdapter();
        rv.setAdapter(adapter);

        Button btnCriar = v.findViewById(R.id.btn_criar_evento);
        btnCriar.setOnClickListener(x -> criarEvento());

        carregarEventos();
        return v;
    }

    private void abrirDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    atualizarTextoDataHora();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void abrirTimePicker() {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedTime = String.format("%02d:%02d:00", hourOfDay, minute);
                    atualizarTextoDataHora();
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
        ).show();
    }

    private void atualizarTextoDataHora() {
        if (selectedDate != null && selectedTime != null) {
            tvDataHoraSelecionada.setText(selectedDate + " " + selectedTime.substring(0, 5));
        } else if (selectedDate != null) {
            tvDataHoraSelecionada.setText(selectedDate + " — hora não selecionada");
        } else if (selectedTime != null) {
            tvDataHoraSelecionada.setText("Data não selecionada — " + selectedTime.substring(0, 5));
        } else {
            tvDataHoraSelecionada.setText("Nenhuma data/hora selecionada (opcional)");
        }
    }

    private void criarEvento() {
        String nome = etNome.getText().toString().trim();
        String cap  = etCapacidade.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(getContext(), "Nome do evento é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cap.isEmpty()) {
            Toast.makeText(getContext(), "Capacidade é obrigatória", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacidade;
        try {
            capacidade = Integer.parseInt(cap);
            if (capacidade <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Capacidade deve ser um número maior que zero", Toast.LENGTH_SHORT).show();
            return;
        }

        Evento evento = new Evento();
        evento.setNome(nome);
        evento.setDescricao(etDescricao.getText().toString().trim());
        evento.setLocal(etLocal.getText().toString().trim());
        evento.setCapacidade(capacidade);

        // data é opcional — envia só se ambas selecionadas
        if (selectedDate != null && selectedTime != null) {
            evento.setData(selectedDate + "T" + selectedTime);
        }

        ApiClient.get().criarEvento(evento).enqueue(new Callback<Evento>() {
            @Override
            public void onResponse(@NonNull Call<Evento> call, @NonNull Response<Evento> response) {
                if (response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Evento criado com sucesso!", Toast.LENGTH_SHORT).show();
                        limparFormulario();
                        carregarEventos();
                    });
                } else {
                    String erroBody = "";
                    try {
                        if (response.errorBody() != null) erroBody = response.errorBody().string();
                    } catch (IOException ignored) {}
                    String msg = erroBody.isEmpty()
                            ? "Erro ao criar evento (código " + response.code() + ")"
                            : "Erro " + response.code() + ": " + erroBody;
                    showError(msg);
                }
            }
            @Override public void onFailure(@NonNull Call<Evento> call, @NonNull Throwable t) {
                showError("Falha de conexão com o servidor.\nVerifique se o backend está rodando.\n" + t.getMessage());
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
        etNome.setText(""); etDescricao.setText("");
        etLocal.setText(""); etCapacidade.setText("");
        selectedDate = null; selectedTime = null;
        atualizarTextoDataHora();
    }

    private void showError(String msg) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show());
    }
}
