package br.edu.unicid.controledeacesso.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.R;
import br.edu.unicid.controledeacesso.model.ParticipantePresenca;
import java.util.ArrayList;
import java.util.List;

public class RelatorioAdapter extends RecyclerView.Adapter<RelatorioAdapter.VH> {

    private final List<ParticipantePresenca> items = new ArrayList<>();

    public void setItems(List<ParticipantePresenca> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_relatorio_participante, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ParticipantePresenca p = items.get(pos);

        h.tvNome.setText(p.getNome() != null ? p.getNome() : "—");
        h.tvEmail.setText(p.getEmail() != null ? p.getEmail() : "");

        String status = p.getStatus() != null ? p.getStatus() : "—";
        h.tvStatus.setText(status);
        switch (status) {
            case "DENTRO":  h.tvStatus.setBackgroundColor(Color.parseColor("#2E7D32")); break;
            case "SAIU":    h.tvStatus.setBackgroundColor(Color.parseColor("#F57F17")); break;
            default:        h.tvStatus.setBackgroundColor(Color.parseColor("#9E9E9E")); break;
        }

        // Horários de entrada e saída
        boolean temHorario = p.getEntradaEm() != null || p.getSaidaEm() != null;
        h.layoutHorarios.setVisibility(temHorario ? View.VISIBLE : View.GONE);
        if (p.getEntradaEm() != null)
            h.tvEntrada.setText("▶ Entrada: " + formatarHorario(p.getEntradaEm()));
        else
            h.tvEntrada.setText("");
        if (p.getSaidaEm() != null)
            h.tvSaida.setText("◀ Saída: " + formatarHorario(p.getSaidaEm()));
        else
            h.tvSaida.setText("");
    }

    private String formatarHorario(String iso) {
        // "2025-06-01T14:30:00" → "14:30"
        try {
            String[] partes = iso.split("T");
            return partes.length > 1 ? partes[1].substring(0, 5) : iso;
        } catch (Exception e) { return iso; }
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvNome, tvEmail, tvStatus, tvEntrada, tvSaida;
        LinearLayout layoutHorarios;
        VH(View v) {
            super(v);
            tvNome         = v.findViewById(R.id.tv_rel_nome);
            tvEmail        = v.findViewById(R.id.tv_rel_email);
            tvStatus       = v.findViewById(R.id.tv_rel_status);
            tvEntrada      = v.findViewById(R.id.tv_rel_entrada);
            tvSaida        = v.findViewById(R.id.tv_rel_saida);
            layoutHorarios = v.findViewById(R.id.layout_horarios);
        }
    }
}
