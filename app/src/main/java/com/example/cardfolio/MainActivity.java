package com.example.cardfolio;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etBuscar;
    private LinearLayout contenedorResultados;
    private List<Carta> catalogo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etBuscar = findViewById(R.id.etBuscar);
        contenedorResultados = findViewById(R.id.contenedorResultados);
        Button btnBuscar = findViewById(R.id.btnBuscar);

        inicializarCatalogo();
        mostrarBienvenida();

        btnBuscar.setOnClickListener(v -> {
            String query = etBuscar.getText().toString().trim();
            buscarCartas(query);
        });

        findViewById(R.id.navPortfolio).setOnClickListener(v ->
                Toast.makeText(this, "Portfolio — próximamente", Toast.LENGTH_SHORT).show()
        );
        findViewById(R.id.navPerfil).setOnClickListener(v ->
                Toast.makeText(this, "Perfil — próximamente", Toast.LENGTH_SHORT).show()
        );
    }

    private void inicializarCatalogo() {
        catalogo.add(new Carta("Dinomorphia Kentregina", "Super Rare", R.drawable.dinomorphia_kentregina_sr, 15.00));
        catalogo.add(new Carta("Dinomorphia Frenzy", "Super Rare", R.drawable.dinomorphia_frenzy_sr, 8.00));
        catalogo.add(new Carta("Dinomorphia Diplos", "Common", R.drawable.dinomorphia_diplos_c, 1.50));
        catalogo.add(new Carta("Dinomorphia Domain", "Ultra Rare", R.drawable.dinomorphia_domain_ur, 0.23));
        catalogo.add(new Carta("Dinomorphia Therizia", "Super Rare", R.drawable.dinomorphia_therizia_sr, 0.46));
        catalogo.add(new Carta("Dinomorphia Sonic", "Common", R.drawable.dinomorphia_sonic_c, 0.12));
        catalogo.add(new Carta("Dinomorphia Reversion", "Common", R.drawable.dinomorphia_reversion_c, 0.13));
        catalogo.add(new Carta("Shaddoll Construct", "Ultra Rare", R.drawable.shaddoll_construct_ur, 2.40));
    }

    private void mostrarBienvenida() {
        contenedorResultados.removeAllViews();

        TextView tv = new TextView(this);
        tv.setText(getString(R.string.bienvenida_texto));
        tv.setTextColor(getColor(R.color.color_texto_secundario));
        tv.setTextSize(15);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, dpToPx(40), 0, 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tv.setLayoutParams(params);
        contenedorResultados.addView(tv);
    }

    private void buscarCartas(String query) {
        contenedorResultados.removeAllViews();

        if (query.isEmpty()) {
            mostrarBienvenida();
            return;
        }

        boolean hayResultados = false;
        for (Carta carta : catalogo) {
            if (carta.getNombre().toLowerCase().contains(query.toLowerCase())) {
                contenedorResultados.addView(crearVistaCarta(carta));
                hayResultados = true;
            }
        }

        if (!hayResultados) {
            TextView tv = new TextView(this);
            tv.setText(getString(R.string.sin_resultados));
            tv.setTextColor(getColor(R.color.color_texto_secundario));
            tv.setTextSize(14);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0, dpToPx(40), 0, 0);
            contenedorResultados.addView(tv);
        }
    }

    private LinearLayout crearVistaCarta(Carta carta) {
        // Contenedor con bordes redondeados
        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setOrientation(LinearLayout.HORIZONTAL);
        contenedor.setBackground(getDrawable(R.drawable.fondo_carta));
        contenedor.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

        LinearLayout.LayoutParams paramsContenedor = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsContenedor.setMargins(0, 0, 0, dpToPx(10));
        contenedor.setLayoutParams(paramsContenedor);

        // Imagen de la carta
        ImageView imagen = new ImageView(this);
        LinearLayout.LayoutParams paramsImagen = new LinearLayout.LayoutParams(dpToPx(60), dpToPx(84));
        imagen.setLayoutParams(paramsImagen);
        imagen.setImageResource(carta.getImagenResId());
        imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
        contenedor.addView(imagen);

        // Info: nombre, rareza y valor
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams paramsInfo = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        paramsInfo.setMarginStart(dpToPx(12));
        info.setLayoutParams(paramsInfo);

        TextView tvNombre = new TextView(this);
        tvNombre.setText(carta.getNombre());
        tvNombre.setTextColor(getColor(R.color.color_texto_primario));
        tvNombre.setTextSize(14);
        tvNombre.setTypeface(null, Typeface.BOLD);

        // Rareza con color dinámico según tipo
        TextView tvRareza = new TextView(this);
        tvRareza.setText(carta.getRareza());
        tvRareza.setTextColor(getColorPorRareza(carta.getRareza()));
        tvRareza.setTextSize(12);
        tvRareza.setTypeface(null, Typeface.ITALIC);

        TextView tvValor = new TextView(this);
        tvValor.setText("$ " + carta.getValor());
        tvValor.setTextColor(getColor(R.color.color_texto_secundario));
        tvValor.setTextSize(12);

        info.addView(tvNombre);
        info.addView(tvRareza);
        info.addView(tvValor);
        contenedor.addView(info);

        // Botón agregar con feedback visual al presionar
        Button btnAgregar = new Button(this);
        btnAgregar.setText(getString(R.string.btn_agregar_carta));
        btnAgregar.setTextColor(getColor(R.color.color_fondo));
        btnAgregar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.color_acento)));

        LinearLayout.LayoutParams paramBtn = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramBtn.gravity = Gravity.CENTER_VERTICAL;
        btnAgregar.setLayoutParams(paramBtn);

        btnAgregar.setOnClickListener(v -> {
            btnAgregar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.color_acento_oscuro)));
            new Handler().postDelayed(() ->
                    btnAgregar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.color_acento))),
                    250
            );
            Toast.makeText(this, "\"" + carta.getNombre() + "\" " + getString(R.string.carta_agregada), Toast.LENGTH_SHORT).show();
        });

        contenedor.addView(btnAgregar);
        return contenedor;
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
