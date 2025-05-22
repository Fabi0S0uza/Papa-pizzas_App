package br.com.estudos.myapplication.ui;

import android.app.AlertDialog; // Import AlertDialog
import android.content.DialogInterface; // Import DialogInterface
import android.os.Bundle;
import android.util.Log; // Import Log for debugging
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.estudos.myapplication.R;

public class Delete extends AppCompatActivity {

    private static final String TAG = "DeleteActivity"; // Tag for logging

    // Instância do Firebase Firestore
    private FirebaseFirestore db;
    // ListView para exibir utilizadores e produtos
    private ListView listUsers, listProducts;
    // Adapters para as ListViews
    private ArrayAdapter<String> userAdapter, productAdapter;
    // Listas para armazenar dados de utilizadores e produtos (nomes/CPFs para exibição)
    private List<String> userList = new ArrayList<>();
    private List<String> productList = new ArrayList<>();
    // Mapas para ligar nomes/CPFs de exibição aos IDs dos documentos do Firestore
    private Map<String, String> userIdMap = new HashMap<>();
    private Map<String, String> productIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout para esta atividade
        setContentView(R.layout.activity_delete);

        // Obtém a instância do Firebase Firestore
        db = FirebaseFirestore.getInstance();
        // Inicializa as ListViews a partir do layout
        listUsers = findViewById(R.id.listUsers);
        listProducts = findViewById(R.id.listProducts);

        // Inicializa os Adapters com contexto, layout e listas de dados
        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);

        // Define os adapters para as respetivas ListViews
        listUsers.setAdapter(userAdapter);
        listProducts.setAdapter(productAdapter);

        // Busca os dados iniciais para utilizadores e produtos
        fetchUsers();
        fetchProducts();

        // Define o listener de clique nos itens para a lista de utilizadores
        listUsers.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected CPF from the list
            String selectedCpf = userList.get(position);
            // Get the corresponding user ID from the map
            String userId = userIdMap.get(selectedCpf);

            // Chama o método para exibir o diálogo de confirmação para o utilizador
            if (userId != null) {
                showDeleteConfirmationDialog("Utilizador", selectedCpf, userId, true);
            } else {
                Toast.makeText(this, "Não foi possível obter o ID do utilizador.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Attempted to delete user with null ID for CPF: " + selectedCpf);
            }
        });

        // Define o listener de clique nos itens para a lista de produtos
        listProducts.setOnItemClickListener((parent, view, position, id) -> {
            // Obtém o nome do produto selecionado da lista
            String selectedProduct = productList.get(position);
            // Obtém o ID do produto correspondente a partir do mapa
            String productId = productIdMap.get(selectedProduct);

            // Chama o método para exibir o diálogo de confirmação para o produto
            if (productId != null) {
                showDeleteConfirmationDialog("Produto", selectedProduct, productId, false);
            } else {
                Toast.makeText(this, "Não foi possível obter o ID do produto.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Attempted to delete product with null ID for product: " + selectedProduct);
            }
        });
    }

    // Método para buscar utilizadores no Firestore
    private void fetchUsers() {
        db.collection("users").get().addOnSuccessListener(query -> {
            // Limpa os dados existentes
            userList.clear();
            userIdMap.clear();
            // Itera pelos documentos e adiciona dados de utilizador às listas e mapa
            for (QueryDocumentSnapshot doc : query) {
                String cpf = doc.getString("cpf");
                String docId = doc.getId(); // Get the document ID

                // **Reinforced Null Check:** Ensure both cpf and docId are not null
                if (cpf != null && docId != null) {
                    userList.add(cpf); // Adiciona CPF à lista para exibição
                    userIdMap.put(cpf, docId); // Mapeia CPF para o ID do documento
                    Log.d(TAG, "Fetched user with CPF: " + cpf + " and ID: " + docId);
                } else {
                    // Log a warning if a document is missing CPF or ID
                    Log.w(TAG, "Skipping user document " + doc.getId() + " due to missing CPF or ID.");
                }
            }
            // Notifica o adapter que os dados foram alterados
            userAdapter.notifyDataSetChanged();
            Log.d(TAG, "User list updated. Count: " + userList.size());
        }).addOnFailureListener(e -> {
            // Log and show error if fetching users fails
            Log.e(TAG, "Error fetching users: ", e);
            Toast.makeText(this, "Erro ao carregar utilizadores: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Método para buscar produtos (pizzas) no Firestore
    private void fetchProducts() {
        db.collection("pizzas").get().addOnSuccessListener(query -> {
            // Limpa os dados existentes
            productList.clear();
            productIdMap.clear();
            // Itera pelos documentos e adiciona dados de produto às listas e mapa
            for (QueryDocumentSnapshot doc : query) {
                String name = doc.getString("nome");
                String docId = doc.getId(); // Get the document ID

                // **Reinforced Null Check:** Ensure both name and docId are not null
                if (name != null && docId != null) {
                    productList.add(name); // Adiciona nome do produto à lista para exibição
                    productIdMap.put(name, docId); // Mapeia nome do produto para o ID do documento
                    Log.d(TAG, "Fetched product with name: " + name + " and ID: " + docId);
                } else {
                    // Log a warning if a document is missing 'nome' or ID
                    Log.w(TAG, "Skipping product document " + doc.getId() + " due to missing 'nome' or ID.");
                }
            }
            // Notifica o adapter que os dados foram alterados
            productAdapter.notifyDataSetChanged();
            Log.d(TAG, "Product list updated. Count: " + productList.size());
        }).addOnFailureListener(e -> {
            // Log and show error if fetching products fails
            Log.e(TAG, "Error fetching products: ", e);
            Toast.makeText(this, "Erro ao carregar produtos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Método para exibir um diálogo de confirmação antes de excluir
    private void showDeleteConfirmationDialog(String itemType, String itemName, String itemId, boolean isUser) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir este " + itemType + ": " + itemName + "?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Se o utilizador confirmar, chama o método de exclusão apropriado
                        if (isUser) {
                            deleteUser(itemId);
                        } else {
                            deleteProduct(itemId);
                        }
                    }
                })
                .setNegativeButton("Não", null) // O null listener simplesmente fecha o diálogo
                .setIcon(android.R.drawable.ic_dialog_alert) // Ícone de alerta
                .show();
    }


    // Método para excluir um utilizador pelo seu ID de documento
    private void deleteUser(String userId) {
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Exibe mensagem de sucesso
                    Toast.makeText(this, "Utilizador excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User document with ID " + userId + " deleted successfully.");
                    // Atualiza a lista de utilizadores após a exclusão
                    fetchUsers();
                })
                .addOnFailureListener(e -> {
                    // Exibe mensagem de erro
                    Toast.makeText(this, "Erro ao excluir utilizador", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting user document with ID " + userId + ": ", e);
                });
    }

    // Método para excluir um produto pelo seu ID de documento
    private void deleteProduct(String productId) {
        db.collection("pizzas").document(productId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Exibe mensagem de sucesso
                    Toast.makeText(this, "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Product document with ID " + productId + " deleted successfully.");
                    // Atualiza a lista de produtos após a exclusão
                    fetchProducts();
                })
                .addOnFailureListener(e -> {
                    // Exibe mensagem de erro
                    Toast.makeText(this, "Erro ao excluir produto", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting product document with ID " + productId + ": ", e);
                });
    }
}
