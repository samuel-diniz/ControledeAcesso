package br.edu.unicid.controledeacesso.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.R;
import br.edu.unicid.controledeacesso.adapter.LeituraAdapter;
import br.edu.unicid.controledeacesso.api.ApiClient;
import br.edu.unicid.controledeacesso.model.Evento;
import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.model.Leitura;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private Spinner spinnerEvento;
    private TextView tvCapacidade, tvCheckins, tvRestantes;
    private LeituraAdapter leituraAdapter;
    private List<Evento> eventos = new ArrayList<>();
    private Evento eventoSelecionado;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable pollingTask = this::atualizarDashboard;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        spinnerEvento = v.findViewById(R.id.spinner_evento);
        tvCapacidade  = v.findViewById(R.id.tv_capacidade);
        tvCheckins    = v.findViewById(R.id.tv_checkins);
        tvRestantes   = v.findViewById(R.id.tv_restantes);

        RecyclerView rv = v.findViewById(R.id.rv_leituras);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        leituraAdapter = new LeituraAdapter();
        rv.setAdapter(leituraAdapter);

        spinnerEvento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View view, int pos, long id) {
                eventoSelecionado = eventos.get(pos);
                atualizarDashboard();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        carregarEventos();
        return v;
    }

    private void carregarEventos() {
        ApiClient.get().listarEventos().enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Evento>> call, @NonNull Response<List<Evento>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    eventos = r.body();
                    requireActivity().runOnUiThread(() -> {
                        ArrayAdapter<Evento> a = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_spinner_item, eventos);
                        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerEvento.setAdapter(a);
                    });
                }
            }
            @Override public void onFailure(@NonNull Call<List<Evento>> c, @NonNull Throwable t) {}
        });
    }

    private void atualizarDashboard() {
        if (eventoSelecionado == null) return;
        long eventoId = eventoSelecionado.getId();

        ApiClient.get().listarIngressosPorEvento(eventoId).enqueue(new Callback<List<Ingresso>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ingresso>> call, @NonNull Response<List<Ingresso>> r) {
                if (!r.isSuccessful() || r.body() == null) return;
                List<Ingresso> ingressos = r.body();
                long usados = 0;
                for (Ingresso i : ingressos) if ("USADO".equals(i.getStatus())) usados++;
                final long checkins = usados;
                final int capacidade = eventoSelecionado.getCapacidade();
                requireActivity().runOnUiThread(() -> {
                    tvCapacidade.setText(String.valueOf(capacidade));
                    tvCheckins.setText(String.valueOf(checkins));
                    tvRestantes.setText(String.valueOf(capacidade - checkins));
                });
            }
            @Override public void onFailure(@NonNull Call<List<Ingresso>> c, @NonNull Throwable t) {}
        });

        ApiClient.get().listarLeituras(eventoId).enqueue(new Callback<List<Leitura>>() {
            @Override
            public void onResponse(@NonNull Call<List<Leitura>> call, @NonNull Response<List<Leitura>> r) {
                if (r.isSuccessful() && r.body() != null)
                    requireActivity().runOnUiThread(() -> leituraAdapter.setItems(r.body()));
            }
            @Override public void onFailure(@NonNull Call<List<Leitura>> c, @NonNull Throwable t) {}
        });
    }

    @Override public void onResume() {
        super.onResume();
        // Polling a cada 5 segundos
        handler.post(new Runnable() {
            @Override public void run() {
                atualizarDashboard();
                handler.postDelayed(this, 5000);
            }
        });
    }

    @Override public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }
}
