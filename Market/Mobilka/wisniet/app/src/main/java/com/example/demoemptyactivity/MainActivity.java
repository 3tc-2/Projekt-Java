package com.example.demoemptyactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        // Konto testowe (jeśli już istnieje, nic się nie dzieje)
        UserStore.register(this, "demo", "demo123", "Demo", "Użytkownik", "demo@wisniet.pl");

        // Zapamiętana sesja: od razu przechodzimy do sklepu
        if (UserStore.getCurrentUser(this) != null) {
            openShop();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        EditText loginText = findViewById(R.id.inputLogin);
        EditText passwordText = findViewById(R.id.inputPassword);
        Button loginButton = findViewById(R.id.submitButton);
        Button signUpButton = findViewById(R.id.submitButton2);

        loginButton.setOnClickListener(v -> {
            String login = loginText.getText().toString().trim();
            String password = passwordText.getText().toString();

            if (login.isEmpty()) {
                loginText.setError("Wpisz login");
                return;
            }
            if (password.isEmpty()) {
                passwordText.setError("Wpisz hasło");
                return;
            }
            if (!UserStore.checkLogin(this, login, password)) {
                Toast.makeText(this, "Nieprawidłowy login lub hasło", Toast.LENGTH_SHORT).show();
                return;
            }

            UserStore.setCurrentUser(this, login);
            openShop();
        });

        signUpButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SignUpActivity.class)));
    }

    private void openShop() {
        startActivity(new Intent(MainActivity.this, ShoppingActivity.class));
        finish();
    }
}