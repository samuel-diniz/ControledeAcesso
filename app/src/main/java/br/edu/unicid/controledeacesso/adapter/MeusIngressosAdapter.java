package br.edu.unicid.controledeacesso.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.R;
import br.edu.unicid.controledeacesso.model.Ingresso;
import java.util.ArrayList;
import java.util.List;

public class MeusIngressosAdapter extends RecyclerView.Adapter<MeusIngressosAdapter.VH> {

    public interface OnVerQrCodeListener {
        void onVerQrCode(Ingresso ingresso);
    }

    private final List<Ingresso> items = new ArrayList<>();
    private OnVerQrCodeListener listener;

    public void setItems(List<Ingresso> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnVerQrCodeListener(OnVerQrCodeListener l) {
        this.listener = l;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meu_ingresso, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Ingresso ingresso = items.get(position);

        String nomeEvento = "";
        String localEvento = "";
        if (ingresso.getEvento() != null) {
            nomeEvento = ingresso.getEvento().getNome() != null ? ingresso.getEvento().getNome() : "";
            localEvento = ingresso.getEvento().getLocal() != null ? ingresso.getEvento().getLocal() : "";
        }
        holder.tvNome.setText(nomeEvento);
        holder.tvLocal.setText(localEvento.isEmpty() ? "Local não informado" : localEvento);

        String status = ingresso.getStatus() != null ? ingresso.getStatus() : "";
        String statusLabel;
        int statusColor;
        switch (status) {
            case "PENDENTE":
                statusLabel = "Aguardando entrada";
                statusColor = 0xFF1565C0;
                break;
            case "DENTRO":
                statusLabel = "Voce esta dentro!";
                statusColor = 0xFF2E7D32;
                break;
            case "SAIU":
                statusLabel = "Saida registrada";
                statusColor = 0xFF757575;
                break;
            default:
                statusLabel = status.isEmpty() ? "PENDENTE" : status;
                statusColor = 0xFF757575;
                break;
        }
        holder.tvStatusBadge.setText(statusLabel);
        holder.tvStatusBadge.setBackgroundColor(statusColor);

        holder.btnVerQrCode.setOnClickListener(v -> {
            if (listener != null) listener.onVerQrCode(ingresso);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvNome, tvLocal, tvStatusBadge;
        Button btnVerQrCode;

        VH(View itemView) {
            super(itemView);
            tvNome        = itemView.findViewById(R.id.tv_evento_nome);
            tvLocal       = itemView.findViewById(R.id.tv_evento_local);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            btnVerQrCode  = itemView.findViewById(R.id.btn_ver_qrcode);
        }
    }
}
