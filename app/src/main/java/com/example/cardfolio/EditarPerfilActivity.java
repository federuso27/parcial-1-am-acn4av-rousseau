package com.example.cardfolio;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText etNombreUsuario;
    private TextView tvErrorNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        tvErrorNombre   = findViewById(R.id.tvErrorNombre);

        String nombreActual = getIntent().getStringExtra("nombre_actual");
        if (!TextUtils.isEmpty(nombreActual)) {
            etNombreUsuario.setText(nombreActual);
            etNombreUsuario.setSelection(nombreActual.length());
        }

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        findViewById(R.id.btnGuardar).setOnClickListener(v -> guardarCambios());
    }

    private void guardarCambios() {
        String nuevoNombre = etNombreUsuario.getText().toString().trim();

        if (TextUtils.isEmpty(nuevoNombre)) {
            tvErrorNombre.setText(getString(R.string.editar_perfil_error_vacio));
            tvErrorNombre.setVisibility(View.VISIBLE);
            return;
        }
        tvErrorNombre.setVisibility(View.GONE);

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) { finish(); return; }

        UserProfileChangeRequest solicitud = new UserProfileChangeRequest.Builder()
                .setDisplayName(nuevoNombre)
                .build();

        usuario.updateProfile(solicitud).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                setResult(RESULT_OK);
                finish();
            } else {
                tvErrorNombre.setText(getString(R.string.editar_perfil_error_guardar));
                tvErrorNombre.setVisibility(View.VISIBLE);
            }
        });
    }
}
