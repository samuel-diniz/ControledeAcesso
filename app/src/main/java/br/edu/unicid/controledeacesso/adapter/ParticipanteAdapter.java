package br.edu.unicid.controledeacesso.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.R;
import br.edu.unicid.controledeacesso.model.Participante;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipanteAdapter extends RecyclerView.Adapter<ParticipanteAdapter.VH> {

    public interface OnGerarIngresso      { void onGerar(Participante participante); }
    public interface OnEditarParticipante { void onEditar(Participante participante); }
    public interface OnDeletarParticipante{ void onDeletar(Participante participante); }

    private final List<Participante> items = new ArrayList<>();
    private Map<Long, String> statusMap = new HashMap<>();

    private OnGerarIngresso       gerarListener;
    private OnEditarParticipante  editarListener;
    private OnDeletarParticipante deletarListener;

    public void setListener(OnGerarIngresso l)               { this.gerarListener   = l; }
    public void setOnEditarListener(OnEditarParticipante l)  { this.editarListener  = l; }
    public void setOnDeletarListener(OnDeletarParticipante l){ this.deletarListener = l; }

    public void setItems(List<Participante> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    /** Update status badges — pass participanteId → status map from the event's ingressos */
    public void setStatusMap(Map<Long, String> map) {
        this.statusMap = map != null ? map : new HashMap<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participante, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Participante p = items.get(pos);

        String nome = p.getNome() != null ? p.getNome() : "?";
        h.avatar.setText(String.valueOf(nome.charAt(0)).toUpperCase());
        h.nome.setText(nome);
        h.email.setText(p.getEmail() != null ? p.getEmail() : "");

        // Show status badge only when an event filter is active
        if (!statusMap.isEmpty()) {
            h.statusBadge.setVisibility(View.VISIBLE);
            if (p.getId() != null && statusMap.containsKey(p.getId())) {
                applyStatus(h.statusBadge, statusMap.get(p.getId()));
            } else {
                h.statusBadge.setText("SEM INGRESSO");
                h.statusBadge.setBackgroundColor(Color.parseColor("#94A3B8"));
                h.statusBadge.setTextColor(Color.WHITE);
            }
        } else {
            h.statusBadge.setVisibility(View.GONE);
        }

        h.btnGerar.setOnClickListener(v  -> { if (gerarListener   != null) gerarListener.onGerar(p);    });
        h.btnEditar.setOnClickListener(v -> { if (editarListener  != null) editarListener.onEditar(p);  });
        h.btnDeletar.setOnClickListener(v-> { if (deletarListener != null) deletarListener.onDeletar(p);});
    }

    private void applyStatus(TextView badge, String status) {
        if (status == null) status = "—";
        switch (status) {
            case "DENTRO":
                badge.setText("● DENTRO");
                badge.setBackgroundColor(Color.parseColor("#16A34A"));
                badge.setTextColor(Color.WHITE);
                break;
            case "SAIU":
                badge.setText("✔ SAIU");
                badge.setBackgroundColor(Color.parseColor("#475569"));
                badge.setTextColor(Color.WHITE);
                break;
            case "PENDENTE":
                badge.setText("⏳ PENDENTE");
                badge.setBackgroundColor(Color.parseColor("#D97706"));
                badge.setTextColor(Color.WHITE);
                break;
            default:
                badge.setText(status);
                badge.setBackgroundColor(Color.parseColor("#94A3B8"));
                badge.setTextColor(Color.WHITE);
                break;
        }
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView avatar, nome, email, statusBadge;
        Button btnGerar, btnEditar, btnDeletar;

        VH(View v) {
            super(v);
            avatar      = v.findViewById(R.id.tv_avatar);
            nome        = v.findViewById(R.id.tv_participante_nome);
            email       = v.findViewById(R.id.tv_participante_email);
            statusBadge = v.findViewById(R.id.tv_status_ingresso);
            btnGerar    = v.findViewById(R.id.btn_gerar_ingresso);
            btnEditar   = v.findViewById(R.id.btn_editar_participante);
            btnDeletar  = v.findViewById(R.id.btn_deletar_participante);
        }
    }
}
