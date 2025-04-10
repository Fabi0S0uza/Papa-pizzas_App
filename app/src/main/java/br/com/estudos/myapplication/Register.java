package br.com.estudos.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import br.com.estudos.myapplication.User;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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

        // Verificando se a senha e a confirmação de senha são iguais
        if (!password.equals(confirmPassword)) {
            Toast.makeText(Register.this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Salvando dados no Firestore
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
                                    Toast.makeText(Register.this, "Erro ao salvar dados no Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Se a criação do usuário falhar
                        Toast.makeText(Register.this, "Falha no cadastro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
