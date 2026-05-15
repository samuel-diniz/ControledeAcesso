package br.edu.unicid.controledeacesso;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrCodeActivity extends AppCompatActivity {

    private ImageView ivQrCode;
    private EditText etToken;
    private TextView tvNomeParticipante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        ivQrCode           = findViewById(R.id.iv_qrcode);
        etToken            = findViewById(R.id.et_token);
        tvNomeParticipante = findViewById(R.id.tv_nome_participante);
        Button btnGerar    = findViewById(R.id.btn_gerar_qr);

        // Recebe token via Intent (quando vindo do ParticipanteFragment)
        String token = getIntent().getStringExtra("token");
        String nome  = getIntent().getStringExtra("nomeParticipante");
        if (token != null && !token.isEmpty()) {
            etToken.setText(token);
            if (nome != null) tvNomeParticipante.setText(nome);
            gerarQrCode(token);
        }

        btnGerar.setOnClickListener(v -> {
            String t = etToken.getText().toString().trim();
            if (t.isEmpty()) {
                Toast.makeText(this, "Digite o token", Toast.LENGTH_SHORT).show();
            } else {
                gerarQrCode(t);
            }
        });
    }

    private void gerarQrCode(String conteudo) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(conteudo, BarcodeFormat.QR_CODE, 512, 512);
            int w = matrix.getWidth();
            int h = matrix.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++)
                    pixels[y * w + x] = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bmp.setPixels(pixels, 0, w, 0, 0, w, h);
            ivQrCode.setImageBitmap(bmp);
        } catch (WriterException e) {
            Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
        }
    }
}
