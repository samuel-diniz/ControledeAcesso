package br.edu.unicid.controledeacesso.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.R;
import br.edu.unicid.controledeacesso.model.Solicitacao;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoAdapter extends RecyclerView.Adapter<SolicitacaoAdapter.VH> {

    private final List<Solicitacao> items = new ArrayList<>();

    public void setItems(List<Solicitacao> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitacao, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Solicitacao s = items.get(pos);

        String nomeEvento = (s.getEvento() != null && s.getEvento().getNome() != null)
                ? s.getEvento().getNome() : "—";
        h.tvEvento.setText(nomeEvento);

        String data = s.getSolicitadoEm() != null ? formatarData(s.getSolicitadoEm()) : "";
        h.tvData.setText("Solicitado em: " + data);

        String status = s.getStatus() != null ? s.getStatus() : "—";
        h.tvStatus.setText(status);

        // Cor do badge por status
        switch (status) {
            case "APROVADO":
                h.tvStatus.setBackgroundColor(Color.parseColor("#2E7D32")); break;
            case "REJEITADO":
                h.tvStatus.setBackgroundColor(Color.parseColor("#C62828")); break;
            default: // PENDENTE
                h.tvStatus.setBackgroundColor(Color.parseColor("#F57F17")); break;
        }
    }

    @Override public int getItemCount() { return items.size(); }

    private String formatarData(String raw) {
        try {
            String[] partes = raw.split("T");
            String[] ymd = partes[0].split("-");
            String data = ymd[2] + "/" + ymd[1] + "/" + ymd[0];
            String hora = partes.length > 1 ? partes[1].substring(0, 5) : "";
            return data + " " + hora;
        } catch (Exception e) {
            return raw;
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvEvento, tvData, tvStatus;

        VH(View v) {
            super(v);
            tvEvento = v.findViewById(R.id.tv_sol_evento_nome);
            tvData   = v.findViewById(R.id.tv_sol_solicitado_em);
            tvStatus = v.findViewById(R.id.tv_sol_status);
        }
    }
}
