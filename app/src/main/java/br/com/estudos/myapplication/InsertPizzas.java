package br.com.estudos.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class InsertPizzas extends AppCompatActivity {

    private EditText nomeEditText, precoEditText, urlImagem;
    private ImageView imagemImageView;
    private Button salvarButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_pizzas);

        nomeEditText = findViewById(R.id.nomeEditText);
        precoEditText = findViewById(R.id.precoEditText);
        imagemImageView = findViewById(R.id.imagemImageView);
        salvarButton = findViewById(R.id.salvarButton);
        urlImagem = findViewById(R.id.urlImagem);

        db = FirebaseFirestore.getInstance();

        salvarButton.setOnClickListener(v -> {
            String nome = nomeEditText.getText().toString();
            double preco = Double.parseDouble(precoEditText.getText().toString());
            String imagemUrl = urlImagem.getText().toString();

            Pizza pizza = new Pizza(nome, preco, imagemUrl);
            salvarPizzaNoFirestore(pizza);
        });
    }

    private void salvarPizzaNoFirestore(Pizza pizza) {
        db.collection("pizzas")
                .add(pizza)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(InsertPizzas.this, "Pizza cadastrada com sucesso", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(InsertPizzas.this, "Erro ao cadastrar pizza", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        nomeEditText.setText("");
        precoEditText.setText("");
        urlImagem.setText("");

    }

    // Deletar pizza
    public void deletarPizza(String pizzaId) {
        db.collection("pizzas").document(pizzaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Pizza excluída com sucesso", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao excluir pizza: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public void listarPizzas() {
        db.collection("pizzas").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String pizzaId = document.getId(); // ID do documento
                            String nomePizza = document.getString("name");
                            double preco = document.getDouble("preco");

                            // Criar um botão para excluir pizza
                            Button btnDeletePizza = new Button(this);
                            btnDeletePizza.setText("Excluir " + nomePizza);
                            btnDeletePizza.setOnClickListener(v -> deletarPizza(pizzaId));

                            // Exibir o botão ou pizzas na interface (você pode usar um LinearLayout para adicionar os botões dinamicamente)
                            // Exemplo: addView do botão no layout.
                        }
                    } else {
                        Toast.makeText(this, "Erro ao carregar pizzas", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
