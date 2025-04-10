package br.com.estudos.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UserManagement extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private EditText user;
    private Button btn_Cadastrar, btn_Deletar, btn_DeletarUser, btn_Cupom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // Inicializa Firestore e FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Inicializa os componentes da interface
        user = findViewById(R.id.editTextNames);
        btn_Cadastrar = findViewById(R.id.btn_Cadastrar);
        btn_Deletar = findViewById(R.id.btn_Deletar);  // Botão para deletar produtos
        btn_DeletarUser = findViewById(R.id.btn_DeletarUser); // Botão para deletar usuários
        btn_Cupom = findViewById(R.id.btn_Cupom); //adicionar cupom
        btn_DeletarUser.setOnClickListener(v -> deleteUser());   // Deleta usuário
        btn_Deletar.setOnClickListener(v -> deleteProduct());    // Deleta produto

        btn_Cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(UserManagement.this, InsertPizzas.class);
                startActivity(in);
            }
        });

        btn_Cupom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(UserManagement.this, AdminActivity.class);
                startActivity(in);
            }
        });
    }

    private void deleteUser() {
        String cpf = user.getText().toString().trim(); // Pegando o CPF digitado

        if (TextUtils.isEmpty(cpf)) {
            Toast.makeText(this, "Digite um CPF para excluir o usuário!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("cpf", cpf) // Comparação EXATA com o Firestore
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Nenhum usuário encontrado com este CPF!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String userId = documentSnapshot.getId();
                        Log.d("Firebase", "Usuário encontrado para deletar: " + documentSnapshot.getData());

                        // Deletar usuário do Firestore
                        db.collection("users").document(userId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Usuário excluído com sucesso!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firebase", "Erro ao excluir usuário", e);
                                    Toast.makeText(this, "Erro ao excluir o usuário", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Erro ao buscar usuário", e);
                    Toast.makeText(this, "Erro ao buscar o usuário", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteProduct() {
        String nomeProduto = user.getText().toString().trim(); // Pegando o nome do produto digitado

        if (TextUtils.isEmpty(nomeProduto)) {
            Toast.makeText(this, "Digite o nome do produto para excluir!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("pizzas")
                .whereEqualTo("nome", nomeProduto) // Comparação EXATA com o Firestore
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Nenhum produto encontrado com este nome!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String productId = documentSnapshot.getId();
                        Log.d("Firebase", "Produto encontrado para deletar: " + documentSnapshot.getData());

                        // Deletar produto do Firestore
                        db.collection("pizzas").document(productId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firebase", "Erro ao excluir produto", e);
                                    Toast.makeText(this, "Erro ao excluir o produto", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Erro ao buscar produto", e);
                    Toast.makeText(this, "Erro ao buscar o produto", Toast.LENGTH_SHORT).show();
                });


    }




}
