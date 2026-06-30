package com.example.cardfolio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private Button btnAccion;
    private TextView tvError, tvToggleModo;
    private boolean modoRegistro = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            irAMain();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnAccion = findViewById(R.id.btnAccion);
        tvError = findViewById(R.id.tvError);
        tvToggleModo = findViewById(R.id.tvToggleModo);

        btnAccion.setOnClickListener(v -> ejecutarAccion());
        tvToggleModo.setOnClickListener(v -> toggleModo());
    }

    private void ejecutarAccion() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            mostrarError(getString(R.string.login_error_campos));
            return;
        }

        tvError.setVisibility(View.GONE);
        btnAccion.setEnabled(false);
        btnAccion.setText(getString(R.string.login_cargando));

        if (modoRegistro) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            irAMain();
                        } else {
                            btnAccion.setEnabled(true);
                            btnAccion.setText(getString(R.string.login_btn_registrarse));
                            mostrarError(task.getException() != null
                                    ? task.getException().getLocalizedMessage()
                                    : getString(R.string.login_error_registro));
                        }
                    });
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            irAMain();
                        } else {
                            btnAccion.setEnabled(true);
                            btnAccion.setText(getString(R.string.login_btn_ingresar));
                            mostrarError(task.getException() != null
                                    ? task.getException().getLocalizedMessage()
                                    : getString(R.string.login_error_credenciales));
                        }
                    });
        }
    }

    private void toggleModo() {
        modoRegistro = !modoRegistro;
        tvError.setVisibility(View.GONE);
        if (modoRegistro) {
            btnAccion.setText(getString(R.string.login_btn_registrarse));
            tvToggleModo.setText(getString(R.string.login_toggle_login));
        } else {
            btnAccion.setText(getString(R.string.login_btn_ingresar));
            tvToggleModo.setText(getString(R.string.login_toggle_registro));
        }
    }

    private void mostrarError(String mensaje) {
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
    }

    private void irAMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
