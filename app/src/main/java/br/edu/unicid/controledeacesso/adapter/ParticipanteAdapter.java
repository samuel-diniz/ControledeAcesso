package br.edu.unicid.controledeacesso.adapter;

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
import java.util.List;

public class ParticipanteAdapter extends RecyclerView.Adapter<ParticipanteAdapter.VH> {

    public interface OnGerarIngresso      { void onGerar(Participante participante); }
    public interface OnEditarParticipante { void onEditar(Participante participante); }
    public interface OnDeletarParticipante{ void onDeletar(Participante participante); }

    private final List<Participante> items = new ArrayList<>();
    private OnGerarIngresso       gerarListener;
    private OnEditarParticipante  editarListener;
    private OnDeletarParticipante deletarListener;

    public void setListener(OnGerarIngresso l)          { this.gerarListener   = l; }
    public void setOnEditarListener(OnEditarParticipante l) { this.editarListener  = l; }
    public void setOnDeletarListener(OnDeletarParticipante l){ this.deletarListener = l; }

    public void setItems(List<Participante> list) {
        items.clear();
        if (list != null) items.addAll(list);
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
        h.nome.setText(p.getNome());
        h.email.setText(p.getEmail());
        h.btnGerar.setOnClickListener(v -> { if (gerarListener != null) gerarListener.onGerar(p); });
        h.btnEditar.setOnClickListener(v -> { if (editarListener != null) editarListener.onEditar(p); });
        h.btnDeletar.setOnClickListener(v -> { if (deletarListener != null) deletarListener.onDeletar(p); });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView nome, email;
        Button btnGerar, btnEditar, btnDeletar;

        VH(View v) {
            super(v);
            nome      = v.findViewById(R.id.tv_participante_nome);
            email     = v.findViewById(R.id.tv_participante_email);
            btnGerar  = v.findViewById(R.id.btn_gerar_ingresso);
            btnEditar = v.findViewById(R.id.btn_editar_participante);
            btnDeletar= v.findViewById(R.id.btn_deletar_participante);
        }
    }
}
