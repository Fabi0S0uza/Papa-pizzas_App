package br.com.estudos.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    TextView textSenhaIncorreta, textSenhaIncorreta2;
    private EditText nameEditText, cpfEditText, phoneEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar campos
        nameEditText = findViewById(R.id.name_edit_text);
        cpfEditText = findViewById(R.id.cpf_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        registerButton = findViewById(R.id.register_button);
        textSenhaIncorreta = findViewById(R.id.textViewSenhaIncorreta);
        textSenhaIncorreta2 = findViewById(R.id.textViewSenhaIncorreta2);

        // Ação do botão de registro
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String name = nameEditText.getText().toString();
        String cpf = cpfEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Verificar se todos os campos foram preenchidos
        if (name.isEmpty() || cpf.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(Register.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar se as senhas coincidem
        if (!password.equals(confirmPassword)) {
            textSenhaIncorreta.setVisibility(View.VISIBLE);
            textSenhaIncorreta.setText("As senhas não coincidem");
            textSenhaIncorreta2.setText("As senhas não coincidem");

            return;
        } else {
            textSenhaIncorreta.setVisibility(View.GONE); // Esconde o aviso se senhas estiverem corretas
        }

        // Criar usuário no Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Salvar dados adicionais no Firestore
                        User user = new User(name, cpf, phone, email);
                        db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(Register.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Register.this, Login.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Register.this, "Erro ao salvar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(Register.this, "Erro no cadastro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
