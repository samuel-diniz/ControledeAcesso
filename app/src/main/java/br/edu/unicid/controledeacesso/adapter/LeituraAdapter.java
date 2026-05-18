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

        int dotColor;
        int textColor;
        String label;
        switch (resultado) {
            case "VALIDO":
                dotColor  = Color.parseColor("#16A34A");
                textColor = Color.parseColor("#16A34A");
                label = "VÁLIDO";
                break;
            case "JA_USADO":
                dotColor  = Color.parseColor("#D97706");
                textColor = Color.parseColor("#D97706");
                label = "JÁ USADO";
                break;
            case "LOTADO":
                dotColor  = Color.parseColor("#1E40AF");
                textColor = Color.parseColor("#1E40AF");
                label = "LOTADO";
                break;
            case "NAO_ENTROU":
                dotColor  = Color.parseColor("#7C3AED");
                textColor = Color.parseColor("#7C3AED");
                label = "NÃO ENTROU";
                break;
            default:
                dotColor  = Color.parseColor("#DC2626");
                textColor = Color.parseColor("#DC2626");
                label = resultado;
                break;
        }

        h.statusDot.setBackgroundColor(dotColor);
        h.resultado.setText(label);
        h.resultado.setTextColor(textColor);

        String hora = l.getLidoEm() != null ? l.getLidoEm().replace("T", " ") : "";
        if (hora.length() > 16) hora = hora.substring(0, 16);
        h.hora.setText(hora);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        View statusDot;
        TextView participante, resultado, hora;
        VH(View v) {
            super(v);
            statusDot    = v.findViewById(R.id.view_status_dot);
            participante = v.findViewById(R.id.tv_leitura_participante);
            resultado    = v.findViewById(R.id.tv_leitura_resultado);
            hora         = v.findViewById(R.id.tv_leitura_hora);
        }
    }
}
