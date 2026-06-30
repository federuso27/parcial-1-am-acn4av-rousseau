package com.example.cardfolio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String userId;

    private final List<Carta> catalogo = new ArrayList<>();
    private final List<Carta> coleccion = new ArrayList<>();

    private ImageView navBuscarIcon, navPortfolioIcon, navPerfilIcon;
    private TextView navBuscarText, navPortfolioText, navPerfilText;

    private BuscarFragment buscarFragment;
    private PortfolioFragment portfolioFragment;
    private PerfilFragment perfilFragment;
    private Fragment fragmentActivo;

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

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

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        inicializarCatalogo();
        cargarColeccionDesdeFirestore();

        navBuscarIcon = findViewById(R.id.navBuscarIcon);
        navPortfolioIcon = findViewById(R.id.navPortfolioIcon);
        navPerfilIcon = findViewById(R.id.navPerfilIcon);
        navBuscarText = findViewById(R.id.navBuscarText);
        navPortfolioText = findViewById(R.id.navPortfolioText);
        navPerfilText = findViewById(R.id.navPerfilText);

        buscarFragment = new BuscarFragment();
        portfolioFragment = new PortfolioFragment();
        perfilFragment = new PerfilFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, buscarFragment, "buscar")
                .add(R.id.fragmentContainer, portfolioFragment, "portfolio")
                .add(R.id.fragmentContainer, perfilFragment, "perfil")
                .hide(portfolioFragment)
                .hide(perfilFragment)
                .commit();

        fragmentActivo = buscarFragment;

        findViewById(R.id.navBuscar).setOnClickListener(v -> switchFragment(buscarFragment, "buscar"));
        findViewById(R.id.navPortfolio).setOnClickListener(v -> switchFragment(portfolioFragment, "portfolio"));
        findViewById(R.id.navPerfil).setOnClickListener(v -> switchFragment(perfilFragment, "perfil"));
    }

    private void switchFragment(Fragment fragment, String tab) {
        if (fragmentActivo == fragment) return;
        getSupportFragmentManager().beginTransaction()
                .hide(fragmentActivo)
                .show(fragment)
                .commit();
        fragmentActivo = fragment;
        actualizarNavBar(tab);
    }

    private void actualizarNavBar(String tab) {
        int inactivo = getColor(R.color.color_texto_secundario);
        int activo = getColor(R.color.color_acento);

        navBuscarIcon.setColorFilter(tab.equals("buscar") ? activo : inactivo);
        navBuscarText.setTextColor(tab.equals("buscar") ? activo : inactivo);
        navPortfolioIcon.setColorFilter(tab.equals("portfolio") ? activo : inactivo);
        navPortfolioText.setTextColor(tab.equals("portfolio") ? activo : inactivo);
        navPerfilIcon.setColorFilter(tab.equals("perfil") ? activo : inactivo);
        navPerfilText.setTextColor(tab.equals("perfil") ? activo : inactivo);
    }

    private void inicializarCatalogo() {
        catalogo.add(new Carta("Dinomorphia Kentregina", "Super Rare", R.drawable.dinomorphia_kentregina_sr, 15.00, "Yu-Gi-Oh!"));
        catalogo.add(new Carta("Dinomorphia Frenzy", "Super Rare", R.drawable.dinomorphia_frenzy_sr, 8.00, "Yu-Gi-Oh!"));
        catalogo.add(new Carta("Dinomorphia Diplos", "Common", R.drawable.dinomorphia_diplos_c, 1.50, "Yu-Gi-Oh!"));
        catalogo.add(new Carta("Dinomorphia Domain", "Ultra Rare", R.drawable.dinomorphia_domain_ur, 0.23, "Yu-Gi-Oh!"));
        catalogo.add(new Carta("Dinomorphia Therizia", "Super Rare", R.drawable.dinomorphia_therizia_sr, 0.46, "Yu-Gi-Oh!"));
        catalogo.add(new Carta("Dinomorphia Sonic", "Common", R.drawable.dinomorphia_sonic_c, 0.12, "Yu-Gi-Oh!"));
        catalogo.add(new Carta("Dinomorphia Reversion", "Common", R.drawable.dinomorphia_reversion_c, 0.13, "Yu-Gi-Oh!"));
        catalogo.add(new Carta("Shaddoll Construct", "Ultra Rare", R.drawable.shaddoll_construct_ur, 2.40, "Yu-Gi-Oh!"));
        catalogo.add(new Carta("Blue-Eyes White Dragon", "Ultra rare", R.drawable.blueeyes_white_dragon_ur, 0.78, "Yu-Gi-Oh!"));
    }

    public List<Carta> getCatalogo() { return catalogo; }
    public List<Carta> getColeccion() { return coleccion; }

    public void guardarCartaEnFirestore(Carta carta) {
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", carta.getNombre());
        data.put("juego", carta.getJuego());
        db.collection("users").document(userId).collection("coleccion").add(data);
    }

    private void cargarColeccionDesdeFirestore() {
        db.collection("users").document(userId).collection("coleccion")
                .get()
                .addOnSuccessListener(snapshots -> {
                    coleccion.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String nombre = doc.getString("nombre");
                        String juego = doc.getString("juego");
                        Carta carta = buscarEnCatalogo(nombre, juego);
                        if (carta != null) coleccion.add(carta);
                    }
                });
    }

    private Carta buscarEnCatalogo(String nombre, String juego) {
        for (Carta c : catalogo) {
            if (c.getNombre().equals(nombre) && c.getJuego().equals(juego)) return c;
        }
        return null;
    }
}
