package com.example.demoemptyactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        EditText imieText = findViewById(R.id.inputImie);
        EditText nazwiskoText = findViewById(R.id.inputNazwisko);
        EditText emailText = findViewById(R.id.inputEmail);
        EditText loginText = findViewById(R.id.inputLogin2);
        EditText passwordText = findViewById(R.id.inputPassword2);
        Button signUpButton = findViewById(R.id.submitButton3);
        ImageView backButton = findViewById(R.id.backButton);

        signUpButton.setOnClickListener(view -> {
            boolean hasEmpty = false;
            hasEmpty |= isEmpty(imieText, "Wpisz imię");
            hasEmpty |= isEmpty(nazwiskoText, "Wpisz nazwisko");
            hasEmpty |= isEmpty(emailText, "Wpisz e-mail");
            hasEmpty |= isEmpty(loginText, "Wpisz login");
            hasEmpty |= isEmpty(passwordText, "Wpisz hasło");
            if (hasEmpty) {
                return;
            }

            String imie = imieText.getText().toString().trim();
            String nazwisko = nazwiskoText.getText().toString().trim();
            String email = emailText.getText().toString().trim();
            String login = loginText.getText().toString().trim();
            String password = passwordText.getText().toString();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailText.setError("Niepoprawny adres e-mail");
                return;
            }
            if (password.length() < 6) {
                passwordText.setError("Hasło musi mieć min. 6 znaków");
                return;
            }
            if (!UserStore.register(this, login, password, imie, nazwisko, email)) {
                loginText.setError("Ten login jest już zajęty");
                return;
            }

            UserStore.setCurrentUser(this, login);
            Toast.makeText(this, "Konto utworzone!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SignUpActivity.this, ShoppingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        backButton.setOnClickListener(view -> finish());
    }

    private boolean isEmpty(EditText field, String message) {
        if (field.getText().toString().trim().isEmpty()) {
            field.setError(message);
            return true;
        }
        return false;
    }
}