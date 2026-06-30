package com.example.cardfolio;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PortfolioFragment extends Fragment {

    private TextView tvValorTotal, tvCantidad;
    private LinearLayout contenedor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_portfolio, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvValorTotal = view.findViewById(R.id.tvValorTotal);
        tvCantidad = view.findViewById(R.id.tvCantidad);
        contenedor = view.findViewById(R.id.contenedorPortfolio);
        mostrarPortfolio();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) mostrarPortfolio();
    }

    private void mostrarPortfolio() {
        if (contenedor == null) return;
        contenedor.removeAllViews();

        List<Carta> coleccion = ((MainActivity) requireActivity()).getColeccion();

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
            TextView tvVacio = new TextView(requireContext());
            tvVacio.setText(getString(R.string.portfolio_vacio));
            tvVacio.setTextColor(requireContext().getColor(R.color.color_texto_secundario));
            tvVacio.setTextSize(15);
            tvVacio.setGravity(Gravity.CENTER);
            tvVacio.setPadding(0, dp(24), 0, 0);
            tvVacio.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contenedor.addView(tvVacio);
        } else {
            for (Map.Entry<String, Carta> entry : cartasUnicas.entrySet()) {
                int cantidad = cantidades.get(entry.getKey());
                contenedor.addView(crearVistaCarta(entry.getValue(), cantidad));
            }
        }
    }

    private LinearLayout crearVistaCarta(Carta carta, int cantidad) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.fondo_carta));
        row.setPadding(dp(12), dp(12), dp(12), dp(12));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(10));
        row.setLayoutParams(params);

        ImageView imagen = new ImageView(requireContext());
        imagen.setLayoutParams(new LinearLayout.LayoutParams(dp(60), dp(84)));
        imagen.setImageResource(carta.getImagenResId());
        imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
        row.addView(imagen);

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
        tvValor.setText(String.format(Locale.getDefault(), "$ %.2f", carta.getValor() * cantidad));
        tvValor.setTextColor(requireContext().getColor(R.color.color_texto_secundario));
        tvValor.setTextSize(12);

        TextView tvJuego = new TextView(requireContext());
        tvJuego.setText(carta.getJuego());
        tvJuego.setTextColor(requireContext().getColor(R.color.color_texto_secundario));
        tvJuego.setTextSize(11);

        info.addView(tvNombre);
        info.addView(tvRareza);
        info.addView(tvValor);
        info.addView(tvJuego);
        row.addView(info);

        if (cantidad > 1) {
            TextView tvBadge = new TextView(requireContext());
            tvBadge.setText("x" + cantidad);
            tvBadge.setTextColor(requireContext().getColor(R.color.color_acento));
            tvBadge.setTextSize(18);
            tvBadge.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams paramsBadge = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsBadge.gravity = Gravity.CENTER_VERTICAL;
            paramsBadge.setMarginStart(dp(8));
            tvBadge.setLayoutParams(paramsBadge);
            row.addView(tvBadge);
        }

        return row;
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
}
