package br.com.estudos.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email_edit_text);
        resetPasswordButton = findViewById(R.id.reset_password_button);

        resetPasswordButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPassword.this, "Digite um e-mail válido", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "E-mail enviado com instruções para redefinir a senha", Toast.LENGTH_LONG).show();
                            finish(); // Fecha a activity se desejar
                        } else {
                            Toast.makeText(ForgotPassword.this, "Erro ao enviar e-mail: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
