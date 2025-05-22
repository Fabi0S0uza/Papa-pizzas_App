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

import br.com.estudos.myapplication.ui.Delete;

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
        btn_Cadastrar = findViewById(R.id.btn_Cadastrar);
        btn_Deletar = findViewById(R.id.btn_Deletar);  // Botão para deletar produtos
        btn_DeletarUser = findViewById(R.id.btn_DeletarUser); // Botão para deletar usuários
        btn_Cupom = findViewById(R.id.btn_Cupom); //adicionar cupom



        btn_DeletarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(UserManagement.this, Delete.class);
                startActivity(in);
            }

        });   // Deleta usuário
        btn_Deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(UserManagement.this, Delete.class);
                startActivity(in);
            }
        });    // Deleta produto

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







}
