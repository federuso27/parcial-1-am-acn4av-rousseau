package com.example.cardfolio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PerfilFragment extends Fragment {

    private TextView tvEmailUsuario, tvStatCartas, tvStatValor, tvStatJuegos, tvStatMasValiosa;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEmailUsuario = view.findViewById(R.id.tvEmailUsuario);
        tvStatCartas = view.findViewById(R.id.tvStatCartas);
        tvStatValor = view.findViewById(R.id.tvStatValor);
        tvStatJuegos = view.findViewById(R.id.tvStatJuegos);
        tvStatMasValiosa = view.findViewById(R.id.tvStatMasValiosa);

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) tvEmailUsuario.setText(usuario.getEmail());

        view.findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> cerrarSesion());

        actualizarStats();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) actualizarStats();
    }

    private void actualizarStats() {
        if (tvStatCartas == null) return;

        List<Carta> coleccion = ((MainActivity) requireActivity()).getColeccion();

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
        tvStatMasValiosa.setText(cartaMasValiosa != null ? cartaMasValiosa.getNombre() : "—");
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
