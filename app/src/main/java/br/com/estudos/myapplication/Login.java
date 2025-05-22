package br.com.estudos.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink, forgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        registerLink = findViewById(R.id.register_link);
        forgotPasswordLink = findViewById(R.id.forgot_password_link);

        loginButton.setOnClickListener(v -> loginUser());
        registerLink.setOnClickListener(v -> navigateToRegister());
        forgotPasswordLink.setOnClickListener(v -> navigateToForgotPassword());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Verifica se o email é do admin
                        if (email.equals("admin@email.com")) {
                            startActivity(new Intent(Login.this, UserManagement.class));
                        } else {
                            startActivity(new Intent(Login.this, MainActivity.class));
                        }
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Falha no login. Verifique as credenciais.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToRegister() {
        startActivity(new Intent(Login.this, Register.class));
    }

    private void navigateToForgotPassword() {
        startActivity(new Intent(Login.this, ForgotPassword.class));
    }
}
