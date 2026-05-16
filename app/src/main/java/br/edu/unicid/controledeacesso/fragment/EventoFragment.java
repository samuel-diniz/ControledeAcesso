package br.edu.unicid.controledeacesso.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

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

        v.findViewById(R.id.btn_data).setOnClickListener(x -> abrirDatePicker());
        v.findViewById(R.id.btn_hora).setOnClickListener(x -> abrirTimePicker());

        RecyclerView rv = v.findViewById(R.id.rv_eventos);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventoAdapter();
        adapter.setOnEditarListener(this::mostrarDialogEdicao);
        adapter.setOnDeletarListener(this::confirmarDelecao);
        rv.setAdapter(adapter);

        v.findViewById(R.id.btn_criar_evento).setOnClickListener(x -> criarEvento());

        carregarEventos();
        return v;
    }

    private void abrirDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (view, year, month, day) -> {
                    selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                    atualizarTextoDataHora();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void abrirTimePicker() {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(requireContext(),
                (view, hour, minute) -> {
                    selectedTime = String.format("%02d:%02d:00", hour, minute);
                    atualizarTextoDataHora();
                },
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
        ).show();
    }

    private void atualizarTextoDataHora() {
        if (selectedDate != null && selectedTime != null)
            tvDataHoraSelecionada.setText(selectedDate + " às " + selectedTime.substring(0, 5));
        else if (selectedDate != null)
            tvDataHoraSelecionada.setText(selectedDate + " — hora não selecionada");
        else if (selectedTime != null)
            tvDataHoraSelecionada.setText("Data não selecionada — " + selectedTime.substring(0, 5));
        else
            tvDataHoraSelecionada.setText("Nenhuma data/hora selecionada");
    }

    private void criarEvento() {
        String nome = etNome.getText().toString().trim();
        String cap  = etCapacidade.getText().toString().trim();
        if (nome.isEmpty()) { toast("Nome do evento é obrigatório"); return; }
        if (cap.isEmpty())  { toast("Capacidade é obrigatória"); return; }
        if (selectedDate == null) { toast("Selecione a data do evento"); return; }
        if (selectedTime == null) { toast("Selecione o horário do evento"); return; }

        int capacidade;
        try {
            capacidade = Integer.parseInt(cap);
            if (capacidade <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            toast("Capacidade deve ser um número maior que zero"); return;
        }

        Evento evento = new Evento();
        evento.setNome(nome);
        evento.setDescricao(etDescricao.getText().toString().trim());
        evento.setLocal(etLocal.getText().toString().trim());
        evento.setCapacidade(capacidade);
        evento.setData(selectedDate + "T" + selectedTime);

        ApiClient.get().criarEvento(evento).enqueue(new Callback<Evento>() {
            @Override
            public void onResponse(@NonNull Call<Evento> call, @NonNull Response<Evento> r) {
                if (r.isSuccessful()) {
                    runUI(() -> { toast("Evento criado com sucesso!"); limpar(); carregarEventos(); });
                } else {
                    String err = "";
                    try { if (r.errorBody() != null) err = r.errorBody().string(); } catch (IOException ignored) {}
                    showError("Erro ao criar evento" + (err.isEmpty() ? " (" + r.code() + ")" : ": " + err));
                }
            }
            @Override public void onFailure(@NonNull Call<Evento> c, @NonNull Throwable t) {
                showError("Falha de conexão: " + t.getMessage());
            }
        });
    }

    private void mostrarDialogEdicao(Evento evento) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(android.R.layout.activity_list_item, null);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 0);

        EditText etN = new EditText(requireContext()); etN.setHint("Nome *"); etN.setText(evento.getNome());
        EditText etL = new EditText(requireContext()); etL.setHint("Local"); etL.setText(evento.getLocal());
        EditText etC = new EditText(requireContext()); etC.setHint("Capacidade *");
        etC.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etC.setText(String.valueOf(evento.getCapacidade()));

        layout.addView(etN); layout.addView(etL); layout.addView(etC);

        new AlertDialog.Builder(requireContext())
                .setTitle("✏ Editar Evento")
                .setView(layout)
                .setPositiveButton("Salvar", (d, w) -> {
                    String nome = etN.getText().toString().trim();
                    String cap  = etC.getText().toString().trim();
                    if (nome.isEmpty() || cap.isEmpty()) { toast("Nome e capacidade são obrigatórios"); return; }
                    Evento atualizado = new Evento();
                    atualizado.setNome(nome);
                    atualizado.setLocal(etL.getText().toString().trim());
                    atualizado.setCapacidade(Integer.parseInt(cap));
                    atualizado.setDescricao(evento.getDescricao());
                    atualizado.setData(evento.getData());
                    ApiClient.get().atualizarEvento(evento.getId(), atualizado).enqueue(new Callback<Evento>() {
                        @Override
                        public void onResponse(@NonNull Call<Evento> call, @NonNull Response<Evento> r) {
                            if (r.isSuccessful()) runUI(() -> { toast("Evento atualizado!"); carregarEventos(); });
                            else showError("Erro ao atualizar evento");
                        }
                        @Override public void onFailure(@NonNull Call<Evento> c, @NonNull Throwable t) {
                            showError("Falha: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarDelecao(Evento evento) {
        new AlertDialog.Builder(requireContext())
                .setTitle("🗑 Excluir Evento")
                .setMessage("Deseja excluir \"" + evento.getNome() + "\"?\nEsta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (d, w) ->
                        ApiClient.get().deletarEvento(evento.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> r) {
                                if (r.isSuccessful()) runUI(() -> { toast("Evento excluído!"); carregarEventos(); });
                                else showError("Erro ao excluir evento");
                            }
                            @Override public void onFailure(@NonNull Call<Void> c, @NonNull Throwable t) {
                                showError("Falha: " + t.getMessage());
                            }
                        }))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void carregarEventos() {
        ApiClient.get().listarEventos().enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Evento>> call, @NonNull Response<List<Evento>> r) {
                if (r.isSuccessful() && r.body() != null) runUI(() -> adapter.setItems(r.body()));
            }
            @Override public void onFailure(@NonNull Call<List<Evento>> c, @NonNull Throwable t) {}
        });
    }

    private void limpar() {
        etNome.setText(""); etDescricao.setText(""); etLocal.setText(""); etCapacidade.setText("");
        selectedDate = null; selectedTime = null; atualizarTextoDataHora();
    }

    private void toast(String msg) { Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show(); }
    private void showError(String msg) { runUI(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show()); }
    private void runUI(Runnable r) { if (getActivity() != null) requireActivity().runOnUiThread(r); }
}
