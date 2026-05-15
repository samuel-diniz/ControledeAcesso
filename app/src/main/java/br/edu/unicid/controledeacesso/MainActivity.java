package br.edu.unicid.controledeacesso;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View cardOrganizador  = findViewById(R.id.card_organizador);
        View cardParticipante = findViewById(R.id.card_participante);
        View btnScanner       = findViewById(R.id.btn_scanner);

        cardOrganizador.setOnClickListener(v ->
                startActivity(new Intent(this, AdminActivity.class)));

        cardParticipante.setOnClickListener(v ->
                startActivity(new Intent(this, ParticipanteLoginActivity.class)));

        btnScanner.setOnClickListener(v ->
                startActivity(new Intent(this, ScannerActivity.class)));
    }
}
