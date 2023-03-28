package com.example.ArtShare.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ArtShare.Models.Avatar;
import com.example.ArtShare.Models.User;
import com.example.ArtShare.R;
import com.example.ArtShare.Repositories.AuthRepository;
import com.example.ArtShare.Utils;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputConfirmPassword;

    private AuthRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);

        repository = new AuthRepository();

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view -> new Thread(this::registerUser).start());
    }

    public void showLoginLayout(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    private void registerUser() {
        String username = inputUsername.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

        if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
            if (password.equals(confirmPassword)) {
                User user = new User();
                user.username = inputUsername.getText().toString();
                user.email = inputEmail.getText().toString();
                user.password = inputPassword.getText().toString();

                Bitmap bitmapImage = ((BitmapDrawable)getResources().getDrawable(R.drawable.standard_avatar)).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.WEBP, 100, stream);
                Avatar avatar = new Avatar();
                avatar.image = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
                user.avatar = avatar;

                repository.register(this, user, response -> {
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
    }
}