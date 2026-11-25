package com.example.demoemptyactivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShoppingActivity extends AppCompatActivity {

    private static final String[] CATEGORIES =
            {"Wszystko", "Kobiety", "Mężczyźni", "Dzieci", "Elektronika", "Dom"};

    private static final int RED = Color.parseColor("#CC3D49");
    private static final int INACTIVE = Color.parseColor("#A58D8F");
    private static final int TEXT = Color.parseColor("#D9D9D9");

    private ProductAdapter adapter;
    private LinearLayout chipContainer;
    private TextView sectionTitle, emptyText, labelHome, labelFav;
    private ImageView iconHome, iconFav;

    private String category = "Wszystko";
    private String query = "";
    private boolean showFavorites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        chipContainer = findViewById(R.id.chipContainer);
        sectionTitle = findViewById(R.id.sectionTitle);
        emptyText = findViewById(R.id.emptyText);
        iconHome = findViewById(R.id.iconHome);
        iconFav = findViewById(R.id.iconFav);
        labelHome = findViewById(R.id.labelHome);
        labelFav = findViewById(R.id.labelFav);

        RecyclerView productList = findViewById(R.id.productList);
        productList.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(this, new ProductAdapter.Listener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(ShoppingActivity.this, ProductActivity.class);
                intent.putExtra("product_id", product.id);
                startActivity(intent);
            }

            @Override
            public void onHeartClick(Product product) {
                UserStore.toggleFavorite(ShoppingActivity.this, product.id);
                refresh();
            }
        });
        productList.setAdapter(adapter);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString().trim().toLowerCase();
                refresh();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.navHome).setOnClickListener(v -> {
            showFavorites = false;
            refresh();
        });
        findViewById(R.id.navFav).setOnClickListener(v -> {
            showFavorites = true;
            refresh();
        });
        findViewById(R.id.navSell).setOnClickListener(v ->
                Toast.makeText(this, "Dodawanie ogłoszeń już wkrótce", Toast.LENGTH_SHORT).show());
        findViewById(R.id.navMsg).setOnClickListener(v ->
                Toast.makeText(this, "Wiadomości już wkrótce", Toast.LENGTH_SHORT).show());
        findViewById(R.id.navProfile).setOnClickListener(v -> showProfile());

        buildChips();
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            refresh();
        }
    }

    private void buildChips() {
        chipContainer.removeAllViews();
        float d = getResources().getDisplayMetrics().density;

        for (String cat : CATEGORIES) {
            boolean selected = cat.equals(category);

            TextView chip = new TextView(this);
            chip.setText(cat);
            chip.setTextSize(14);
            chip.setPadding((int) (16 * d), (int) (8 * d), (int) (16 * d), (int) (8 * d));
            chip.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip);
            chip.setTextColor(TEXT);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd((int) (8 * d));
            chip.setLayoutParams(lp);

            chip.setOnClickListener(v -> {
                category = cat;
                buildChips();
                refresh();
            });
            chipContainer.addView(chip);
        }
    }

    private void refresh() {
        Set<String> favs = UserStore.getFavorites(this);
        List<Product> result = new ArrayList<>();

        for (Product p : Product.all()) {
            if (showFavorites && !favs.contains(String.valueOf(p.id))) continue;
            if (!category.equals("Wszystko") && !p.category.equals(category)) continue;
            if (!query.isEmpty()
                    && !p.title.toLowerCase().contains(query)
                    && !p.brand.toLowerCase().contains(query)) continue;
            result.add(p);
        }

        adapter.setItems(result);
        sectionTitle.setText(showFavorites ? "Ulubione" : "Dla Ciebie");

        if (result.isEmpty()) {
            emptyText.setText(showFavorites && query.isEmpty() && category.equals("Wszystko")
                    ? "Nie masz jeszcze ulubionych.\nKliknij serduszko przy produkcie."
                    : "Brak wyników");
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }

        updateNav();
    }

    private void updateNav() {
        iconHome.setColorFilter(showFavorites ? INACTIVE : RED);
        labelHome.setTextColor(showFavorites ? INACTIVE : RED);
        iconFav.setColorFilter(showFavorites ? RED : INACTIVE);
        labelFav.setTextColor(showFavorites ? RED : INACTIVE);
    }

    private void showProfile() {
        String login = UserStore.getCurrentUser(this);
        String name = UserStore.getName(this, login);
        String email = UserStore.getEmail(this, login);

        new AlertDialog.Builder(this)
                .setTitle(name)
                .setMessage("Login: " + login + "\nE-mail: " + email)
                .setPositiveButton("Wyloguj", (dialog, which) -> {
                    UserStore.logout(this);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Zamknij", null)
                .show();
    }
}