package br.edu.unicid.controledeacesso.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.R;
import br.edu.unicid.controledeacesso.adapter.RelatorioAdapter;
import br.edu.unicid.controledeacesso.api.ApiClient;
import br.edu.unicid.controledeacesso.model.Evento;
import br.edu.unicid.controledeacesso.model.RelatorioEvento;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class RelatorioFragment extends Fragment {

    private Spinner spinnerEvento;
    private ProgressBar pbOcupacao;
    private TextView tvEventoNome, tvOcupacaoLabel;
    private TextView tvAprovados, tvDentro, tvSaiu, tvAusente;
    private TextView tvSemDados;
    private LinearLayout layoutStats, layoutLista;
    private RelatorioAdapter adapter;
    private List<Evento> eventos = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_relatorio, container, false);

        spinnerEvento    = v.findViewById(R.id.spinner_evento_relatorio);
        pbOcupacao       = v.findViewById(R.id.pb_ocupacao);
        tvEventoNome     = v.findViewById(R.id.tv_rel_evento_nome);
        tvOcupacaoLabel  = v.findViewById(R.id.tv_rel_ocupacao_label);
        tvAprovados      = v.findViewById(R.id.tv_total_aprovados);
        tvDentro         = v.findViewById(R.id.tv_total_dentro);
        tvSaiu           = v.findViewById(R.id.tv_total_saiu);
        tvAusente        = v.findViewById(R.id.tv_total_ausente);
        tvSemDados       = v.findViewById(R.id.tv_sem_dados_relatorio);
        layoutStats      = v.findViewById(R.id.layout_stats);
        layoutLista      = v.findViewById(R.id.layout_lista);

        RecyclerView rv = v.findViewById(R.id.rv_relatorio);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RelatorioAdapter();
        rv.setAdapter(adapter);

        v.findViewById(R.id.btn_gerar_relatorio).setOnClickListener(x -> gerarRelatorio());

        carregarEventos();
        return v;
    }

    private void carregarEventos() {
        ApiClient.get().listarEventos().enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Evento>> call, @NonNull Response<List<Evento>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    eventos = r.body();
                    runUI(() -> {
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

    private void gerarRelatorio() {
        Evento ev = (Evento) spinnerEvento.getSelectedItem();
        if (ev == null) { Toast.makeText(getContext(), "Selecione um evento", Toast.LENGTH_SHORT).show(); return; }

        ApiClient.get().getRelatorioEvento(ev.getId()).enqueue(new Callback<RelatorioEvento>() {
            @Override
            public void onResponse(@NonNull Call<RelatorioEvento> call, @NonNull Response<RelatorioEvento> r) {
                if (r.isSuccessful() && r.body() != null) {
                    runUI(() -> exibirRelatorio(r.body()));
                } else {
                    runUI(() -> Toast.makeText(getContext(), "Erro ao gerar relatório", Toast.LENGTH_SHORT).show());
                }
            }
            @Override public void onFailure(@NonNull Call<RelatorioEvento> c, @NonNull Throwable t) {
                runUI(() -> Toast.makeText(getContext(), "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void exibirRelatorio(RelatorioEvento rel) {
        tvSemDados.setVisibility(View.GONE);
        layoutStats.setVisibility(View.VISIBLE);
        layoutLista.setVisibility(View.VISIBLE);

        tvEventoNome.setText(rel.getEventoNome());
        int pct = (int) rel.getPercentualOcupacao();
        tvOcupacaoLabel.setText("Ocupação: " + pct + "%  (" + rel.getTotalDentro() + " dentro de " + rel.getCapacidade() + " vagas)");

        pbOcupacao.setProgress(pct);
        // Cor da barra por nível de ocupação
        int cor;
        if (pct >= 90)      cor = Color.parseColor("#C62828");  // vermelho
        else if (pct >= 60) cor = Color.parseColor("#F57F17");  // amarelo
        else                cor = Color.parseColor("#2E7D32");  // verde
        pbOcupacao.getProgressDrawable().setColorFilter(cor,
                android.graphics.PorterDuff.Mode.SRC_IN);

        tvAprovados.setText(String.valueOf(rel.getTotalAprovados()));
        tvDentro.setText(String.valueOf(rel.getTotalDentro()));
        tvSaiu.setText(String.valueOf(rel.getTotalSaiu()));
        tvAusente.setText(String.valueOf(rel.getTotalAusente()));

        adapter.setItems(rel.getParticipantes());
    }

    private void runUI(Runnable r) {
        if (getActivity() != null) requireActivity().runOnUiThread(r);
    }
}
