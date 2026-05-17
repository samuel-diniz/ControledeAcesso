package br.edu.unicid.controledeacesso.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import br.edu.unicid.controledeacesso.R;
import br.edu.unicid.controledeacesso.adapter.SolicitacaoAdminAdapter;
import br.edu.unicid.controledeacesso.api.ApiClient;
import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.model.Solicitacao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class SolicitacoesFragment extends Fragment {

    private SolicitacaoAdminAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvSemSolicitacoes;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_solicitacoes, container, false);

        swipeRefresh      = v.findViewById(R.id.swipe_solicitacoes);
        tvSemSolicitacoes = v.findViewById(R.id.tv_sem_solicitacoes);
        RecyclerView rv   = v.findViewById(R.id.rv_solicitacoes);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SolicitacaoAdminAdapter();
        adapter.setOnAprovarListener(this::confirmarAprovacao);
        adapter.setOnRejeitarListener(this::confirmarRejeicao);
        rv.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::carregarSolicitacoes);
        swipeRefresh.setColorSchemeColors(0xFF1565C0);

        carregarSolicitacoes();
        return v;
    }

    private void carregarSolicitacoes() {
        swipeRefresh.setRefreshing(true);
        ApiClient.get().listarSolicitacoesPendentes().enqueue(new Callback<List<Solicitacao>>() {
            @Override
            public void onResponse(@NonNull Call<List<Solicitacao>> call,
                                   @NonNull Response<List<Solicitacao>> r) {
                runUI(() -> {
                    swipeRefresh.setRefreshing(false);
                    if (r.isSuccessful() && r.body() != null) {
                        List<Solicitacao> lista = r.body();
                        adapter.setItems(lista);
                        tvSemSolicitacoes.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
                    } else {
                        tvSemSolicitacoes.setVisibility(View.VISIBLE);
                    }
                });
            }
            @Override public void onFailure(@NonNull Call<List<Solicitacao>> c, @NonNull Throwable t) {
                runUI(() -> {
                    swipeRefresh.setRefreshing(false);
                    toast("Falha ao carregar solicitações");
                });
            }
        });
    }

    private void confirmarAprovacao(Solicitacao s) {
        String nome  = s.getParticipante() != null ? s.getParticipante().getNome() : "?";
        String evento = s.getEvento() != null ? s.getEvento().getNome() : "?";
        new AlertDialog.Builder(requireContext())
                .setTitle("✔ Aprovar Solicitação")
                .setMessage("Aprovar " + nome + " para o evento \"" + evento + "\"?\n\n"
                        + "Um ingresso será gerado automaticamente.")
                .setPositiveButton("Aprovar", (d, w) -> aprovar(s))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarRejeicao(Solicitacao s) {
        String nome  = s.getParticipante() != null ? s.getParticipante().getNome() : "?";
        String evento = s.getEvento() != null ? s.getEvento().getNome() : "?";
        new AlertDialog.Builder(requireContext())
                .setTitle("✖ Rejeitar Solicitação")
                .setMessage("Rejeitar a solicitação de " + nome + " para \"" + evento + "\"?")
                .setPositiveButton("Rejeitar", (d, w) -> rejeitar(s))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void aprovar(Solicitacao s) {
        ApiClient.get().aprovarSolicitacao(s.getId()).enqueue(new Callback<Ingresso>() {
            @Override
            public void onResponse(@NonNull Call<Ingresso> call, @NonNull Response<Ingresso> r) {
                if (r.isSuccessful()) {
                    runUI(() -> {
                        toast("✅ Solicitação aprovada! Ingresso gerado.");
                        carregarSolicitacoes();
                    });
                } else {
                    showError("Erro ao aprovar solicitação (" + r.code() + ")");
                }
            }
            @Override public void onFailure(@NonNull Call<Ingresso> c, @NonNull Throwable t) {
                showError("Falha: " + t.getMessage());
            }
        });
    }

    private void rejeitar(Solicitacao s) {
        ApiClient.get().rejeitarSolicitacao(s.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> r) {
                if (r.isSuccessful()) {
                    runUI(() -> {
                        toast("Solicitação rejeitada.");
                        carregarSolicitacoes();
                    });
                } else {
                    showError("Erro ao rejeitar solicitação (" + r.code() + ")");
                }
            }
            @Override public void onFailure(@NonNull Call<Void> c, @NonNull Throwable t) {
                showError("Falha: " + t.getMessage());
            }
        });
    }

    private void toast(String msg) { Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show(); }
    private void showError(String msg) { runUI(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show()); }
    private void runUI(Runnable r) { if (getActivity() != null) requireActivity().runOnUiThread(r); }
}
