package com.example.cardfolio;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class PerfilActivity extends AppCompatActivity {

    public static final String EXTRA_COLECCION = "EXTRA_COLECCION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.perfilRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ArrayList<Carta> coleccion = (ArrayList<Carta>) getIntent().getSerializableExtra(EXTRA_COLECCION);
        if (coleccion == null) coleccion = new ArrayList<>();

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        calcularYMostrarStats(coleccion);
    }

    private void calcularYMostrarStats(ArrayList<Carta> coleccion) {
        TextView tvStatCartas   = findViewById(R.id.tvStatCartas);
        TextView tvStatValor    = findViewById(R.id.tvStatValor);
        TextView tvStatJuegos   = findViewById(R.id.tvStatJuegos);
        TextView tvStatMasValiosa = findViewById(R.id.tvStatMasValiosa);

        double totalValor = 0;
        Set<String> juegosDistintos = new HashSet<>();
        Carta cartaMasValiosa = null;

        for (Carta c : coleccion) {
            totalValor += c.getValor();
            juegosDistintos.add(c.getJuego());
            if (cartaMasValiosa == null || c.getValor() > cartaMasValiosa.getValor()) {
                cartaMasValiosa = c;
            }
        }

        tvStatCartas.setText(String.valueOf(coleccion.size()));
        tvStatValor.setText(String.format(Locale.getDefault(), "$ %.2f", totalValor));
        tvStatJuegos.setText(String.valueOf(juegosDistintos.size()));

        if (cartaMasValiosa != null) {
            tvStatMasValiosa.setText(cartaMasValiosa.getNombre());
        } else {
            tvStatMasValiosa.setText("—");
        }
    }
}
