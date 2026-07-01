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
        int p = R.drawable.placeholder_carta;
        catalogo.add(new Carta("\"A Case for K9\"", "Starlight Rare", p, 120.00, "Yu-Gi-Oh!", url(80181649)));
        catalogo.add(new Carta("\"A\" Cell Breeding Device", "Common", p, 0.20, "Yu-Gi-Oh!", url(34541863)));
        catalogo.add(new Carta("\"A\" Cell Incubator", "Common", p, 0.15, "Yu-Gi-Oh!", url(64163367)));
        catalogo.add(new Carta("\"A\" Cell Recombination Device", "Common", p, 0.25, "Yu-Gi-Oh!", url(91231901)));
        catalogo.add(new Carta("\"A\" Cell Scatter Burst", "Common", p, 0.10, "Yu-Gi-Oh!", url(73262676)));
        catalogo.add(new Carta("\"Infernoble Arms\" - Almace", "Secret Rare", p, 28.00, "Yu-Gi-Oh!", url(98319530)));
        catalogo.add(new Carta("\"Infernoble Arms\" - Durendal", "Super Rare", p, 5.00, "Yu-Gi-Oh!", url(37478723)));
        catalogo.add(new Carta("\"Infernoble Arms\" - Hauteclere", "Super Rare", p, 4.50, "Yu-Gi-Oh!", url(64867422)));
        catalogo.add(new Carta("\"Infernoble Arms\" - Joyeuse", "Super Rare", p, 3.50, "Yu-Gi-Oh!", url(90861137)));
        catalogo.add(new Carta("1st Movement Solo", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(44256816)));
        catalogo.add(new Carta("3-Hump Lacooda", "Common", p, 0.10, "Yu-Gi-Oh!", url(86988864)));
        catalogo.add(new Carta("30,000-Year White Turtle", "Common", p, 0.15, "Yu-Gi-Oh!", url(11714098)));
        catalogo.add(new Carta("4-Starred Ladybug of Doom", "Common", p, 0.20, "Yu-Gi-Oh!", url(83994646)));
        catalogo.add(new Carta("7", "Rare", p, 2.00, "Yu-Gi-Oh!", url(67048711)));
        catalogo.add(new Carta("7 Colored Fish", "Common", p, 0.25, "Yu-Gi-Oh!", url(23771716)));
        catalogo.add(new Carta("7 Completed", "Common", p, 0.30, "Yu-Gi-Oh!", url(86198326)));
        catalogo.add(new Carta("8-Claws Scorpion", "Common", p, 0.10, "Yu-Gi-Oh!", url(14261867)));
        catalogo.add(new Carta("A Bao A Qu, the Lightless Shadow", "Secret Rare", p, 35.00, "Yu-Gi-Oh!", url(4731783)));
        catalogo.add(new Carta("A Cat of Ill Omen", "Common", p, 0.20, "Yu-Gi-Oh!", url(24140059)));
        catalogo.add(new Carta("A Deal with Dark Ruler", "Common", p, 0.15, "Yu-Gi-Oh!", url(6850209)));
        catalogo.add(new Carta("A Feather of the Phoenix", "Super Rare", p, 6.00, "Yu-Gi-Oh!", url(49140998)));
        catalogo.add(new Carta("A Feint Plan", "Common", p, 0.10, "Yu-Gi-Oh!", url(68170903)));
        catalogo.add(new Carta("A Hero Emerges", "Common", p, 0.20, "Yu-Gi-Oh!", url(21597117)));
        catalogo.add(new Carta("A Hero Lives", "Common", p, 0.25, "Yu-Gi-Oh!", url(8949584)));
        catalogo.add(new Carta("A Legendary Ocean", "Common", p, 0.30, "Yu-Gi-Oh!", url(295517)));
        catalogo.add(new Carta("A Major Upset", "Rare", p, 1.50, "Yu-Gi-Oh!", url(32207100)));
        catalogo.add(new Carta("A Man with Wdjat", "Common", p, 0.10, "Yu-Gi-Oh!", url(51351302)));
        catalogo.add(new Carta("A Rival Appears!", "Common", p, 0.15, "Yu-Gi-Oh!", url(5728014)));
        catalogo.add(new Carta("A Shattered, Colorless Realm", "Common", p, 0.20, "Yu-Gi-Oh!", url(44553392)));
        catalogo.add(new Carta("A Wild Monster Appears!", "Secret Rare", p, 25.00, "Yu-Gi-Oh!", url(23587624)));
        catalogo.add(new Carta("A Wingbeat of Giant Dragon", "Common", p, 0.10, "Yu-Gi-Oh!", url(28596933)));
        catalogo.add(new Carta("A-Assault Core", "Secret Rare", p, 32.00, "Yu-Gi-Oh!", url(30012506)));
        catalogo.add(new Carta("A-Team: Trap Disposal Unit", "Rare", p, 1.00, "Yu-Gi-Oh!", url(13026402)));
        catalogo.add(new Carta("A-to-Z Energy Load", "Ultra Rare", p, 10.00, "Yu-Gi-Oh!", url(75926389)));
        catalogo.add(new Carta("A-to-Z-Dragon Buster Cannon", "Ultra Rare", p, 15.00, "Yu-Gi-Oh!", url(65172015)));
        catalogo.add(new Carta("A.I. Challenge You", "Common", p, 0.20, "Yu-Gi-Oh!", url(28645123)));
        catalogo.add(new Carta("A.I. Connect", "Super Rare", p, 3.50, "Yu-Gi-Oh!", url(79015062)));
        catalogo.add(new Carta("A.I. Contact", "Ultra Rare", p, 12.00, "Yu-Gi-Oh!", url(10493654)));
        catalogo.add(new Carta("A.I. Love Fusion", "Common", p, 0.25, "Yu-Gi-Oh!", url(59332125)));
        catalogo.add(new Carta("A.I. Meet You", "Common", p, 0.10, "Yu-Gi-Oh!", url(6552971)));
        catalogo.add(new Carta("A.I. Shadow", "Common", p, 0.15, "Yu-Gi-Oh!", url(77421977)));
        catalogo.add(new Carta("A.I.'s Ritual", "Common", p, 0.20, "Yu-Gi-Oh!", url(85327820)));
        catalogo.add(new Carta("A.I.'s Show", "Ultra Rare", p, 11.00, "Yu-Gi-Oh!", url(54374642)));
        catalogo.add(new Carta("A.I.dle Reborn", "Common", p, 0.10, "Yu-Gi-Oh!", url(22933016)));
        catalogo.add(new Carta("A.I.Q", "Common", p, 0.15, "Yu-Gi-Oh!", url(69381150)));
        catalogo.add(new Carta("A/D Changer", "Common", p, 0.20, "Yu-Gi-Oh!", url(96146814)));
        catalogo.add(new Carta("Abaki", "Common", p, 0.10, "Yu-Gi-Oh!", url(12694768)));
        catalogo.add(new Carta("Abare Ushioni", "Common", p, 0.15, "Yu-Gi-Oh!", url(89718302)));
        catalogo.add(new Carta("ABC-Dragon Buster", "Secret Rare", p, 40.00, "Yu-Gi-Oh!", url(1561110)));
        catalogo.add(new Carta("Abduction", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(21846145)));
        catalogo.add(new Carta("Abominable Chamber of the Unchained", "Common", p, 0.20, "Yu-Gi-Oh!", url(80801743)));
        catalogo.add(new Carta("Abominable Unchained Soul", "Super Rare", p, 5.00, "Yu-Gi-Oh!", url(1966438)));
        catalogo.add(new Carta("Abomination's Prison", "Ultra Rare", p, 13.00, "Yu-Gi-Oh!", url(27412542)));
        catalogo.add(new Carta("Absolute Axon Kicker", "Common", p, 0.10, "Yu-Gi-Oh!", url(80073414)));
        catalogo.add(new Carta("Absolute Crusader", "Super Rare", p, 3.50, "Yu-Gi-Oh!", url(70832512)));
        catalogo.add(new Carta("Absolute End", "Common", p, 0.15, "Yu-Gi-Oh!", url(27744077)));
        catalogo.add(new Carta("Absolute King - Megaplunder", "Common", p, 0.20, "Yu-Gi-Oh!", url(27553701)));
        catalogo.add(new Carta("Absolute King Back Jack", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(60990740)));
        catalogo.add(new Carta("Absolute Powerforce", "Common", p, 0.10, "Yu-Gi-Oh!", url(51779204)));
        catalogo.add(new Carta("Absorb Fusion", "Secret Rare", p, 28.00, "Yu-Gi-Oh!", url(71422989)));
        catalogo.add(new Carta("Absorbing Jar", "Rare", p, 1.50, "Yu-Gi-Oh!", url(3900605)));
        catalogo.add(new Carta("Absorbing Kid from the Sky", "Common", p, 0.15, "Yu-Gi-Oh!", url(49771608)));
        catalogo.add(new Carta("Absorouter Dragon", "Rare", p, 2.00, "Yu-Gi-Oh!", url(67748760)));
        catalogo.add(new Carta("Abyss Actor - Comic Relief", "Common", p, 0.20, "Yu-Gi-Oh!", url(15308295)));
        catalogo.add(new Carta("Abyss Actor - Curtain Raiser", "Ultra Rare", p, 15.00, "Yu-Gi-Oh!", url(44179224)));
        catalogo.add(new Carta("Abyss Actor - Evil Heel", "Secret Rare", p, 30.00, "Yu-Gi-Oh!", url(52240819)));
        catalogo.add(new Carta("Abyss Actor - Extras", "Super Rare", p, 5.00, "Yu-Gi-Oh!", url(88412339)));
        catalogo.add(new Carta("Abyss Actor - Funky Comedian", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(99634927)));
        catalogo.add(new Carta("Abyss Actor - Hyper Director", "Ultra Rare", p, 12.00, "Yu-Gi-Oh!", url(2368215)));
        catalogo.add(new Carta("Abyss Actor - Leading Lady", "Secret Rare", p, 35.00, "Yu-Gi-Oh!", url(24907044)));
        catalogo.add(new Carta("Abyss Actor - Liberty Dramatist", "Common", p, 0.25, "Yu-Gi-Oh!", url(65477143)));
        catalogo.add(new Carta("Abyss Actor - Mellow Madonna", "Common", p, 0.30, "Yu-Gi-Oh!", url(78310590)));
        catalogo.add(new Carta("Abyss Actor - Sassy Rookie", "Super Rare", p, 6.00, "Yu-Gi-Oh!", url(51028231)));
        catalogo.add(new Carta("Abyss Actor - Super Producer", "Common", p, 0.20, "Yu-Gi-Oh!", url(47404795)));
        catalogo.add(new Carta("Abyss Actor - Superstar", "Secret Rare", p, 28.00, "Yu-Gi-Oh!", url(25629622)));
        catalogo.add(new Carta("Abyss Actor - Trendy Understudy", "Common", p, 0.15, "Yu-Gi-Oh!", url(39024589)));
        catalogo.add(new Carta("Abyss Actor - Twinkle Little Star", "Common", p, 0.20, "Yu-Gi-Oh!", url(7279373)));
        catalogo.add(new Carta("Abyss Actor - Wild Hope", "Secret Rare", p, 32.00, "Yu-Gi-Oh!", url(51391183)));
        catalogo.add(new Carta("Abyss Actors Back Stage", "Super Rare", p, 4.50, "Yu-Gi-Oh!", url(59057953)));
        catalogo.add(new Carta("Abyss Actors' Curtain Call", "Common", p, 0.10, "Yu-Gi-Oh!", url(4682617)));
        catalogo.add(new Carta("Abyss Actors' Dress Rehearsal", "Super Rare", p, 5.00, "Yu-Gi-Oh!", url(6004133)));
        catalogo.add(new Carta("Abyss Dweller", "Ultra Rare", p, 18.00, "Yu-Gi-Oh!", url(21044178)));
        catalogo.add(new Carta("Abyss Flower", "Common", p, 0.10, "Yu-Gi-Oh!", url(40387124)));
        catalogo.add(new Carta("Abyss Keeper", "Common", p, 0.15, "Yu-Gi-Oh!", url(82184400)));
        catalogo.add(new Carta("Abyss of the Sacred Beasts", "Common", p, 0.20, "Yu-Gi-Oh!", url(86132414)));
        catalogo.add(new Carta("Abyss Playhouse - Fantastic Theater", "Common", p, 0.25, "Yu-Gi-Oh!", url(77297908)));
        catalogo.add(new Carta("Abyss Prop - Wild Wagon", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(86578200)));
        catalogo.add(new Carta("Abyss Script - Abysstainment", "Ultra Rare", p, 14.00, "Yu-Gi-Oh!", url(70564929)));
        catalogo.add(new Carta("Abyss Script - Dramatic Story", "Common", p, 0.10, "Yu-Gi-Oh!", url(33503878)));
        catalogo.add(new Carta("Abyss Script - Fantasy Magic", "Super Rare", p, 3.50, "Yu-Gi-Oh!", url(87390798)));
        catalogo.add(new Carta("Abyss Script - Fire Dragon's Lair", "Super Rare", p, 5.00, "Yu-Gi-Oh!", url(50179591)));
        catalogo.add(new Carta("Abyss Script - Opening Ceremony", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(23784496)));
        catalogo.add(new Carta("Abyss Script - Rise of the Abyss King", "Secret Rare", p, 30.00, "Yu-Gi-Oh!", url(13662809)));
        catalogo.add(new Carta("Abyss Script - Romantic Terror", "Common", p, 0.15, "Yu-Gi-Oh!", url(41803903)));
        catalogo.add(new Carta("Abyss Shark", "Ultra Rare", p, 16.00, "Yu-Gi-Oh!", url(61496006)));
        catalogo.add(new Carta("Abyss Soldier", "Rare", p, 2.00, "Yu-Gi-Oh!", url(18318842)));
        catalogo.add(new Carta("Abyss Stungray", "Common", p, 0.20, "Yu-Gi-Oh!", url(97232518)));
        catalogo.add(new Carta("Abyss Warrior", "Common", p, 0.25, "Yu-Gi-Oh!", url(88409165)));
        catalogo.add(new Carta("Abyss-scale of Cetus", "Common", p, 0.10, "Yu-Gi-Oh!", url(19596712)));
        catalogo.add(new Carta("Abyss-scale of the Kraken", "Common", p, 0.15, "Yu-Gi-Oh!", url(8719957)));
    }

    private String url(int cardId) {
        return "https://images.ygoprodeck.com/images/cards/" + cardId + ".jpg";
    }

    public List<Carta> getCatalogo() { return catalogo; }
    public List<Carta> getColeccion() { return coleccion; }

    public void eliminarCartaDeFirestore(Carta carta) {
        db.collection("users").document(userId).collection("coleccion")
                .whereEqualTo("nombre", carta.getNombre())
                .whereEqualTo("juego", carta.getJuego())
                .limit(1)
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (QueryDocumentSnapshot doc : snapshots) {
                        doc.getReference().delete();
                    }
                });
    }

    public void limpiarColeccionEnFirestore() {
        db.collection("users").document(userId).collection("coleccion")
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (QueryDocumentSnapshot doc : snapshots) {
                        doc.getReference().delete();
                    }
                });
    }

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
