package com.example.cardfolio;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class BuscarFragment extends Fragment {

    private EditText etBuscar;
    private LinearLayout contenedorResultados;
    private LinearLayout contenedorJuegos;
    private Button btnQuitarFiltro = null;
    private String juegoSeleccionado = null;
    private TextView bannerExito;
    private final Handler bannerHandler = new Handler();
    private Runnable bannerOcultarRunnable;

    private List<Juego> juegos = new ArrayList<>();
    private List<LinearLayout> itemsJuego = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buscar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etBuscar = view.findViewById(R.id.etBuscar);
        contenedorResultados = view.findViewById(R.id.contenedorResultados);
        contenedorJuegos = view.findViewById(R.id.contenedorJuegos);
        bannerExito = view.findViewById(R.id.bannerExito);
        Button btnBuscar = view.findViewById(R.id.btnBuscar);

        inicializarJuegos();
        mostrarBienvenida();

        btnBuscar.setOnClickListener(v -> buscarCartas(etBuscar.getText().toString().trim()));
    }

    private void inicializarJuegos() {
        juegos.add(new Juego("Yu-Gi-Oh!", R.drawable.logo_yugioh));
        juegos.add(new Juego("Pokémon", R.drawable.logo_pokemon));
        juegos.add(new Juego("Magic", R.drawable.logo_magic));
        juegos.add(new Juego("One Piece", R.drawable.logo_onepiece));
        juegos.add(new Juego("Riftbound", R.drawable.logo_riftbound));

        for (Juego juego : juegos) {
            LinearLayout item = crearItemJuego(juego);
            itemsJuego.add(item);
            contenedorJuegos.addView(item);
        }
    }

    private LinearLayout crearItemJuego(Juego juego) {
        LinearLayout item = new LinearLayout(requireContext());
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.fondo_carta));
        item.setPadding(dp(16), dp(12), dp(16), dp(12));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(10));
        item.setLayoutParams(params);

        ImageView logo = new ImageView(requireContext());
        logo.setLayoutParams(new LinearLayout.LayoutParams(dp(120), dp(70)));
        logo.setImageResource(juego.getLogoResId());
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        TextView tvNombre = new TextView(requireContext());
        tvNombre.setText(juego.getNombre());
        tvNombre.setTextColor(requireContext().getColor(R.color.color_texto_primario));
        tvNombre.setTextSize(16);
        tvNombre.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams paramsNombre = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        paramsNombre.setMarginStart(dp(16));
        tvNombre.setLayoutParams(paramsNombre);

        item.addView(logo);
        item.addView(tvNombre);
        item.setOnClickListener(v -> seleccionarJuego(juego, item));

        return item;
    }

    private void seleccionarJuego(Juego juego, LinearLayout item) {
        for (LinearLayout i : itemsJuego) {
            if (i != item) i.setVisibility(View.GONE);
        }
        item.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.fondo_juego_seleccionado));
        juegoSeleccionado = juego.getNombre();

        if (btnQuitarFiltro == null) {
            btnQuitarFiltro = new Button(requireContext());
            btnQuitarFiltro.setText(getString(R.string.btn_quitar_filtro));
            btnQuitarFiltro.setTextColor(requireContext().getColor(R.color.color_acento));
            btnQuitarFiltro.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            btnQuitarFiltro.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnQuitarFiltro.setOnClickListener(v -> limpiarFiltro());
            contenedorJuegos.addView(btnQuitarFiltro);
        }

        etBuscar.setText("");
        mostrarCartasPorJuego(juego.getNombre());
    }

    private void limpiarFiltro() {
        for (LinearLayout item : itemsJuego) {
            item.setVisibility(View.VISIBLE);
            item.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.fondo_carta));
        }
        if (btnQuitarFiltro != null) {
            contenedorJuegos.removeView(btnQuitarFiltro);
            btnQuitarFiltro = null;
        }
        juegoSeleccionado = null;
        etBuscar.setText("");
        mostrarBienvenida();
    }

    private void mostrarCartasPorJuego(String nombreJuego) {
        contenedorResultados.removeAllViews();

        if (!nombreJuego.equals("Yu-Gi-Oh!")) {
            TextView tv = new TextView(requireContext());
            tv.setText(getString(R.string.juego_proximamente));
            tv.setTextColor(requireContext().getColor(R.color.color_texto_secundario));
            tv.setTextSize(15);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0, dp(24), 0, 0);
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contenedorResultados.addView(tv);
            return;
        }

        for (Carta carta : getMain().getCatalogo()) {
            if (carta.getJuego().equals(nombreJuego)) {
                contenedorResultados.addView(crearVistaCarta(carta));
            }
        }
    }

    private void mostrarBienvenida() {
        contenedorResultados.removeAllViews();
        TextView tv = new TextView(requireContext());
        tv.setText(getString(R.string.bienvenida_texto));
        tv.setTextColor(requireContext().getColor(R.color.color_texto_secundario));
        tv.setTextSize(15);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, dp(24), 0, 0);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        contenedorResultados.addView(tv);
    }

    private void buscarCartas(String query) {
        contenedorResultados.removeAllViews();

        if (query.isEmpty()) {
            if (juegoSeleccionado != null) mostrarCartasPorJuego(juegoSeleccionado);
            else mostrarBienvenida();
            return;
        }

        boolean hayResultados = false;
        for (Carta carta : getMain().getCatalogo()) {
            boolean coincideNombre = carta.getNombre().toLowerCase().contains(query.toLowerCase());
            boolean coincideJuego = juegoSeleccionado == null || carta.getJuego().equals(juegoSeleccionado);
            if (coincideNombre && coincideJuego) {
                contenedorResultados.addView(crearVistaCarta(carta));
                hayResultados = true;
            }
        }

        if (!hayResultados) {
            TextView tv = new TextView(requireContext());
            tv.setText(getString(R.string.sin_resultados));
            tv.setTextColor(requireContext().getColor(R.color.color_texto_secundario));
            tv.setTextSize(14);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0, dp(24), 0, 0);
            contenedorResultados.addView(tv);
        }
    }

    private LinearLayout crearVistaCarta(Carta carta) {
        LinearLayout contenedor = new LinearLayout(requireContext());
        contenedor.setOrientation(LinearLayout.HORIZONTAL);
        contenedor.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.fondo_carta));
        contenedor.setPadding(dp(12), dp(12), dp(12), dp(12));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(10));
        contenedor.setLayoutParams(params);

        ImageView imagen = new ImageView(requireContext());
        imagen.setLayoutParams(new LinearLayout.LayoutParams(dp(60), dp(84)));
        imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(requireContext())
                .load(carta.getImageUrl())
                .placeholder(carta.getImagenResId())
                .error(carta.getImagenResId())
                .into(imagen);
        contenedor.addView(imagen);

        LinearLayout info = new LinearLayout(requireContext());
        info.setOrientation(LinearLayout.VERTICAL);
        info.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams paramsInfo = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        paramsInfo.setMarginStart(dp(12));
        info.setLayoutParams(paramsInfo);

        TextView tvNombre = new TextView(requireContext());
        tvNombre.setText(carta.getNombre());
        tvNombre.setTextColor(requireContext().getColor(R.color.color_texto_primario));
        tvNombre.setTextSize(14);
        tvNombre.setTypeface(null, Typeface.BOLD);

        TextView tvRareza = new TextView(requireContext());
        tvRareza.setText(carta.getRareza());
        tvRareza.setTextColor(getColorPorRareza(carta.getRareza()));
        tvRareza.setTextSize(12);
        tvRareza.setTypeface(null, Typeface.ITALIC);

        TextView tvValor = new TextView(requireContext());
        tvValor.setText("$ " + carta.getValor());
        tvValor.setTextColor(requireContext().getColor(R.color.color_texto_secundario));
        tvValor.setTextSize(12);

        info.addView(tvNombre);
        info.addView(tvRareza);
        info.addView(tvValor);
        contenedor.addView(info);

        Button btnAgregar = new Button(requireContext());
        btnAgregar.setText(getString(R.string.btn_agregar_carta));
        btnAgregar.setTextColor(requireContext().getColor(R.color.color_fondo));
        btnAgregar.setBackgroundTintList(ColorStateList.valueOf(requireContext().getColor(R.color.color_acento)));
        LinearLayout.LayoutParams paramBtn = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramBtn.gravity = Gravity.CENTER_VERTICAL;
        btnAgregar.setLayoutParams(paramBtn);

        btnAgregar.setOnClickListener(v -> {
            getMain().getColeccion().add(carta);
            getMain().guardarCartaEnFirestore(carta);

            ScaleAnimation escala = new ScaleAnimation(
                    1f, 1.15f, 1f, 1.15f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            escala.setDuration(120);
            escala.setRepeatMode(Animation.REVERSE);
            escala.setRepeatCount(1);
            btnAgregar.startAnimation(escala);

            btnAgregar.setBackgroundTintList(ColorStateList.valueOf(requireContext().getColor(R.color.color_exito_claro)));
            mostrarBannerExito(carta.getNombre());
            new Handler().postDelayed(() ->
                    btnAgregar.setBackgroundTintList(ColorStateList.valueOf(requireContext().getColor(R.color.color_acento))),
                    1500);
        });

        contenedor.addView(btnAgregar);
        return contenedor;
    }

    private void mostrarBannerExito(String nombreCarta) {
        if (bannerOcultarRunnable != null) {
            bannerHandler.removeCallbacks(bannerOcultarRunnable);
        }

        bannerExito.setText("✓  \"" + nombreCarta + "\" " + getString(R.string.exito_agregada));
        bannerExito.setVisibility(View.VISIBLE);
        bannerExito.clearAnimation();

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(300);
        bannerExito.startAnimation(fadeIn);

        bannerOcultarRunnable = () -> {
            AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
            fadeOut.setDuration(400);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation a) {}
                @Override public void onAnimationRepeat(Animation a) {}
                @Override public void onAnimationEnd(Animation a) {
                    bannerExito.setVisibility(View.GONE);
                }
            });
            bannerExito.startAnimation(fadeOut);
        };

        bannerHandler.postDelayed(bannerOcultarRunnable, 2000);
    }

    private int getColorPorRareza(String rareza) {
        switch (rareza) {
            case "Super Rare":  return requireContext().getColor(R.color.color_rareza_super_rare);
            case "Ultra Rare":  return requireContext().getColor(R.color.color_rareza_ultra_rare);
            case "Secret Rare": return requireContext().getColor(R.color.color_rareza_secret_rare);
            default:            return requireContext().getColor(R.color.color_rareza_common);
        }
    }

    private int dp(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }

    private MainActivity getMain() {
        return (MainActivity) requireActivity();
    }
}
