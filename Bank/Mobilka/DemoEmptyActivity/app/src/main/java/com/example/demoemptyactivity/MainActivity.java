package com.example.demoemptyactivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView answer;
    TextView getResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getResponse = findViewById(R.id.etResponse);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void firstRequest(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.0.195:8080/";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                answer.setText("Response => "+response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                answer.setText("Response => "+error.toString());
            }
        });
        queue.add(jsObjRequest);
    }


    public void register(View button) {
        String url = "http://192.168.0.195:8080/register";

        JSONObject json = new JSONObject();

        EditText etName = findViewById(R.id.name);
        EditText etLastname = findViewById(R.id.surname);
        EditText etMail = findViewById(R.id.email);
        EditText etPassword = findViewById(R.id.password);
        EditText etPesel = findViewById(R.id.pesel);
        EditText etTelefon = findViewById(R.id.telefon);

        TextView etResponse = findViewById(R.id.etResponse);

        String name = etName.getText().toString();
        String surname = etLastname.getText().toString();
        String email = etMail.getText().toString();
        String password = etPassword.getText().toString();
        String pesel = etPesel.getText().toString();
        String telefon = etTelefon.getText().toString();

        try {
            json.put("name", name);
            json.put("surname", surname);
            json.put("email", email);
            json.put("password", password);
            json.put("pesel", pesel);
            json.put("telefon", telefon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = json.toString();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    etName.setText("");
                    etLastname.setText("");
                    etMail.setText("");
                    etPassword.setText("");
                    etPesel.setText("");
                    etTelefon.setText("");

                    Toast.makeText(MainActivity.this, "Dane zostały dodane!", Toast.LENGTH_SHORT).show();
                    etResponse.setText("Dane zostały dodane! ID: " + response.trim());
                },



                error -> {
                    String errorMessage = "Wystąpił błąd";

                    if(error.networkResponse != null && error.networkResponse.data != null) {
                        String responseBody = new String(error.networkResponse.data, java.nio.charset.StandardCharsets.UTF_8);
                        Log.d("testy", requestBody);

                        try {
                            org.json.JSONObject jsonObject = new org.json.JSONObject(responseBody);
                            if (jsonObject.has("message") && !jsonObject.getString("message").isEmpty()) {
                                errorMessage = jsonObject.getString("message");
                            }
                        } catch (JSONException e) {
                            if (!responseBody.isEmpty()) {
                                errorMessage = responseBody;
                            }
                        }
                    } else {
                        Log.d("testy", error.toString());
                    }

                    Toast.makeText(MainActivity.this, "Nie udało się", Toast.LENGTH_SHORT).show();
                    etResponse.setText(errorMessage);
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };

        queue.add(request);
    }

    }
