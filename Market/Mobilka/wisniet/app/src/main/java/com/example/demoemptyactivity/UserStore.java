package com.example.demoemptyactivity;

import android.content.Context;
import android.content.SharedPreferences;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

public class UserStore {
    private static final String PREFS = "wisniet_prefs";
    private static final String KEY_CURRENT = "current_user";

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return text;
        }
    }

    private static String key(String login) {
        return "user_" + login.toLowerCase();
    }

    // Zwraca false, jeśli login jest już zajęty
    public static boolean register(Context c, String login, String password,
                                   String imie, String nazwisko, String email) {
        String k = key(login);
        if (prefs(c).contains(k + "_pass")) {
            return false;
        }
        prefs(c).edit()
                .putString(k + "_pass", hash(password))
                .putString(k + "_name", imie + " " + nazwisko)
                .putString(k + "_email", email)
                .apply();
        return true;
    }

    public static boolean checkLogin(Context c, String login, String password) {
        String saved = prefs(c).getString(key(login) + "_pass", null);
        return saved != null && saved.equals(hash(password));
    }

    public static void setCurrentUser(Context c, String login) {
        prefs(c).edit().putString(KEY_CURRENT, login.toLowerCase()).apply();
    }

    public static String getCurrentUser(Context c) {
        return prefs(c).getString(KEY_CURRENT, null);
    }

    public static void logout(Context c) {
        prefs(c).edit().remove(KEY_CURRENT).apply();
    }

    public static String getName(Context c, String login) {
        return prefs(c).getString(key(login) + "_name", "Użytkownik");
    }

    public static String getEmail(Context c, String login) {
        return prefs(c).getString(key(login) + "_email", "-");
    }

    public static Set<String> getFavorites(Context c) {
        return new HashSet<>(prefs(c).getStringSet("fav_" + getCurrentUser(c), new HashSet<>()));
    }

    public static boolean isFavorite(Context c, int productId) {
        return getFavorites(c).contains(String.valueOf(productId));
    }

    // Zwraca true, jeśli produkt jest teraz w ulubionych
    public static boolean toggleFavorite(Context c, int productId) {
        Set<String> favs = getFavorites(c);
        String id = String.valueOf(productId);
        boolean nowFavorite;
        if (favs.contains(id)) {
            favs.remove(id);
            nowFavorite = false;
        } else {
            favs.add(id);
            nowFavorite = true;
        }
        prefs(c).edit().putStringSet("fav_" + getCurrentUser(c), favs).apply();
        return nowFavorite;
    }
}