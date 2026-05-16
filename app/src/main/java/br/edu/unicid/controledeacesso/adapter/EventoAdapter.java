package br.edu.unicid.controledeacesso.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.R;
import br.edu.unicid.controledeacesso.model.Evento;
import java.util.ArrayList;
import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.VH> {

    public interface OnEventoClick   { void onClick(Evento evento); }
    public interface OnEditarEvento  { void onEditar(Evento evento); }
    public interface OnDeletarEvento { void onDeletar(Evento evento); }

    private final List<Evento> items = new ArrayList<>();
    private OnEventoClick   clickListener;
    private OnEditarEvento  editarListener;
    private OnDeletarEvento deletarListener;

    public void setListener(OnEventoClick l)        { this.clickListener   = l; }
    public void setOnEditarListener(OnEditarEvento l)  { this.editarListener  = l; }
    public void setOnDeletarListener(OnDeletarEvento l){ this.deletarListener = l; }

    public void setItems(List<Evento> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Evento e = items.get(pos);
        h.nome.setText(e.getNome());
        h.local.setText(e.getLocal() != null && !e.getLocal().isEmpty() ? e.getLocal() : "Local não informado");
        h.capacidade.setText("Capacidade: " + e.getCapacidade());
        h.itemView.setOnClickListener(v -> { if (clickListener != null) clickListener.onClick(e); });
        h.btnEditar.setOnClickListener(v -> { if (editarListener != null) editarListener.onEditar(e); });
        h.btnDeletar.setOnClickListener(v -> { if (deletarListener != null) deletarListener.onDeletar(e); });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView nome, local, capacidade;
        Button btnEditar, btnDeletar;

        VH(View v) {
            super(v);
            nome       = v.findViewById(R.id.tv_evento_nome);
            local      = v.findViewById(R.id.tv_evento_local);
            capacidade = v.findViewById(R.id.tv_evento_capacidade);
            btnEditar  = v.findViewById(R.id.btn_editar_evento);
            btnDeletar = v.findViewById(R.id.btn_deletar_evento);
        }
    }
}
