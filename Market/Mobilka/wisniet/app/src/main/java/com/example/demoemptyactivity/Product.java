package com.example.demoemptyactivity;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Product {
    public final int id;
    public final String title;
    public final int price;
    public final String size;
    public final String brand;
    public final String category;
    public final String description;
    public final String seller;

    public Product(int id, String title, int price, String size, String brand,
                   String category, String description, String seller) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.size = size;
        this.brand = brand;
        this.category = category;
        this.description = description;
        this.seller = seller;
    }

    public String priceText() {
        return price + " zł";
    }

    public String metaText() {
        return size.equals("-") ? brand : size + " · " + brand;
    }

    // Szuka zdjęcia produkt_<id> w drawable (np. produkt_1.png). Zwraca 0, jeśli go nie ma.
    public int imageRes(Context c) {
        return c.getResources().getIdentifier("produkt_" + id, "drawable", c.getPackageName());
    }

    private static final List<Product> ALL = new ArrayList<>();

    static {
        ALL.add(new Product(1, "Kurtka jeansowa", 89, "M", "Levi's", "Kobiety",
                "Klasyczna kurtka jeansowa, noszona kilka razy. Stan bardzo dobry, bez wad.", "ania_k"));
        ALL.add(new Product(2, "Sneakersy białe", 120, "42", "Nike", "Mężczyźni",
                "Białe sneakersy, rozmiar 42. Noszone sezon, czyszczone, podeszwa w dobrym stanie.", "marek88"));
        ALL.add(new Product(3, "Sweter wełniany", 55, "S", "Zara", "Kobiety",
                "Ciepły sweter w kolorze écru. Nie mechaci się, bardzo miękki.", "kasia_w"));
        ALL.add(new Product(4, "Bluza z kapturem", 70, "L", "H&M", "Mężczyźni",
                "Czarna bluza z kapturem, lekko ocieplana. Bez śladów użytkowania.", "tomek_s"));
        ALL.add(new Product(5, "Kombinezon zimowy", 95, "92", "Reima", "Dzieci",
                "Kombinezon zimowy dla dziecka, wodoodporny. Używany jeden sezon.", "mama_oli"));
        ALL.add(new Product(6, "Słuchawki bezprzewodowe", 150, "-", "Sony", "Elektronika",
                "Słuchawki bluetooth z redukcją szumów. Sprawne, w zestawie etui i kabel.", "audio_pl"));
        ALL.add(new Product(7, "Sukienka letnia", 60, "M", "Mango", "Kobiety",
                "Lekka sukienka w kwiaty, idealna na lato. Noszona dwa razy.", "ania_k"));
        ALL.add(new Product(8, "Lampka biurkowa", 35, "-", "IKEA", "Dom",
                "Lampka biurkowa z regulowanym ramieniem. Działa bez zarzutu.", "dom_i_ogrod"));
        ALL.add(new Product(9, "Buty dziecięce", 40, "28", "Adidas", "Dzieci",
                "Buty sportowe dla dziecka, rozmiar 28. Lekko znoszone.", "mama_oli"));
        ALL.add(new Product(10, "Smartwatch", 180, "-", "Xiaomi", "Elektronika",
                "Smartwatch z pulsometrem i GPS. Bateria trzyma kilka dni, w zestawie ładowarka.", "audio_pl"));
    }

    public static List<Product> all() {
        return ALL;
    }

    public static Product findById(int id) {
        for (Product p : ALL) {
            if (p.id == id) return p;
        }
        return null;
    }
}