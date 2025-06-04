package br.com.estudos.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast; // Keep Toast for potential future use or debugging

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.estudos.myapplication.ui.Delete;
// Removed: import br.com.estudos.myapplication.ui.home.HomeFragment; // HomeFragment is a Fragment, not an Activity

public class UserManagement extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    // Removed: private EditText user; // Not used in the provided code
    private Button btn_Cadastrar, btn_Deletar, btn_DeletarUser, btn_Cupom;
    private ImageView logo; // Declare ImageView here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI components
        btn_Cadastrar = findViewById(R.id.btn_Cadastrar);
        btn_Deletar = findViewById(R.id.btn_Deletar);  // Button to delete products
        btn_DeletarUser = findViewById(R.id.btn_DeletarUser); // Button to delete users
        btn_Cupom = findViewById(R.id.btn_Cupom); // Button to add coupon
        logo = findViewById(R.id.logoAdm); // Initialize logo ImageView here

        // Set OnClickListeners for buttons
        btn_DeletarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(UserManagement.this, Delete.class);
                startActivity(in);
            }
        });

        btn_Deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(UserManagement.this, Delete.class);
                startActivity(in);
            }
        });

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

        // Set OnClickListener for the logo ImageView
        logo.setOnClickListener(view -> {
            // Assuming MainActivity is the activity that hosts HomeFragment
            // If not, you might need to adjust this to the correct main activity of your app.
            Intent intent = new Intent(UserManagement.this, MainActivity.class);
            startActivity(intent);
            finish(); // Optional: finish this activity so user can't navigate back to it with back button
        });
    }
}