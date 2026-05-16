package br.edu.unicid.controledeacesso.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.R;
import br.edu.unicid.controledeacesso.model.Solicitacao;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoAdminAdapter extends RecyclerView.Adapter<SolicitacaoAdminAdapter.VH> {

    public interface OnAprovar  { void onAprovar(Solicitacao s); }
    public interface OnRejeitar { void onRejeitar(Solicitacao s); }

    private final List<Solicitacao> items = new ArrayList<>();
    private OnAprovar  aprovarListener;
    private OnRejeitar rejeitarListener;

    public void setOnAprovarListener(OnAprovar l)   { this.aprovarListener  = l; }
    public void setOnRejeitarListener(OnRejeitar l) { this.rejeitarListener = l; }

    public void setItems(List<Solicitacao> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitacao_admin, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Solicitacao s = items.get(pos);

        // Participante
        String nomeP = (s.getParticipante() != null && s.getParticipante().getNome() != null)
                ? s.getParticipante().getNome() : "—";
        String emailP = (s.getParticipante() != null && s.getParticipante().getEmail() != null)
                ? s.getParticipante().getEmail() : "";
        h.tvParticipante.setText(nomeP);
        h.tvEmail.setText(emailP);

        // Evento
        String nomeE = (s.getEvento() != null && s.getEvento().getNome() != null)
                ? "🎟 " + s.getEvento().getNome() : "—";
        h.tvEvento.setText(nomeE);

        // Data
        String data = s.getSolicitadoEm() != null
                ? formatarData(s.getSolicitadoEm()) : "";
        h.tvData.setText("Solicitado em: " + data);

        // Botões
        h.btnAprovar.setOnClickListener(v -> { if (aprovarListener != null) aprovarListener.onAprovar(s); });
        h.btnRejeitar.setOnClickListener(v -> { if (rejeitarListener != null) rejeitarListener.onRejeitar(s); });
    }

    @Override public int getItemCount() { return items.size(); }

    /** Exibe só a parte amigável do LocalDateTime (ex: "2025-06-01T14:30:00" → "01/06/2025 14:30") */
    private String formatarData(String raw) {
        try {
            // formato ISO: YYYY-MM-DDTHH:mm:ss
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
        TextView tvParticipante, tvEmail, tvEvento, tvData;
        Button btnAprovar, btnRejeitar;

        VH(View v) {
            super(v);
            tvParticipante = v.findViewById(R.id.tv_sol_participante);
            tvEmail        = v.findViewById(R.id.tv_sol_email);
            tvEvento       = v.findViewById(R.id.tv_sol_evento);
            tvData         = v.findViewById(R.id.tv_sol_data);
            btnAprovar     = v.findViewById(R.id.btn_aprovar);
            btnRejeitar    = v.findViewById(R.id.btn_rejeitar);
        }
    }
}
