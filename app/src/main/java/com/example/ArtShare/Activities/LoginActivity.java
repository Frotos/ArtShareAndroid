package com.example.ArtShare.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ArtShare.Models.User;
import com.example.ArtShare.R;
import com.example.ArtShare.Repositories.AuthRepository;
import com.example.ArtShare.Utils;

import org.json.JSONException;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputPassword;

    AuthRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(Utils.loadLocale(this));
        setContentView(R.layout.activity_login);

        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);

        repository = new AuthRepository();

        String sessionId = Utils.loadSessionId(this);
        if (!sessionId.equals("")) {
            new Thread(() -> loginUserBySession(sessionId)).start();
        }

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> new Thread(this::loginUser).start());
    }

    public void showRegisterLayout(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void loginUser() {
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        if (!username.isEmpty() && !password.isEmpty()) {
            User user = new User();
            if (username.contains("@")) {
                user.email = inputUsername.getText().toString();
            } else {
                user.username = inputUsername.getText().toString();
            }
            user.password = inputPassword.getText().toString();

            repository.login(this, user, response -> {
                try {
                    Utils.saveSessionId(this, response.getString("token"));
                    this.startActivity(new Intent(this, MainActivity.class));
                    this.finish();
                } catch (JSONException exception) {
                    Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loginUserBySession(String sessionId) {
        repository.loginBySession(this, sessionId, response -> {
            try {
                Utils.saveSessionId(this, response.getString("token"));
                this.startActivity(new Intent(this, MainActivity.class));
                this.finish();
            } catch (JSONException exception) {
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLocale(String locale) {
        Locale newLocale = new Locale(locale);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = newLocale;
        resources.updateConfiguration(configuration, displayMetrics);
    }
}