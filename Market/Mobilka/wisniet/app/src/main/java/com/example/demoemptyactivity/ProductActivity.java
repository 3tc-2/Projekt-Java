package com.example.demoemptyactivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProductActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        Product p = Product.findById(getIntent().getIntExtra("product_id", -1));
        if (p == null) {
            finish();
            return;
        }

        ImageView img = findViewById(R.id.imgProduct);
        int res = p.imageRes(this);
        if (res != 0) {
            img.setImageResource(res);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setPadding(0, 0, 0, 0);
            img.setAlpha(1f);
        }

        ((TextView) findViewById(R.id.textPrice)).setText(p.priceText());
        ((TextView) findViewById(R.id.textTitle)).setText(p.title);
        ((TextView) findViewById(R.id.textMeta)).setText(p.metaText() + " · " + p.category);
        ((TextView) findViewById(R.id.textDescription)).setText(p.description);
        ((TextView) findViewById(R.id.textSeller)).setText("Sprzedający: " + p.seller);

        ImageView heart = findViewById(R.id.btnHeart);
        heart.setImageResource(UserStore.isFavorite(this, p.id)
                ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        heart.setOnClickListener(v -> {
            boolean fav = UserStore.toggleFavorite(this, p.id);
            heart.setImageResource(fav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnMessage).setOnClickListener(v ->
                Toast.makeText(this, "Wiadomość do " + p.seller + " już wkrótce",
                        Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnBuy).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Kup teraz")
                        .setMessage("Kupić \"" + p.title + "\" za " + p.priceText() + "?")
                        .setPositiveButton("Kup", (dialog, which) -> {
                            Toast.makeText(this, "Zamówienie złożone!", Toast.LENGTH_LONG).show();
                            finish();
                        })
                        .setNegativeButton("Anuluj", null)
                        .show());
    }
}