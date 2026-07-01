package com.example.cardfolio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PerfilFragment extends Fragment {

    private TextView tvNombreUsuario, tvEmailUsuario, tvStatCartas, tvStatValor, tvStatJuegos;
    private ActivityResultLauncher<Intent> editarPerfilLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editarPerfilLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        cargarNombreUsuario();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvEmailUsuario  = view.findViewById(R.id.tvEmailUsuario);
        tvStatCartas    = view.findViewById(R.id.tvStatCartas);
        tvStatValor     = view.findViewById(R.id.tvStatValor);
        tvStatJuegos    = view.findViewById(R.id.tvStatJuegos);

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) tvEmailUsuario.setText(usuario.getEmail());

        cargarNombreUsuario();

        view.findViewById(R.id.btnEditarPerfil).setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditarPerfilActivity.class);
            intent.putExtra("nombre_actual", tvNombreUsuario.getText().toString());
            editarPerfilLauncher.launch(intent);
        });

        view.findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> cerrarSesion());

        actualizarStats();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) actualizarStats();
    }

    private void cargarNombreUsuario() {
        if (tvNombreUsuario == null) return;
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        String nombre = (usuario != null) ? usuario.getDisplayName() : null;
        tvNombreUsuario.setText(!TextUtils.isEmpty(nombre) ? nombre : getString(R.string.perfil_usuario));
    }

    private void actualizarStats() {
        if (tvStatCartas == null) return;

        List<Carta> coleccion = ((MainActivity) requireActivity()).getColeccion();

        double totalValor = 0;
        Set<String> juegosDistintos = new HashSet<>();

        for (Carta c : coleccion) {
            totalValor += c.getValor();
            juegosDistintos.add(c.getJuego());
        }

        tvStatCartas.setText(String.valueOf(coleccion.size()));
        tvStatValor.setText(String.format(Locale.getDefault(), "$ %.2f", totalValor));
        tvStatJuegos.setText(String.valueOf(juegosDistintos.size()));
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
