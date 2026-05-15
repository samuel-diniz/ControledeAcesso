package br.edu.unicid.controledeacesso;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.unicid.controledeacesso.adapter.MeusIngressosAdapter;
import br.edu.unicid.controledeacesso.api.ApiClient;
import br.edu.unicid.controledeacesso.model.Ingresso;
import br.edu.unicid.controledeacesso.model.Participante;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class ParticipanteLoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private RecyclerView rvMeusIngressos;
    private TextView tvSemIngressos;
    private MeusIngressosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participante_login);

        etEmail         = findViewById(R.id.et_email);
        rvMeusIngressos = findViewById(R.id.rv_meus_ingressos);
        tvSemIngressos  = findViewById(R.id.tv_sem_ingressos);
        Button btnBuscar = findViewById(R.id.btn_buscar);
        ImageButton btnVoltar = findViewById(R.id.btn_voltar);

        btnVoltar.setOnClickListener(v -> finish());

        rvMeusIngressos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MeusIngressosAdapter();
        rvMeusIngressos.setAdapter(adapter);

        adapter.setOnVerQrCodeListener(ingresso -> {
            String token = ingresso.getToken() != null ? ingresso.getToken() : "";
            String nome  = (ingresso.getParticipante() != null && ingresso.getParticipante().getNome() != null)
                    ? ingresso.getParticipante().getNome() : "";
            Intent intent = new Intent(this, QrCodeActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("nomeParticipante", nome);
            startActivity(intent);
        });

        btnBuscar.setOnClickListener(v -> buscarIngressos());
    }

    private void buscarIngressos() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Digite seu e-mail", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.get().buscarParticipantePorEmail(email).enqueue(new Callback<Participante>() {
            @Override
            public void onResponse(@NonNull Call<Participante> call, @NonNull Response<Participante> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long participanteId = response.body().getId();
                    if (participanteId != null) {
                        carregarIngressos(participanteId);
                    } else {
                        mostrarSemIngressos();
                    }
                } else {
                    runOnUiThread(() -> {
                        mostrarSemIngressos();
                        Toast.makeText(ParticipanteLoginActivity.this,
                                "Participante não encontrado para este e-mail", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<Participante> call, @NonNull Throwable t) {
                runOnUiThread(() ->
                        Toast.makeText(ParticipanteLoginActivity.this,
                                "Falha de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void carregarIngressos(long participanteId) {
        ApiClient.get().listarIngressosPorParticipante(participanteId).enqueue(new Callback<List<Ingresso>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ingresso>> call, @NonNull Response<List<Ingresso>> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        adapter.setItems(response.body());
                        rvMeusIngressos.setVisibility(View.VISIBLE);
                        tvSemIngressos.setVisibility(View.GONE);
                    } else {
                        mostrarSemIngressos();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<Ingresso>> call, @NonNull Throwable t) {
                runOnUiThread(() ->
                        Toast.makeText(ParticipanteLoginActivity.this,
                                "Falha de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void mostrarSemIngressos() {
        rvMeusIngressos.setVisibility(View.GONE);
        tvSemIngressos.setVisibility(View.VISIBLE);
        adapter.setItems(null);
    }
}
