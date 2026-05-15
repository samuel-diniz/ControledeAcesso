package br.edu.unicid.controledeacesso.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.R;
import br.edu.unicid.controledeacesso.model.Leitura;
import java.util.ArrayList;
import java.util.List;

public class LeituraAdapter extends RecyclerView.Adapter<LeituraAdapter.VH> {

    private final List<Leitura> items = new ArrayList<>();

    public void setItems(List<Leitura> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leitura, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Leitura l = items.get(pos);

        String participanteNome = "—";
        if (l.getIngresso() != null && l.getIngresso().getParticipante() != null) {
            participanteNome = l.getIngresso().getParticipante().getNome();
        }
        h.participante.setText(participanteNome);

        String resultado = l.getResultado() != null ? l.getResultado() : "?";
        h.resultado.setText(resultado);

        switch (resultado) {
            case "VALIDO":   h.resultado.setTextColor(Color.parseColor("#2E7D32")); break;
            case "JA_USADO": h.resultado.setTextColor(Color.parseColor("#F57F17")); break;
            case "LOTADO":   h.resultado.setTextColor(Color.parseColor("#1565C0")); break;
            default:         h.resultado.setTextColor(Color.parseColor("#C62828")); break;
        }

        String hora = l.getLidoEm() != null ? l.getLidoEm().replace("T", " ") : "";
        if (hora.length() > 16) hora = hora.substring(0, 16);
        h.hora.setText(hora);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView participante, resultado, hora;
        VH(View v) {
            super(v);
            participante = v.findViewById(R.id.tv_leitura_participante);
            resultado    = v.findViewById(R.id.tv_leitura_resultado);
            hora         = v.findViewById(R.id.tv_leitura_hora);
        }
    }
}
