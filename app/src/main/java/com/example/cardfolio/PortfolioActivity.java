package com.example.cardfolio;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class PortfolioActivity extends AppCompatActivity {

    public static final String EXTRA_COLECCION = "EXTRA_COLECCION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_portfolio);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.portfolioRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ArrayList<Carta> coleccion = (ArrayList<Carta>) getIntent().getSerializableExtra(EXTRA_COLECCION);
        if (coleccion == null) coleccion = new ArrayList<>();

        TextView tvValorTotal = findViewById(R.id.tvValorTotal);
        TextView tvCantidad = findViewById(R.id.tvCantidad);
        LinearLayout contenedor = findViewById(R.id.contenedorPortfolio);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        // Agrupar cartas por nombre, preservando el orden de inserción
        LinkedHashMap<String, Carta> cartasUnicas = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> cantidades = new LinkedHashMap<>();
        for (Carta c : coleccion) {
            String nombre = c.getNombre();
            if (!cartasUnicas.containsKey(nombre)) {
                cartasUnicas.put(nombre, c);
                cantidades.put(nombre, 1);
            } else {
                cantidades.put(nombre, cantidades.get(nombre) + 1);
            }
        }

        double total = 0;
        for (Carta c : coleccion) total += c.getValor();

        tvValorTotal.setText(String.format(Locale.getDefault(), "$ %.2f", total));
        tvCantidad.setText(coleccion.size() + " " + getString(R.string.portfolio_cartas_label));

        if (coleccion.isEmpty()) {
            TextView tvVacio = new TextView(this);
            tvVacio.setText(getString(R.string.portfolio_vacio));
            tvVacio.setTextColor(getColor(R.color.color_texto_secundario));
            tvVacio.setTextSize(15);
            tvVacio.setGravity(Gravity.CENTER);
            tvVacio.setPadding(0, dpToPx(24), 0, 0);
            tvVacio.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            contenedor.addView(tvVacio);
        } else {
            for (Map.Entry<String, Carta> entry : cartasUnicas.entrySet()) {
                int cantidad = cantidades.get(entry.getKey());
                contenedor.addView(crearVistaCarta(entry.getValue(), cantidad));
            }
        }
    }

    private LinearLayout crearVistaCarta(Carta carta, int cantidad) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackground(AppCompatResources.getDrawable(this, R.drawable.fondo_carta));
        row.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(10));
        row.setLayoutParams(params);

        ImageView imagen = new ImageView(this);
        imagen.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(60), dpToPx(84)));
        imagen.setImageResource(carta.getImagenResId());
        imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
        row.addView(imagen);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams paramsInfo = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        paramsInfo.setMarginStart(dpToPx(12));
        info.setLayoutParams(paramsInfo);

        TextView tvNombre = new TextView(this);
        tvNombre.setText(carta.getNombre());
        tvNombre.setTextColor(getColor(R.color.color_texto_primario));
        tvNombre.setTextSize(14);
        tvNombre.setTypeface(null, Typeface.BOLD);

        TextView tvRareza = new TextView(this);
        tvRareza.setText(carta.getRareza());
        tvRareza.setTextColor(getColorPorRareza(carta.getRareza()));
        tvRareza.setTextSize(12);
        tvRareza.setTypeface(null, Typeface.ITALIC);

        TextView tvValor = new TextView(this);
        tvValor.setText(String.format(Locale.getDefault(), "$ %.2f", carta.getValor() * cantidad));
        tvValor.setTextColor(getColor(R.color.color_texto_secundario));
        tvValor.setTextSize(12);

        TextView tvJuego = new TextView(this);
        tvJuego.setText(carta.getJuego());
        tvJuego.setTextColor(getColor(R.color.color_texto_secundario));
        tvJuego.setTextSize(11);

        info.addView(tvNombre);
        info.addView(tvRareza);
        info.addView(tvValor);
        info.addView(tvJuego);
        row.addView(info);

        // Badge de cantidad (solo si hay más de una unidad)
        if (cantidad > 1) {
            TextView tvBadge = new TextView(this);
            tvBadge.setText("x" + cantidad);
            tvBadge.setTextColor(getColor(R.color.color_acento));
            tvBadge.setTextSize(18);
            tvBadge.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams paramsBadge = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            paramsBadge.gravity = Gravity.CENTER_VERTICAL;
            paramsBadge.setMarginStart(dpToPx(8));
            tvBadge.setLayoutParams(paramsBadge);
            row.addView(tvBadge);
        }

        return row;
    }

    private int getColorPorRareza(String rareza) {
        switch (rareza) {
            case "Super Rare":  return getColor(R.color.color_rareza_super_rare);
            case "Ultra Rare":  return getColor(R.color.color_rareza_ultra_rare);
            case "Secret Rare": return getColor(R.color.color_rareza_secret_rare);
            default:            return getColor(R.color.color_rareza_common);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
