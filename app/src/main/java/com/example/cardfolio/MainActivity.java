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

        // 100 cartas Yu-Gi-Oh adicionales
        catalogo.add(new Carta("Abyss-scale of the Mizuchi", "Common", p, 0.50, "Yu-Gi-Oh!", url(72932673)));
        catalogo.add(new Carta("Abyss-scorn", "Common", p, 0.15, "Yu-Gi-Oh!", url(79206750)));
        catalogo.add(new Carta("Abyss-sphere", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(60202749)));
        catalogo.add(new Carta("Abyss-squall", "Common", p, 0.20, "Yu-Gi-Oh!", url(34707034)));
        catalogo.add(new Carta("Abyss-steam", "Common", p, 0.15, "Yu-Gi-Oh!", url(63941169)));
        catalogo.add(new Carta("Abyss-sting Triaina", "Super Rare", p, 3.00, "Yu-Gi-Oh!", url(81878201)));
        catalogo.add(new Carta("Abyss-strom", "Common", p, 0.15, "Yu-Gi-Oh!", url(97697447)));
        catalogo.add(new Carta("Abyssal Designator", "Common", p, 0.20, "Yu-Gi-Oh!", url(89801755)));
        catalogo.add(new Carta("Abyssal Kingshark", "Common", p, 0.25, "Yu-Gi-Oh!", url(44223284)));
        catalogo.add(new Carta("Abyssrhine, the Atlantean Spirit", "Rare", p, 1.50, "Yu-Gi-Oh!", url(17080584)));
        catalogo.add(new Carta("Abysstrite, the Atlantean Spirit", "Rare", p, 2.00, "Yu-Gi-Oh!", url(9453320)));
        catalogo.add(new Carta("Accel Synchro Stardust Dragon", "Ultra Rare", p, 12.00, "Yu-Gi-Oh!", url(30983281)));
        catalogo.add(new Carta("Accel Synchron", "Super Rare", p, 3.50, "Yu-Gi-Oh!", url(37675907)));
        catalogo.add(new Carta("Accellight", "Common", p, 0.15, "Yu-Gi-Oh!", url(34838437)));
        catalogo.add(new Carta("Accesscode Talker", "Secret Rare", p, 40.00, "Yu-Gi-Oh!", url(86066372)));
        catalogo.add(new Carta("Accumulated Fortune", "Common", p, 0.15, "Yu-Gi-Oh!", url(98444741)));
        catalogo.add(new Carta("Achacha Archer", "Common", p, 0.20, "Yu-Gi-Oh!", url(98865920)));
        catalogo.add(new Carta("Achacha Chanbara", "Common", p, 0.15, "Yu-Gi-Oh!", url(62476197)));
        catalogo.add(new Carta("Achichi @Ignister", "Common", p, 0.15, "Yu-Gi-Oh!", url(15808381)));
        catalogo.add(new Carta("Acid Crawler", "Common", p, 0.10, "Yu-Gi-Oh!", url(77568553)));
        catalogo.add(new Carta("Acid Rain", "Common", p, 0.15, "Yu-Gi-Oh!", url(21323861)));
        catalogo.add(new Carta("Acid Trap Hole", "Common", p, 0.20, "Yu-Gi-Oh!", url(41356845)));
        catalogo.add(new Carta("Acidic Downpour", "Common", p, 0.15, "Yu-Gi-Oh!", url(35956022)));
        catalogo.add(new Carta("Acorno", "Common", p, 0.10, "Yu-Gi-Oh!", url(21051977)));
        catalogo.add(new Carta("Acrobat Monkey", "Common", p, 0.10, "Yu-Gi-Oh!", url(47372349)));
        catalogo.add(new Carta("Acrobatic Magician", "Common", p, 0.15, "Yu-Gi-Oh!", url(33656832)));
        catalogo.add(new Carta("Ad Libitum of Despia", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(81555617)));
        catalogo.add(new Carta("Adamancipator Analyzer", "Common", p, 0.20, "Yu-Gi-Oh!", url(11302671)));
        catalogo.add(new Carta("Adamancipator Crystal - Dragite", "Super Rare", p, 3.50, "Yu-Gi-Oh!", url(10286023)));
        catalogo.add(new Carta("Adamancipator Crystal - Leonite", "Super Rare", p, 3.00, "Yu-Gi-Oh!", url(47897376)));
        catalogo.add(new Carta("Adamancipator Crystal - Raptite", "Common", p, 0.30, "Yu-Gi-Oh!", url(74891384)));
        catalogo.add(new Carta("Adamancipator Friends", "Common", p, 0.15, "Yu-Gi-Oh!", url(99927991)));
        catalogo.add(new Carta("Adamancipator Relief", "Common", p, 0.20, "Yu-Gi-Oh!", url(9341993)));
        catalogo.add(new Carta("Adamancipator Researcher", "Super Rare", p, 5.00, "Yu-Gi-Oh!", url(85914562)));
        catalogo.add(new Carta("Adamancipator Resonance", "Common", p, 0.25, "Yu-Gi-Oh!", url(45730592)));
        catalogo.add(new Carta("Adamancipator Risen - Dragite", "Ultra Rare", p, 10.00, "Yu-Gi-Oh!", url(9464441)));
        catalogo.add(new Carta("Adamancipator Risen - Leonite", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(47674738)));
        catalogo.add(new Carta("Adamancipator Risen - Raptite", "Super Rare", p, 3.50, "Yu-Gi-Oh!", url(73079836)));
        catalogo.add(new Carta("Adamancipator Seeker", "Common", p, 0.30, "Yu-Gi-Oh!", url(48519867)));
        catalogo.add(new Carta("Adamancipator Signs", "Common", p, 0.20, "Yu-Gi-Oh!", url(72957245)));
        catalogo.add(new Carta("Adhesion Trap Hole", "Common", p, 0.20, "Yu-Gi-Oh!", url(62325062)));
        catalogo.add(new Carta("Adhesive Explosive", "Common", p, 0.10, "Yu-Gi-Oh!", url(53828396)));
        catalogo.add(new Carta("Adreus, Keeper of Armageddon", "Super Rare", p, 4.50, "Yu-Gi-Oh!", url(94119480)));
        catalogo.add(new Carta("Advance Draw", "Common", p, 0.20, "Yu-Gi-Oh!", url(51630558)));
        catalogo.add(new Carta("Advance Zone", "Common", p, 0.20, "Yu-Gi-Oh!", url(76224717)));
        catalogo.add(new Carta("Advanced Crystal Beast Amber Mammoth", "Super Rare", p, 3.00, "Yu-Gi-Oh!", url(18847598)));
        catalogo.add(new Carta("Advanced Crystal Beast Amethyst Cat", "Super Rare", p, 3.00, "Yu-Gi-Oh!", url(19963185)));
        catalogo.add(new Carta("Advanced Crystal Beast Cobalt Eagle", "Super Rare", p, 3.50, "Yu-Gi-Oh!", url(45236142)));
        catalogo.add(new Carta("Advanced Crystal Beast Emerald Tortoise", "Super Rare", p, 3.00, "Yu-Gi-Oh!", url(46358784)));
        catalogo.add(new Carta("Advanced Crystal Beast Ruby Carbuncle", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(83575471)));
        catalogo.add(new Carta("Advanced Crystal Beast Sapphire Pegasus", "Super Rare", p, 5.00, "Yu-Gi-Oh!", url(71620241)));
        catalogo.add(new Carta("Advanced Crystal Beast Topaz Tiger", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(72843899)));
        catalogo.add(new Carta("Advanced Dark", "Ultra Rare", p, 10.00, "Yu-Gi-Oh!", url(12644061)));
        catalogo.add(new Carta("Advanced Heraldry Art", "Ultra Rare", p, 8.00, "Yu-Gi-Oh!", url(61314842)));
        catalogo.add(new Carta("Advanced Ritual Art", "Common", p, 0.50, "Yu-Gi-Oh!", url(46052429)));
        catalogo.add(new Carta("Aegaion the Sea Castrum", "Common", p, 0.20, "Yu-Gi-Oh!", url(10678778)));
        catalogo.add(new Carta("Aegis of Gaia", "Common", p, 0.15, "Yu-Gi-Oh!", url(47060347)));
        catalogo.add(new Carta("Aegis of the Ocean Dragon Lord", "Common", p, 0.20, "Yu-Gi-Oh!", url(7935043)));
        catalogo.add(new Carta("Aerial Eater", "Common", p, 0.10, "Yu-Gi-Oh!", url(28143384)));
        catalogo.add(new Carta("Aerial Recharge", "Common", p, 0.15, "Yu-Gi-Oh!", url(70875955)));
        catalogo.add(new Carta("After the Struggle", "Common", p, 0.15, "Yu-Gi-Oh!", url(25345186)));
        catalogo.add(new Carta("Afterglow", "Common", p, 0.10, "Yu-Gi-Oh!", url(43575579)));
        catalogo.add(new Carta("Against the Wind", "Common", p, 0.15, "Yu-Gi-Oh!", url(64952266)));
        catalogo.add(new Carta("Agave Dragon", "Rare", p, 1.50, "Yu-Gi-Oh!", url(2411269)));
        catalogo.add(new Carta("Agido the Ancient Sentinel", "Super Rare", p, 3.50, "Yu-Gi-Oh!", url(62320425)));
        catalogo.add(new Carta("Ahrima, the Wicked Warden", "Common", p, 0.20, "Yu-Gi-Oh!", url(86377375)));
        catalogo.add(new Carta("Air Armor Ninja", "Common", p, 0.15, "Yu-Gi-Oh!", url(69023354)));
        catalogo.add(new Carta("Airknight Parshath", "Super Rare", p, 3.00, "Yu-Gi-Oh!", url(18036057)));
        catalogo.add(new Carta("Airorca", "Common", p, 0.15, "Yu-Gi-Oh!", url(84747429)));
        catalogo.add(new Carta("Aitsu", "Common", p, 0.10, "Yu-Gi-Oh!", url(48202661)));
        catalogo.add(new Carta("Aiwass, Divine Spirit of the Law", "Ultra Rare", p, 8.00, "Yu-Gi-Oh!", url(84288367)));
        catalogo.add(new Carta("Aiwass, the Magistus Spell Spirit", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(35877582)));
        catalogo.add(new Carta("Akashic Magician", "Ultra Rare", p, 9.00, "Yu-Gi-Oh!", url(28776350)));
        catalogo.add(new Carta("Akz, the Pumer", "Common", p, 0.15, "Yu-Gi-Oh!", url(38837163)));
        catalogo.add(new Carta("Al-Lumi'raj", "Common", p, 0.15, "Yu-Gi-Oh!", url(25795273)));
        catalogo.add(new Carta("Alba-Lenatus the Abyss Dragon", "Secret Rare", p, 32.00, "Yu-Gi-Oh!", url(3410461)));
        catalogo.add(new Carta("Albaz the Ashen", "Ultra Rare", p, 10.00, "Yu-Gi-Oh!", url(37683547)));
        catalogo.add(new Carta("Albion the Branded Dragon", "Secret Rare", p, 28.00, "Yu-Gi-Oh!", url(87746184)));
        catalogo.add(new Carta("Albion the Sanctifire Dragon", "Ultra Rare", p, 15.00, "Yu-Gi-Oh!", url(38811586)));
        catalogo.add(new Carta("Alchemy Beast - Alkahest Leon", "Common", p, 0.15, "Yu-Gi-Oh!", url(89095175)));
        catalogo.add(new Carta("Alchemy Cycle", "Common", p, 0.20, "Yu-Gi-Oh!", url(28299043)));
        catalogo.add(new Carta("Alchemic Magician", "Super Rare", p, 3.00, "Yu-Gi-Oh!", url(26828788)));
        catalogo.add(new Carta("Aleister the Invoker", "Super Rare", p, 5.00, "Yu-Gi-Oh!", url(86120751)));
        catalogo.add(new Carta("Aleister the Invoker of Madness", "Secret Rare", p, 22.00, "Yu-Gi-Oh!", url(77490226)));
        catalogo.add(new Carta("Alien Ammonite", "Common", p, 0.15, "Yu-Gi-Oh!", url(34541863)));
        catalogo.add(new Carta("Alien Dog", "Common", p, 0.15, "Yu-Gi-Oh!", url(65228957)));
        catalogo.add(new Carta("Alien Hypno", "Common", p, 0.20, "Yu-Gi-Oh!", url(39892835)));
        catalogo.add(new Carta("Alien Mars", "Common", p, 0.10, "Yu-Gi-Oh!", url(32303804)));
        catalogo.add(new Carta("Alien Mother", "Rare", p, 1.00, "Yu-Gi-Oh!", url(49491927)));
        catalogo.add(new Carta("Alien Overlord", "Rare", p, 1.50, "Yu-Gi-Oh!", url(74601967)));
        catalogo.add(new Carta("Alien Psychic", "Common", p, 0.10, "Yu-Gi-Oh!", url(64353454)));
        catalogo.add(new Carta("Alien Skull", "Common", p, 0.15, "Yu-Gi-Oh!", url(90009107)));
        catalogo.add(new Carta("Alien Shocktrooper", "Common", p, 0.10, "Yu-Gi-Oh!", url(71645792)));
        catalogo.add(new Carta("Alien Warrior", "Common", p, 0.15, "Yu-Gi-Oh!", url(94510015)));
        catalogo.add(new Carta("Allure of Darkness", "Super Rare", p, 4.00, "Yu-Gi-Oh!", url(1475311)));
        catalogo.add(new Carta("Alligator's Sword", "Common", p, 0.10, "Yu-Gi-Oh!", url(64428736)));
        catalogo.add(new Carta("Ally of Justice Catastor", "Ultra Rare", p, 11.00, "Yu-Gi-Oh!", url(26593852)));

        // 100 cartas Pokémon
        catalogo.add(new Carta("Bulbasaur", "Common", p, 0.25, "Pokémon", urlPoke(1)));
        catalogo.add(new Carta("Ivysaur", "Uncommon", p, 0.75, "Pokémon", urlPoke(2)));
        catalogo.add(new Carta("Venusaur ex", "Double Rare", p, 8.00, "Pokémon", urlPoke(3)));
        catalogo.add(new Carta("Charmander", "Common", p, 1.00, "Pokémon", urlPoke(4)));
        catalogo.add(new Carta("Charmeleon", "Uncommon", p, 1.50, "Pokémon", urlPoke(5)));
        catalogo.add(new Carta("Charizard ex", "Double Rare", p, 45.00, "Pokémon", urlPoke(6)));
        catalogo.add(new Carta("Squirtle", "Common", p, 0.75, "Pokémon", urlPoke(7)));
        catalogo.add(new Carta("Wartortle", "Uncommon", p, 0.75, "Pokémon", urlPoke(8)));
        catalogo.add(new Carta("Blastoise ex", "Double Rare", p, 12.00, "Pokémon", urlPoke(9)));
        catalogo.add(new Carta("Caterpie", "Common", p, 0.15, "Pokémon", urlPoke(10)));
        catalogo.add(new Carta("Metapod", "Common", p, 0.15, "Pokémon", urlPoke(11)));
        catalogo.add(new Carta("Butterfree", "Rare", p, 1.50, "Pokémon", urlPoke(12)));
        catalogo.add(new Carta("Weedle", "Common", p, 0.15, "Pokémon", urlPoke(13)));
        catalogo.add(new Carta("Kakuna", "Common", p, 0.15, "Pokémon", urlPoke(14)));
        catalogo.add(new Carta("Beedrill", "Rare", p, 2.00, "Pokémon", urlPoke(15)));
        catalogo.add(new Carta("Pidgey", "Common", p, 0.15, "Pokémon", urlPoke(16)));
        catalogo.add(new Carta("Pidgeotto", "Uncommon", p, 0.50, "Pokémon", urlPoke(17)));
        catalogo.add(new Carta("Pidgeot ex", "Double Rare", p, 6.00, "Pokémon", urlPoke(18)));
        catalogo.add(new Carta("Rattata", "Common", p, 0.15, "Pokémon", urlPoke(19)));
        catalogo.add(new Carta("Raticate", "Uncommon", p, 0.50, "Pokémon", urlPoke(20)));
        catalogo.add(new Carta("Spearow", "Common", p, 0.15, "Pokémon", urlPoke(21)));
        catalogo.add(new Carta("Fearow", "Uncommon", p, 0.50, "Pokémon", urlPoke(22)));
        catalogo.add(new Carta("Ekans", "Common", p, 0.15, "Pokémon", urlPoke(23)));
        catalogo.add(new Carta("Arbok", "Uncommon", p, 0.75, "Pokémon", urlPoke(24)));
        catalogo.add(new Carta("Pikachu", "Common", p, 2.00, "Pokémon", urlPoke(25)));
        catalogo.add(new Carta("Raichu", "Rare", p, 3.00, "Pokémon", urlPoke(26)));
        catalogo.add(new Carta("Sandshrew", "Common", p, 0.15, "Pokémon", urlPoke(27)));
        catalogo.add(new Carta("Sandslash", "Uncommon", p, 0.50, "Pokémon", urlPoke(28)));
        catalogo.add(new Carta("Nidoran♀", "Common", p, 0.20, "Pokémon", urlPoke(29)));
        catalogo.add(new Carta("Nidorina", "Uncommon", p, 0.50, "Pokémon", urlPoke(30)));
        catalogo.add(new Carta("Nidoqueen ex", "Double Rare", p, 5.00, "Pokémon", urlPoke(31)));
        catalogo.add(new Carta("Nidoran♂", "Common", p, 0.20, "Pokémon", urlPoke(32)));
        catalogo.add(new Carta("Nidorino", "Uncommon", p, 0.50, "Pokémon", urlPoke(33)));
        catalogo.add(new Carta("Nidoking ex", "Double Rare", p, 5.00, "Pokémon", urlPoke(34)));
        catalogo.add(new Carta("Clefairy", "Common", p, 0.50, "Pokémon", urlPoke(35)));
        catalogo.add(new Carta("Clefable", "Rare", p, 2.00, "Pokémon", urlPoke(36)));
        catalogo.add(new Carta("Vulpix", "Common", p, 0.50, "Pokémon", urlPoke(37)));
        catalogo.add(new Carta("Ninetales", "Rare", p, 2.00, "Pokémon", urlPoke(38)));
        catalogo.add(new Carta("Jigglypuff", "Common", p, 0.50, "Pokémon", urlPoke(39)));
        catalogo.add(new Carta("Wigglytuff", "Uncommon", p, 0.75, "Pokémon", urlPoke(40)));
        catalogo.add(new Carta("Zubat", "Common", p, 0.15, "Pokémon", urlPoke(41)));
        catalogo.add(new Carta("Golbat", "Uncommon", p, 0.50, "Pokémon", urlPoke(42)));
        catalogo.add(new Carta("Oddish", "Common", p, 0.20, "Pokémon", urlPoke(43)));
        catalogo.add(new Carta("Gloom", "Uncommon", p, 0.40, "Pokémon", urlPoke(44)));
        catalogo.add(new Carta("Vileplume", "Rare", p, 1.50, "Pokémon", urlPoke(45)));
        catalogo.add(new Carta("Paras", "Common", p, 0.15, "Pokémon", urlPoke(46)));
        catalogo.add(new Carta("Parasect", "Uncommon", p, 0.40, "Pokémon", urlPoke(47)));
        catalogo.add(new Carta("Venonat", "Common", p, 0.15, "Pokémon", urlPoke(48)));
        catalogo.add(new Carta("Venomoth", "Uncommon", p, 0.50, "Pokémon", urlPoke(49)));
        catalogo.add(new Carta("Diglett", "Common", p, 0.15, "Pokémon", urlPoke(50)));
        catalogo.add(new Carta("Dugtrio", "Rare", p, 1.50, "Pokémon", urlPoke(51)));
        catalogo.add(new Carta("Meowth", "Common", p, 0.50, "Pokémon", urlPoke(52)));
        catalogo.add(new Carta("Persian", "Uncommon", p, 0.75, "Pokémon", urlPoke(53)));
        catalogo.add(new Carta("Psyduck", "Common", p, 0.50, "Pokémon", urlPoke(54)));
        catalogo.add(new Carta("Golduck", "Uncommon", p, 0.75, "Pokémon", urlPoke(55)));
        catalogo.add(new Carta("Mankey", "Common", p, 0.15, "Pokémon", urlPoke(56)));
        catalogo.add(new Carta("Primeape", "Uncommon", p, 0.50, "Pokémon", urlPoke(57)));
        catalogo.add(new Carta("Growlithe", "Common", p, 0.25, "Pokémon", urlPoke(58)));
        catalogo.add(new Carta("Arcanine ex", "Double Rare", p, 6.00, "Pokémon", urlPoke(59)));
        catalogo.add(new Carta("Poliwag", "Common", p, 0.15, "Pokémon", urlPoke(60)));
        catalogo.add(new Carta("Poliwhirl", "Uncommon", p, 0.40, "Pokémon", urlPoke(61)));
        catalogo.add(new Carta("Poliwrath", "Rare", p, 1.50, "Pokémon", urlPoke(62)));
        catalogo.add(new Carta("Abra", "Common", p, 0.50, "Pokémon", urlPoke(63)));
        catalogo.add(new Carta("Kadabra", "Uncommon", p, 1.00, "Pokémon", urlPoke(64)));
        catalogo.add(new Carta("Alakazam ex", "Double Rare", p, 10.00, "Pokémon", urlPoke(65)));
        catalogo.add(new Carta("Machop", "Common", p, 0.15, "Pokémon", urlPoke(66)));
        catalogo.add(new Carta("Machoke", "Uncommon", p, 0.40, "Pokémon", urlPoke(67)));
        catalogo.add(new Carta("Machamp ex", "Double Rare", p, 8.00, "Pokémon", urlPoke(68)));
        catalogo.add(new Carta("Bellsprout", "Common", p, 0.15, "Pokémon", urlPoke(69)));
        catalogo.add(new Carta("Weepinbell", "Uncommon", p, 0.40, "Pokémon", urlPoke(70)));
        catalogo.add(new Carta("Victreebel", "Rare", p, 1.50, "Pokémon", urlPoke(71)));
        catalogo.add(new Carta("Tentacool", "Common", p, 0.15, "Pokémon", urlPoke(72)));
        catalogo.add(new Carta("Tentacruel", "Uncommon", p, 0.50, "Pokémon", urlPoke(73)));
        catalogo.add(new Carta("Geodude", "Common", p, 0.15, "Pokémon", urlPoke(74)));
        catalogo.add(new Carta("Graveler", "Uncommon", p, 0.40, "Pokémon", urlPoke(75)));
        catalogo.add(new Carta("Golem ex", "Double Rare", p, 5.00, "Pokémon", urlPoke(76)));
        catalogo.add(new Carta("Ponyta", "Common", p, 0.25, "Pokémon", urlPoke(77)));
        catalogo.add(new Carta("Rapidash", "Uncommon", p, 0.75, "Pokémon", urlPoke(78)));
        catalogo.add(new Carta("Slowpoke", "Common", p, 0.25, "Pokémon", urlPoke(79)));
        catalogo.add(new Carta("Slowbro ex", "Double Rare", p, 7.00, "Pokémon", urlPoke(80)));
        catalogo.add(new Carta("Magnemite", "Common", p, 0.15, "Pokémon", urlPoke(81)));
        catalogo.add(new Carta("Magneton", "Uncommon", p, 0.50, "Pokémon", urlPoke(82)));
        catalogo.add(new Carta("Farfetch'd", "Common", p, 0.50, "Pokémon", urlPoke(83)));
        catalogo.add(new Carta("Doduo", "Common", p, 0.15, "Pokémon", urlPoke(84)));
        catalogo.add(new Carta("Dodrio", "Uncommon", p, 0.40, "Pokémon", urlPoke(85)));
        catalogo.add(new Carta("Seel", "Common", p, 0.20, "Pokémon", urlPoke(86)));
        catalogo.add(new Carta("Dewgong", "Uncommon", p, 0.50, "Pokémon", urlPoke(87)));
        catalogo.add(new Carta("Grimer", "Common", p, 0.15, "Pokémon", urlPoke(88)));
        catalogo.add(new Carta("Muk ex", "Double Rare", p, 5.00, "Pokémon", urlPoke(89)));
        catalogo.add(new Carta("Shellder", "Common", p, 0.15, "Pokémon", urlPoke(90)));
        catalogo.add(new Carta("Cloyster", "Rare", p, 1.50, "Pokémon", urlPoke(91)));
        catalogo.add(new Carta("Gastly", "Common", p, 0.25, "Pokémon", urlPoke(92)));
        catalogo.add(new Carta("Haunter", "Uncommon", p, 0.75, "Pokémon", urlPoke(93)));
        catalogo.add(new Carta("Gengar ex", "Double Rare", p, 18.00, "Pokémon", urlPoke(94)));
        catalogo.add(new Carta("Onix", "Common", p, 0.25, "Pokémon", urlPoke(95)));
        catalogo.add(new Carta("Drowzee", "Common", p, 0.15, "Pokémon", urlPoke(96)));
        catalogo.add(new Carta("Hypno", "Uncommon", p, 0.50, "Pokémon", urlPoke(97)));
        catalogo.add(new Carta("Krabby", "Common", p, 0.15, "Pokémon", urlPoke(98)));
        catalogo.add(new Carta("Kingler", "Uncommon", p, 0.40, "Pokémon", urlPoke(99)));
        catalogo.add(new Carta("Voltorb", "Common", p, 0.15, "Pokémon", urlPoke(100)));
    }

    private String urlPoke(int number) {
        return "https://images.pokemontcg.io/sv3pt5/" + number + ".png";
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
