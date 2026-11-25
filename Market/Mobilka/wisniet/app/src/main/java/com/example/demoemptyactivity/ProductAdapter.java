package com.example.demoemptyactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.Holder> {

    public interface Listener {
        void onProductClick(Product product);

        void onHeartClick(Product product);
    }

    private final Context context;
    private final Listener listener;
    private List<Product> items = new ArrayList<>();

    public ProductAdapter(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setItems(List<Product> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Product p = items.get(position);

        h.price.setText(p.priceText());
        h.title.setText(p.title);
        h.meta.setText(p.metaText());

        int res = p.imageRes(context);
        if (res != 0) {
            h.img.setImageResource(res);
            h.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            h.img.setPadding(0, 0, 0, 0);
            h.img.setAlpha(1f);
        } else {
            int pad = (int) (24 * context.getResources().getDisplayMetrics().density);
            h.img.setImageResource(R.drawable.tlo_wisnia);
            h.img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            h.img.setPadding(pad, pad, pad, pad);
            h.img.setAlpha(0.7f);
        }

        h.heart.setImageResource(UserStore.isFavorite(context, p.id)
                ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        h.itemView.setOnClickListener(v -> listener.onProductClick(p));
        h.heart.setOnClickListener(v -> listener.onHeartClick(p));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView img, heart;
        TextView price, title, meta;

        Holder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.imgProduct);
            heart = v.findViewById(R.id.btnHeart);
            price = v.findViewById(R.id.textPrice);
            title = v.findViewById(R.id.textTitle);
            meta = v.findViewById(R.id.textMeta);
        }
    }
}