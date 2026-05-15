package br.edu.unicid.controledeacesso;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAdmin   = findViewById(R.id.btn_admin);
        Button btnScanner = findViewById(R.id.btn_scanner);
        Button btnQrCode  = findViewById(R.id.btn_qrcode);

        btnAdmin.setOnClickListener(v ->
                startActivity(new Intent(this, AdminActivity.class)));
        btnScanner.setOnClickListener(v ->
                startActivity(new Intent(this, ScannerActivity.class)));
        btnQrCode.setOnClickListener(v ->
                startActivity(new Intent(this, QrCodeActivity.class)));
    }
}
