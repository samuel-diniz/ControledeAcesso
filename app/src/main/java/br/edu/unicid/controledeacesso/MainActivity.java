package br.edu.unicid.controledeacesso;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.unicid.controledeacesso.api.ApiClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa o ApiClient com o IP salvo (ou o padrão)
        ApiClient.get(this);

        findViewById(R.id.card_organizador).setOnClickListener(v ->
                startActivity(new Intent(this, AdminActivity.class)));

        findViewById(R.id.card_participante).setOnClickListener(v ->
                startActivity(new Intent(this, ParticipanteLoginActivity.class)));

        findViewById(R.id.btn_scanner).setOnClickListener(v ->
                startActivity(new Intent(this, ScannerActivity.class)));

        // Botão de configuração do IP
        TextView btnConfigIp = findViewById(R.id.btn_config_ip);
        btnConfigIp.setText("⚙ Servidor: " + ApiClient.getBaseUrl(this));
        btnConfigIp.setOnClickListener(v -> mostrarDialogConfigIp(btnConfigIp));
    }

    private void mostrarDialogConfigIp(TextView btnConfigIp) {
        EditText etIp = new EditText(this);
        etIp.setHint("Ex: http://192.168.15.11:8080/");
        etIp.setText(ApiClient.getBaseUrl(this));

        new AlertDialog.Builder(this)
                .setTitle("⚙ Configurar IP do servidor")
                .setMessage("Digite o IP do notebook na rede Wi-Fi.\n\nVeja o IP rodando ipconfig no notebook.")
                .setView(etIp)
                .setPositiveButton("Salvar", (d, w) -> {
                    String novoIp = etIp.getText().toString().trim();
                    if (novoIp.isEmpty() || !novoIp.startsWith("http")) {
                        Toast.makeText(this, "URL inválida. Ex: http://192.168.X.X:8080/", Toast.LENGTH_LONG).show();
                        return;
                    }
                    ApiClient.setBaseUrl(this, novoIp);
                    btnConfigIp.setText("⚙ Servidor: " + ApiClient.getBaseUrl(this));
                    Toast.makeText(this, "IP salvo! Conexão atualizada.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
