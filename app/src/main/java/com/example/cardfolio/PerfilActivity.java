package com.example.cardfolio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            TextView tvEmail = findViewById(R.id.tvEmailUsuario);
            tvEmail.setText(usuario.getEmail());
        }

        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        calcularYMostrarStats(coleccion);
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
