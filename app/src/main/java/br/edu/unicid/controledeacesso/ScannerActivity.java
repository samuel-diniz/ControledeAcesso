package br.edu.unicid.controledeacesso;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import br.edu.unicid.controledeacesso.api.ApiClient;
import br.edu.unicid.controledeacesso.model.CheckInRequest;
import br.edu.unicid.controledeacesso.model.CheckInResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION = 100;

    private DecoratedBarcodeView scannerView;
    private TextView tvResultado, tvMensagem, tvParticipante;
    private View cardResultado;
    private RadioGroup rgModo;
    private boolean escaneando = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        scannerView    = findViewById(R.id.barcode_scanner);
        tvResultado    = findViewById(R.id.tv_resultado);
        tvMensagem     = findViewById(R.id.tv_mensagem);
        tvParticipante = findViewById(R.id.tv_participante_nome);
        cardResultado  = findViewById(R.id.card_resultado);
        rgModo         = findViewById(R.id.rg_modo);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            iniciarScanner();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }
    }

    private String getModoSelecionado() {
        RadioButton rbEntrada = findViewById(R.id.rb_entrada);
        return rbEntrada.isChecked() ? "ENTRADA" : "SAIDA";
    }

    private void iniciarScanner() {
        scannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (!escaneando) return;
                escaneando = false;
                validarToken(result.getText());
            }
        });
    }

    private void validarToken(String token) {
        String modo = getModoSelecionado();
        CheckInRequest req = new CheckInRequest(token, "Scanner Android", modo);
        ApiClient.get().validarToken(req).enqueue(new Callback<CheckInResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckInResponse> call,
                                   @NonNull Response<CheckInResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mostrarResultado(response.body());
                } else {
                    mostrarErro("Erro na resposta do servidor");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckInResponse> call, @NonNull Throwable t) {
                mostrarErro("Falha de conexao: " + t.getMessage());
            }
        });
    }

    private void mostrarResultado(CheckInResponse resp) {
        runOnUiThread(() -> {
            cardResultado.setVisibility(View.VISIBLE);
            tvResultado.setText(resp.getResultado());
            tvMensagem.setText(resp.getMensagem());

            String nomeParticipante = "";
            if (resp.getParticipante() != null) {
                nomeParticipante = resp.getParticipante().getNome();
            }
            tvParticipante.setText(nomeParticipante);

            String resultado = resp.getResultado() != null ? resp.getResultado() : "";
            switch (resultado) {
                case "VALIDO":
                    cardResultado.setBackgroundColor(0xFF2E7D32); break;
                case "JA_USADO":
                    cardResultado.setBackgroundColor(0xFFF57F17); break;
                case "LOTADO":
                    cardResultado.setBackgroundColor(0xFF1565C0); break;
                case "NAO_ENTROU":
                    cardResultado.setBackgroundColor(0xFF6A1B9A); break;
                default:
                    cardResultado.setBackgroundColor(0xFFC62828); break;
            }

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                cardResultado.setVisibility(View.GONE);
                escaneando = true;
            }, 3000);
        });
    }

    private void mostrarErro(String msg) {
        runOnUiThread(() -> {
            cardResultado.setVisibility(View.VISIBLE);
            tvResultado.setText("ERRO");
            tvMensagem.setText(msg);
            tvParticipante.setText("");
            cardResultado.setBackgroundColor(0xFFC62828);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                cardResultado.setVisibility(View.GONE);
                escaneando = true;
            }, 3000);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                            @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniciarScanner();
        }
    }

    @Override protected void onResume()  { super.onResume();  scannerView.resume();  }
    @Override protected void onPause()   { super.onPause();   scannerView.pause();   }
}
