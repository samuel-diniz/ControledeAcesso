package br.edu.unicid.controledeacesso;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private TextView tvResultado, tvMensagem, tvParticipante, tvTipoBadge, tvResultadoIcon, tvModoLabel;
    private View cardResultado;
    private RadioGroup rgModo;
    private final AtomicBoolean escaneando = new AtomicBoolean(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        scannerView      = findViewById(R.id.barcode_scanner);
        tvResultado      = findViewById(R.id.tv_resultado);
        tvMensagem       = findViewById(R.id.tv_mensagem);
        tvParticipante   = findViewById(R.id.tv_participante_nome);
        tvTipoBadge      = findViewById(R.id.tv_tipo_badge);
        tvResultadoIcon  = findViewById(R.id.tv_resultado_icon);
        tvModoLabel      = findViewById(R.id.tv_modo_label);
        cardResultado    = findViewById(R.id.card_resultado);
        rgModo           = findViewById(R.id.rg_modo);

        // Update mode label when user switches
        rgModo.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isEntrada = checkedId == R.id.rb_entrada;
            if (isEntrada) {
                tvModoLabel.setText("ENTRADA");
                tvModoLabel.setBackgroundColor(0x4D4CAF50);
            } else {
                tvModoLabel.setText("SAÍDA");
                tvModoLabel.setBackgroundColor(0x4DFF9800);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            iniciarScanner();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }
    }

    /** Read mode on main thread — safe for UI access */
    private String getModoSelecionado() {
        RadioButton rbEntrada = findViewById(R.id.rb_entrada);
        return rbEntrada.isChecked() ? "ENTRADA" : "SAIDA";
    }

    private void iniciarScanner() {
        scannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                // compareAndSet ensures only one scan is processed at a time
                if (!escaneando.compareAndSet(true, false)) return;
                // getModoSelecionado() is safe — ZXing fires this on the main thread
                String modo = getModoSelecionado();
                validarToken(result.getText(), modo);
            }
        });
    }

    private void validarToken(String token, String modo) {
        CheckInRequest req = new CheckInRequest(token, "Scanner UniPass", modo);
        ApiClient.get().validarToken(req).enqueue(new Callback<CheckInResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckInResponse> call,
                                   @NonNull Response<CheckInResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mostrarResultado(response.body(), modo);
                } else {
                    mostrarErro("Erro na resposta do servidor (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckInResponse> call, @NonNull Throwable t) {
                mostrarErro("Sem conexão com o servidor");
            }
        });
    }

    private void mostrarResultado(CheckInResponse resp, String modo) {
        runOnUiThread(() -> {
            String resultado = resp.getResultado() != null ? resp.getResultado() : "INVALIDO";

            // Icon + background color
            String icon;
            int bgColor;
            switch (resultado) {
                case "VALIDO":
                    icon = "ENTRADA".equals(modo) ? "✅" : "🚪";
                    bgColor = 0xFF16A34A; break;
                case "JA_USADO":
                    icon = "⚠️";
                    bgColor = 0xFFD97706; break;
                case "LOTADO":
                    icon = "🚫";
                    bgColor = 0xFF1E40AF; break;
                case "NAO_ENTROU":
                    icon = "❓";
                    bgColor = 0xFF7C3AED; break;
                default:
                    icon = "❌";
                    bgColor = 0xFFDC2626; break;
            }

            // Apply new background (replace drawable with solid color)
            cardResultado.setBackgroundColor(bgColor);
            tvResultadoIcon.setText(icon);

            // Friendly result label
            String label;
            switch (resultado) {
                case "VALIDO":      label = "ENTRADA".equals(modo) ? "Acesso Liberado" : "Saída Registrada"; break;
                case "JA_USADO":   label = "ENTRADA".equals(modo) ? "Já Está Dentro" : "Saída Já Registrada"; break;
                case "LOTADO":     label = "Evento Lotado"; break;
                case "NAO_ENTROU": label = "Não Registrou Entrada"; break;
                default:           label = "Token Inválido"; break;
            }
            tvResultado.setText(label);

            String nome = resp.getParticipante() != null ? resp.getParticipante().getNome() : "";
            tvParticipante.setText(nome);
            tvMensagem.setText(resp.getMensagem());
            tvTipoBadge.setText(modo);

            // Animate and show
            cardResultado.setVisibility(View.VISIBLE);
            cardResultado.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                cardResultado.setVisibility(View.GONE);
                escaneando.set(true);
            }, 3500);
        });
    }

    private void mostrarErro(String msg) {
        runOnUiThread(() -> {
            cardResultado.setBackgroundColor(0xFFDC2626);
            tvResultadoIcon.setText("❌");
            tvResultado.setText("Erro de Conexão");
            tvMensagem.setText(msg);
            tvParticipante.setText("");
            tvTipoBadge.setText("—");
            cardResultado.setVisibility(View.VISIBLE);
            cardResultado.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                cardResultado.setVisibility(View.GONE);
                escaneando.set(true);
            }, 3500);
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

    @Override protected void onResume() { super.onResume(); scannerView.resume(); }
    @Override protected void onPause()  { super.onPause();  scannerView.pause();  }
}
